package com.duc.offlinemusicplayer.presentation.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.duc.offlinemusicplayer.domain.model.PlaybackState
import com.duc.offlinemusicplayer.domain.model.SearchResults
import com.duc.offlinemusicplayer.domain.model.SearchTab
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.model.SortOrder
import com.duc.offlinemusicplayer.domain.repository.MusicRepository
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.domain.repository.PlaylistRepository
import com.duc.offlinemusicplayer.presentation.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Combines `query` + `activeTab` + `sortOrder` and triggers a debounced search.
 * Result categories are sorted client-side based on [SortOrder].
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val playbackRepository: PlaybackRepository,
) : BaseVM() {

    private val _query = MutableLiveData("")
    private val _activeTab = MutableLiveData(SearchTab.ALL)
    private val _sortOrder = MutableLiveData(SortOrder.ALPHABETICAL_ASC)
    private val _results = MutableLiveData(SearchResults())

    val query: LiveData<String> = _query
    val activeTab: LiveData<SearchTab> = _activeTab
    val sortOrder: LiveData<SortOrder> = _sortOrder
    val results: LiveData<SearchResults> = _results
    val playlists: LiveData<List<com.duc.offlinemusicplayer.domain.model.Playlist>> = playlistRepository.observeAllPlaylists().asLiveData()

    val playbackState: LiveData<PlaybackState> = playbackRepository.observePlaybackState()

    private var searchJob: Job? = null

    private val trigger = MediatorLiveData<Unit>().apply {
        addSource(_query) { value = Unit }
        addSource(_activeTab) { value = Unit }
        addSource(_sortOrder) { value = Unit }
    }

    init {
        // Drive search whenever any input changes (debounced inside [runSearch]).
        trigger.observeForever { runSearch() }
    }

    fun setQuery(value: String) {
        if (_query.value == value) return
        _query.value = value
    }

    fun setTab(tab: SearchTab) {
        if (_activeTab.value == tab) return
        _activeTab.value = tab
    }

    fun setSortOrder(order: SortOrder) {
        if (_sortOrder.value == order) return
        _sortOrder.value = order
    }

    fun playSong(song: Song) {
        viewModelScope.launch { playbackRepository.play(song) }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.setFavorite(song.id, !song.isFavorite)
            val currentResults = _results.value
            if (currentResults != null) {
                val updatedSongs = currentResults.songs.map {
                    if (it.id == song.id) it.copy(isFavorite = !song.isFavorite) else it
                }
                _results.postValue(currentResults.copy(songs = updatedSongs))
            }
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(playlistId, songId)
        }
    }

    private fun runSearch() {
        searchJob?.cancel()
        val q = _query.value.orEmpty().trim()
        val tab = _activeTab.value ?: SearchTab.ALL
        val order = _sortOrder.value ?: SortOrder.ALPHABETICAL_ASC

        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            runCatching { search(q, tab, order) }
                .onSuccess { _results.postValue(it) }
                .onFailure { _results.postValue(SearchResults()) }
        }
    }

    private suspend fun search(query: String, tab: SearchTab, order: SortOrder): SearchResults {
        val q = query.trim()
        val raw = when (tab) {
            SearchTab.ALL -> SearchResults(
                songs = if (q.isEmpty()) musicRepository.searchSongs("") else musicRepository.searchSongs(
                    q
                ),
                playlists = if (q.isEmpty()) playlistRepository.searchPlaylists("") else playlistRepository.searchPlaylists(
                    q
                ),
                albums = if (q.isEmpty()) musicRepository.searchAlbums("") else musicRepository.searchAlbums(
                    q
                ),
                artists = if (q.isEmpty()) musicRepository.searchArtists("") else musicRepository.searchArtists(
                    q
                ),
                folders = if (q.isEmpty()) musicRepository.searchFolders("") else musicRepository.searchFolders(
                    q
                ),
            )

            SearchTab.SONGS -> SearchResults(
                songs = if (q.isEmpty()) musicRepository.searchSongs("") else musicRepository.searchSongs(
                    q
                )
            )

            SearchTab.PLAYLISTS -> SearchResults(
                playlists = if (q.isEmpty()) playlistRepository.searchPlaylists(
                    ""
                ) else playlistRepository.searchPlaylists(q)
            )

            SearchTab.ALBUMS -> SearchResults(
                albums = if (q.isEmpty()) musicRepository.searchAlbums(
                    ""
                ) else musicRepository.searchAlbums(q)
            )

            SearchTab.ARTISTS -> SearchResults(
                artists = if (q.isEmpty()) musicRepository.searchArtists(
                    ""
                ) else musicRepository.searchArtists(q)
            )

            SearchTab.FOLDERS -> SearchResults(
                folders = if (q.isEmpty()) musicRepository.searchFolders(
                    ""
                ) else musicRepository.searchFolders(q)
            )
        }
        return raw.applySortOrder(order)
    }

    private fun SearchResults.applySortOrder(order: SortOrder): SearchResults = copy(
        songs = sortSongs(songs, order),
        playlists = sortByName(playlists, order) { it.name },
        albums = sortByName(albums, order) { it.name },
        artists = sortByName(artists, order) { it.name },
        folders = sortByName(folders, order) { it.name },
    )

    private fun sortSongs(list: List<Song>, order: SortOrder): List<Song> = when (order) {
        SortOrder.ALPHABETICAL_ASC -> list.sortedBy { it.title.lowercase() }
        SortOrder.ALPHABETICAL_DESC -> list.sortedByDescending { it.title.lowercase() }
        SortOrder.DATE_ADDED_ASC -> list.sortedBy { it.dateAddedSec }
        SortOrder.DATE_ADDED_DESC -> list.sortedByDescending { it.dateAddedSec }
        SortOrder.DURATION_ASC -> list.sortedBy { it.durationMs }
        SortOrder.DURATION_DESC -> list.sortedByDescending { it.durationMs }
    }

    // Non-song collections only have a name → date/duration sorts fall back to alphabetical.
    private inline fun <T> sortByName(
        list: List<T>,
        order: SortOrder,
        crossinline name: (T) -> String,
    ): List<T> = when (order) {
        SortOrder.ALPHABETICAL_DESC -> list.sortedByDescending { name(it).lowercase() }
        else -> list.sortedBy { name(it).lowercase() }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    companion object {
        private const val DEBOUNCE_MS = 300L
    }
}
