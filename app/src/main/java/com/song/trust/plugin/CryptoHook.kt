package com.song.trust.plugin

import android.util.Base64
import com.song.trust.utils.CommonUtils
import com.song.trust.utils.HexUtils
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
                            param?.args?.get(0)
                        }"
                    )
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

        XposedBridge.hookAllMethods(
            MessageDigest::class.java,
            "digest",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    try {
                        if (param?.args?.size == 1) {
                            val inputArray = param.args?.get(0) as ByteArray
                            val input = String(inputArray)
                            if (!CommonUtils.isMessyCode(input)) {
                                XposedLogger.log(
                                    "${loadPackageParam.packageName} MessageDigest.digest args: ${
                                        String(inputArray, Charset.defaultCharset())
                                    }"
                                )
                            } else {
                                XposedLogger.log(
                                    "${loadPackageParam.packageName} MessageDigest.digest args Base64: ${
                                        Base64.encodeToString(
                                            inputArray,
                                            Base64.DEFAULT
                                        )
                                    }, HEX: ${HexUtils.bytesToHex(inputArray)}"
                                )
                            }
                        }
                        param?.result?.let {
                            if (it is ByteArray) {
                                XposedLogger.log(
                                    "${loadPackageParam.packageName} MessageDigest.digest Result Base64: ${
                                        Base64.encodeToString(
                                            it,
                                            Base64.DEFAULT
                                        )
                                    }, HEX: ${HexUtils.bytesToHex(it)}"
                                )
                            }
                        }

                    } catch (e: Exception) {
                        XposedLogger.log("MessageDigest.digest Exception: $e")
                    }
                }

            }
        )

//        try {
//            XposedBridge.hookAllMethods(
//                MessageDigest::class.java,
//                "update",
//                object : XC_MethodHook() {
//                    override fun beforeHookedMethod(param: MethodHookParam?) {
//                        try {
//                            when (param?.args?.size) {
//                                1 -> {
//                                    val input = param.args[0]
////                                if (input is ByteBuffer) {
////                                    printLog(
////                                        input.array(),
////                                        loadPackageParam.packageName,
////                                        "MessageDigest.update(ByteBuffer) args"
////                                    )
////                                } else
//                                    if (input is ByteArray) {
//                                        printLog(
//                                            input,
//                                            loadPackageParam.packageName,
//                                            "MessageDigest.update(byte[]) args"
//                                        )
//                                    }
//                                }
//                                3 -> {
//                                    val input = param.args[0]
//                                    if (input is ByteArray) {
//                                        printLog(
//                                            input,
//                                            loadPackageParam.packageName,
//                                            "MessageDigest.update(byte[],int,int) args"
//                                        )
//                                    }
//                                }
//                            }
//                        } catch (e: Exception) {
//                            XposedLogger.log("MessageDigest.digest Exception: $e")
//                        }
//                    }
//                }
//            )
//        } catch (e: Exception) {
//            XposedLogger.log("MessageDigest.digest Exception: $e")
//        }

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
                            // 打印加密前后数据
                            when (param.args.size) {
                                0 -> printLog(
                                    param.result as ByteArray,
                                    loadPackageParam.packageName,
                                    "Cipher.doFinal(byte[]) encrypt result"
                                )
                                1 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[]) encrypt args"
                                    )
                                    printLog(
                                        param.result as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[]) encrypt result"
                                    )
                                }
                                2 -> if (param.args[0] is ByteBuffer) {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(ByteBuffer,ByteBuffer) encrypt args"
                                    )
                                    printLog(
                                        param.args[1] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(ByteBuffer,ByteBuffer) encrypt result"
                                    )
                                }
                                3 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int) encrypt args"
                                    )
                                    printLog(
                                        param.result as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int) encrypt result"
                                    )
                                }
                                4 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[]) encrypt args"
                                    )
                                    printLog(
                                        param.args[3] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[]) encrypt result"
                                    )
                                }
                                5 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[],int) encrypt args"
                                    )
                                    printLog(
                                        param.args[3] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[],int) encrypt result"
                                    )
                                }
                            }
                        } else if (opmode == Cipher.DECRYPT_MODE) {
                            // 打印解密前后数据
                            when (param.args.size) {
                                1 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[]) decrypt args"
                                    )
                                    printLog(
                                        param.result as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[]) decrypt result"
                                    )
                                }
                                2 -> if (param.args[1] is ByteBuffer) {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(ByteBuffer,ByteBuffer) decrypt args"
                                    )
                                    printLog(
                                        param.args[1] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(ByteBuffer,ByteBuffer) decrypt result"
                                    )
                                }
                                3 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int) decrypt args"
                                    )
                                    printLog(
                                        param.result,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int) decrypt result"
                                    )
                                }
                                4 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[]) decrypt args"
                                    )
                                    printLog(
                                        param.args[3] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[]) decrypt result"
                                    )
                                }
                                5 -> {
                                    printLog(
                                        param.args[0] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[],int) decrypt args"
                                    )
                                    printLog(
                                        param.args[3] as ByteArray,
                                        loadPackageParam.packageName,
                                        "Cipher.doFinal(byte[],int,int,byte[],int) decrypt result"
                                    )
                                }
                            }
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
                    XposedLogger.log(
                        "$$tag($packageName) MessyData Base64: ${
                            Base64.encodeToString(
                                data,
                                Base64.DEFAULT
                            )
                        }"
                    )
                }
            }
        } else if (data is ByteBuffer) {
            XposedLogger.log("$tag($packageName): $data")
        }
    }

}