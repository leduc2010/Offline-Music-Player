package com.duc.offlinemusicplayer.presentation.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSession @Inject constructor() {
    var isAppOpenFirstTimeInSession: Boolean = true
    var isAdShowing: Boolean = false
    var enableResumeAd: Boolean = true
}

