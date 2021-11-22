package com.song.trust.preferences

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import com.song.trust.utils.ValueUtils.castSet
import com.song.trust.utils.ValueUtils.castString
import com.song.trust.utils.ValueUtils.parseSet
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by chensongsong on 2021/11/15.
 */
class ProviderPreferences @JvmOverloads constructor(
    private val context: Context,
    authorities: String,
    prefName: String,
    private val allow: Boolean = true
) : SharedPreferences {
    private val handler: Handler
    private val uri: Uri
    private val weakHashMap: WeakHashMap<OnSharedPreferenceChangeListener, PrefObserver> =
        WeakHashMap()

    private inner class PrefObserver(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener) :
        ContentObserver(
            handler
        ) {
        private val weakReference: WeakReference<OnSharedPreferenceChangeListener> =
            WeakReference(onSharedPreferenceChangeListener)

        override fun deliverSelfNotifications(): Boolean {
            return true
        }

        override fun onChange(z: Boolean, uri: Uri?) {
            val lastPathSegment = uri!!.lastPathSegment
            val onSharedPreferenceChangeListener = weakReference.get()
            if (onSharedPreferenceChangeListener == null) {
                context.contentResolver.unregisterContentObserver(this)
            } else {
                onSharedPreferenceChangeListener.onSharedPreferenceChanged(
                    this@ProviderPreferences,
                    lastPathSegment
                )
            }
        }

    }

    private inner class AppEditor : Editor {
        private val list: ArrayList<ContentValues> = ArrayList()
        override fun apply() {
            commit()
        }

        override fun clear(): Editor {
            delete("")
            return this
        }

        override fun commit(): Boolean {
            val uri = uri.buildUpon().appendPath("").build()
            return bulkInsert(uri, list.toTypedArray())
        }

        override fun putBoolean(key: String, value: Boolean): Editor {
            makeValues(key, 6).put("value", if (value) 1 else 0)
            return this
        }

        override fun putFloat(key: String, value: Float): Editor {
            makeValues(key, 5).put("value", value)
            return this
        }

        override fun putInt(key: String, value: Int): Editor {
            makeValues(key, 3).put("value", value)
            return this
        }

        override fun putLong(key: String, value: Long): Editor {
            makeValues(key, 4).put("value", value)
            return this
        }

        override fun putString(key: String, value: String?): Editor {
            makeValues(key, 1).put("value", value)
            return this
        }

        override fun putStringSet(key: String, set: Set<String>?): Editor {
            makeValues(key, 2).put("value", castString(set))
            return this
        }

        override fun remove(key: String): Editor {
            check(key)
            delete(key)
            return this
        }

        private fun delete(key: String) {
            val contentValues = newContentValues(key, 0)
            contentValues.putNull("value")
            list.add(0, contentValues)
        }

        private fun newContentValues(key: String, type: Int): ContentValues {
            val contentValues = ContentValues(4)
            contentValues.put("key", key)
            contentValues.put("type", type)
            return contentValues
        }

        private fun makeValues(key: String, type: Int): ContentValues {
            check(key)
            val contentValues = newContentValues(key, type)
            list.add(contentValues)
            return contentValues
        }

    }

    private fun query(uri: Uri, projection: Array<String>): Cursor? {
        var cursor: Cursor?
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
        } catch (e: Exception) {
            cursor = null
            throwAppException(e)
        }
        if (cursor != null || !allow) {
            return cursor
        }
        throw RuntimeException("query() failed or returned null cursor")
    }

    private fun queryValue(cursor: Cursor, typeIndex: Int, valueIndex: Int): Any? {
        return when (val type = cursor.getInt(typeIndex)) {
            1 -> cursor.getString(valueIndex)
            2 -> parseSet(cursor.getString(valueIndex))
            3 -> cursor.getInt(valueIndex)
            4 -> cursor.getLong(valueIndex)
            5 -> cursor.getFloat(valueIndex)
            6 -> cursor.getInt(valueIndex) != 0
            else -> throw AssertionError("Invalid expected type: $type")
        }
    }

    private fun query(key: String, defValue: Any?, type: Int): Any? {
        check(key)
        val cursor = query(uri.buildUpon().appendPath(key).build(), arrayOf("type", "value"))
        cursor?.use {
            if (it.moveToFirst()) {
                val indexType = it.getColumnIndexOrThrow("type")
                val i = it.getInt(indexType)
                if (i == 0) {
                    return defValue
                }
                if (i == type) {
                    val value =
                        queryValue(it, indexType, it.getColumnIndexOrThrow("value"))
                    it.close()
                    return value
                }
                throw ClassCastException("Preference type mismatch")
            }
        }
        return defValue
    }

    private fun bulkQuery(): Map<String, Any?> {
        val query = query(uri.buildUpon().appendPath("").build(), arrayOf("key", "type", "value"))
        return query.use {
            val hashMap = HashMap<String, Any?>()
            if (it == null) {
                return hashMap
            }
            val indexKey = it.getColumnIndexOrThrow("key")
            val indexType = it.getColumnIndexOrThrow("type")
            val indexValue = it.getColumnIndexOrThrow("value")
            while (it.moveToNext()) {
                hashMap[it.getString(indexKey)] = queryValue(it, indexType, indexValue)
            }
            hashMap
        }
    }

    private fun throwAppException(exc: Exception) {
        if (allow) {
            throw RuntimeException(exc.message)
        }
    }

    fun bulkInsert(uri: Uri?, contentValuesArr: Array<ContentValues>): Boolean {
        return try {
            val bulkInsert = context.contentResolver.bulkInsert(uri!!, contentValuesArr)
            if (bulkInsert == contentValuesArr.size || !allow) {
                return bulkInsert == contentValuesArr.size
            }
            throw Exception("bulkInsert() failed")
        } catch (e: Exception) {
            throwAppException(e)
            false
        }
    }

    private fun containsKey(key: String): Boolean {
        check(key)
        var result = true
        val query = query(uri.buildUpon().appendPath(key).build(), arrayOf("type"))
        query?.use {
            if (it.moveToFirst()) {
                if (it.getInt(it.getColumnIndexOrThrow("type")) == 0) {
                    result = false
                }
                return result
            }
        }
        return false
    }

    override fun contains(key: String): Boolean {
        return containsKey(key)
    }

    override fun edit(): Editor {
        return AppEditor()
    }

    override fun getAll(): Map<String, *> {
        return bulkQuery()
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return query(key, defValue, 6) as Boolean
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return query(key, defValue, 5) as Float
    }

    override fun getInt(key: String, defValue: Int): Int {
        return query(key, defValue, 3) as Int
    }

    override fun getLong(key: String, defValue: Long): Long {
        return query(key, defValue, 4) as Long
    }

    override fun getString(key: String, defValue: String?): String? {
        return query(key, defValue, 1) as String?
    }

    @TargetApi(11)
    override fun getStringSet(str: String, set: Set<String>?): Set<String>? {
        return castSet(query(str, set, 2))
    }

    override fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener) {
        checkParameter("listener", onSharedPreferenceChangeListener)
        if (!weakHashMap.containsKey(onSharedPreferenceChangeListener)) {
            val observer = PrefObserver(onSharedPreferenceChangeListener)
            weakHashMap[onSharedPreferenceChangeListener] = observer
            context.contentResolver.registerContentObserver(uri, true, observer)
        }
    }

    override fun unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener) {
        checkParameter("listener", onSharedPreferenceChangeListener)
        val observer = weakHashMap.remove(onSharedPreferenceChangeListener)
        if (observer != null) {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    companion object {
        private fun checkParameter(name: String, obj: Any?) {
            requireNotNull(obj) { "$name is null" }
        }

        private fun check(key: String) {
            require(!TextUtils.isEmpty(key)) { "Key is null or empty" }
        }
    }

    init {
        checkParameter("context", context)
        checkParameter("authority", authorities)
        checkParameter("prefName", prefName)
        handler = Handler(context.mainLooper)
        uri = Uri.parse("content://$authorities").buildUpon().appendPath(prefName).build()
    }
}