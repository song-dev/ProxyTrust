package com.song.trust.plugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.X509TrustManagerExtensions;
import android.os.Build;
import android.util.Log;

import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class CertificateHook {

    private static final String TAG = "JustTrustMe";
    private String currentPackageName = "";

    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {

        currentPackageName = loadPackageParam.packageName;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            findAndHookMethod(X509TrustManagerExtensions.class, "checkServerTrusted", X509Certificate[].class, String.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return param.args[0];
                }
            });
        }

        findAndHookMethod("android.security.net.config.NetworkSecurityTrustManager", loadPackageParam.classLoader, "checkPins", List.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return null;
            }
        });

        /* external/apache-http/src/org/apache/http/conn/ssl/SSLSocketFactory.java */
        /* public SSLSocketFactory( ... ) */
        Log.d(TAG, "Hooking SSLSocketFactory(String, KeyStore, String, KeyStore) for: " + currentPackageName);
        findAndHookConstructor(SSLSocketFactory.class, String.class, KeyStore.class, String.class, KeyStore.class,
                SecureRandom.class, HostNameResolver.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String algorithm = (String) param.args[0];
                        KeyStore keystore = (KeyStore) param.args[1];
                        String keystorePassword = (String) param.args[2];
                        SecureRandom random = (SecureRandom) param.args[4];
                        KeyManager[] keymanagers = null;
                        TrustManager[] trustmanagers = null;
                        if (keystore != null) {
                            keymanagers = (KeyManager[]) callStaticMethod(SSLSocketFactory.class, "createKeyManagers", keystore, keystorePassword);
                        }
                        trustmanagers = new TrustManager[]{new ImSureItsLegitTrustManager()};
                        setObjectField(param.thisObject, "sslcontext", SSLContext.getInstance(algorithm));
                        callMethod(getObjectField(param.thisObject, "sslcontext"), "init", keymanagers, trustmanagers, random);
                        setObjectField(param.thisObject, "socketfactory",
                                callMethod(getObjectField(param.thisObject, "sslcontext"), "getSocketFactory"));
                    }

                });


        /* external/apache-http/src/org/apache/http/conn/ssl/SSLSocketFactory.java */
        /* public static SSLSocketFactory getSocketFactory() */
        Log.d(TAG, "Hooking static SSLSocketFactory(String, KeyStore, String, KeyStore) for: " + currentPackageName);
        findAndHookMethod("org.apache.http.conn.ssl.SSLSocketFactory", loadPackageParam.classLoader, "getSocketFactory", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return (SSLSocketFactory) newInstance(SSLSocketFactory.class);
            }
        });

        /* external/apache-http/src/org/apache/http/conn/ssl/SSLSocketFactory.java */
        /* public boolean isSecure(Socket) */
        Log.d(TAG, "Hooking SSLSocketFactory(Socket) for: " + currentPackageName);
        findAndHookMethod("org.apache.http.conn.ssl.SSLSocketFactory", loadPackageParam.classLoader, "isSecure", Socket.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return true;
            }
        });

        /* JSSE Hooks */
        /* libcore/luni/src/main/java/javax/net/ssl/TrustManagerFactory.java */
        /* public final TrustManager[] getTrustManager() */
        Log.d(TAG, "Hooking TrustManagerFactory.getTrustManagers() for: " + currentPackageName);
        findAndHookMethod("javax.net.ssl.TrustManagerFactory", loadPackageParam.classLoader, "getTrustManagers", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if (hasTrustManagerImpl()) {
                    Class<?> cls = findClass("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader);
                    TrustManager[] managers = (TrustManager[]) param.getResult();
                    if (managers.length > 0 && cls.isInstance(managers[0]))
                        return;
                }
                param.setResult(new TrustManager[]{new ImSureItsLegitTrustManager()});
            }
        });

        /* libcore/luni/src/main/java/javax/net/ssl/HttpsURLConnection.java */
        /* public void setDefaultHostnameVerifier(HostnameVerifier) */
        Log.d(TAG, "Hooking HttpsURLConnection.setDefaultHostnameVerifier for: " + currentPackageName);
        findAndHookMethod("javax.net.ssl.HttpsURLConnection", loadPackageParam.classLoader, "setDefaultHostnameVerifier",
                HostnameVerifier.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });

        /* libcore/luni/src/main/java/javax/net/ssl/HttpsURLConnection.java */
        /* public void setSSLSocketFactory(SSLSocketFactory) */
        Log.d(TAG, "Hooking HttpsURLConnection.setSSLSocketFactory for: " + currentPackageName);
        findAndHookMethod("javax.net.ssl.HttpsURLConnection", loadPackageParam.classLoader, "setSSLSocketFactory", javax.net.ssl.SSLSocketFactory.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });

        /* libcore/luni/src/main/java/javax/net/ssl/HttpsURLConnection.java */
        /* public void setHostnameVerifier(HostNameVerifier) */
        Log.d(TAG, "Hooking HttpsURLConnection.setHostnameVerifier for: " + currentPackageName);
        findAndHookMethod("javax.net.ssl.HttpsURLConnection", loadPackageParam.classLoader, "setHostnameVerifier", HostnameVerifier.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        return null;
                    }
                });

