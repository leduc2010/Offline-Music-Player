package com.duc.offlinemusicplayer.presentation.ui.language

import com.duc.offlinemusicplayer.domain.model.LanguageModel
import com.duc.offlinemusicplayer.domain.repository.AppSettingRepository
import com.duc.offlinemusicplayer.presentation.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository
) : BaseVM() {

    val listLanguage: List<LanguageModel> = appSettingRepository.getListLanguages()

    fun changeLanguage(newLanguage: LanguageModel?) {
        if (newLanguage == null) return
        appSettingRepository.changeLanguage(newLanguage)
    }

    fun getCurrentLanguage(): LanguageModel {
        return appSettingRepository.getCurrentLanguage()
    }

    fun setNeedReinit(isReinit: Boolean) {
        appSettingRepository.setNeedReinit(isReinit)
    }
}
