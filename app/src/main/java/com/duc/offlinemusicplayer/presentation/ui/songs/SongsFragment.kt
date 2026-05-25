package com.duc.offlinemusicplayer.presentation.ui.songs

import androidx.recyclerview.widget.LinearLayoutManager
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentSongsBinding
import com.duc.offlinemusicplayer.domain.model.SortOrder
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.ui.songs.SongsFragmentDirections
import com.duc.offlinemusicplayer.presentation.utils.findNavControllerSafely
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : BaseFragment<FragmentSongsBinding, SongsViewModel>() {

    private val adapter by lazy {
        SongListAdapter(
            onSongClick = { song, _ -> mViewModel.playSong(song) },
            onFavoriteClick = { mViewModel.toggleFavorite(it) },
            onMoreClick = { song ->
                val list = mViewModel.songs.value.orEmpty()
                val idx = list.indexOfFirst { it.id == song.id }
                val view = if (idx >= 0) {
                    mBinding.rvSongs.findViewHolderForAdapterPosition(idx)?.itemView?.findViewById<android.view.View>(R.id.ivMore) ?: mBinding.rvSongs
                } else {
                    mBinding.rvSongs
                }
                val popup = androidx.appcompat.widget.PopupMenu(requireContext(), view)
                popup.menu.add(0, 1, 0, "Add to Playlist")
                popup.setOnMenuItemClickListener { menuItem ->
                    if (menuItem.itemId == 1) {
                        val playlists = mViewModel.playlists.value.orEmpty()
                        if (playlists.isEmpty()) {
                            android.widget.Toast.makeText(requireContext(), "Please create a playlist first", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            val playlistPopup = androidx.appcompat.widget.PopupMenu(requireContext(), view)
                            playlists.forEachIndexed { index, playlist ->
                                playlistPopup.menu.add(0, index, index, playlist.name)
                            }
                            playlistPopup.setOnMenuItemClickListener { plMenuItem ->
                                val selectedPlaylist = playlists[plMenuItem.itemId]
                                mViewModel.addSongToPlaylist(selectedPlaylist.id, song.id)
                                android.widget.Toast.makeText(requireContext(), "Added to ${selectedPlaylist.name}", android.widget.Toast.LENGTH_SHORT).show()
                                true
                            }
                            playlistPopup.show()
                        }
                        true
                    } else false
                }
                popup.show()
            }
        )
    }

    override fun getClassVM(): Class<SongsViewModel> = SongsViewModel::class.java

    override fun initViewBinding(): FragmentSongsBinding =
        FragmentSongsBinding.inflate(layoutInflater)

    override fun initView() {
        mBinding.rvSongs.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvSongs.adapter = adapter

        mBinding.btnPlay.setOnClickListener { mViewModel.playAll() }
        mBinding.btnShuffle.setOnClickListener { mViewModel.shuffleAll() }

        mBinding.tvSort.setOnClickListener {
            SortBottomSheetFragment(
                initial = mViewModel.sortOrder.value ?: SortOrder.ALPHABETICAL_ASC,
                onApply = { mViewModel.setSortOrder(it) },
            ).show(parentFragmentManager, "SortSheet")
        }

        mBinding.toolbar.setOnEndClick {
            findNavControllerSafely()?.navigate(SongsFragmentDirections.actionSongsToSearch())
        }
        mBinding.toolbar.setOnStartClick {
            navigationViewModel.openSettingDrawer()
        }

        mViewModel.songs.observe(viewLifecycleOwner) { adapter.submitList(it) }

        mViewModel.sortOrder.observe(viewLifecycleOwner) { order ->
            mBinding.tvSort.text = formatSortLabel(order)
        }

        mViewModel.playbackState.observe(viewLifecycleOwner) { state ->
            adapter.currentPlayingId = state?.currentSong?.id ?: -1L
        }
    }

    private fun formatSortLabel(order: SortOrder): String {
        val sortBy = when (order) {
            SortOrder.ALPHABETICAL_ASC, SortOrder.ALPHABETICAL_DESC -> getString(R.string.sort_alphabetical)
            SortOrder.DATE_ADDED_ASC, SortOrder.DATE_ADDED_DESC -> getString(R.string.sort_added_time)
            SortOrder.DURATION_ASC, SortOrder.DURATION_DESC -> getString(R.string.sort_duration)
        }
        val sortOrderLabel = when (order) {
            SortOrder.ALPHABETICAL_ASC, SortOrder.DATE_ADDED_ASC, SortOrder.DURATION_ASC -> getString(R.string.sort_order_az)
            SortOrder.ALPHABETICAL_DESC, SortOrder.DATE_ADDED_DESC, SortOrder.DURATION_DESC -> getString(R.string.sort_order_za)
        }
        return getString(R.string.sort_label_with_order, sortBy, sortOrderLabel)
    }
}