//        /* WebView Hooks */
//        /* frameworks/base/core/java/android/webkit/WebViewClient.java */
//        /* public void onReceivedSslError(Webview, SslErrorHandler, SslError) */
//        Log.d(TAG, "Hooking WebViewClient.onReceivedSslError(WebView, SslErrorHandler, SslError) for: " + currentPackageName);
//        findAndHookMethod("android.webkit.WebViewClient", lpparam.classLoader, "onReceivedSslError",
//                WebView.class, SslErrorHandler.class, SslError.class, new XC_MethodReplacement() {
//                    @Override
//                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                        ((SslErrorHandler) param.args[1]).proceed();
//                        return null;
//                    }
//                });
//
//        /* frameworks/base/core/java/android/webkit/WebViewClient.java */
//        /* public void onReceivedError(WebView, int, String, String) */
//        Log.d(TAG, "Hooking WebViewClient.onReceivedSslError(WebView, int, string, string) for: " + currentPackageName);
//
//        findAndHookMethod("android.webkit.WebViewClient", lpparam.classLoader, "onReceivedError",
//                WebView.class, int.class, String.class, String.class, new XC_MethodReplacement() {
//                    @Override
//                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                        return null;
//                    }
//                });

        //SSLContext.init >> (null,ImSureItsLegitTrustManager,null)
        findAndHookMethod("javax.net.ssl.SSLContext", loadPackageParam.classLoader, "init", KeyManager[].class, TrustManager[].class, SecureRandom.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0] = null;
                param.args[1] = new TrustManager[]{new ImSureItsLegitTrustManager()};
                param.args[2] = null;
            }
        });

        // Multi-dex support: https://github.com/rovo89/XposedBridge/issues/30#issuecomment-68486449
        findAndHookMethod("android.app.Application",
                loadPackageParam.classLoader,
                "attach",
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // Hook OkHttp or third party libraries.
                        Context context = (Context) param.args[0];
                        processOkHttp(context.getClassLoader());
                        processXUtils(context.getClassLoader());
                    }
                }
        );

        /* Only for newer devices should we try to hook TrustManagerImpl */
        if (hasTrustManagerImpl()) {
            /* TrustManagerImpl Hooks */
            /* external/conscrypt/src/platform/java/org/conscrypt/TrustManagerImpl.java */
            Log.d(TAG, "Hooking com.android.org.conscrypt.TrustManagerImpl for: " + currentPackageName);

            /* public void checkServerTrusted(X509Certificate[] chain, String authType) */
            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader,
                    "checkServerTrusted", X509Certificate[].class, String.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            return 0;
                        }
                    });

            /* public List<X509Certificate> checkServerTrusted(X509Certificate[] chain,
                                    String authType, String host) throws CertificateException */
            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader,
                    "checkServerTrusted", X509Certificate[].class, String.class,
                    String.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
                            return list;
                        }
                    });


            /* public List<X509Certificate> checkServerTrusted(X509Certificate[] chain,
                                    String authType, SSLSession session) throws CertificateException */
            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader,
                    "checkServerTrusted", X509Certificate[].class, String.class,
                    SSLSession.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            return new ArrayList<X509Certificate>();
                        }
                    });

            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader, "checkTrusted", X509Certificate[].class, String.class, SSLSession.class, SSLParameters.class, boolean.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return new ArrayList<X509Certificate>();
                }
            });


            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader, "checkTrusted", X509Certificate[].class, byte[].class, byte[].class, String.class, String.class, boolean.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return new ArrayList<X509Certificate>();
                }
            });
        }

    } // End Hooks

    /* Helpers */
    // Check for TrustManagerImpl class
    @SuppressLint("PrivateApi")
    private boolean hasTrustManagerImpl() {

        try {
            Class.forName("com.android.org.conscrypt.TrustManagerImpl");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    private javax.net.ssl.SSLSocketFactory getEmptySSLFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new ImSureItsLegitTrustManager()}, null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (KeyManagementException e) {
            return null;
        }
    }

    private void processXUtils(ClassLoader classLoader) {
        Log.d(TAG, "Hooking org.xutils.http.RequestParams.setSslSocketFactory(SSLSocketFactory) (3) for: " + currentPackageName);
        try {
            classLoader.loadClass("org.xutils.http.RequestParams");
            findAndHookMethod("org.xutils.http.RequestParams", classLoader, "setSslSocketFactory", javax.net.ssl.SSLSocketFactory.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.args[0] = getEmptySSLFactory();
                }
            });
            findAndHookMethod("org.xutils.http.RequestParams", classLoader, "setHostnameVerifier", HostnameVerifier.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.args[0] = new ImSureItsLegitHostnameVerifier();
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "org.xutils.http.RequestParams not found in " + currentPackageName + "-- not hooking");
        }
    }

    void processOkHttp(ClassLoader classLoader) {
        /* hooking OKHTTP by SQUAREUP */
        /* com/squareup/okhttp/CertificatePinner.java available online @ https://github.com/square/okhttp/blob/master/okhttp/src/main/java/com/squareup/okhttp/CertificatePinner.java */
        /* public void check(String hostname, List<Certificate> peerCertificates) throws SSLPeerUnverifiedException{}*/
        /* Either returns true or a exception so blanket return true */
        /* Tested against version 2.5 */
        Log.d(TAG, "Hooking com.squareup.okhttp.CertificatePinner.check(String,List) (2.5) for: " + currentPackageName);

        try {
            classLoader.loadClass("com.squareup.okhttp.CertificatePinner");
            findAndHookMethod("com.squareup.okhttp.CertificatePinner",
                    classLoader,
                    "check",
                    String.class,
                    List.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            return true;
                        }
                    });
        } catch (ClassNotFoundException e) {
            // pass
            Log.d(TAG, "OKHTTP 2.5 not found in " + currentPackageName + "-- not hooking");
        }

        //https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/CertificatePinner.java#L144
        Log.d(TAG, "Hooking okhttp3.CertificatePinner.check(String,List) (3.x) for: " + currentPackageName);

        try {
            classLoader.loadClass("okhttp3.CertificatePinner");
            findAndHookMethod("okhttp3.CertificatePinner",
                    classLoader,
                    "check",
                    String.class,
                    List.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            return null;
                        }
                    });
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "OKHTTP 3.x not found in " + currentPackageName + " -- not hooking");
            // pass
        }

        //https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
        try {
            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier");
            findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
                    classLoader,
                    "verify",
                    String.class,
                    javax.net.ssl.SSLSession.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            return true;
                        }
                    });
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "OKHTTP 3.x not found in " + currentPackageName + " -- not hooking OkHostnameVerifier.verify(String, SSLSession)");
            // pass
        }

        //https://github.com/square/okhttp/blob/parent-3.0.1/okhttp/src/main/java/okhttp3/internal/tls/OkHostnameVerifier.java
        try {
            classLoader.loadClass("okhttp3.internal.tls.OkHostnameVerifier");
            findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier",
                    classLoader,
                    "verify",
                    String.class,
                    java.security.cert.X509Certificate.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            return true;
                        }
                    });
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "OKHTTP 3.x not found in " + currentPackageName + " -- not hooking OkHostnameVerifier.verify(String, X509)(");
            // pass
        }

        //https://github.com/square/okhttp/blob/okhttp_4.2.x/okhttp/src/main/java/okhttp3/CertificatePinner.kt
        Log.d(TAG, "Hooking okhttp3.CertificatePinner.check(String,List) (4.2.0+) for: " + currentPackageName);

        try {
            classLoader.loadClass("okhttp3.CertificatePinner");
            findAndHookMethod("okhttp3.CertificatePinner",
                    classLoader,
                    "check$okhttp",
                    String.class,
                    "kotlin.jvm.functions.Function0",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            return null;
                        }
                    });
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "OKHTTP 4.2.0+ not found in " + currentPackageName + " -- not hooking");
            // pass
        }

    }

    @SuppressLint("TrustAllX509TrustManager")
    private static class ImSureItsLegitTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public List<X509Certificate> checkServerTrusted(X509Certificate[] chain, String authType, String host) throws CertificateException {
            ArrayList<X509Certificate> list = new ArrayList<>();
            return list;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class ImSureItsLegitHostnameVerifier implements HostnameVerifier {

        @SuppressLint("BadHostnameVerifier")
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}