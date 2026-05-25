package com.duc.offlinemusicplayer.presentation.ui.visualizer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duc.offlinemusicplayer.domain.model.PlaybackState
import com.duc.offlinemusicplayer.domain.model.VisualizerCategory
import com.duc.offlinemusicplayer.domain.model.VisualizerItem
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.domain.repository.VisualizerRepository
import com.duc.offlinemusicplayer.presentation.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisualizerViewModel @Inject constructor(
    private val visualizerRepository: VisualizerRepository,
    private val playbackRepository: PlaybackRepository
) : BaseVM() {

    private val _categories = MutableLiveData<List<VisualizerCategory>>()
    val categories: LiveData<List<VisualizerCategory>> = _categories

    private val _visualizerItems = MutableLiveData<List<VisualizerItem>>()
    val visualizerItems: LiveData<List<VisualizerItem>> = _visualizerItems

    private val _downloadedIds = MutableLiveData<Set<String>>()
    val downloadedIds: LiveData<Set<String>> = _downloadedIds

    private val _appliedId = MutableLiveData<String?>()
    val appliedId: LiveData<String?> = _appliedId

    private val _selectedCategoryTag = MutableLiveData<String>("trending")
    val selectedCategoryTag: LiveData<String> = _selectedCategoryTag

    // Tracks simulated download progress: Key = VisualizerId, Value = Progress (0 to 100)
    private val _downloadProgress = MutableLiveData<Map<String, Int>>(emptyMap())
    val downloadProgress: LiveData<Map<String, Int>> = _downloadProgress

    val playbackState: LiveData<PlaybackState> = playbackRepository.observePlaybackState()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            visualizerRepository.getCategories().collectLatest {
                _categories.postValue(it)
            }
        }
        viewModelScope.launch {
            visualizerRepository.getVisualizerItems().collectLatest {
                _visualizerItems.postValue(it)
            }
        }
        viewModelScope.launch {
            visualizerRepository.observeDownloadedIds().collectLatest {
                _downloadedIds.postValue(it)
            }
        }
        viewModelScope.launch {
            visualizerRepository.observeAppliedId().collectLatest {
                _appliedId.postValue(it)
            }
        }
    }

    fun selectCategory(tag: String) {
        _selectedCategoryTag.value = tag
    }

    fun downloadVisualizer(id: String) {
        val currentProgress = _downloadProgress.value.orEmpty().toMutableMap()
        if (currentProgress.containsKey(id)) return // Already downloading

        viewModelScope.launch {
            currentProgress[id] = 0
            _downloadProgress.postValue(currentProgress)

            // Simulate progress from 0 to 100
            for (p in 10..100 step 10) {
                delay(120L)
                currentProgress[id] = p
                _downloadProgress.postValue(currentProgress)
            }

            // Complete download
            visualizerRepository.downloadVisualizer(id)
            currentProgress.remove(id)
            _downloadProgress.postValue(currentProgress)
        }
    }

    fun applyVisualizer(id: String) {
        viewModelScope.launch {
            visualizerRepository.applyVisualizer(id)
        }
    }
}
