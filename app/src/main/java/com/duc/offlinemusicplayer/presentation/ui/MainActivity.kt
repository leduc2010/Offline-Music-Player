package com.duc.offlinemusicplayer.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.duc.offlinemusicplayer.databinding.ActivityMainBinding
import com.duc.offlinemusicplayer.playback.PlayerService
import com.duc.offlinemusicplayer.presentation.base.BaseActivity
import com.duc.offlinemusicplayer.presentation.base.NothingViewModel
import com.duc.offlinemusicplayer.presentation.utils.ext.EventObserver
import com.duc.offlinemusicplayer.presentation.viewmodel.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, NothingViewModel>() {

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
        initNavController()
        observeNavigationEvents()
        requestCorePermissionsIfNeeded()
    }

    private fun initNavController() {
        val navHost = supportFragmentManager.findFragmentById(com.duc.offlinemusicplayer.R.id.mainHost) as NavHostFragment
        navController = navHost.navController
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
}
