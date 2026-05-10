package com.duc.offlinemusicplayer.presentation.utils

import android.util.Log
import com.duc.offlinemusicplayer.BuildConfig

object AppLog {
    private const val TAG = "OfflineMusic"

    fun d(tag: String = TAG, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun e(tag: String = TAG, msg: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, throwable)
        }
    }

    fun lifeCircle(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d("LifeCycle_$tag", msg)
        }
    }
}
