package com.duc.offlinemusicplayer.data.source.local.pref

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty
import androidx.core.content.edit

@Suppress("UNCHECKED_CAST", "unused")
abstract class Preferences(context: Context, private val name: String? = null) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(name ?: javaClass.simpleName, Context.MODE_PRIVATE)
    }

    abstract class PrefDelegate<T>(val prefKey: String?) {
        abstract operator fun getValue(thisRef: Any?, property: KProperty<*>): T
        abstract operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
    }

    enum class StorageType {
        String,
        Int,
        Float,
        Boolean,
        Long,
        StringSet,
        Gson
    }

    inner class GenericPrefDelegate<T>(
        prefKey: String? = null,
        private val defaultValue: T?,
        private val type: StorageType,
        private val clazz: Class<T>? = null
    ) :
        PrefDelegate<T?>(prefKey) {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            try {
                when (type) {
                    StorageType.String ->
                        prefs.getString(prefKey ?: property.name, defaultValue as String?) as T

                    StorageType.Int ->
                        prefs.getInt(prefKey ?: property.name, defaultValue as Int) as T

                    StorageType.Float ->
                        prefs.getFloat(prefKey ?: property.name, defaultValue as Float) as T

                    StorageType.Boolean ->
                        prefs.getBoolean(prefKey ?: property.name, defaultValue as Boolean) as T

                    StorageType.Long ->
                        prefs.getLong(prefKey ?: property.name, defaultValue as Long) as T

                    StorageType.StringSet ->
                        prefs.getStringSet(
                            prefKey ?: property.name,
                            defaultValue as Set<String>
                        ) as T

                    StorageType.Gson -> {
                        val json = prefs.getString(prefKey ?: property.name, null)
                        if (json == null) defaultValue as T
                        else com.google.gson.Gson().fromJson(json, clazz)
                    }
                }
            } catch (e: Exception) {
                prefs.edit(commit = true) { remove(prefKey) }
                defaultValue as T
            }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            when (type) {
                StorageType.String -> {
                    prefs.edit(commit = true) {
                        putString(
                            prefKey ?: property.name,
                            value as String?
                        )
                    }
                }

                StorageType.Int -> {
                    prefs.edit(commit = true) { putInt(prefKey ?: property.name, value as Int) }
                }

                StorageType.Float -> {
                    prefs.edit(commit = true) { putFloat(prefKey ?: property.name, value as Float) }
                }

                StorageType.Boolean -> {
                    prefs.edit(commit = true) {
                        putBoolean(
                            prefKey ?: property.name,
                            value as Boolean
                        )
                    }
                }

                StorageType.Long -> {
                    prefs.edit(commit = true) { putLong(prefKey ?: property.name, value as Long) }
                }

                StorageType.StringSet -> {
                    prefs.edit(commit = true) {
                        putStringSet(prefKey ?: property.name, value as Set<String>)
                    }
                }

                StorageType.Gson -> {
                    prefs.edit(commit = true) {
                        putString(prefKey ?: property.name, com.google.gson.Gson().toJson(value))
                    }
                }
            }
        }

    }

    fun stringPref(prefKey: String? = null, defaultValue: String? = null) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.String)

    fun intPref(prefKey: String? = null, defaultValue: Int = 0) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.Int)

    fun floatPref(prefKey: String? = null, defaultValue: Float = 0f) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.Float)

    fun booleanPref(prefKey: String? = null, defaultValue: Boolean = false) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.Boolean)

    fun longPref(prefKey: String? = null, defaultValue: Long = 0L) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.Long)

    fun stringSetPref(prefKey: String? = null, defaultValue: Set<String> = HashSet()) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.StringSet)

    fun stringMutableSetPref(
        prefKey: String? = null,
        defaultValue: MutableSet<String> = HashSet()
    ) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.StringSet)

    fun <T> gsonPref(prefKey: String? = null, defaultValue: T? = null, clazz: Class<T>) =
        GenericPrefDelegate(prefKey, defaultValue, StorageType.Gson, clazz)

}