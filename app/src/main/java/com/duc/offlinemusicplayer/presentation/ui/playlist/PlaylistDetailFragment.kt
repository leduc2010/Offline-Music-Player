package com.duc.offlinemusicplayer.presentation.ui.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentPlaylistDetailBinding
import com.duc.offlinemusicplayer.domain.model.Song
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.ui.songs.SongListAdapter
import com.duc.offlinemusicplayer.presentation.utils.findNavControllerSafely
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailFragment : BaseFragment<FragmentPlaylistDetailBinding, PlaylistsViewModel>() {

    private val args: PlaylistDetailFragmentArgs by navArgs()
    private lateinit var adapter: SongListAdapter
    private var songsList: List<Song> = emptyList()

    override fun getClassVM(): Class<PlaylistsViewModel> = PlaylistsViewModel::class.java

    override fun initViewBinding(): FragmentPlaylistDetailBinding =
        FragmentPlaylistDetailBinding.inflate(layoutInflater)

    override fun initView() {
        // Set toolbar title and back button behavior
        mBinding.toolbar.setTitleText(args.title)
        mBinding.toolbar.setOnStartClick {
            navigationViewModel.back()
        }
        mBinding.toolbar.setOnEndClick {
            findNavControllerSafely()?.navigate(PlaylistDetailFragmentDirections.actionPlaylistDetailToSearch())
        }

        // Setup songs adapter
        adapter = SongListAdapter(
            onSongClick = { _, index ->
                mViewModel.playAll(songsList, index)
            },
            onFavoriteClick = { song ->
                mViewModel.toggleFavorite(song)
            },
            onMoreClick = { song ->
                // For custom playlists, allow removing the song from the playlist
                if (args.type == "CUSTOM") {
                    val view = mBinding.rvSongs.findViewHolderForAdapterPosition(songsList.indexOf(song))?.itemView?.findViewById<View>(R.id.ivMore) ?: mBinding.rvSongs
                    val popup = PopupMenu(requireContext(), view)
                    popup.menu.add(0, 1, 0, "Remove from Playlist")
                    popup.setOnMenuItemClickListener { menuItem ->
                        if (menuItem.itemId == 1) {
                            mViewModel.removeSongFromPlaylist(args.playlistId, song.id)
                            true
                        } else false
                    }
                    popup.show()
                }
            }
        )

        mBinding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvSongs.adapter = adapter

        // Setup Play All & Shuffle clicks
        mBinding.btnPlay.safeOnClickListener {
            mViewModel.playAll(songsList)
        }

        mBinding.btnShuffle.safeOnClickListener {
            mViewModel.shuffleAll(songsList)
        }

        // Fetch & observe the correct list of songs based on type
        when (args.type) {
            "FAVORITE" -> {
                mViewModel.favoriteSongs.observe(viewLifecycleOwner) { songs ->
                    updateSongsList(songs)
                }
            }
            "RECENT_PLAYED" -> {
                mViewModel.recentPlayedSongs.observe(viewLifecycleOwner) { songs ->
                    updateSongsList(songs)
                }
            }
            "RECENT_ADDED" -> {
                mViewModel.recentAddedSongs.observe(viewLifecycleOwner) { songs ->
                    updateSongsList(songs)
                }
            }
            "MOST_LISTENING" -> {
                mViewModel.mostListeningSongs.observe(viewLifecycleOwner) { songs ->
                    updateSongsList(songs)
                }
            }
            "CUSTOM" -> {
                mViewModel.observeSongsInPlaylist(args.playlistId).observe(viewLifecycleOwner) { songs ->
                    updateSongsList(songs)
                }
            }
        }

        // Observe playback state to show current playing indicator color
        mViewModel.playbackState.observe(viewLifecycleOwner) { state ->
            adapter.currentPlayingId = state?.currentSong?.id ?: -1L
        }
    }

    private fun updateSongsList(songs: List<Song>) {
        songsList = songs
        adapter.submitList(songs)
        
        val isEmpty = songs.isEmpty()
        mBinding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        mBinding.rvSongs.visibility = if (isEmpty) View.GONE else View.VISIBLE
        mBinding.btnPlay.isEnabled = !isEmpty
        mBinding.btnShuffle.isEnabled = !isEmpty
    }
}
