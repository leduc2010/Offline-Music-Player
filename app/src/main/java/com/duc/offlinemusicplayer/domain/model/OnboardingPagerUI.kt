package com.duc.offlinemusicplayer.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPagerUI(
    val pos: Int,
    @DrawableRes val image: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
)
