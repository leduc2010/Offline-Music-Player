package com.duc.offlinemusicplayer.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.data.source.remote.cloud.FirebaseMgr
import com.duc.offlinemusicplayer.databinding.FragmentNativeAdFullscreenBinding
import com.duc.offlinemusicplayer.databinding.ItemOnboardingPagerBinding
import com.duc.offlinemusicplayer.domain.model.OnboardingPagerUI
import com.duc.offlinemusicplayer.utils.LocaleUtils
import com.leansoft.ads.ui.onboarding.LeansoftOnboardingInterface
import com.leansoft.ads.utils.LeansoftAdPlacement
import com.leansoft.ads.utils.LeansoftAdPlacementKt
import com.leansoft.ads.view.NativeAdViewContainer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingImpl @Inject constructor(
    private val firebaseMgr: FirebaseMgr
) : LeansoftOnboardingInterface() {

    private val bindingMapper: HashMap<LeansoftAdPlacement, ViewBinding> = HashMap()
    private val onClickNextMapper: HashMap<LeansoftAdPlacement, () -> Unit> = HashMap()

    private val leansoftNativeFullPlacements = LeansoftAdPlacementKt.getLeansofttNativeFullPlacements()

    private val isEasyMode: Boolean get() = firebaseMgr.getBoolean("lfo_easy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        placement: LeansoftAdPlacement
    ): View? {
        return if (leansoftNativeFullPlacements.contains(placement)) {
            val binding = FragmentNativeAdFullscreenBinding.inflate(inflater, container, false)
            bindingMapper[placement] = binding
            binding.root
        } else {
            val binding = ItemOnboardingPagerBinding.inflate(inflater, container, false)
            bindingMapper[placement] = binding
            binding.root
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
        placement: LeansoftAdPlacement
    ) {
        val context = view.context
        if (leansoftNativeFullPlacements.contains(placement)) {
            val mBinding = bindingMapper[placement] as FragmentNativeAdFullscreenBinding
            mBinding.nativeDefault.setCollapsibleClick { onClickNextMapper[placement]?.invoke() }
        } else {
            val mBinding = bindingMapper[placement] as ItemOnboardingPagerBinding
            
            mBinding.btnNext.setOnClickListener {
                onClickNextMapper[placement]?.invoke()
            }
            mBinding.btnNextEasy.setOnClickListener {
                onClickNextMapper[placement]?.invoke()
            }

            val mList = listOf(
                OnboardingPagerUI(0, R.mipmap.ic_launcher, R.string.obd_title_1, R.string.obd_des_1),
                OnboardingPagerUI(1, R.mipmap.ic_launcher, R.string.obd_title_2, R.string.obd_des_2),
                OnboardingPagerUI(2, R.mipmap.ic_launcher, R.string.obd_title_3, R.string.obd_des_3)
            )

            val item = when (placement) {
                LeansoftAdPlacement.NATIVE_OBD_1 -> mList[0]
                LeansoftAdPlacement.NATIVE_OBD_2 -> mList[1]
                else -> mList[2]
            }

            val isLastOb = placement == LeansoftAdPlacement.NATIVE_OBD_3
            val btnTextRes = if (isLastOb) R.string.get_started else R.string.next

            val nextText = LocaleUtils.getString(context, btnTextRes)
            mBinding.btnNext.text = nextText
            mBinding.btnNextEasy.text = nextText

            mBinding.ivBg.setImageResource(item.image)
            mBinding.title.text = LocaleUtils.getString(context, item.title)
            mBinding.subTitle.text = LocaleUtils.getString(context, item.description)
            
            mBinding.indicator.totalTabs = mList.size
            mBinding.indicator.currentTab = item.pos

            mBinding.adNativeSpace1.isVisible = true
            mBinding.adNativeSpace2.isVisible = false
        }
    }

    override fun getNativeAdContainer(placement: LeansoftAdPlacement): NativeAdViewContainer {
        return if (leansoftNativeFullPlacements.contains(placement)) {
            (bindingMapper[placement] as FragmentNativeAdFullscreenBinding).nativeDefault
        } else {
            (bindingMapper[placement] as ItemOnboardingPagerBinding).adNative
        }
    }

    override fun registerNextClick(placement: LeansoftAdPlacement, listener: () -> Unit) {
        onClickNextMapper[placement] = listener
    }

    override fun updateUI(placement: LeansoftAdPlacement, needEasy: Boolean) {
        if (!leansoftNativeFullPlacements.contains(placement)) {
            (bindingMapper[placement] as? ItemOnboardingPagerBinding)?.let { binding ->
                if (needEasy && isEasyMode) {
                    binding.btnNext.isVisible = false
                    binding.btnNextEasy.isVisible = true
                } else {
                    binding.btnNext.isVisible = true
                    binding.btnNextEasy.isVisible = false
                }
            }
        }
    }
}
