package com.duc.offlinemusicplayer.presentation.ui.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.duc.offlinemusicplayer.domain.model.PlaybackState
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.model.VisualizerItem
import com.duc.offlinemusicplayer.domain.repository.MusicRepository
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.domain.repository.VisualizerRepository
import com.duc.offlinemusicplayer.presentation.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playbackRepository: PlaybackRepository,
    private val visualizerRepository: VisualizerRepository
) : BaseVM() {

    val playbackState: LiveData<PlaybackState> = playbackRepository.observePlaybackState()

    private val _appliedVisualizer = MediatorLiveData<VisualizerItem?>()
    val appliedVisualizer: LiveData<VisualizerItem?> = _appliedVisualizer

    private val appliedIdFlow = visualizerRepository.observeAppliedId().asLiveData()
    private val visualizerItemsFlow = visualizerRepository.getVisualizerItems().asLiveData()

    init {
        _appliedVisualizer.addSource(appliedIdFlow) { id ->
            combineVisualizer(id, visualizerItemsFlow.value)
        }
        _appliedVisualizer.addSource(visualizerItemsFlow) { items ->
            combineVisualizer(appliedIdFlow.value, items)
        }
    }

    private fun combineVisualizer(id: String?, items: List<VisualizerItem>?) {
        if (id == null || items == null) {
            _appliedVisualizer.value = null
            return
        }
        _appliedVisualizer.value = items.firstOrNull { it.id == id }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            val state = playbackState.value ?: return@launch
            if (state.isPlaying) {
                playbackRepository.pause()
            } else {
                playbackRepository.play()
            }
        }
    }

    fun skipNext() {
        viewModelScope.launch {
            playbackRepository.skipNext()
        }
    }

    fun skipPrevious() {
        viewModelScope.launch {
            playbackRepository.skipPrevious()
        }
    }

    fun seekTo(positionMs: Long) {
        viewModelScope.launch {
            playbackRepository.seekTo(positionMs)
        }
    }

    fun rewind10() {
        viewModelScope.launch {
            val state = playbackState.value ?: return@launch
            val newPos = (state.positionMs - 10000).coerceAtLeast(0L)
            playbackRepository.seekTo(newPos)
        }
    }

    fun forward10() {
        viewModelScope.launch {
            val state = playbackState.value ?: return@launch
            val newPos = (state.positionMs + 10000).coerceAtMost(state.durationMs)
            playbackRepository.seekTo(newPos)
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            musicRepository.setFavorite(song.id, !song.isFavorite)
        }
    }
}
