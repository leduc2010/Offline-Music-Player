package com.duc.offlinemusicplayer.utils

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import com.duc.offlinemusicplayer.data.source.local.pref.PreferenceHelper
import java.util.Locale

object LocaleUtils {
    fun getString(context: Context, @StringRes resId: Int, lang: String? = null): String {
        val languageCode = lang ?: getAppLanguage(context)
        val locale = Locale(languageCode)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getString(resId)
    }

    private fun getAppLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("OfflineMusicPlayer", Context.MODE_PRIVATE)
        return prefs.getString("currentLangCode", "en") ?: "en"
    }
}
