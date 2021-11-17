package com.song.trust.plugin

import android.annotation.SuppressLint
import com.song.trust.utils.XposedLogger
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * Created by chensongsong on 2021/11/16.
 */
class OKHttpHook {

    //        // Multi-dex support: https://github.com/rovo89/XposedBridge/issues/30#issuecomment-68486449
//        findAndHookMethod("android.app.Application",
//                loadPackageParam.classLoader,
//                "attach",
//                Context.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        // Hook OkHttp or third party libraries.
//                        Context context = (Context) param.args[0];
//                        processOkHttp(context.getClassLoader());
////                        processXUtils(context.getClassLoader());
//                    }
//                }
//        );

    //    private void processXUtils(ClassLoader classLoader) {
//        Log.d(TAG, "Hooking org.xutils.http.RequestParams.setSslSocketFactory(SSLSocketFactory) (3) for: " + currentPackageName);
//        try {
//            classLoader.loadClass("org.xutils.http.RequestParams");
//            findAndHookMethod("org.xutils.http.RequestParams", classLoader, "setSslSocketFactory", javax.net.ssl.SSLSocketFactory.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    param.args[0] = getEmptySSLFactory();
//                }
//            });
//            findAndHookMethod("org.xutils.http.RequestParams", classLoader, "setHostnameVerifier", HostnameVerifier.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    param.args[0] = new ImSureItsLegitHostnameVerifier();
//                }
//            });
//        } catch (Exception e) {
//            Log.d(TAG, "org.xutils.http.RequestParams not found in " + currentPackageName + "-- not hooking");
//        }
//    }

    //    public void processOkHttp(ClassLoader classLoader, String currentPackageName) {
//        /* hooking OKHTTP by SQUAREUP */
//        /* com/squareup/okhttp/CertificatePinner.java available online @ https://github.com/square/okhttp/blob/master/okhttp/src/main/java/com/squareup/okhttp/CertificatePinner.java */
//        /* public void check(String hostname, List<Certificate> peerCertificates) throws SSLPeerUnverifiedException{}*/
//        /* Either returns true or a exception so blanket return true */
//        /* Tested against version 2.5 */
//        Log.d(TAG, "Hooking com.squareup.okhttp.CertificatePinner.check(String,List) (2.5) for: " + currentPackageName);
//
//        try {
//            classLoader.loadClass("com.squareup.okhttp.CertificatePinner");
//            findAndHookMethod("com.squareup.okhttp.CertificatePinner",
//                    classLoader,
//                    "check",
//                    String.class,
//                    List.class,
//                    new XC_MethodReplacement() {
//                        @Override
//                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                            return true;
//                        }
//                    });
//        } catch (ClassNotFoundException e) {
//            // pass
//            Log.d(TAG, "OKHTTP 2.5 not found in " + currentPackageName + "-- not hooking");
//        }
//
//        //https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/CertificatePinner.java#L144
//        Log.d(TAG, "Hooking okhttp3.CertificatePinner.check(String,List) (3.x) for: " + currentPackageName);
//
//        try {
//            classLoader.loadClass("okhttp3.CertificatePinner");
//            findAndHookMethod("okhttp3.CertificatePinner",
//                    classLoader,
//                    "check",
//                    String.class,
//                    List.class,
//                    new XC_MethodReplacement() {
//                        @Override
//                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                            return null;
//                        }
//                    });
//        } catch (ClassNotFoundException e) {
//            Log.d(TAG, "OKHTTP 3.x not found in " + currentPackageName + " -- not hooking");
//            // pass
//        }
//
//        //https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
//        try {
//            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier");
//            findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
//                    classLoader,
//                    "verify",
//                    String.class,
//                    javax.net.ssl.SSLSession.class,
//                    new XC_MethodReplacement() {
//                        @Override
//                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                            return true;
//                        }
//                    });
//        } catch (ClassNotFoundException e) {
//            Log.d(TAG, "OKHTTP 3.x not found in " + currentPackageName + " -- not hooking OkHostnameVerifier.verify(String, SSLSession)");
//            // pass
//        }
//
//        //https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
//        try {
//            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier");
//            findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
//                    classLoader,
//                    "verify",
//                    String.class,
//                    java.security.cert.X509Certificate.class,
//                    new XC_MethodReplacement() {
//                        @Override
//                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                            return true;
//                        }
//                    });
//        } catch (ClassNotFoundException e) {
//            Log.d(TAG, "OKHTTP 3.x not found in " + currentPackageName + " -- not hooking OkHostnameVerifier.verify(String, X509)(");
//            // pass
//        }
//
//        //https://github.com/square/okhttp/blob/okhttp_4.2.x/okhttp/src/main/java/okhttp3/CertificatePinner.kt
//        Log.d(TAG, "Hooking okhttp3.CertificatePinner.check(String,List) (4.2.0+) for: " + currentPackageName);
//
//        try {
//            classLoader.loadClass("okhttp3.CertificatePinner");
//            findAndHookMethod("okhttp3.CertificatePinner",
//                    classLoader,
//                    "check$okhttp",
//                    String.class,
//                    "kotlin.jvm.functions.Function0",
//                    new XC_MethodReplacement() {
//                        @Override
//                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                            return null;
//                        }
//                    });
//        } catch (ClassNotFoundException e) {
//            Log.d(TAG, "OKHTTP 4.2.0+ not found in " + currentPackageName + " -- not hooking");
//            // pass
//        }
//
//    }

