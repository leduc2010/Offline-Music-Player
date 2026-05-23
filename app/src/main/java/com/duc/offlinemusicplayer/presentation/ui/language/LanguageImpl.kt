package com.duc.offlinemusicplayer.presentation.ui.language

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.data.source.remote.cloud.FirebaseMgr
import com.duc.offlinemusicplayer.databinding.FragmentLanguageBinding
import com.duc.offlinemusicplayer.domain.repository.AppSettingRepository
import com.duc.offlinemusicplayer.presentation.utils.safeOnClickListener
import com.leansoft.ads.ui.language.LeansoftLanguageInterface
import com.leansoft.ads.view.NativeAdViewContainer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageImpl @Inject constructor(
    private val firebaseMgr: FirebaseMgr,
    private val appSettingRepository: AppSettingRepository
) : LeansoftLanguageInterface {

    private lateinit var binding: FragmentLanguageBinding
    private var setLanguageListener: ((String) -> Unit)? = null
    private var selectLanguageListener: ((String) -> Unit)? = null

    private var isLfoDupScreen = false
    private val isEasyMode: Boolean get() = firebaseMgr.getBoolean("lfo_easy")
    private val delaySeconds get() = firebaseMgr.getString("delay_btn_next_lfo").toLongOrNull() ?: 0L
    private var isDelayFinished = false
    private var isSelected = false
    private var lastNeedEasy = false
    private var languageAdapter: LanguageAdapter? = null
    private var selectedLanguageCode: String = "en"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
        languageCodeSelected: String?
    ) {
        Glide.with(view)
            .asGif()
            .load(R.drawable.hand_click)
            .into(binding.handClick)

        isLfoDupScreen = languageCodeSelected?.isNotEmpty() == true
        isSelected = isLfoDupScreen
        binding.handClick.isVisible = !isSelected

        selectedLanguageCode = languageCodeSelected ?: ""
        isSelected = selectedLanguageCode.isNotEmpty() && isLfoDupScreen
        binding.handClick.isVisible = !isSelected
        languageAdapter = LanguageAdapter(
            onItemClick = { item ->
                selectedLanguageCode = item.code
                binding.handClick.isVisible = false
                isSelected = true
                if (!isLfoDupScreen) {
                    isDelayFinished = true
                    selectLanguageListener?.invoke(item.code)
                }
                refreshButtonVisibility(lastNeedEasy)
            },
            selectedLanguageCode = selectedLanguageCode
        )

        binding.toolbar.hideIconBack()
        languageAdapter?.setItems(appSettingRepository.getListLanguages())
        binding.recyclerView.adapter = languageAdapter

        binding.btnTick.safeOnClickListener {
            setLanguageListener?.invoke(selectedLanguageCode)
        }
        binding.btnOK.safeOnClickListener {
            setLanguageListener?.invoke(selectedLanguageCode)
        }
    }

    private fun triggerDisplayLogic(needEasy: Boolean) {
        if (isDelayFinished) {
            refreshButtonVisibility(needEasy)
        } else {
            binding.root.postDelayed({
                isDelayFinished = true
                refreshButtonVisibility(needEasy)
            }, delaySeconds * 1000L)
        }
    }

    override fun getNativeAdContainer(): NativeAdViewContainer {
        return binding.adNative
    }

    override fun registerSelectLanguageEvent(listener: (String) -> Unit) {
        selectLanguageListener = listener
    }

    override fun registerSetLanguageEvent(listener: (String) -> Unit) {
        setLanguageListener = listener
    }

    override fun updateUI(needEasy: Boolean) {
        lastNeedEasy = needEasy
        if (isEasyMode && needEasy || !isLfoDupScreen) {
            isDelayFinished = true
        }

        if (isLfoDupScreen) {
            triggerDisplayLogic(needEasy)
        } else {
            refreshButtonVisibility(needEasy)
        }
    }

    private fun refreshButtonVisibility(needEasy: Boolean) {
        if (!isSelected) {
            binding.btnOKContainer.isVisible = false
            return
        }

        binding.btnOKContainer.isVisible = true
        if (isEasyMode && needEasy) {
            binding.btnOK.isVisible = isDelayFinished
            binding.btnTick.isVisible = false
        } else {
            binding.btnOK.isVisible = false
            binding.btnTick.isVisible = isDelayFinished
        }
    }

}
