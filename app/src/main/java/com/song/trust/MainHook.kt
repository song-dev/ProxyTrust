package com.song.trust

import com.song.trust.plugin.DeviceHook
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * Created by chensongsong on 2021/11/12.
 */
class MainHook : IXposedHookLoadPackage {

    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        // 加载获取包名
        XposedLogger.log("Loaded App: ${loadPackageParam.packageName}")
        DeviceHook().handleLoadPackage(loadPackageParam)
    }
}