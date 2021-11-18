package com.song.trust.plugin

import com.song.trust.utils.CommonUtils
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.nio.ByteBuffer
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
//        XposedHelpers.findAndHookMethod(
//            Cipher::class.java,
//            "doFinal",
//            ByteArray::class.java,
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam?) {
//                    val cipher = param?.thisObject as Cipher
//                    val opmode = XposedHelpers.getIntField(cipher, "opmode")
//                    XposedLogger.log("Cipher.doFinal opmode: $opmode")
//                    try {
//                        if (opmode == Cipher.ENCRYPT_MODE) {
//                            // 打印加密前数据
//                            val inputArray = param.args?.get(0) as ByteArray
//                            XposedLogger.log(
//                                "${loadPackageParam.packageName} Cipher.doFinal encrypt: ${
//                                    String(inputArray, Charset.defaultCharset())
//                                }"
//                            )
//                        } else if (opmode == Cipher.DECRYPT_MODE) {
//                            // 打印解密后数据
//                            XposedLogger.log(
//                                "${loadPackageParam.packageName} Cipher.doFinal decrypt: ${
//                                    String(param.result as ByteArray, Charset.defaultCharset())
//                                }"
//                            )
//                        }
//                    } catch (e: Exception) {
//                        XposedLogger.log("Cipher.doFinal Exception: $e")
//                    }
//                }
//            }
//        )

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
                            when (param.args.size) {
                                1 -> printLog(
                                    param.args[0] as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[]) encrypt"
                                )
                                2 -> if (param.args[0] is ByteBuffer) (
                                        printLog(
                                            param.args[0] as ByteArray,
                                            loadPackageParam.packageName,
                                            "Cipher.doFinal(ByteBuffer,ByteBuffer) encrypt"
                                        ))
                                3 -> printLog(
                                    param.args[0] as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[],int,int) encrypt"
                                )
                                4 -> printLog(
                                    param.args[0] as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[],int,int,byte[]) encrypt"
                                )
                                5 -> printLog(
                                    param.args[0] as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[],int,int,byte[],int) encrypt"
                                )
                            }
                        } else if (opmode == Cipher.DECRYPT_MODE) {
                            // 打印解密后数据
                            when (param.args.size) {
                                1 -> printLog(
                                    param.result as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[]) decrypt"
                                )
                                2 -> if (param.args[1] is ByteBuffer) (
                                        printLog(
                                            param.args[1] as ByteArray,
                                            loadPackageParam.packageName,
                                            "Cipher.doFinal(ByteBuffer,ByteBuffer) decrypt"
                                        ))
                                3 -> printLog(
                                    param.result,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[],int,int) decrypt"
                                )
                                4 -> printLog(
                                    param.args[3] as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[],int,int,byte[]) decrypt"
                                )
                                5 -> printLog(
                                    param.args[3] as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[],int,int,byte[],int) decrypt"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        XposedLogger.log("Cipher.doFinal Exception: $e")
                    }
                }
            }
        )

    }

    private fun printLog(data: Any, packageName: String, tag: String) {
        if (data is ByteArray) {
            // data 类型: zip String
            val string = String(data, Charset.defaultCharset())
            if (!CommonUtils.isMessyCode(string)) {
                XposedLogger.log("$tag($packageName): $string")
            } else {
                // 尝试 zip 解压缩
                val unzip = CommonUtils.unzip(data)
                if (!unzip.isNullOrBlank() && !CommonUtils.isMessyCode(unzip)) {
                    XposedLogger.log("$tag($packageName): $unzip")
                } else {
                    XposedLogger.log("$$tag($packageName) MessyData: $string")
                }
            }
        } else if (data is ByteBuffer) {
            XposedLogger.log("$tag($packageName): $data")
        }
    }

}