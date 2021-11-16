package com.song.trust.plugin

import android.webkit.WebView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

/**
 * Created by chensongsong on 2021/11/12.
 */
class WebDebugHook {

    fun hook() {
        XposedBridge.hookAllMethods(
            WebView::class.java,
            "setWebContentsDebuggingEnabled",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    param?.args?.set(0, true)
                }
            }
        )

    }

}