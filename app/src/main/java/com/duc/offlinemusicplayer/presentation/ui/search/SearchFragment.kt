package com.duc.offlinemusicplayer.presentation.ui.search

import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentSearchBinding
import com.duc.offlinemusicplayer.domain.model.SearchResults
import com.duc.offlinemusicplayer.domain.model.SearchTab
import com.duc.offlinemusicplayer.domain.model.SortOrder
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.ui.songs.SortBottomSheetFragment
import com.duc.offlinemusicplayer.presentation.utils.findNavControllerSafely
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    private val adapter by lazy {
        SearchResultsAdapter(
            onSongClick = { mViewModel.playSong(it) },
            onSongFavoriteClick = { mViewModel.toggleFavorite(it) },
            onSongMoreClick = { song ->
                val view = mBinding.rvResults
                val popup = PopupMenu(requireContext(), view)
                popup.menu.add(0, 1, 0, "Add to Playlist")
                popup.setOnMenuItemClickListener { menuItem ->
                    if (menuItem.itemId == 1) {
                        val playlists = mViewModel.playlists.value.orEmpty()
                        if (playlists.isEmpty()) {
                            android.widget.Toast.makeText(requireContext(), "Please create a playlist first", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            val playlistPopup = PopupMenu(requireContext(), view)
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
            },
            onPlaylistClick = { playlist ->
                findNavControllerSafely()?.navigate(
                    SearchFragmentDirections.actionSearchToPlaylistDetail(
                        type = "CUSTOM",
                        playlistId = playlist.id,
                        title = playlist.name
                    )
                )
            },
            onAlbumClick = { /* TODO open album */ },
            onArtistClick = { /* TODO open artist */ },
            onFolderClick = { /* TODO open folder */ },
            onViewAllClick = { tab -> selectTab(tab) },
        )
    }

    override fun getClassVM(): Class<SearchViewModel> = SearchViewModel::class.java

    override fun initViewBinding(): FragmentSearchBinding =
        FragmentSearchBinding.inflate(layoutInflater)

    override fun initView() {
        setupRecycler()
        setupSearchBar()
        setupTabs()
        setupSort()

        mBinding.btnBack.setOnClickListener { onBackPressed() }

        observeState()
    }

    private fun setupRecycler() {
        mBinding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvResults.adapter = adapter
    }

    private fun setupSearchBar() {
        mBinding.etSearch.doAfterTextChanged { text ->
            val query = text?.toString().orEmpty()
            mViewModel.setQuery(query)
            mBinding.btnClear.isVisible = query.isNotEmpty()
        }
        mBinding.btnClear.setOnClickListener { mBinding.etSearch.setText("") }
    }

    private fun setupTabs() {
        SearchTab.values().forEach { tab ->
            val title = getString(tabTitleRes(tab))
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(title).setTag(tab))
        }
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                (tab.tag as? SearchTab)?.let { mViewModel.setTab(it) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun setupSort() {
        mBinding.tvSort.setOnClickListener {
            SortBottomSheetFragment(
                initial = mViewModel.sortOrder.value ?: SortOrder.ALPHABETICAL_ASC,
                onApply = { mViewModel.setSortOrder(it) },
            ).show(parentFragmentManager, "SortSheet")
        }
    }

    private fun observeState() {
        mViewModel.results.observe(viewLifecycleOwner) { results ->
            val tab = mViewModel.activeTab.value ?: SearchTab.ALL
            val items = buildItems(results, tab)
            adapter.submitList(items)

            val hasQuery = !mViewModel.query.value.isNullOrBlank()
            val isEmpty = items.isEmpty()
            mBinding.emptyState.isVisible = isEmpty && hasQuery
            mBinding.rvResults.isVisible = !isEmpty
            mBinding.tvSort.isVisible = !isEmpty
        }

        mViewModel.activeTab.observe(viewLifecycleOwner) { tab ->
            // Re-emit to rebuild items when tab changes (results LiveData may not refire if same).
            mViewModel.results.value?.let { adapter.submitList(buildItems(it, tab)) }
            syncTabSelection(tab)
        }

        mViewModel.sortOrder.observe(viewLifecycleOwner) { order ->
            mBinding.tvSort.text = formatSortLabel(order)
        }
    }

    private fun selectTab(tab: SearchTab) {
        val index = SearchTab.values().indexOf(tab).coerceAtLeast(0)
        mBinding.tabLayout.getTabAt(index)?.select()
    }

    private fun syncTabSelection(tab: SearchTab) {
        val index = SearchTab.values().indexOf(tab).coerceAtLeast(0)
        if (mBinding.tabLayout.selectedTabPosition != index) {
            mBinding.tabLayout.getTabAt(index)?.select()
        }
    }

    private fun buildItems(results: SearchResults, tab: SearchTab): List<SearchItem> {
        if (results.isEmpty) return emptyList()
        return when (tab) {
            SearchTab.ALL -> buildList {
                if (results.songs.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.SONGS))
                    results.songs.take(MAX_PER_SECTION_SONGS)
                        .forEach { add(SearchItem.SongRow(it)) }
                }
                if (results.playlists.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.PLAYLISTS))
                    results.playlists.take(MAX_PER_SECTION_OTHER)
                        .forEach { add(SearchItem.PlaylistRow(it)) }
                }
                if (results.albums.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.ALBUMS))
                    results.albums.take(MAX_PER_SECTION_OTHER)
                        .forEach { add(SearchItem.AlbumRow(it)) }
                }
                if (results.artists.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.ARTISTS))
                    results.artists.take(MAX_PER_SECTION_OTHER)
                        .forEach { add(SearchItem.ArtistRow(it)) }
                }
                if (results.folders.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.FOLDERS))
                    results.folders.take(MAX_PER_SECTION_OTHER)
                        .forEach { add(SearchItem.FolderRow(it)) }
                }
            }

            SearchTab.SONGS -> results.songs.map { SearchItem.SongRow(it) }
            SearchTab.PLAYLISTS -> results.playlists.map { SearchItem.PlaylistRow(it) }
            SearchTab.ALBUMS -> results.albums.map { SearchItem.AlbumRow(it) }
            SearchTab.ARTISTS -> results.artists.map { SearchItem.ArtistRow(it) }
            SearchTab.FOLDERS -> results.folders.map { SearchItem.FolderRow(it) }
        }
    }

    private fun tabTitleRes(tab: SearchTab): Int = when (tab) {
        SearchTab.ALL -> R.string.tab_all
        SearchTab.SONGS -> R.string.songs
        SearchTab.PLAYLISTS -> R.string.playlists
        SearchTab.ALBUMS -> R.string.albums
        SearchTab.ARTISTS -> R.string.artists
        SearchTab.FOLDERS -> R.string.folders
    }

    private fun formatSortLabel(order: SortOrder): String {
        val sortBy = when (order) {
            SortOrder.ALPHABETICAL_ASC, SortOrder.ALPHABETICAL_DESC -> getString(R.string.sort_alphabetical)
            SortOrder.DATE_ADDED_ASC, SortOrder.DATE_ADDED_DESC -> getString(R.string.sort_added_time)
            SortOrder.DURATION_ASC, SortOrder.DURATION_DESC -> getString(R.string.sort_duration)
        }
        val sortOrderLabel = when (order) {
            SortOrder.ALPHABETICAL_ASC, SortOrder.DATE_ADDED_ASC, SortOrder.DURATION_ASC -> getString(
                R.string.sort_order_az
            )

            SortOrder.ALPHABETICAL_DESC, SortOrder.DATE_ADDED_DESC, SortOrder.DURATION_DESC -> getString(
                R.string.sort_order_za
            )
        }
        return getString(R.string.sort_label_with_order, sortBy, sortOrderLabel)
    }

    companion object {
        private const val MAX_PER_SECTION_SONGS = 2
        private const val MAX_PER_SECTION_OTHER = 1
    }
}
