package com.duc.offlinemusicplayer.presentation.ui.search

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
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    private val adapter by lazy {
        SearchResultsAdapter(
            onSongClick = { mViewModel.playSong(it) },
            onSongFavoriteClick = { mViewModel.toggleFavorite(it) },
            onSongMoreClick = { /* TODO context menu */ },
            onPlaylistClick = { /* TODO open playlist */ },
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
                    results.songs.take(MAX_PER_SECTION_SONGS).forEach { add(SearchItem.SongRow(it)) }
                }
                if (results.playlists.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.PLAYLISTS))
                    results.playlists.take(MAX_PER_SECTION_OTHER).forEach { add(SearchItem.PlaylistRow(it)) }
                }
                if (results.albums.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.ALBUMS))
                    results.albums.take(MAX_PER_SECTION_OTHER).forEach { add(SearchItem.AlbumRow(it)) }
                }
                if (results.artists.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.ARTISTS))
                    results.artists.take(MAX_PER_SECTION_OTHER).forEach { add(SearchItem.ArtistRow(it)) }
                }
                if (results.folders.isNotEmpty()) {
                    add(SearchItem.Header(SearchTab.FOLDERS))
                    results.folders.take(MAX_PER_SECTION_OTHER).forEach { add(SearchItem.FolderRow(it)) }
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
        SearchTab.ALL -> com.duc.offlinemusicplayer.R.string.tab_all
        SearchTab.SONGS -> com.duc.offlinemusicplayer.R.string.songs
        SearchTab.PLAYLISTS -> com.duc.offlinemusicplayer.R.string.playlists
        SearchTab.ALBUMS -> com.duc.offlinemusicplayer.R.string.albums
        SearchTab.ARTISTS -> com.duc.offlinemusicplayer.R.string.artists
        SearchTab.FOLDERS -> com.duc.offlinemusicplayer.R.string.folders
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

    companion object {
        private const val MAX_PER_SECTION_SONGS = 2
        private const val MAX_PER_SECTION_OTHER = 1
    }
}
