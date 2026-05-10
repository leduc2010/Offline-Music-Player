package com.duc.offlinemusicplayer.presentation.ui.uninstall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duc.offlinemusicplayer.ads.AdPlacement
import com.duc.offlinemusicplayer.databinding.FragmentUninstallBinding
import com.leansoft.ads.ui.uninstall.LeansoftUninstallInterface
import com.leansoft.ads.view.NativeAdViewContainer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UninstallImpl @Inject constructor() : LeansoftUninstallInterface {
    private lateinit var mBinding: FragmentUninstallBinding
    private var listener: ((Bundle) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentUninstallBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(mBinding) {
            nativeAdContainer.adsPlacement = AdPlacement.NATIVE_UNINSTALL.id
        }
    }

    override fun getNativeAdViewContainer(): NativeAdViewContainer {
        return mBinding.nativeAdContainer
    }

    override fun registerGoToMainListener(listener: (Bundle) -> Unit) {
        this.listener = listener
    }
}
