package com.lanier.roco.picturebook.manager

import androidx.core.content.edit
import com.lanier.roco.picturebook.manager.SPManager.sharedPref
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SPDelegate<T>(
    private val key: String,
    private val def: T,
): ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return with(sharedPref) {
            when (def) {
                is Boolean -> getBoolean(key, def as Boolean)
                is String -> getString(key, def as String)
                is Int -> getInt(key, def as Int)
                is Long -> getLong(key, def as Long)
                is Float -> getFloat(key, def as Float)
                else -> throw IllegalArgumentException("Unsupported type")
            } as T
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        sharedPref.edit {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }
}