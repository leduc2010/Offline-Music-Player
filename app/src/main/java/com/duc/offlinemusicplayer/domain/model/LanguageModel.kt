package com.duc.offlinemusicplayer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageModel(
    val name: String,
    val code: String,
    val display: String,
    val flagRes: Int
) : Parcelable
