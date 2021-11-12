package com.song.trust.utils

import de.robv.android.xposed.XposedBridge

/**
 * Created by chensongsong on 2021/11/12.
 */
object XposedLogger {

    fun log(msg: String) {
        XposedBridge.log("${Constants.TAG}==>$msg")
    }

    fun log(tag: String, msg: String) {
        XposedBridge.log("$tag==>$msg")
    }

}