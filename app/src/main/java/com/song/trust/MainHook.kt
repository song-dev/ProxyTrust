package com.song.trust

import android.app.Application
import android.content.Context
import com.song.trust.preferences.ProviderPreferences
import com.song.trust.utils.Constants
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONObject

/**
 * Created by chensongsong on 2021/11/12.
 */
class MainHook : IXposedHookLoadPackage {

    private val FILTER =
        listOf("android", "me.weishu.exp", "de.robv.android.xposed.installer")

    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        // 加载获取包名
        XposedLogger.log("Loaded App: ${loadPackageParam.packageName}")
        if (!FILTER.contains(loadPackageParam.packageName)) {
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
                        val value: String? =
                            providerPreferences.getString(loadPackageParam.packageName, null)
                        if (value != null && value.isNotBlank()) {
                            val jsonObject = JSONObject(value)
                            // 代理处理
                        }
                    }
                })
        }

    }
}