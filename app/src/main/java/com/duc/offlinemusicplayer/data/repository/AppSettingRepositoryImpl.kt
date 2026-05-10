package com.duc.offlinemusicplayer.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import com.duc.offlinemusicplayer.domain.model.LanguageModel
import com.duc.offlinemusicplayer.domain.repository.AppSettingRepository
import javax.inject.Inject

class AppSettingRepositoryImpl @Inject constructor(
    private val appSetting: PreferenceHelper,
    private val remoteConfig: FirebaseRemoteConfig
) : AppSettingRepository {
    companion object {
        private const val KEY_IAP_ENABLE = "iap_enable"
        private const val KEY_IAP_TRIAL = "iap_trial"
        private const val KEY_IAP_APP_OPEN = "iap_app_open"
    }

    override fun checkFirstInstall(): Boolean {
        return appSetting.isFirstTimeOpen
    }

    override suspend fun disableFirstInstall() {
        appSetting.isFirstTimeOpen = false
    }

    override fun getListLanguages(): List<LanguageModel> {
        return appSetting.listLanguage
    }

    override fun getCurrentLanguage(): LanguageModel {
        return appSetting.currentLanguage
    }

    override fun changeLanguage(newLanguage: LanguageModel) {
        appSetting.currentLanguage = newLanguage
    }

    override fun isShowLangFirstOpen(): Boolean {
        return appSetting.isShowLangFirstOpen
    }

    override suspend fun disableShowLangFirstOpen() {
        appSetting.isShowLangFirstOpen = false
    }

    override fun isShowIntro(): Boolean {
        return appSetting.isShowIntro
    }

    override suspend fun disableShowIntro() {
        appSetting.isShowIntro = false
    }

    override fun setNeedReinit(isReinit: Boolean) {
        appSetting.isChangeLanguage = isReinit
    }

    override fun needReinit(): Boolean {
        return appSetting.isChangeLanguage
    }

    override fun isIapEnable(): Boolean {
        return remoteConfig.getBoolean(KEY_IAP_ENABLE)
    }

    override fun isIapTrialMode(): Boolean {
        return remoteConfig.getBoolean(KEY_IAP_TRIAL)
    }

    override fun isIapAppOpen(): Boolean {
        return isIapEnable() && remoteConfig.getBoolean(KEY_IAP_APP_OPEN)
    }
}
