package com.duc.offlinemusicplayer.splash

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.databinding.FragmentSplashBinding
import com.leansoft.ads.ui.splash.LeansoftSplashInterface
import com.leansoft.ads.view.BannerAdViewContainer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SplashImpl @Inject constructor() : LeansoftSplashInterface() {

    private lateinit var mBinding: FragmentSplashBinding
    private var progressAnimator: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSplashBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(mBinding.imgLogo)
            .asGif()
            .load(R.drawable.splash_loading)
            .into(mBinding.imgLogo)
    }

    override fun getBannerAdContainer(): BannerAdViewContainer {
        return mBinding.bannerAdContainer
    }
}
