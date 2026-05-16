package com.duc.offlinemusicplayer.domain.repository

import androidx.lifecycle.LiveData
import com.duc.offlinemusicplayer.domain.model.PlaybackState
import com.duc.offlinemusicplayer.domain.model.Song

interface PlaybackRepository {
    fun observePlaybackState(): LiveData<PlaybackState>

    suspend fun setQueue(queue: List<Song>, startIndex: Int)

    suspend fun play(song: Song)

    suspend fun play()

    suspend fun pause()

    suspend fun skipNext()

    suspend fun skipPrevious()

    suspend fun seekTo(positionMs: Long)
}