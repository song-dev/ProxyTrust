package com.song.trust.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by chensongsong on 2021/11/15.
 */
object PreferencesUtils {
    fun putString(context: Context, key: String?, value: String?): Boolean {
        val editor = getSharedPreferences(context).edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun remove(context: Context, key: String?): Boolean {
        val editor = getSharedPreferences(context).edit()
        editor.remove(key)
        return editor.commit()
    }

    fun getString(context: Context, key: String?): String? {
        return getString(context, key, null)
    }

    fun getString(context: Context, key: String?, defaultValue: String?): String? {
        return getSharedPreferences(context).getString(key, defaultValue)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFName, Context.MODE_PRIVATE)
    }

    fun putInt(context: Context, key: String?, value: Int): Boolean {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun getInt(context: Context, key: String?): Int {
        return getInt(context, key, -1)
    }

    fun getInt(context: Context, key: String?, defaultValue: Int): Int {
        return getSharedPreferences(context).getInt(key, defaultValue)
    }

    fun putLong(context: Context, key: String?, value: Long): Boolean {
        val editor = getSharedPreferences(context).edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun getLong(context: Context, key: String?): Long {
        return getLong(context, key, -1)
    }

    fun getLong(context: Context, key: String?, defaultValue: Long): Long {
        return getSharedPreferences(context).getLong(key, defaultValue)
    }

    fun putFloat(context: Context, key: String?, value: Float): Boolean {
        val editor = getSharedPreferences(context).edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    fun getEditor(context: Context): SharedPreferences.Editor {
        return getSharedPreferences(context).edit()
    }

    fun getFloat(context: Context, key: String?): Float {
        return getFloat(context, key, -1f)
    }

    fun getFloat(context: Context, key: String?, defaultValue: Float): Float {
        return getSharedPreferences(context).getFloat(key, defaultValue)
    }

    fun putBoolean(context: Context, key: String?, value: Boolean): Boolean {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

    fun getBoolean(context: Context, key: String?): Boolean {
        return getBoolean(context, key, false)
    }

    fun getBoolean(context: Context, key: String?, defaultValue: Boolean): Boolean {
        return getSharedPreferences(context).getBoolean(key, defaultValue)
    }
}