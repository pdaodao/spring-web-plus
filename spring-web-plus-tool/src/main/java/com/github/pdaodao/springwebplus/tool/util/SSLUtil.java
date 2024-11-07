package com.github.pdaodao.springwebplus.tool.util;

import org.apache.http.conn.ssl.NoopHostnameVerifier;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SSLUtil {

    private static X509TrustManager TRUST_MANAGER = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    };


    private static SSLSocketFactory Factory;
    private static SSLContext sc;
    private static HostnameVerifier hostnameVerifier;

    static {
        try {
            hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            final TrustManager[] trustAllCerts = new TrustManager[1];
            trustAllCerts[0] = TRUST_MANAGER;
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            Factory = sc.getSocketFactory();

            HttpsURLConnection.setDefaultSSLSocketFactory(
                    Factory);
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        } catch (Exception e) {

        }
    }

    public static SSLSocketFactory getSslSocketFactory() {
        return Factory;
    }

    public static SSLContext getSc() {
        return sc;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }
}
