package com.song.trust.plugin;

import android.annotation.SuppressLint;
import android.net.http.X509TrustManagerExtensions;
import android.os.Build;

import com.song.trust.utils.XposedLogger;

import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.newInstance;

/**
 * Created by chensongsong on 2021/11/16.
 */
public class CertificateHook {

    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        XposedLogger.INSTANCE.log("CertificateHook.handleLoadPackage");
        String packageName = loadPackageParam.packageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            XposedHelpers.findAndHookMethod(X509TrustManagerExtensions.class, "checkServerTrusted",
                    X509Certificate[].class, String.class, String.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedLogger.INSTANCE.log("CertificateHook X509TrustManagerExtensions checkServerTrusted");
                            X509Certificate[] array = (X509Certificate[]) param.args[0];
                            return Arrays.asList(array);
                        }
                    });
        }
        XposedHelpers.findAndHookMethod("android.security.net.config.NetworkSecurityTrustManager",
                loadPackageParam.classLoader, "checkPins", List.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook NetworkSecurityTrustManager checkPins");
                        return null;
                    }
                });
        XposedHelpers.findAndHookConstructor(SSLSocketFactory.class, String.class, KeyStore.class, String.class, KeyStore.class,
                SecureRandom.class, HostNameResolver.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook SSLSocketFactory(String, KeyStore, String, KeyStore) for: " + packageName);
                        String algorithm = (String) param.args[0];
                        KeyStore keystore = (KeyStore) param.args[1];
                        String keystorePassword = (String) param.args[2];
                        SecureRandom random = (SecureRandom) param.args[4];
                        KeyManager[] keyManagers = null;
                        TrustManager[] trustManagers;
                        if (keystore != null) {
                            keyManagers = (KeyManager[]) XposedHelpers.callStaticMethod(SSLSocketFactory.class,
                                    "createKeyManagers", keystore, keystorePassword);
                        }
                        trustManagers = new TrustManager[]{new ImSureItsLegitTrustManager()};
                        XposedHelpers.setObjectField(param.thisObject, "sslcontext", SSLContext.getInstance(algorithm));
                        XposedHelpers.callMethod(XposedHelpers.getObjectField(param.thisObject, "sslcontext"),
                                "init", keyManagers, trustManagers, random);
                        XposedHelpers.setObjectField(param.thisObject, "socketfactory",
                                XposedHelpers.callMethod(XposedHelpers.getObjectField(param.thisObject, "sslcontext"),
                                        "getSocketFactory"));
                    }

                });


        findAndHookMethod("org.apache.http.conn.ssl.SSLSocketFactory", loadPackageParam.classLoader,
                "getSocketFactory", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook SSLSocketFactory.getSocketFactory for: " + packageName);
                        return (SSLSocketFactory) newInstance(SSLSocketFactory.class);
                    }
                });

        findAndHookMethod("org.apache.http.conn.ssl.SSLSocketFactory", loadPackageParam.classLoader,
                "isSecure", Socket.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook SSLSocketFactory.isSecure for: " + packageName);
                        return true;
                    }
                });
        findAndHookMethod("javax.net.ssl.TrustManagerFactory", loadPackageParam.classLoader,
                "getTrustManagers", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook TrustManagerFactory.getTrustManagers for: " + packageName);
                        if (hasTrustManagerImpl()) {
                            Class<?> cls = findClass("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader);
                            TrustManager[] managers = (TrustManager[]) param.getResult();
                            if (managers.length > 0 && cls.isInstance(managers[0])) {
                                return;
                            }
                        }
                        param.setResult(new TrustManager[]{new ImSureItsLegitTrustManager()});
                    }
                });

        findAndHookMethod("javax.net.ssl.HttpsURLConnection", loadPackageParam.classLoader,
                "setDefaultHostnameVerifier", HostnameVerifier.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook HttpsURLConnection.setDefaultHostnameVerifier for: " + packageName);
                        return null;
                    }
                });
        findAndHookMethod("javax.net.ssl.HttpsURLConnection", loadPackageParam.classLoader,
                "setSSLSocketFactory", javax.net.ssl.SSLSocketFactory.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook HttpsURLConnection.setSSLSocketFactory for: " + packageName);
                        return null;
                    }
                });

        findAndHookMethod("javax.net.ssl.HttpsURLConnection", loadPackageParam.classLoader,
                "setHostnameVerifier", HostnameVerifier.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook HttpsURLConnection.setHostnameVerifier for: " + packageName);
                        return null;
                    }
                });

        findAndHookMethod("javax.net.ssl.SSLContext", loadPackageParam.classLoader,
                "init", KeyManager[].class, TrustManager[].class, SecureRandom.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedLogger.INSTANCE.log("CertificateHook SSLContext.init for: " + packageName);
                        param.args[0] = null;
                        param.args[1] = new TrustManager[]{new ImSureItsLegitTrustManager()};
                        param.args[2] = null;
                    }
                });

        if (hasTrustManagerImpl()) {
            /* external/conscrypt/src/platform/java/org/conscrypt/TrustManagerImpl.java */
            XposedLogger.INSTANCE.log("CertificateHook TrustManagerImpl: " + packageName);
            /* public void checkServerTrusted(X509Certificate[] chain, String authType) */
            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader,
                    "checkServerTrusted", X509Certificate[].class, String.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedLogger.INSTANCE.log("CertificateHook TrustManagerImpl checkServerTrusted void: " + packageName);
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
                            XposedLogger.INSTANCE.log("CertificateHook TrustManagerImpl checkServerTrusted session: " + packageName);
                            return new ArrayList<X509Certificate>();
                        }
                    });


            /* public List<X509Certificate> checkServerTrusted(X509Certificate[] chain,
                                    String authType, SSLSession session) throws CertificateException */
            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader,
                    "checkServerTrusted", X509Certificate[].class, String.class,
                    SSLSession.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedLogger.INSTANCE.log("CertificateHook TrustManagerImpl checkServerTrusted List: " + packageName);
                            return new ArrayList<X509Certificate>();
                        }
                    });

            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader,
                    "checkTrusted", X509Certificate[].class, String.class, SSLSession.class,
                    SSLParameters.class, boolean.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedLogger.INSTANCE.log("CertificateHook TrustManagerImpl checkTrusted: " + packageName);
                            return new ArrayList<X509Certificate>();
                        }
                    });


            findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", loadPackageParam.classLoader,
                    "checkTrusted", X509Certificate[].class, byte[].class, byte[].class, String.class,
                    String.class, boolean.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedLogger.INSTANCE.log("CertificateHook TrustManagerImpl checkTrusted more: " + packageName);
                            return new ArrayList<X509Certificate>();
                        }
                    });
        }

    }

    @SuppressLint("PrivateApi")
    private boolean hasTrustManagerImpl() {
        try {
            Class.forName("com.android.org.conscrypt.TrustManagerImpl");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
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
            return new ArrayList<>();
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}