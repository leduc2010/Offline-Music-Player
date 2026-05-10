package com.duc.offlinemusicplayer.ads

import com.duc.offlinemusicplayer.App
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.leansoft.ads.AdConfig
import com.duc.offlinemusicplayer.R
import javax.inject.Inject

class AdConfigImpl @Inject constructor() : AdConfig() {

    override fun enableAllAds(): Boolean {
        return super.enableAllAds()
    }

    override fun nativeAdChoicesPosition(): Int {
        return NativeAdOptions.ADCHOICES_TOP_RIGHT
    }

    override fun getLayoutLoading(): Int {
        return R.layout.fragment_loading_dialog
    }
}
