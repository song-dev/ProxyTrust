package com.song.trust.plugin

import android.webkit.WebChromeClient
import de.robv.android.xposed.XposedBridge

/**
 * Created by chensongsong on 2021/11/12.
 */
class JSAlertHook {

    fun hook() {
        XposedBridge.hookAllMethods(
            WebChromeClient::class.java,
            "onJsAlert",
            ValueMethodHook(false)
        )
    }

}