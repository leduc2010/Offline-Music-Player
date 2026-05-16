package com.duc.offlinemusicplayer.presentation.utils

import android.util.Log

object AppLog {
    private const val TAG = "OfflineMusic"

    fun d(tag: String = TAG, msg: String) {
        Log.d(tag, msg)
    }

    fun e(tag: String = TAG, msg: String, throwable: Throwable? = null) {
        Log.e(tag, msg, throwable)
    }

    fun lifeCircle(tag: String, msg: String) {
        Log.d("LifeCycle_$tag", msg)
    }
}
