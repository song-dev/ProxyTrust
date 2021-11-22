package com.song.trust.plugin

import android.net.http.SslError
import android.os.Build
import android.webkit.*
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Created by chensongsong on 2021/11/16.
 */
class WebViewHook {

    fun hook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {

        /**
         * frameworks/base/core/java/android/webkit/WebViewClient.java
         * public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
         */
        XposedLogger.log("WebViewClient.onReceivedSslError(WebView, SslErrorHandler, SslError) for: ${loadPackageParam.packageName}")
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
                    XposedLogger.log("WebViewClient.onReceivedSslError: ${param.args[2] as SslError}")
                    return null
                }
            })

        /**
         * frameworks/base/core/java/android/webkit/WebViewClient.java
         * public void onReceivedError(WebView, int, String, String)
         */
        XposedLogger.log("WebViewClient.onReceivedError(WebView, int, string, string) for: ${loadPackageParam.packageName}")
        XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",
            loadPackageParam.classLoader,
            "onReceivedError",
            WebView::class.java,
            Int::class.javaPrimitiveType,
            String::class.java,
            String::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    XposedLogger.log(
                        "WebViewClient.onReceivedError(WebView, int, string, string): ${param.args[1] as Int}, " +
                                "${param.args[2] as String}, ${param.args[3] as String}, hooked for: ${loadPackageParam.packageName}"
                    )
                }
            })

        /**
         * frameworks/base/core/java/android/webkit/WebViewClient.java
         * public void onReceivedError(WebView, WebResourceRequest, WebResourceError)
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            XposedLogger.log("WebViewClient.onReceivedError(WebView, int, string, string) for: ${loadPackageParam.packageName}")
            XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",
                loadPackageParam.classLoader,
                "onReceivedError",
                WebView::class.java,
                WebResourceRequest::class.java,
                WebResourceError::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        XposedLogger.log(
                            "WebViewClient.onReceivedError(WebView, int, string, string): " +
                                    "${(param.args[1] as WebResourceRequest).url}, ${(param.args[2] as WebResourceError).description}" +
                                    ", ${(param.args[2] as WebResourceError).errorCode}, hooked for: ${loadPackageParam.packageName}"
                        )
                    }
                })
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            XposedLogger.log("WebViewClient.onReceivedHttpError(WebView, WebResourceRequest, WebResourceResponse) for: ${loadPackageParam.packageName}")
            XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",
                loadPackageParam.classLoader,
                "onReceivedHttpError",
                WebView::class.java,
                WebResourceRequest::class.java,
                WebResourceResponse::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        XposedLogger.log(
                            "WebViewClient.onReceivedHttpError(WebView, WebResourceRequest, WebResourceResponse): " +
                                    "${(param.args[1] as WebResourceRequest).url}, ${(param.args[2] as WebResourceResponse).statusCode}" +
                                    ", ${(param.args[2] as WebResourceResponse).reasonPhrase}, hooked for: ${loadPackageParam.packageName}"
                        )
                    }
                })
        }

        XposedLogger.log("WebViewClient.onLoadResource(WebView, String) for: ${loadPackageParam.packageName}")
        XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",
            loadPackageParam.classLoader,
            "onLoadResource",
            WebView::class.java,
            String::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    XposedLogger.log(
                        "WebViewClient.onLoadResource(WebView, String): ${param.args[1] as String}, hooked for: ${loadPackageParam.packageName}"
                    )
                }
            })

        XposedLogger.log("WebChromeClient.onReceivedTitle(WebView, String) for: ${loadPackageParam.packageName}")
        XposedHelpers.findAndHookMethod("android.webkit.WebChromeClient",
            loadPackageParam.classLoader,
            "onReceivedTitle",
            WebView::class.java,
            String::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    XposedLogger.log(
                        "WebChromeClient.onReceivedTitle(WebView, String): ${param.args[1] as String}, hooked for: ${loadPackageParam.packageName}"
                    )
                }
            })

        XposedLogger.log("WebChromeClient.onProgressChanged(WebView, int) for: ${loadPackageParam.packageName}")
        XposedHelpers.findAndHookMethod("android.webkit.WebChromeClient",
            loadPackageParam.classLoader,
            "onProgressChanged",
            WebView::class.java,
            Int::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    XposedLogger.log(
                        "WebChromeClient.onProgressChanged(WebView, int): ${param.args[1] as Int}, hooked for: ${loadPackageParam.packageName}"
                    )
                }
            })

    }

}