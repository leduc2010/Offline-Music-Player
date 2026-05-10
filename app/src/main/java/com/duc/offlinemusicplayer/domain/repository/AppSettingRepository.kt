package com.duc.offlinemusicplayer.domain.repository

import com.duc.offlinemusicplayer.domain.model.LanguageModel

interface AppSettingRepository {

    fun checkFirstInstall(): Boolean

    suspend fun disableFirstInstall()

    fun getListLanguages(): List<LanguageModel>

    fun getCurrentLanguage(): LanguageModel

    fun changeLanguage(newLanguage: LanguageModel)

    fun isShowLangFirstOpen(): Boolean

    suspend fun disableShowLangFirstOpen()

    fun isShowIntro(): Boolean

    suspend fun disableShowIntro()

    fun setNeedReinit(isReinit: Boolean)

    fun needReinit(): Boolean

    fun isIapEnable(): Boolean

    fun isIapTrialMode(): Boolean

    fun isIapAppOpen(): Boolean
}
