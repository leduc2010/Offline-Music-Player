package com.duc.offlinemusicplayer.data.source.local.pref

import android.content.Context
import com.duc.offlinemusicplayer.R
import com.duc.offlinemusicplayer.domain.model.LanguageModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        LanguageModel("English", "en", "English", R.drawable.ic_flag_us),
        LanguageModel::class.java
    )

    private val defaultLanguageList = listOf(
        LanguageModel("English", "en", "English", R.drawable.ic_flag_us),
        LanguageModel("French", "fr", "Français", R.drawable.ic_flag_france),
        LanguageModel("Marathi", "hi", "मराठी (India)", R.drawable.ic_flag_indian),
        LanguageModel("Spanish", "es", "Espanol", R.drawable.ic_flag_spain),
        LanguageModel("Chinese", "zh", "Chinese", R.drawable.ic_flag_china),
        LanguageModel("Portuguese", "pt", "Português (Portugal)", R.drawable.ic_flag_portugal),
        LanguageModel("Russian", "ru", "Русский", R.drawable.ic_flag_russia),
        LanguageModel("Indonesian", "in", "Indonesian", R.drawable.ic_flag_indo),
        LanguageModel("Filipino", "fil", "Philippines", R.drawable.ic_flag_philippines),
        LanguageModel("Bengali", "bn", "বাংলা", R.drawable.ic_flag_bangladesh),
        LanguageModel("Portuguese (Brazil)", "br", "Português (Brazil)", R.drawable.ic_flag_brazil),
        LanguageModel("Afrikaans", "af", "Afrikaans", R.drawable.ic_flag_south_africa),
        LanguageModel("German", "de", "Deutsch", R.drawable.ic_flag_german),
        LanguageModel("English (Canada)", "en-rCA", "Canada", R.drawable.ic_flag_canada),
        LanguageModel("English (UK)", "en-rGB", "English", R.drawable.ic_flag_england),
        LanguageModel("Korean", "ko", "Korean", R.drawable.ic_flag_south_korea),
        LanguageModel("Dutch", "nl", "Dutch", R.drawable.ic_flag_netherlands)
    )

    var listLanguage: List<LanguageModel>
        get() {
            val json = getString("listLanguage", "")
            return if (json.isNullOrEmpty()) {
                defaultLanguageList
            } else {
                Gson().fromJson(json, object : TypeToken<List<LanguageModel>>() {}.type)
            }
        }
        set(value) {
            putString("listLanguage", Gson().toJson(value))
        }

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
