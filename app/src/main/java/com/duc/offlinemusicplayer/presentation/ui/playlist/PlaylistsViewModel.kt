package com.duc.offlinemusicplayer.presentation.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import com.duc.offlinemusicplayer.domain.model.Playlist
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.repository.MusicRepository
import com.duc.offlinemusicplayer.domain.repository.PlaylistRepository
import com.duc.offlinemusicplayer.presentation.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.domain.model.PlaybackState

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val playbackRepository: PlaybackRepository,
    private val preferenceHelper: PreferenceHelper,
) : BaseVM() {

    val playlists: LiveData<List<Playlist>> = playlistRepository.observeAllPlaylists().asLiveData()

    val favoriteSongs: LiveData<List<Song>> = musicRepository.observeFavorites().asLiveData()

    val recentPlayedSongs: LiveData<List<Song>> = musicRepository.observeSongs().map { allSongs ->
        val recentIds = preferenceHelper.getRecentPlayedIds()
        recentIds.mapNotNull { id -> allSongs.firstOrNull { it.id == id } }
    }.asLiveData()

    val recentAddedSongs: LiveData<List<Song>> = musicRepository.observeSongs().map { allSongs ->
        allSongs.sortedByDescending { it.dateAddedSec }
    }.asLiveData()

    val mostListeningSongs: LiveData<List<Song>> = musicRepository.observeSongs().map { allSongs ->
        val countsMap = preferenceHelper.getMostListeningCounts()
        allSongs.filter { countsMap.containsKey(it.id) }
            .sortedByDescending { countsMap[it.id] ?: 0 }
    }.asLiveData()

    val playbackState: LiveData<PlaybackState> = playbackRepository.observePlaybackState()

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                playlistRepository.createPlaylist(name.trim())
            }
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(id)
        }
    }

    fun togglePin(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.pinPlaylist(playlist.id, !playlist.isPinned)
        }
    }

    fun observeSongsInPlaylist(playlistId: Long): LiveData<List<Song>> =
        playlistRepository.observeSongsInPlaylist(playlistId).asLiveData()

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.setFavorite(song.id, !song.isFavorite)
        }
    }

    fun playAll(songs: List<Song>, startIndex: Int = 0) {
        viewModelScope.launch {
            if (songs.isNotEmpty()) {
                playbackRepository.setQueue(songs, startIndex)
            }
        }
    }

    fun shuffleAll(songs: List<Song>) {
        viewModelScope.launch {
            val shuffled = songs.shuffled()
            if (shuffled.isNotEmpty()) {
                playbackRepository.setQueue(shuffled, 0)
            }
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            playbackRepository.play(song)
        }
    }
}
