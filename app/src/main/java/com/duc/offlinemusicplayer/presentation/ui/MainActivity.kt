package com.duc.offlinemusicplayer.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.ActivityMainBinding
import com.duc.offlinemusicplayer.domain.repository.PlaybackRepository
import com.duc.offlinemusicplayer.playback.PlayerService
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.presentation.base.BaseActivity
import com.duc.offlinemusicplayer.presentation.base.NothingViewModel
import com.duc.offlinemusicplayer.presentation.utils.ext.EventObserver
import com.duc.offlinemusicplayer.presentation.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, NothingViewModel>() {

    @Inject
    lateinit var playbackRepository: PlaybackRepository

    private lateinit var navController: NavController
    private val navigationViewModel: NavigationViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) startPlayerService()
    }

    override fun getClassVM(): Class<NothingViewModel> = NothingViewModel::class.java

    override fun initViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initNavController()
        observeNavigationEvents()
        bindGlobalMiniPlayer()
        bindGlobalBottomBar()
        requestCorePermissionsIfNeeded()
    }

    private fun observeNavigationEvents() {
        navigationViewModel.naviDirection.observe(this, EventObserver { direction ->
            navController.navigate(direction)
        })
        navigationViewModel.actionDestination.observe(this, EventObserver { action ->
            navController.navigate(action.destination, action.args, action.navOptions)
        })
        navigationViewModel.actionBack.observe(this, EventObserver {
            navController.popBackStack()
        })
        navigationViewModel.actionBackToFrag.observe(this, EventObserver { destinationId ->
            navController.popBackStack(destinationId, false)
        })
    }

    private fun initNavController() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun bindGlobalMiniPlayer() {
        val miniBinding = mBinding.globalMiniPlayer

        playbackRepository.observePlaybackState().observe(this) { state ->
            val song = state?.currentSong
            miniBinding.root.visibility = if (song != null) android.view.View.VISIBLE else android.view.View.GONE
            if (song != null) {
                miniBinding.tvMiniTitle.text = song.title
                miniBinding.tvMiniArtist.text = song.artist.ifBlank { "Unknown" }
            }
            miniBinding.btnMiniPlay.setImageResource(
                if (state?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        miniBinding.btnMiniPlay.setOnClickListener {
            val state = playbackRepository.observePlaybackState().value
            lifecycleScope.launch {
                if (state?.isPlaying == true) playbackRepository.pause() else playbackRepository.play()
            }
        }

        miniBinding.btnMiniNext.setOnClickListener {
            lifecycleScope.launch { playbackRepository.skipNext() }
        }
    }

    private fun bindGlobalBottomBar() {
        val navBinding = mBinding.globalBottomBar

        Glide.with(this)
            .asGif()
            .load(R.drawable.tab_center_bottom_nav)
            .into(navBinding.ivTabCenter)

        fun setActive(tab: Tab) {
            val activeColor = getColor(R.color.green_primary)
            val inactiveColor = getColor(R.color.text_secondary)

            navBinding.ivTabSongs.setColorFilter(if (tab == Tab.SONGS) activeColor else inactiveColor)
            navBinding.tvTabSongs.setTextColor(if (tab == Tab.SONGS) activeColor else inactiveColor)

            navBinding.ivTabPlaylists.setColorFilter(if (tab == Tab.PLAYLISTS) activeColor else inactiveColor)
            navBinding.tvTabPlaylists.setTextColor(if (tab == Tab.PLAYLISTS) activeColor else inactiveColor)

            navBinding.ivTabLibrary.setColorFilter(if (tab == Tab.LIBRARY) activeColor else inactiveColor)
            navBinding.tvTabLibrary.setTextColor(if (tab == Tab.LIBRARY) activeColor else inactiveColor)

            navBinding.ivTabFolders.setColorFilter(if (tab == Tab.FOLDERS) activeColor else inactiveColor)
            navBinding.tvTabFolders.setTextColor(if (tab == Tab.FOLDERS) activeColor else inactiveColor)
        }

        setActive(Tab.SONGS)

        navBinding.tabSongs.setOnClickListener {
            setActive(Tab.SONGS)
            if (navController.currentDestination?.id != R.id.songsFragment) {
                navController.popBackStack(R.id.songsFragment, false)
            }
        }
        navBinding.tabPlaylists.setOnClickListener { setActive(Tab.PLAYLISTS) }
        navBinding.tabLibrary.setOnClickListener { setActive(Tab.LIBRARY) }
        navBinding.tabFolders.setOnClickListener { setActive(Tab.FOLDERS) }
        navBinding.tabCenter.setOnClickListener { }
    }

    private fun requestCorePermissionsIfNeeded() {
        val permissionsToRequest = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
                add(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }.filterNot { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            startPlayerService()
            return
        }
        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private fun startPlayerService() {
        val intent = Intent(this, PlayerService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private enum class Tab { SONGS, PLAYLISTS, LIBRARY, FOLDERS }
}
