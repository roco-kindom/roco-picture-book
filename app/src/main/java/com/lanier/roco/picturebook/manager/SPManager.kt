package com.lanier.roco.picturebook.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SPManager {

    private const val PREFERENCE_FILE_KEY = "common_pref_key"

    lateinit var sharedPref: SharedPreferences
        private set

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences(combineFileKey(context), Context.MODE_PRIVATE)
    }

    inline fun <reified T : Any> put(key: String, value: T) {
        sharedPref.edit {
            when (T::class) {
                Boolean::class -> putBoolean(key, value as Boolean)
                String::class -> putString(key, value as String)
                Int::class -> putInt(key, value as Int)
                Long::class -> putLong(key, value as Long)
                Float::class -> putFloat(key, value as Float)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    inline fun <reified T : Any> get(key: String, def: T) : T {
        return with(sharedPref) {
            when (T::class) {
                Boolean::class -> getBoolean(key, def as Boolean)
                String::class -> getString(key, def as String)
                Int::class -> getInt(key, def as Int)
                Long::class -> getLong(key, def as Long)
                Float::class -> getFloat(key, def as Float)
                else -> throw IllegalArgumentException("Unsupported type")
            } as T
        }
    }

    private fun combineFileKey(context: Context): String {
        return context.packageName + "." + PREFERENCE_FILE_KEY
    }
}
