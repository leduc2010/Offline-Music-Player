package com.duc.offlinemusicplayer

import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import com.duc.offlinemusicplayer.data.source.remote.cloud.FirebaseMgr
import com.duc.offlinemusicplayer.presentation.utils.AppSession
import com.leansoft.ads.AdConfig
import com.leansoft.ads.AdManager
import com.leansoft.ads.AdsApplication
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : AdsApplication() {
    companion object {
        lateinit var instance: App
            private set
    }

    @Inject
    lateinit var adConfig: AdConfig

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var sesstion: AppSession

    @Inject
    lateinit var firebaseMgr: FirebaseMgr

    override fun onCreate() {
        super.onCreate()
        instance = this

        AdManager.instance.canShowAdResume = false
    }

}