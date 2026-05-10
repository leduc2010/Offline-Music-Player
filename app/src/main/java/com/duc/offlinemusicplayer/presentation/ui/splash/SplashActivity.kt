package com.duc.offlinemusicplayer.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import com.duc.offlinemusicplayer.App
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.ads.AdPlacement
import com.duc.offlinemusicplayer.domain.repository.AppSettingRepository
import com.duc.offlinemusicplayer.presentation.ui.MainActivity
import com.duc.offlinemusicplayer.presentation.utils.setLocale
import com.leansoft.ads.AdManager
import com.leansoft.ads.ui.activity.LeansoftSplashActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : LeansoftSplashActivity() {

    @Inject
    lateinit var appSettingRepository: AppSettingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(appSettingRepository.getCurrentLanguage().code)
        AdManager.instance.canShowAdResume = false
    }

    override fun loadedRemoteConfig(isSuccess: Boolean) {
    }

    override fun finishOnboarding(bundle: Bundle) {
        navigateToInside()
    }

    override fun setLanguage(languageCode: String) {
        val language = appSettingRepository.getListLanguages().firstOrNull { it.code == languageCode }
        language?.let {
            appSettingRepository.changeLanguage(it)
        }
    }

    override fun getRemoteConfigDefault(): Int {
        return R.xml.remote_config_defaults
    }

    private fun navigateToInside() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
