package com.duc.offlinemusicplayer.presentation.ui.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentPlaylistsBinding
import com.duc.offlinemusicplayer.databinding.ItemPlaylistBinding
import com.duc.offlinemusicplayer.domain.model.Playlist
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.utils.findNavControllerSafely
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistsFragment : BaseFragment<FragmentPlaylistsBinding, PlaylistsViewModel>() {

    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun getClassVM(): Class<PlaylistsViewModel> = PlaylistsViewModel::class.java

    override fun initViewBinding(): FragmentPlaylistsBinding =
        FragmentPlaylistsBinding.inflate(layoutInflater)

    override fun initView() {
        // Setup toolbar
        mBinding.toolbar.setOnStartClick {
            navigationViewModel.openSettingDrawer()
        }
        mBinding.toolbar.setOnEndClick {
            findNavControllerSafely()?.navigate(PlaylistsFragmentDirections.actionPlaylistsToSearch())
        }

        // Setup custom playlists list
        playlistsAdapter = PlaylistsAdapter(
            onItemClick = { playlist ->
                findNavControllerSafely()?.navigate(
                    PlaylistsFragmentDirections.actionPlaylistsToDetail(
                        type = "CUSTOM",
                        playlistId = playlist.id,
                        title = playlist.name
                    )
                )
            },
            onMoreClick = { view, playlist ->
                showPlaylistOptions(view, playlist)
            }
        )

        mBinding.rvMyPlaylists.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvMyPlaylists.adapter = playlistsAdapter

        // "New Playlist" Click
        mBinding.btnNewPlaylist.safeOnClickListener {
            CreatePlaylistDialogFragment { name ->
                mViewModel.createPlaylist(name)
            }.show(parentFragmentManager, "CreatePlaylistDialog")
        }

        // Predefined Playlist clicks
        mBinding.btnFavoriteSongs.safeOnClickListener {
            findNavControllerSafely()?.navigate(
                PlaylistsFragmentDirections.actionPlaylistsToDetail(
                    type = "FAVORITE",
                    playlistId = -1L,
                    title = "Favourite Songs"
                )
            )
        }

        mBinding.btnRecentPlayed.safeOnClickListener {
            findNavControllerSafely()?.navigate(
                PlaylistsFragmentDirections.actionPlaylistsToDetail(
                    type = "RECENT_PLAYED",
                    playlistId = -1L,
                    title = "Recent Played"
                )
            )
        }

        mBinding.btnRecentAdded.safeOnClickListener {
            findNavControllerSafely()?.navigate(
                PlaylistsFragmentDirections.actionPlaylistsToDetail(
                    type = "RECENT_ADDED",
                    playlistId = -1L,
                    title = "Recent Added"
                )
            )
        }

        mBinding.btnMostListening.safeOnClickListener {
            findNavControllerSafely()?.navigate(
                PlaylistsFragmentDirections.actionPlaylistsToDetail(
                    type = "MOST_LISTENING",
                    playlistId = -1L,
                    title = "Most Listening"
                )
            )
        }

        // Observers to update counts and list dynamically
        mViewModel.playlists.observe(viewLifecycleOwner) { list ->
            playlistsAdapter.submitList(list)
        }

        mViewModel.favoriteSongs.observe(viewLifecycleOwner) { songs ->
            mBinding.tvFavoriteSongsCount.text = "${songs.size} Songs"
        }

        mViewModel.recentPlayedSongs.observe(viewLifecycleOwner) { songs ->
            mBinding.tvRecentPlayedCount.text = "${songs.size} Songs"
        }

        mViewModel.recentAddedSongs.observe(viewLifecycleOwner) { songs ->
            mBinding.tvRecentAddedCount.text = "${songs.size} Songs"
        }

        mViewModel.mostListeningSongs.observe(viewLifecycleOwner) { songs ->
            mBinding.tvMostListeningCount.text = "${songs.size} Songs"
        }
    }

    private fun showPlaylistOptions(view: View, playlist: Playlist) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(0, 1, 0, if (playlist.isPinned) "Unpin" else "Pin")
        popup.menu.add(0, 2, 1, "Delete")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    mViewModel.togglePin(playlist)
                    true
                }
                2 -> {
                    mViewModel.deletePlaylist(playlist.id)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // --- Recycler View Adapter for Custom Playlists ---
    private inner class PlaylistsAdapter(
        private val onItemClick: (Playlist) -> Unit,
        private val onMoreClick: (View, Playlist) -> Unit
    ) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistVH>() {

        private var items = emptyList<Playlist>()

        fun submitList(newItems: List<Playlist>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistVH {
            val binding = ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PlaylistVH(binding)
        }

        override fun onBindViewHolder(holder: PlaylistVH, position: Int) {
            val item = items[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int = items.size

        inner class PlaylistVH(private val binding: ItemPlaylistBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: Playlist) {
                binding.tvName.text = item.name
                binding.tvCount.text = "${item.songCount} Songs"

                // Show Pin green thumbtack icon if pinned
                binding.ivPin.visibility = if (item.isPinned) View.VISIBLE else View.GONE

                binding.root.setOnClickListener { onItemClick(item) }
                binding.btnMore.setOnClickListener { onMoreClick(it, item) }
            }
        }
    }
}
