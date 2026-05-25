package com.duc.offlinemusicplayer.presentation.ui.songs

import android.os.Bundle
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentNowPlayingBinding
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class NowPlayingFragment : BaseFragment<FragmentNowPlayingBinding, NowPlayingViewModel>() {

    private var isUserDraggingSeekBar = false

    override fun getClassVM(): Class<NowPlayingViewModel> = NowPlayingViewModel::class.java

    override fun initViewBinding(): FragmentNowPlayingBinding =
        FragmentNowPlayingBinding.inflate(layoutInflater)

    override fun initView() {
        mBinding.btnBack.safeOnClickListener {
            navigationViewModel.back()
        }

        // Play Pause Action
        val playPauseAction = {
            mViewModel.togglePlayPause()
        }
        mBinding.btnPlayPauseCard.safeOnClickListener { playPauseAction() }
        mBinding.btnPlayPause.safeOnClickListener { playPauseAction() }

        // Auto start playback if paused on entry
        mViewModel.playbackState.value?.let { state ->
            if (!state.isPlaying && state.currentSong != null) {
                mViewModel.togglePlayPause()
            }
        }

        // Navigation skip
        mBinding.btnPrevious.safeOnClickListener {
            mViewModel.skipPrevious()
        }
        mBinding.btnNext.safeOnClickListener {
            mViewModel.skipNext()
        }

        // Rewind & Forward 10s
        mBinding.btnRewind10.safeOnClickListener {
            mViewModel.rewind10()
        }
        mBinding.btnForward10.safeOnClickListener {
            mViewModel.forward10()
        }

        // SeekBar change listener
        mBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mBinding.tvTimeElapsed.text = formatTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserDraggingSeekBar = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserDraggingSeekBar = false
                seekBar?.let {
                    mViewModel.seekTo(it.progress.toLong())
                }
            }
        })

        // Observe playback states
        mViewModel.playbackState.observe(viewLifecycleOwner) { state ->
            val song = state?.currentSong
            if (song != null) {
                mBinding.tvSongTitle.text = song.title
                mBinding.tvSongArtist.text = song.artist.ifBlank { "Unknown" }

                // Heart favorite icon
                mBinding.btnFavorite.setImageResource(
                    if (song.isFavorite) R.drawable.ic_heart_fill else R.drawable.ic_heart
                )
                mBinding.btnFavorite.safeOnClickListener {
                    mViewModel.toggleFavorite(song)
                }

                // Render song duration
                mBinding.tvTimeDuration.text = formatTime(state.durationMs)
                mBinding.seekBar.max = state.durationMs.toInt()

                // Update position
                if (!isUserDraggingSeekBar) {
                    mBinding.seekBar.progress = state.positionMs.toInt()
                    mBinding.tvTimeElapsed.text = formatTime(state.positionMs)
                }

                // Render circular cover art
                Glide.with(this)
                    .load(song.albumArtUri)
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(mBinding.ivCoverArt)
            }

            // Sync play pause button representation
            mBinding.btnPlayPause.setImageResource(
                if (state?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )

            // Connect dynamic visualizer canvas animation state
            mBinding.visualizerView.setPlaying(state?.isPlaying == true)
        }

        // Observe applied visualizer config
        mViewModel.appliedVisualizer.observe(viewLifecycleOwner) { visualizer ->
            if (visualizer != null) {
                // Config custom background image/gif
                val bgUrl = visualizer.config["background"] as? String ?: visualizer.thumbnail
                Glide.with(this)
                    .load(bgUrl)
                    .placeholder(R.color.neutral_900)
                    .into(mBinding.ivBackground)

                // Bind type and configuration parameters
                mBinding.visualizerView.setConfig(visualizer.type, visualizer.config)
            } else {
                // Fallback style
                mBinding.ivBackground.setImageResource(R.color.bg_dark)
                mBinding.visualizerView.setConfig("bar", emptyMap())
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSecs = ms / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}
