package com.song.trust.utils

import java.util.*

/**
 * Created by chensongsong on 2021/11/15.
 */
object ValueUtils {
    private fun castInt(bool: Boolean?): Int? {
        if (bool == null) {
            return null
        }
        return if (bool) 1 else 0
    }

    fun castObject(value: Any?, type: Int): Any? {
        return when {
            type != 0 -> {
                when (type) {
                    1 -> value as String?
                    2 -> parseSet(value as String?)
                    3 -> value as Int?
                    4 -> value as Long?
                    5 -> value as Float?
                    6 -> {
                        return try {
                            castBool(value)
                        } catch (e: ClassCastException) {
                            throw IllegalArgumentException(
                                "Expected type " + type + ", got " + value!!.javaClass,
                                e
                            )
                        }
                    }
                    else -> throw IllegalArgumentException("Unknown type: $type")
                }
            }
            value == null -> {
                null
            }
            else -> {
                throw IllegalArgumentException("Expected null, got non-null value")
            }
        }
    }

    fun castString(set: Set<String>?): String? {
        if (set == null) {
            return null
        }
        val sb = StringBuilder()
        for (replace in set) {
            sb.append(replace.replace("\\", "\\\\").replace(";", "\\;"))
            sb.append(';')
        }
        return sb.toString()
    }

    fun castSet(obj: Any?): Set<String>? {
        return obj as Set<String>?
    }

    fun parseSet(str: String?): Set<String>? {
        if (str == null) {
            return null
        }
        val hashSet = HashSet<String>()
        val sb = StringBuilder()
        var i = 0
        while (i < str.length) {
            var charAt = str[i]
            if (charAt == '\\') {
                i++
                charAt = str[i]
            } else if (charAt == ';') {
                hashSet.add(sb.toString())
                sb.setLength(0)
                i++
            }
            sb.append(charAt)
            i++
        }
        if (sb.isNotEmpty()) {
            hashSet.add(sb.toString())
        }
        return hashSet
    }

    fun parseType(obj: Any?): Int {
        if (obj == null) {
            return 0
        }
        if (obj is String) {
            return 1
        }
        if (obj is Set<*>) {
            return 2
        }
        if (obj is Int) {
            return 3
        }
        if (obj is Long) {
            return 4
        }
        if (obj is Float) {
            return 5
        }
        if (obj is Boolean) {
            return 6
        }
        throw AssertionError("Unknown preference type: " + obj.javaClass)
    }

    fun castObject(obj: Any?): Any? {
        return when (obj) {
            is Boolean -> castInt(obj as Boolean?)
            is Set<*> -> castString(
                castSet(obj)
            )
            else -> obj
        }
    }

    private fun castBool(obj: Any?): Boolean? {
        if (obj == null) {
            return null
        }
        return if (obj is Boolean) {
            obj
        } else obj as Int != 0
    }
}