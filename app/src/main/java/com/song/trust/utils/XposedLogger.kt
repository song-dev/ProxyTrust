package com.song.trust.utils

import de.robv.android.xposed.XposedBridge

/**
 * Created by chensongsong on 2021/11/12.
 */
object XposedLogger {

    private const val PRINT_SIZE = 3800

    fun log(msg: String?) {
        XposedBridge.log("${Constants.TAG}==>$msg")
    }

    fun log(tag: String, msg: String?) {
        XposedBridge.log("$tag==>$msg")
    }

    fun printLongString(data: String) {
        val len = data.length
        if (len > PRINT_SIZE) {
            var n = 0
            while (len - n > PRINT_SIZE) {
                val s = data.substring(n, n + PRINT_SIZE)
                log(s)
                n += PRINT_SIZE
            }
            log(data.substring(n))
        } else {
            log(data)
        }
    }

}