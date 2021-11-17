package com.song.trust.plugin

import android.net.http.SslError
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chensongsong on 2021/11/12.
 */
class JSAlertHook {

    fun hook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        XposedLogger.log("JSAlertHook.hook")
//        XposedBridge.hookAllMethods(
//            WebChromeClient::class.java,
//            "onJsAlert", ValueMethodHook(false)
//        )
//        XposedBridge.hookAllMethods(
//            WebChromeClient::class.java,
//            "onJsAlert",
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam?) {
//                    param?.result = false
//                    XposedLogger.log("WebChromeClient.onJsAlert result: false")
//                }
//            }
//        )
        XposedHelpers.findAndHookMethod("android.webkit.WebChromeClient",
            loadPackageParam.classLoader,
            "onJsAlert",
            WebView::class.java,
            String::class.java,
            String::class.java,
            JsResult::class.java,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    XposedLogger.log("WebChromeClient.onJsAlert result: false")
                    return false
                }

            }
        )
    }

}