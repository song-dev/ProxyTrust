package com.song.trust.plugin

import android.webkit.WebView
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

/**
 * Created by chensongsong on 2021/11/12.
 */
class WebDebugHook {

    fun hook() {
        XposedLogger.log("WebDebugHook.hook")
        XposedBridge.hookAllMethods(
            WebView::class.java,
            "setWebContentsDebuggingEnabled",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    param?.args?.set(0, true)
                    XposedLogger.log("WebView.setWebContentsDebuggingEnabled Parameter: true")
                }
            }
        )

        XposedBridge.hookAllMethods(
            WebView::class.java,
            "getSettings",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    // 执行静态 setWebContentsDebuggingEnabled 方法
                    XposedLogger.log("WebView Constructor callStaticMethod setWebContentsDebuggingEnabled(true)")
                    XposedHelpers.callStaticMethod(
                        WebView::class.java,
                        "setWebContentsDebuggingEnabled",
                        true
                    )
                }
            })

//        XposedBridge.hookAllConstructors(WebView::class.java, object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//                // 执行静态 setWebContentsDebuggingEnabled 方法
//                XposedLogger.log("WebView Constructor callStaticMethod setWebContentsDebuggingEnabled(true)")
//                XposedHelpers.callStaticMethod(
//                    WebView::class.java,
//                    "setWebContentsDebuggingEnabled",
//                    true
//                )
//            }
//        })

    }

}