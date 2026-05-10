package com.duc.offlinemusicplayer.data.source.local.pref

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHelper @Inject constructor(
    @ApplicationContext context: Context
) : Preferences (context, "OfflineMusicPlayer") {
    var currentLangCode by stringPref("currentLangCode", "en")
    var isFirstTimeOpen by booleanPref("isFirstTimeOpen", true)
    var rate by booleanPref("rateApp", false)
    var isShowLangFirstOpen by booleanPref("isShowLangFirstOpen", true)
    var isShowIntro by booleanPref("isShowIntro", true)
    var isChangeLanguage by booleanPref("isChangeLanguage", false)

    var currentLanguage by gsonPref(
        "currentLanguage",
        com.duc.offlinemusicplayer.domain.model.LanguageModel("English", "en", "English", com.duc.offlinemusicplayer.R.drawable.ic_flag_en),
        com.duc.offlinemusicplayer.domain.model.LanguageModel::class.java
    )

    var listLanguage: List<com.duc.offlinemusicplayer.domain.model.LanguageModel>
        get() {
            val json = getString("listLanguage", "")
            return if (json.isNullOrEmpty()) {
                listOf(
                    com.duc.offlinemusicplayer.domain.model.LanguageModel("English", "en", "English", com.duc.offlinemusicplayer.R.drawable.ic_flag_en),
                    com.duc.offlinemusicplayer.domain.model.LanguageModel("Vietnamese", "vi", "Tiếng Việt", com.duc.offlinemusicplayer.R.drawable.ic_flag_vn)
                )
            } else {
                com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<com.duc.offlinemusicplayer.domain.model.LanguageModel>>() {}.type)
            }
        }
        set(value) {
            putString("listLanguage", com.google.gson.Gson().toJson(value))
        }

    // Helper functions from Preferences base class (if not already exposed)
    private fun getString(key: String, defValue: String?): String? {
        val field = Preferences::class.java.getDeclaredField("prefs")
        field.isAccessible = true
        val prefs = field.get(this) as android.content.SharedPreferences
        return prefs.getString(key, defValue)
    }

    private fun putString(key: String, value: String?) {
        val field = Preferences::class.java.getDeclaredField("prefs")
        field.isAccessible = true
        val prefs = field.get(this) as android.content.SharedPreferences
        prefs.edit().putString(key, value).apply()
    }
}