    fun hook(classLoader: ClassLoader, packageName: String) {
        /* com/squareup/okhttp/CertificatePinner.java available online @ https://github.com/square/okhttp/blob/master/okhttp/src/main/java/com/squareup/okhttp/CertificatePinner.java */
        /* public void check(String hostname, List<Certificate> peerCertificates) throws SSLPeerUnverifiedException{}*/
        /* Either returns true or a exception so blanket return true */
        /* Tested against version 2.5 */
        try {
            XposedLogger.log("Hooking com.squareup.okhttp.CertificatePinner.check(String, List) (2.5) for: $packageName")
            classLoader.loadClass("com.squareup.okhttp.CertificatePinner")
            XposedHelpers.findAndHookMethod("com.squareup.okhttp.CertificatePinner",
                classLoader,
                "check",
                String::class.java,
                List::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
                        return true
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 2.5 not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/CertificatePinner.java#L144
        try {
            XposedLogger.log("Hooking okhttp3.CertificatePinner.check(String, List) (3.x) for: $packageName")
            classLoader.loadClass("okhttp3.CertificatePinner")
            XposedHelpers.findAndHookMethod("okhttp3.CertificatePinner",
                classLoader,
                "check",
                String::class.java,
                List::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any? {
                        return null
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 3.x not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
        try {
            XposedLogger.log("Hooking okhttp3.internal.tls.OkHostnameVerifier.verify(String, SSLSession) (3.x) for: $packageName")
            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier")
            XposedHelpers.findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
                classLoader,
                "verify",
                String::class.java,
                SSLSession::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
                        return true
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 3.x not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
        try {
            XposedLogger.log("Hooking okhttp3.internal.tls.OkHostnameVerifier.verify(String, X509Certificate) (3.x) for: $packageName")
            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier")
            XposedHelpers.findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
                classLoader,
                "verify",
                String::class.java,
                X509Certificate::class.java,
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
                        return true
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 3.x not found in $packageName: not hooking")
        }

        // https://github.com/square/okhttp/blob/okhttp_4.2.x/okhttp/src/main/java/okhttp3/CertificatePinner.kt
        try {
            XposedLogger.log("Hooking okhttp3.CertificatePinner.check(String,List) (4.2.0+) for: $packageName")
            classLoader.loadClass("okhttp3.CertificatePinner")
            XposedHelpers.findAndHookMethod("okhttp3.CertificatePinner",
                classLoader,
                "check\$okhttp",
                String::class.java,
                "kotlin.jvm.functions.Function0",
                object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any? {
                        return null
                    }
                })
        } catch (e: ClassNotFoundException) {
            XposedLogger.log("OKHTTP 4.2.0+ not found in $packageName: not hooking")
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