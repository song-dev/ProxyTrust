package com.song.trust.plugin

import android.annotation.SuppressLint
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * Created by chensongsong on 2021/11/16.
 */
class OKHttpHook {

    fun hook(classLoader: ClassLoader, packageName: String) {
        /* com/squareup/okhttp/CertificatePinner.java available online @ https://github.com/square/okhttp/blob/master/okhttp/src/main/java/com/squareup/okhttp/CertificatePinner.java */
        /* public void check(String hostname, List<Certificate> peerCertificates) throws SSLPeerUnverifiedException{}*/
        /* Either returns true or a exception so blanket return true */
        /* Tested against version 2.5 */
        try {
            classLoader.loadClass("com.squareup.okhttp.CertificatePinner")
            XposedHelpers.findAndHookMethod("com.squareup.okhttp.CertificatePinner",
                classLoader,
                "check",
                String::class.java,
                List::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
                        XposedLogger.log("com.squareup.okhttp.CertificatePinner.check(String, List) (2.5) for: $packageName")
                        return true
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 2.5 not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/CertificatePinner.java#L144
        try {
            classLoader.loadClass("okhttp3.CertificatePinner")
            XposedHelpers.findAndHookMethod("okhttp3.CertificatePinner",
                classLoader,
                "check",
                String::class.java,
                List::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any? {
                        XposedLogger.log("okhttp3.CertificatePinner.check(String, List) (3.x) for: $packageName")
                        return null
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 3.x not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
        try {
            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier")
            XposedHelpers.findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
                classLoader,
                "verify",
                String::class.java,
                SSLSession::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
                        XposedLogger.log("okhttp3.internal.tls.OkHostnameVerifier.verify(String, SSLSession) (3.x) for: $packageName")
                        return true
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 3.x not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
        try {
            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier")
            XposedHelpers.findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
                classLoader,
                "verify",
                String::class.java,
                X509Certificate::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
                        XposedLogger.log("okhttp3.internal.tls.OkHostnameVerifier.verify(String, X509Certificate) (3.x) for: $packageName")
                        return true
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 3.x not found in $packageName: not hooking")
        }

        try {
            val loadClass = classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier")
            XposedBridge.hookAllMethods(loadClass,
                "verify",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
                        XposedLogger.log("okhttp3.internal.tls.OkHostnameVerifier.verify() (3.x) for: $packageName")
                        return true
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 3.x not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/okhttp_4.2.x/okhttp/src/main/java/okhttp3/CertificatePinner.kt
        try {
            classLoader.loadClass("okhttp3.CertificatePinner")
            XposedHelpers.findAndHookMethod("okhttp3.CertificatePinner",
                classLoader,
                "check\$okhttp",
                String::class.java,
                "kotlin.jvm.functions.Function0",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any? {
                        XposedLogger.log("okhttp3.CertificatePinner.check(String,List) (4.2.0+) for: $packageName")
                        return null
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 4.2.0+ not found in $packageName: not hooking")
        } catch (e: NoSuchMethodError) {
            XposedLogger.log("OKHTTP 4.2.0+ not found in $packageName Function0: not hooking")
        }catch (e:Throwable){
            XposedLogger.log("OKHTTP 4.2.0+ ${e.message} for $packageName")
        }

        try {
            XposedBridge.hookAllMethods(
                OkHttpClient::class.java,
                "newBuilder",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(methodHookParam: MethodHookParam) {
                        XposedLogger.log("okhttp3.OkHttpClient.Builder.build (*) for: $packageName")
                        XposedHelpers.callMethod(
                            methodHookParam.result,
                            "hostnameVerifier", ImSureItsLegitHostnameVerifier()
                        )
                        XposedHelpers.callMethod(
                            methodHookParam.result,
                            "sslSocketFactory", getEmptySSLFactory()
                        )
                    }
                })

            XposedHelpers.findAndHookMethod(
                OkHttpClient.Builder::class.java, "build",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun beforeHookedMethod(methodHookParam: MethodHookParam) {
                        XposedLogger.log("okhttp3.OkHttpClient.Builder.build (*) for: $packageName")
                        XposedHelpers.callMethod(
                            methodHookParam.thisObject,
                            "hostnameVerifier", ImSureItsLegitHostnameVerifier()
                        )
                        XposedHelpers.callMethod(
                            methodHookParam.thisObject,
                            "sslSocketFactory", getEmptySSLFactory()
                        )
                    }
                })
        } catch (e: Exception) {
            XposedLogger.log("OKHTTP *+ not found in $packageName: not hooking")
        }

    }

    private fun getEmptySSLFactory(): SSLSocketFactory? {
        return try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(ImSureItsLegitTrustManager()), null)
            sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            null
        } catch (e: KeyManagementException) {
            null
        }
    }

    @SuppressLint("TrustAllX509TrustManager")
    private class ImSureItsLegitTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        fun checkServerTrusted(
            chain: Array<X509Certificate>,
            authType: String,
            host: String
        ): List<X509Certificate> {
            return ArrayList<X509Certificate>()
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls<X509Certificate>(0)
        }

    }

    private class ImSureItsLegitHostnameVerifier : HostnameVerifier {
        @SuppressLint("BadHostnameVerifier")
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }

}