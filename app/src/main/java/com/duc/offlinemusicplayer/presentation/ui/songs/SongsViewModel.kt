package com.duc.offlinemusicplayer.presentation.ui.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.duc.offlinemusicplayer.domain.model.PlaybackState
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.model.SortOrder
import com.duc.offlinemusicplayer.domain.repository.MusicRepository
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.presentation.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.duc.offlinemusicplayer.domain.repository.PlaylistRepository
import com.duc.offlinemusicplayer.domain.model.Playlist

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playbackRepository: PlaybackRepository,
    private val playlistRepository: PlaylistRepository,
) : BaseVM() {

    val playlists: LiveData<List<Playlist>> = playlistRepository.observeAllPlaylists().asLiveData()

    private val rawSongs: LiveData<List<Song>> = musicRepository.observeSongs().asLiveData()

    private val _sortOrder = MutableLiveData(SortOrder.ALPHABETICAL_ASC)
    val sortOrder: LiveData<SortOrder> = _sortOrder

    val songs: LiveData<List<Song>> = MediatorLiveData<List<Song>>().apply {
        addSource(rawSongs) { value = sortSongs(it.orEmpty(), _sortOrder.value ?: SortOrder.ALPHABETICAL_ASC) }
        addSource(_sortOrder) { value = sortSongs(rawSongs.value.orEmpty(), it) }
    }

    val playbackState: LiveData<PlaybackState> = playbackRepository.observePlaybackState()

    init {
        // Trigger initial scan
        viewModelScope.launch { runCatching { musicRepository.refreshSongs() } }
    }

    fun playAll(startIndex: Int = 0) {
        viewModelScope.launch {
            val list = songs.value.orEmpty()
            if (list.isNotEmpty()) playbackRepository.setQueue(list, startIndex)
        }
    }

    fun shuffleAll() {
        viewModelScope.launch {
            val list = songs.value.orEmpty().shuffled()
            if (list.isNotEmpty()) playbackRepository.setQueue(list, 0)
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch { playbackRepository.play(song) }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            val state = playbackState.value
            if (state?.isPlaying == true) playbackRepository.pause() else playbackRepository.play()
        }
    }

    fun skipNext() {
        viewModelScope.launch { playbackRepository.skipNext() }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch { musicRepository.setFavorite(song.id, !song.isFavorite) }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }

    private fun sortSongs(list: List<Song>, order: SortOrder): List<Song> = when (order) {
        SortOrder.ALPHABETICAL_ASC -> list.sortedBy { it.title.lowercase() }
        SortOrder.ALPHABETICAL_DESC -> list.sortedByDescending { it.title.lowercase() }
        SortOrder.DATE_ADDED_ASC -> list.sortedBy { it.dateAddedSec }
        SortOrder.DATE_ADDED_DESC -> list.sortedByDescending { it.dateAddedSec }
        SortOrder.DURATION_ASC -> list.sortedBy { it.durationMs }
        SortOrder.DURATION_DESC -> list.sortedByDescending { it.durationMs }
    }
}
