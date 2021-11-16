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
 * Created by chensongsong on 2021/11/15.
 */
class ProtocolHook {

    fun hook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        // WebView 链接地址改为 http
        XposedBridge.hookAllMethods(
            WebView::class.java,
            "loadUrl",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val urlStr: String = param?.args?.get(0) as String
                    if (urlStr.startsWith("https")) {
                        param.args[0] = urlStr.replaceFirst("https", "http")
                    }
                }
            }
        )
        XposedBridge.hookAllMethods(
            WebView::class.java,
            "loadDataWithBaseURL",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val urlStr: String = param?.args?.get(0) as String
                    if (urlStr.startsWith("https")) {
                        param.args[0] = urlStr.replaceFirst("https", "http")
                    }
                }
            }
        )
        // URL 链接地址改为 http
        XposedBridge.hookAllConstructors(URL::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val protocol = param?.args?.get(0)
                if (protocol is String) {
                    if (protocol.startsWith("https")) {
                        param.args[0] = protocol.replaceFirst("https", "http")
                    }
                }
            }
        })

    }

}