package com.duc.offlinemusicplayer.playback

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.duc.offlinemusicplayer.domain.model.PlaybackState
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferenceHelper: PreferenceHelper,
) : PlaybackRepository {

    private val playbackState = MutableLiveData(PlaybackState())
    private var mediaPlayer: MediaPlayer? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var progressRunnable: Runnable? = null

    override fun observePlaybackState(): LiveData<PlaybackState> = playbackState

    override suspend fun setQueue(queue: List<Song>, startIndex: Int) {
        if (queue.isEmpty()) {
            stopAndReleasePlayer()
            playbackState.postValue(PlaybackState())
            return
        }

        val safeIndex = startIndex.coerceIn(0, queue.lastIndex)
        val startSong = queue[safeIndex]

        playbackState.postValue(
            PlaybackState(
                currentSong = startSong,
                queue = queue,
                currentIndex = safeIndex,
                isPlaying = false,
                positionMs = 0L,
                durationMs = startSong.durationMs,
            )
        )
        play(startSong)
    }

    override suspend fun play(song: Song) {
        val state = playbackState.value ?: PlaybackState()
        val existingIndex = state.queue.indexOfFirst { it.id == song.id }

        val queue = if (state.queue.isEmpty()) listOf(song) else state.queue
        val index = when {
            existingIndex >= 0 -> existingIndex
            state.queue.isEmpty() -> 0
            else -> state.currentIndex.coerceAtLeast(0)
        }

        playbackState.postValue(
            state.copy(
                currentSong = song,
                queue = queue,
                currentIndex = index,
                isPlaying = false,
                positionMs = 0L,
                durationMs = song.durationMs,
            )
        )

        startSongInternal(song)
    }

    override suspend fun play() {
        val state = playbackState.value ?: return
        val song = state.currentSong ?: return

        val player = mediaPlayer
        if (player == null) {
            startSongInternal(song)
            return
        }

        if (!player.isPlaying) {
            player.start()
            playbackState.postValue(state.copy(isPlaying = true))
            startProgressUpdates()
        }
    }

    override suspend fun pause() {
        val state = playbackState.value ?: return
        val player = mediaPlayer ?: return
        if (!player.isPlaying) return

        player.pause()
        stopProgressUpdates()
        playbackState.postValue(
            state.copy(
                isPlaying = false,
                positionMs = player.currentPosition.toLong(),
            )
        )
    }

    override suspend fun skipNext() {
        val state = playbackState.value ?: return
        if (state.queue.isEmpty()) return

        val nextIndex = (state.currentIndex + 1).coerceAtMost(state.queue.lastIndex)
        play(state.queue[nextIndex])
    }

    override suspend fun skipPrevious() {
        val state = playbackState.value ?: return
        if (state.queue.isEmpty()) return

        val prevIndex = (state.currentIndex - 1).coerceAtLeast(0)
        play(state.queue[prevIndex])
    }

    override suspend fun seekTo(positionMs: Long) {
        val state = playbackState.value ?: return
        val player = mediaPlayer ?: return
        val safePos = positionMs.coerceIn(0L, state.durationMs).toInt()
        player.seekTo(safePos)
        playbackState.postValue(state.copy(positionMs = safePos.toLong()))
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        val runnable = object : Runnable {
            override fun run() {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    val latest = playbackState.value ?: PlaybackState()
                    playbackState.postValue(latest.copy(positionMs = player.currentPosition.toLong()))
                    handler.postDelayed(this, 500L)
                }
            }
        }
        progressRunnable = runnable
        handler.post(runnable)
    }

    private fun stopProgressUpdates() {
        progressRunnable?.let { handler.removeCallbacks(it) }
        progressRunnable = null
    }

    private fun startSongInternal(song: Song) {
        stopAndReleasePlayer()

        val player = MediaPlayer().apply {
            setDataSource(context, android.net.Uri.parse(song.contentUri))
            setOnPreparedListener {
                it.start()
                preferenceHelper.addRecentPlayedSong(song.id)
                startProgressUpdates()
                val latest = playbackState.value ?: PlaybackState()
                playbackState.postValue(
                    latest.copy(
                        isPlaying = true,
                        durationMs = if (it.duration > 0) it.duration.toLong() else song.durationMs,
                        positionMs = 0L,
                    )
                )
            }
            setOnCompletionListener {
                val latest = playbackState.value ?: PlaybackState()
                if (latest.currentIndex < latest.queue.lastIndex) {
                    val next = latest.queue[latest.currentIndex + 1]
                    playbackState.postValue(latest.copy(currentIndex = latest.currentIndex + 1, currentSong = next))
                    startSongInternal(next)
                } else {
                    stopProgressUpdates()
                    playbackState.postValue(latest.copy(isPlaying = false, positionMs = latest.durationMs))
                }
            }
            setOnErrorListener { _, _, _ ->
                val latest = playbackState.value ?: PlaybackState()
                playbackState.postValue(latest.copy(isPlaying = false))
                true
            }
            prepareAsync()
        }

        mediaPlayer = player
    }

    private fun stopAndReleasePlayer() {
        stopProgressUpdates()
        mediaPlayer?.run {
            runCatching { if (isPlaying) stop() }
            release()
        }
        mediaPlayer = null
    }
}
