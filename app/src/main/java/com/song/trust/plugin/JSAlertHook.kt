package com.song.trust.plugin

import android.webkit.JsResult
import android.webkit.WebView
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodReplacement
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
//                    XposedLogger.log("Hooking WebChromeClient.onJsAlert(WebView, string, string, JsResult) hooked for: ${loadPackageParam.packageName}")
//                }
//            }
//        )
        XposedLogger.log("WebChromeClient.onJsAlert(WebView, string, string, JsResult) for: ${loadPackageParam.packageName}")
        XposedHelpers.findAndHookMethod("android.webkit.WebChromeClient",
            loadPackageParam.classLoader,
            "onJsAlert",
            WebView::class.java,
            String::class.java,
            String::class.java,
            JsResult::class.java,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    XposedLogger.log("WebChromeClient.onJsAlert(WebView, string, string, JsResult) hooked for: ${loadPackageParam.packageName}")
                    return false
                }
            }
        )

    }

}