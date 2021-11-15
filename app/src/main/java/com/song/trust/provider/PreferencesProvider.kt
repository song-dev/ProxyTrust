package com.song.trust.provider

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Build
import com.song.trust.utils.ValueUtils

/**
 * Created by chensongsong on 2021/11/15.
 */
abstract class PreferencesProvider(authorities: String, private val prefNames: Array<PrefName?>) :
    ContentProvider(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val uri: Uri = Uri.parse("content://$authorities")
    private val map: HashMap<String, SharedPreferences> = HashMap(prefNames.size)
    private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private class Model(val prefName: String, val key: String)

    init {
        uriMatcher.addURI(authorities, "*/", 1)
        uriMatcher.addURI(authorities, "*/*", 2)
    }

    constructor (authorities: String, prefNames: Array<String>) : this(
        authorities, PrefName.parse(
            prefNames
        )
    )

    override fun onCreate(): Boolean {
        for (prefName in prefNames) {
            var context: Context? = context
            if (context != null) {
                if (prefName != null && prefName.isMatch && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context = context.createDeviceProtectedStorageContext()
                }
                val sharedPreferences: SharedPreferences =
                    context!!.getSharedPreferences(prefName?.prefName, Context.MODE_PRIVATE)
                sharedPreferences.registerOnSharedPreferenceChangeListener(this)
                if (prefName != null) {
                    map[prefName.prefName] = sharedPreferences
                }
            }
        }
        return true
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        return if (contentValues != null) {
            val model = getModel(uri)
            val prefName = model.prefName
            val key = getKey(model, contentValues)
            val edit: SharedPreferences.Editor = getSharedPreferences(prefName, key, true).edit()
            insertValue(edit, key, contentValues)
            if (edit.commit()) {
                buildUri(prefName, key)
            } else null
        } else {
            null
        }
    }

    override fun bulkInsert(uri: Uri, contentValuesArray: Array<ContentValues>): Int {
        val model = getModel(uri)
        val prefName = model.prefName
        if (!isEmpty(model.key)) {
            val edit: SharedPreferences.Editor = getSharedPreferences(prefName).edit()
            for (contentValues in contentValuesArray) {
                val value = getValue(contentValues)
                checkParameter(prefName, value, true)
                insertValue(edit, value, contentValues)
            }
            return if (edit.commit()) {
                contentValuesArray.size
            } else 0
        }
        throw IllegalArgumentException("Cannot bulk insert with single key URI")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val model = getModel(uri)
        val prefName = model.prefName
        val key = model.key
        val edit: SharedPreferences.Editor = getSharedPreferences(prefName, key, true).edit()
        if (isEmpty(key)) {
            edit.remove(key)
        } else {
            edit.clear()
        }
        return if (edit.commit()) 1 else 0
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val projectionLocal = projection ?: arrayOf("key", "type", "value")
        val model = getModel(uri)
        val prefName = model.prefName
        val key = model.key
        val all: Map<String, *> = getSharedPreferences(prefName, key, false).all
        val matrixCursor = MatrixCursor(projectionLocal)
        if (isEmpty(key)) {
            matrixCursor.addRow(parseValues(projectionLocal, key, all[key]))
        } else {
            for ((mapKey, value) in all) {
                matrixCursor.addRow(parseValues(projectionLocal, mapKey, value))
            }
        }
        return matrixCursor
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return when {
            contentValues == null -> delete(uri, selection, selectionArgs)
            insert(
                uri,
                contentValues
            ) != null -> 1
            else -> 0
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val prefName = getPrefName(sharedPreferences)
        val uri = buildUri(prefName.prefName, key)
        var context: Context? = context
        if (context != null) {
            if (prefName.isMatch && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context = context.createDeviceProtectedStorageContext()
            }
            context!!.contentResolver.notifyChange(uri, null)
        }
    }

    private fun buildUri(prefName: String, key: String): Uri {
        val builder = uri.buildUpon().appendPath(prefName)
        if (isEmpty(key)) {
            builder.appendPath(key)
        }
        return builder.build()
    }

    private fun getModel(uri: Uri): Model {
        val match: Int = uriMatcher.match(uri)
        if (match == 2 || match == 1) {
            val pathSegments = uri.pathSegments
            val prefName = pathSegments[0]
            var key = ""
            if (match == 2) {
                key = pathSegments[1]
            }
            return Model(prefName, key)
        }
        throw IllegalArgumentException("Invalid URI: $uri")
    }

    private fun findSharedPreferences(sharedPreferences: SharedPreferences): String {
        for ((key, value) in map) {
            if (value === sharedPreferences) {
                return key
            }
        }
        throw IllegalArgumentException("Unknown preference file")
    }

    private fun insertValue(
        editor: SharedPreferences.Editor,
        key: String,
        contentValues: ContentValues
    ) {
        val type: Int? = contentValues.getAsInteger("type")
        if (type != null) {
            val value: Any? = ValueUtils.castObject(contentValues.get("value"), type)
            if (isEmpty(key)) {
                when (type) {
                    0 -> {
                        editor.remove(key)
                        return
                    }
                    1 -> {
                        editor.putString(key, value as String)
                        return
                    }
                    2 -> {
                        editor.putStringSet(key, ValueUtils.castSet(value))
                        return
                    }
                    3 -> {
                        editor.putInt(key, value as Int)
                        return
                    }
                    4 -> {
                        editor.putLong(key, value as Long)
                        return
                    }
                    5 -> {
                        editor.putFloat(key, value as Float)
                        return
                    }
                    6 -> {
                        editor.putBoolean(key, value as Boolean)
                        return
                    }
                    else -> throw IllegalArgumentException("Cannot set preference with type $type")
                }
            } else if (type == 0) {
                editor.clear()
            } else {
                throw IllegalArgumentException("Attempting to insert preference with null or empty key")
            }
        } else {
            throw IllegalArgumentException("Invalid or no preference type specified")
        }
    }

    private fun parseValues(projection: Array<String>, key: String, value: Any?): Array<Any?> {
        val objects = arrayOfNulls<Any>(projection.size)
        for (i in objects.indices) {
            when (val field = projection[i]) {
                "key" -> {
                    objects[i] = key
                }
                "type" -> {
                    objects[i] = ValueUtils.parseType(value)
                }
                "value" -> {
                    objects[i] = ValueUtils.castObject(value)
                }
                else -> {
                    throw IllegalArgumentException("Invalid column name: $field")
                }
            }
        }
        return objects
    }

    private fun getSharedPreferences(key: String): SharedPreferences {
        val sharedPreferences: SharedPreferences? = map[key]
        if (sharedPreferences != null) {
            return sharedPreferences
        }
        throw IllegalArgumentException("Unknown preference file name: $key")
    }

    private fun getPrefName(sharedPreferences: SharedPreferences): PrefName {
        val key = findSharedPreferences(sharedPreferences)
        for (prefName in prefNames) {
            if (prefName?.prefName == key) {
                return prefName
            }
        }
        throw IllegalArgumentException("Unknown preference file")
    }

    private fun checkParameter(prefName: String, key: String, z: Boolean) {
        if (!check(prefName, key, z)) {
            throw SecurityException("Insufficient permissions to access: $prefName/$key")
        }
    }

    private fun getSharedPreferences(prefName: String, key: String, z: Boolean): SharedPreferences {
        checkParameter(prefName, key, z)
        return getSharedPreferences(prefName)
    }

    fun check(prefName: String?, key: String?, z: Boolean): Boolean {
        return true
    }

    companion object {
        private fun getValue(contentValues: ContentValues): String {
            val key: String = contentValues.getAsString("key")
            return key ?: ""
        }

        private fun getKey(model: Model, contentValues: ContentValues): String {
            val key = model.key
            val contentKey = getValue(contentValues)
            if (key.isEmpty() || contentKey.isEmpty()) {
                return if (key.isNotEmpty()) key else if (contentKey.isNotEmpty()) contentKey else ""
            }
            if (key == contentKey) {
                return key
            }
            throw IllegalArgumentException("Conflicting keys specified in URI and ContentValues")
        }

        private fun isEmpty(str: String?): Boolean {
            return str != null && str.isNotEmpty()
        }
    }

}