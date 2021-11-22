package com.song.trust.plugin

import android.app.Application
import android.content.Context
import com.song.trust.BuildConfig
import com.song.trust.preferences.ProviderPreferences
import com.song.trust.utils.Constants
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONObject

/**
 * Created by chensongsong on 2021/11/12.
 */
class DeviceHook {

    private val listFilter =
        listOf(
            "android",
            "me.weishu.exp",
            "de.robv.android.xposed.installer",
            BuildConfig.APPLICATION_ID
        )

    fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (!listFilter.contains(loadPackageParam.packageName)) {

//            if (loadPackageParam.packageName.equals("com.geetest.gateauth")) {
//                CryptoHook().hook(loadPackageParam)
//                JSONHook().hook(loadPackageParam)
//                ProtocolHook().hook(loadPackageParam)
//            }
            val instrumentationCls =
                loadPackageParam.classLoader.loadClass("android.app.Instrumentation")
            XposedBridge.hookAllMethods(
                instrumentationCls,
                "newApplication",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        super.afterHookedMethod(param)
                        loadScript(loadPackageParam, param?.result as Context)
                    }
                })
//            XposedBridge.hookAllMethods(
//                Application::class.java,
//                "onCreate",
//                object : XC_MethodHook() {
//                    override fun afterHookedMethod(param: MethodHookParam) {
//                        super.afterHookedMethod(param)
//                        loadScript(loadPackageParam, param.thisObject as Context);
//                    }
//                })

        }
    }

    private fun loadScript(loadPackageParam: LoadPackageParam, context: Context) {
        val providerPreferences =
            ProviderPreferences(context, Constants.AUTHORITIES, Constants.PREFName)
        val value: String? = providerPreferences.getString("target", null)
        XposedLogger.log("Target JSON: $value")
        if (value != null && value.isNotBlank()) {
            val jsonObject = JSONObject(value)
            if (loadPackageParam.packageName.equals(jsonObject.optString("targetPackageName"))) {
                if (jsonObject.optBoolean("certificate")) {
                    CertificateHook().handleLoadPackage(loadPackageParam)
                }
                if (jsonObject.optBoolean("webDebug")) {
                    WebDebugHook().hook()
                }
                if (jsonObject.optBoolean("protocol")) {
                    ProtocolHook().hook(loadPackageParam)
                }
                if (jsonObject.optBoolean("okhttp")) {
                    OKHttpHook().hook(
                        loadPackageParam.classLoader,
                        loadPackageParam.packageName
                    )
                }
                if (jsonObject.optBoolean("webView")) {
                    WebViewHook().hook(loadPackageParam)
                }
                if (jsonObject.optBoolean("jsAlert")) {
                    JSAlertHook().hook(loadPackageParam)
                }
                if (jsonObject.optBoolean("json")) {
                    JSONHook().hook(loadPackageParam)
                }
                if (jsonObject.optBoolean("crypto")) {
                    CryptoHook().hook(loadPackageParam)
                }
            }
        }
    }
}