package com.song.trust.plugin

import android.webkit.WebView
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.net.URL

/**
 * Created by chensongsong on 2021/11/15.
 */
class ProtocolHook {

    fun hook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        XposedLogger.log("ProtocolHook.hook")
        // WebView 链接地址改为 http
        XposedHelpers.findAndHookMethod(
            WebView::class.java,
            "loadUrl",
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val urlStr: String = param?.args?.get(0) as String
                    XposedLogger.log("WebView.loadUrl Parameter: $urlStr")
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
                    XposedLogger.log("WebView.loadDataWithBaseURL Parameter: $urlStr")
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
                    XposedLogger.log("URL Constructors Parameter: $protocol")
                    if (protocol.startsWith("https")) {
                        param.args[0] = protocol.replaceFirst("https", "http")
                    }
                }
            }
        })
        // WebView 链接地址改为 http
        try {
            loadPackageParam.classLoader.loadClass("okhttp3.Request\$Builder")
            XposedHelpers.findAndHookMethod(
                "okhttp3.Request\$Builder",
                loadPackageParam.classLoader,
                "url",
                String::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        val urlStr: String = param?.args?.get(0) as String
                        XposedLogger.log("okhttp3.Request.Builder.url Parameter: $urlStr")
                        if (urlStr.startsWith("https")) {
                            param.args[0] = urlStr.replaceFirst("https", "http")
                        }
                    }
                }
            )
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("ProtocolHook Request.Builder Exception${e.message}")
        }
    }

}