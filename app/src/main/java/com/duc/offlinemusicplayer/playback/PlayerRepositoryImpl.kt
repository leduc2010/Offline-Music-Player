package com.duc.offlinemusicplayer.playback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.duc.offlinemusicplayer.domain.model.PlaybackState
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor() : PlaybackRepository {

    private val playbackState = MutableLiveData(PlaybackState())

    override fun observePlaybackState(): LiveData<PlaybackState> = playbackState

    override suspend fun setQueue(queue: List<Song>, startIndex: Int) {
        if (queue.isEmpty()) {
            playbackState.value = PlaybackState()
            return
        }

        val currentState = playbackState.value ?: PlaybackState()
        val safeIndex = startIndex.coerceIn(0, queue.lastIndex)
        val startSong = queue[safeIndex]

        playbackState.value = currentState.copy(
            queue = queue,
            currentIndex = safeIndex,
            currentSong = startSong,
            isPlaying = true,
            positionMs = 0L,
            durationMs = startSong.durationMs,
        )
    }

    override suspend fun play(song: Song) {
        val currentState = playbackState.value ?: PlaybackState()
        val queue = currentState.queue
        val existingIndex = queue.indexOfFirst { it.id == song.id }
        val nextQueue = if (queue.isEmpty()) listOf(song) else queue
        val nextIndex = when {
            existingIndex >= 0 -> existingIndex
            queue.isEmpty() -> 0
            else -> currentState.currentIndex
        }

        playbackState.value = currentState.copy(
            queue = nextQueue,
            currentIndex = nextIndex,
            currentSong = song,
            isPlaying = true,
            positionMs = 0L,
            durationMs = song.durationMs,
        )
    }

    override suspend fun play() {
        val state = playbackState.value ?: return
        if (state.currentSong == null) return
        playbackState.value = state.copy(isPlaying = true)
    }

    override suspend fun pause() {
        val state = playbackState.value ?: return
        if (state.currentSong == null) return
        playbackState.value = state.copy(isPlaying = false)
    }

    override suspend fun skipNext() {
        val state = playbackState.value ?: return
        if (state.queue.isEmpty()) return

        val nextIndex = (state.currentIndex + 1).coerceAtMost(state.queue.lastIndex)
        val nextSong = state.queue[nextIndex]

        playbackState.value = state.copy(
            currentIndex = nextIndex,
            currentSong = nextSong,
            isPlaying = true,
            positionMs = 0L,
            durationMs = nextSong.durationMs,
        )
    }

    override suspend fun skipPrevious() {
        val state = playbackState.value ?: return
        if (state.queue.isEmpty()) return

        val previousIndex = (state.currentIndex - 1).coerceAtLeast(0)
        val previousSong = state.queue[previousIndex]

        playbackState.value = state.copy(
            currentIndex = previousIndex,
            currentSong = previousSong,
            isPlaying = true,
            positionMs = 0L,
            durationMs = previousSong.durationMs,
        )
    }

    override suspend fun seekTo(positionMs: Long) {
        val state = playbackState.value ?: return
        val currentSong = state.currentSong ?: return

        val safePosition = positionMs.coerceIn(0L, currentSong.durationMs)
        playbackState.value = state.copy(positionMs = safePosition)
    }
}