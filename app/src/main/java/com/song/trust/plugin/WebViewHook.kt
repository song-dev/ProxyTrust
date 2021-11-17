package com.song.trust.plugin

import android.net.http.SslError
import android.os.Build
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.net.URL

/**
 * Created by chensongsong on 2021/11/16.
 */
class WebViewHook {

    fun hook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {

        /**
         * frameworks/base/core/java/android/webkit/WebViewClient.java
         * public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
         */
        XposedLogger.log("Hooking WebViewClient.onReceivedSslError(WebView, SslErrorHandler, SslError) for: ${loadPackageParam.packageName}")
        XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",
            loadPackageParam.classLoader,
            "onReceivedSslError",
            WebView::class.java,
            SslErrorHandler::class.java,
            SslError::class.java,
            object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    (param.args[1] as SslErrorHandler).proceed()
                    XposedLogger.log("WebViewClient.onReceivedSslError(WebView, SslErrorHandler, SslError) hooked for: ${loadPackageParam.packageName}")
                    return null
                }
            })

        /**
         * frameworks/base/core/java/android/webkit/WebViewClient.java
         * public void onReceivedError(WebView, int, String, String)
         */
        XposedLogger.log("Hooking WebViewClient.onReceivedSslError(WebView, int, string, string) for: ${loadPackageParam.packageName}")
        XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",
            loadPackageParam.classLoader,
            "onReceivedError",
            WebView::class.java,
            Int::class.javaPrimitiveType,
            String::class.java,
            String::class.java,
            object : XC_MethodReplacement() {
                @Throws(Throwable::class)
                override fun replaceHookedMethod(param: MethodHookParam): Any? {
                    XposedLogger.log("Hooking WebViewClient.onReceivedSslError(WebView, int, string, string) hooked for: ${loadPackageParam.packageName}")
                    return null
                }
            })

        /**
         * frameworks/base/core/java/android/webkit/WebViewClient.java
         * public void onReceivedError(WebView, WebResourceRequest, WebResourceError)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            XposedLogger.log("Hooking WebViewClient.onReceivedSslError(WebView, int, string, string) for: ${loadPackageParam.packageName}")
            XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",
                loadPackageParam.classLoader,
                "onReceivedError",
                WebView::class.java,
                WebResourceRequest::class.java,
                WebResourceError::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        XposedLogger.log("Hooking WebViewClient.onReceivedSslError(WebView, int, string, string) hooked for: ${loadPackageParam.packageName}")
                        return null
                    }
                })
        }

    }

}