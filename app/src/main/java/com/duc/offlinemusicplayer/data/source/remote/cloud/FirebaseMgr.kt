package com.duc.offlinemusicplayer.data.source.remote.cloud

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMgr @Inject constructor() {
    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3000
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)

    fun getString(key: String): String = remoteConfig.getString(key)

    fun fetch(onComplete: (Boolean) -> Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }


}
