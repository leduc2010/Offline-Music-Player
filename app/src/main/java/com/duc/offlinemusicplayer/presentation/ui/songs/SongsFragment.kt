package com.duc.offlinemusicplayer.presentation.ui.songs

import androidx.recyclerview.widget.LinearLayoutManager
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentSongsBinding
import com.duc.offlinemusicplayer.domain.model.SortOrder
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.ui.songs.SongsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : BaseFragment<FragmentSongsBinding, SongsViewModel>() {

    private val adapter by lazy {
        SongListAdapter(
            onSongClick = { song, _ -> mViewModel.playSong(song) },
            onFavoriteClick = { mViewModel.toggleFavorite(it) },
            onMoreClick = { /* TODO show context menu */ },
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
            navigationViewModel.navigate(SongsFragmentDirections.actionSongsToSearch())
        }
        mBinding.toolbar.setOnStartClick {
            // TODO open drawer/menu action
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
