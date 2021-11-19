package com.song.trust.plugin

import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by chensongsong on 2021/11/17.
 */
class JSONHook {

    fun hook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        XposedLogger.log("JSONHook.hook")
        XposedBridge.hookAllMethods(
            JSONObject::class.java,
            "toString",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    XposedLogger.log("====== ${loadPackageParam.packageName} JSONObject.toString result: ======")
                    XposedLogger.printLongString("${param?.result}")
                }
            }
        )
        XposedBridge.hookAllConstructors(JSONObject::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                XposedLogger.log("====== ${loadPackageParam.packageName} JSONObject.Constructors args: ======")
                if (param?.args?.isNotEmpty() == true) {
                    when (val value = param.args[0]) {
                        is String -> XposedLogger.printLongString("$value")
                        is Map<*, *> -> XposedLogger.printLongString("$value")
                        is JSONObject -> XposedLogger.printLongString("$value")
                    }


                }
            }
        })
        XposedBridge.hookAllMethods(
            JSONArray::class.java,
            "toString",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    XposedLogger.log("====== ${loadPackageParam.packageName} JSONArray.toString result: ======")
                    XposedLogger.printLongString("${param?.result}")
                }
            }
        )
        XposedBridge.hookAllMethods(
            JSONArray::class.java,
            "toJSONObject",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    XposedLogger.log("====== ${loadPackageParam.packageName} JSONArray.toJSONObject result: ======")
                    XposedLogger.printLongString("${param?.result as JSONObject}")
                }
            }
        )
        XposedBridge.hookAllConstructors(JSONArray::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                XposedLogger.log("====== ${loadPackageParam.packageName} JSONArray.Constructors args: ======")
                XposedLogger.printLongString("${param?.thisObject as JSONArray}")
            }
        })


    }

}