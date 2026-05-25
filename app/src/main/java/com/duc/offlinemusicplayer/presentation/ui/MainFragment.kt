package com.duc.offlinemusicplayer.presentation.ui

import android.view.Gravity
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentMainBinding
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.presentation.base.BaseFragment
import com.duc.offlinemusicplayer.presentation.base.BaseVM
import com.duc.offlinemusicplayer.presentation.ui.setting.FeedbackDialogFragment
import com.duc.offlinemusicplayer.presentation.utils.ext.EventObserver
import com.leansoft.ads.AdManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.leansoft.ads.enums.RatingResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    @Inject
    lateinit var playbackRepository: PlaybackRepository

    private lateinit var navController: NavController
    private var isDrawerOpen = false
    private val drawerScale = 0.88f
    private val drawerCornerRadiusDp = 16f
    private var drawerWidth = 0f
    private var drawerActionLocked = false

    override fun getClassVM(): Class<MainViewModel> = MainViewModel::class.java

    override fun initViewBinding(): FragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)

    override fun initView() {
        initNavController()
        setupSettingDrawer()
        bindGlobalMiniPlayer()
        bindGlobalBottomBar()
    }

    private fun initNavController() {
        val navHost = childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
    }

    private fun setupSettingDrawer() {
        mBinding.settingDrawerContainer.layoutParams = mBinding.settingDrawerContainer.layoutParams.apply {
            width = (resources.displayMetrics.widthPixels * 0.57f).toInt()
        }
        mBinding.main.setScrimColor(0x00000000)
        mBinding.main.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)
        mBinding.main.setDrawerElevation(0f)
        mBinding.main.setDrawerShadow(android.R.color.transparent, GravityCompat.START)
        mBinding.main.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                applyDrawerProgress(slideOffset.coerceIn(0f, 1f))
            }
            override fun onDrawerOpened(drawerView: View) { isDrawerOpen = true }
            override fun onDrawerClosed(drawerView: View) { isDrawerOpen = false; applyDrawerProgress(0f) }
            override fun onDrawerStateChanged(newState: Int) = Unit
        })

        mBinding.settingDrawerContainer.post {
            drawerWidth = mBinding.settingDrawerContainer.width.toFloat()
            applyDrawerProgress(0f)
        }

        with(mBinding.settingDrawerContent) {
            tvLanguage.setOnClickListener {
                closeSettingDrawer()
                mBinding.contentCard.postDelayed({
                    navigationViewModel.navigate(MainFragmentDirections.actionMainToLanguage())
                }, 180L)
            }
            rowVisualizer.setOnClickListener {
                closeSettingDrawer()
                mBinding.contentCard.postDelayed({
                    navigationViewModel.navigate(MainFragmentDirections.actionMainToVisualizer())
                }, 180L)
            }
            rowScan.setOnClickListener { closeSettingDrawer() }
            rowEqualizer.setOnClickListener { closeSettingDrawer() }
            rowPrivacy.setOnClickListener { closeSettingDrawer() }
            rowTerm.setOnClickListener { closeSettingDrawer() }
            rowFeedback.setOnClickListener {
                if (drawerActionLocked) return@setOnClickListener
                drawerActionLocked = true
                FeedbackDialogFragment().show(parentFragmentManager, "FeedbackDialog")
                mBinding.contentCard.postDelayed({ drawerActionLocked = false }, 180L)
            }
            rowRateUs.setOnClickListener {
                if (drawerActionLocked) return@setOnClickListener
                drawerActionLocked = true
                AdManager.instance.adDisableByClicked = true
                AdManager.instance.showRatingApp(parentFragmentManager) { ratingResult ->
                    AdManager.instance.adDisableByClicked = false
                    if (ratingResult == RatingResult.SUCCESSFULLY || ratingResult == RatingResult.FEEDBACK) {
                        pref.rate = true
                    }
                    drawerActionLocked = false
                }
            }
        }
    }

    private fun applyDrawerProgress(progress: Float) {
        val radiusPx = drawerCornerRadiusDp * resources.displayMetrics.density
        mBinding.contentCard.translationX = drawerWidth * progress
        val scale = 1f - (1f - drawerScale) * progress
        mBinding.contentCard.scaleX = scale
        mBinding.contentCard.scaleY = scale
        mBinding.contentCard.radius = radiusPx * progress
    }

    private fun openSettingDrawer() { mBinding.main.openDrawer(GravityCompat.START) }
    private fun closeSettingDrawer() { mBinding.main.closeDrawer(GravityCompat.START) }

    private fun bindGlobalMiniPlayer() {
        val mini = mBinding.globalMiniPlayer

        mini.root.setOnClickListener {
            navigationViewModel.navigate(MainFragmentDirections.actionMainToNowPlaying())
        }

        mini.btnMiniPlay.setOnClickListener {
            val state = playbackRepository.observePlaybackState().value ?: return@setOnClickListener
            lifecycleScope.launch {
                if (state.isPlaying) playbackRepository.pause() else playbackRepository.play()
            }
        }

        mini.btnMiniNext.setOnClickListener {
            lifecycleScope.launch {
                playbackRepository.skipNext()
            }
        }

        playbackRepository.observePlaybackState().observe(viewLifecycleOwner) { state ->
            val song = state?.currentSong
            mini.root.visibility = if (song != null) View.VISIBLE else View.GONE
            if (song != null) {
                mini.tvMiniTitle.text = song.title
                mini.tvMiniArtist.text = song.artist.ifBlank { "Unknown" }
            }
            mini.btnMiniPlay.setImageResource(if (state?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play)
        }
    }

    private fun bindGlobalBottomBar() {
        val nav = mBinding.globalBottomBar
        Glide.with(this).asGif().load(R.drawable.tab_center_bottom_nav).into(nav.ivTabCenter)

        fun setActive(tab: Tab) {
            val active = requireContext().getColor(R.color.green_primary)
            val inactive = requireContext().getColor(R.color.text_secondary)
            nav.ivTabSongs.setColorFilter(if (tab == Tab.SONGS) active else inactive)
            nav.tvTabSongs.setTextColor(if (tab == Tab.SONGS) active else inactive)
            nav.ivTabPlaylists.setColorFilter(if (tab == Tab.PLAYLISTS) active else inactive)
            nav.tvTabPlaylists.setTextColor(if (tab == Tab.PLAYLISTS) active else inactive)
            nav.ivTabLibrary.setColorFilter(if (tab == Tab.LIBRARY) active else inactive)
            nav.tvTabLibrary.setTextColor(if (tab == Tab.LIBRARY) active else inactive)
            nav.ivTabFolders.setColorFilter(if (tab == Tab.FOLDERS) active else inactive)
            nav.tvTabFolders.setTextColor(if (tab == Tab.FOLDERS) active else inactive)
        }

        setActive(Tab.SONGS)
        nav.tabSongs.setOnClickListener {
            setActive(Tab.SONGS)
            if (navController.currentDestination?.id != R.id.songsFragment) navController.popBackStack(R.id.songsFragment, false)
        }

        nav.tabPlaylists.setOnClickListener {
            setActive(Tab.PLAYLISTS)
            if (navController.currentDestination?.id != R.id.playlistsFragment) {
                navController.navigate(R.id.playlistsFragment)
            }
        }

        nav.tabLibrary.setOnClickListener {
            setActive(Tab.LIBRARY)
        }

        nav.tabFolders.setOnClickListener {
            setActive(Tab.FOLDERS)
        }

        nav.tabCenter.setOnClickListener {
            navigationViewModel.navigate(MainFragmentDirections.actionMainToVisualizer())
        }

        navigationViewModel.openSettingDrawer.observe(viewLifecycleOwner, EventObserver {
            openSettingDrawer()
        })
    }

    override fun onBackPressed() {
        if (mBinding.main.isDrawerOpen(GravityCompat.START)) {
            closeSettingDrawer()
            return
        }
        super.onBackPressed()
    }

    private enum class Tab { SONGS, PLAYLISTS, LIBRARY, FOLDERS }
}
