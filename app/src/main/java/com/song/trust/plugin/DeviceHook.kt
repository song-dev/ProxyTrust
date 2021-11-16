package com.song.trust.plugin

import android.app.Application
import android.content.Context
import com.song.trust.preferences.ProviderPreferences
import com.song.trust.utils.Constants
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONObject

/**
 * Created by chensongsong on 2021/11/12.
 */
class DeviceHook {

    private val listFilter =
        listOf("android", "me.weishu.exp", "de.robv.android.xposed.installer")

    fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        if (!listFilter.contains(loadPackageParam.packageName)) {
            XposedBridge.hookAllMethods(
                Application::class.java,
                "onCreate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        val providerPreferences = ProviderPreferences(
                            param.thisObject as Context,
                            Constants.AUTHORITIES,
                            Constants.PREFName
                        )
                        val value: String? = providerPreferences.getString("target", null)
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
                                if (jsonObject.optBoolean("webView")) {
                                    WebViewHook().hook(loadPackageParam)
                                }
                                if (jsonObject.optBoolean("jsAlert")) {
                                    JSAlertHook().hook()
                                }
                            }
                        }
                    }
                })
        }

    }
}