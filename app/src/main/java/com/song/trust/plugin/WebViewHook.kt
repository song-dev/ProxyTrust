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

        //        /* WebView Hooks */
//        /* frameworks/base/core/java/android/webkit/WebViewClient.java */
//        /* public void onReceivedSslError(Webview, SslErrorHandler, SslError) */
//        Log.d(TAG, "Hooking WebViewClient.onReceivedSslError(WebView, SslErrorHandler, SslError) for: " + currentPackageName);
//        findAndHookMethod("android.webkit.WebViewClient", lpparam.classLoader, "onReceivedSslError",
//                WebView.class, SslErrorHandler.class, SslError.class, new XC_MethodReplacement() {
//                    @Override
//                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                        ((SslErrorHandler) param.args[1]).proceed();
//                        return null;
//                    }
//                });
//
//        /* frameworks/base/core/java/android/webkit/WebViewClient.java */
//        /* public void onReceivedError(WebView, int, String, String) */
//        Log.d(TAG, "Hooking WebViewClient.onReceivedSslError(WebView, int, string, string) for: " + currentPackageName);
//
//        findAndHookMethod("android.webkit.WebViewClient", lpparam.classLoader, "onReceivedError",
//                WebView.class, int.class, String.class, String.class, new XC_MethodReplacement() {
//                    @Override
//                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                        return null;
//                    }
//                });

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
                        return null
                    }
                })
        }

    }

}