package com.song.trust.plugin

import com.song.trust.utils.CommonUtils
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Cipher

/**
 * Created by chensongsong on 2021/11/17.
 */
class CryptoHook {

    fun hook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        XposedLogger.log("CryptoHook.hook")
        XposedBridge.hookAllMethods(
            Cipher::class.java,
            "getInstance",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    XposedLogger.log(
                        "${loadPackageParam.packageName} Cipher.getInstance args[0]: ${
                            param?.args?.get(
                                0
                            )
                        }"
                    )
                }
            }
        )
        XposedHelpers.findAndHookMethod(
            Cipher::class.java,
            "doFinal",
            ByteArray::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val cipher = param?.thisObject as Cipher
                    val opmode = XposedHelpers.getIntField(cipher, "opmode")
                    XposedLogger.log("Cipher.doFinal opmode: $opmode")
                    try {
                        if (opmode == Cipher.ENCRYPT_MODE) {
                            // 打印加密前数据
                            val inputArray = param.args?.get(0) as ByteArray
                            XposedLogger.log(
                                "${loadPackageParam.packageName} Cipher.doFinal encrypt: ${
                                    String(inputArray, Charset.defaultCharset())
                                }"
                            )
                        } else if (opmode == Cipher.DECRYPT_MODE) {
                            // 打印解密后数据
                            XposedLogger.log(
                                "${loadPackageParam.packageName} Cipher.doFinal decrypt: ${
                                    String(param.result as ByteArray, Charset.defaultCharset())
                                }"
                            )
                        }
                    } catch (e: Exception) {
                        XposedLogger.log("Cipher.doFinal Exception: $e")
                    }
                }
            }
        )

        XposedBridge.hookAllMethods(
            MessageDigest::class.java,
            "getInstance",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    XposedLogger.log(
                        "${loadPackageParam.packageName} MessageDigest.getInstance args[0]: ${
                            param?.args?.get(
                                0
                            )
                        }"
                    )
                }
            }
        )

        XposedHelpers.findAndHookMethod(
            MessageDigest::class.java,
            "digest",
            ByteArray::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    try {
                        if (param?.args?.isNotEmpty() == true) {
                            val inputArray = param.args?.get(0) as ByteArray
                            val input = String(inputArray)
                            if (!CommonUtils.isMessyCode(input)) {
                                XposedLogger.log(
                                    "${loadPackageParam.packageName} MessageDigest.digest: ${
                                        String(inputArray, Charset.defaultCharset())
                                    }"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        XposedLogger.log("MessageDigest.update Exception: $e")
                    }
                }
            }
        )
        XposedBridge.hookAllMethods(
            Cipher::class.java,
            "doFinal",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val cipher = param?.thisObject as Cipher
                    val opmode = XposedHelpers.getIntField(cipher, "opmode")
                    XposedLogger.log("Cipher.doFinal opmode: $opmode")
                    try {
                        if (opmode == Cipher.ENCRYPT_MODE) {
                            // 打印加密前数据
                            val inputArray = param.args?.get(0) as ByteArray
                            XposedLogger.log(
                                "${loadPackageParam.packageName} Cipher.doFinal encrypt: ${
                                    String(inputArray, Charset.defaultCharset())
                                }"
                            )
                        } else if (opmode == Cipher.DECRYPT_MODE) {
                            // 打印解密后数据
                            XposedLogger.log(
                                "${loadPackageParam.packageName} Cipher.doFinal decrypt: ${
                                    String(param.result as ByteArray, Charset.defaultCharset())
                                }"
                            )
                        }
                    } catch (e: Exception) {
                        XposedLogger.log("Cipher.doFinal Exception: $e")
                    }
                }
            }
        )

    }

}