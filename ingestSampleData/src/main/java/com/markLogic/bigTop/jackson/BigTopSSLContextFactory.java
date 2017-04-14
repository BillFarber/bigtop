package com.markLogic.bigTop.jackson;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BigTopSSLContextFactory {
    private static SSLContext sslContext = null;
    
    public static SSLContext getBigTopSSLContext() {
    	if (sslContext == null) {
    		createBigTopSSLContext();
    	}
    	return sslContext;
    }
    
    private static void createBigTopSSLContext() {
        try {
            sslContext = SSLContext.getInstance("TLSv1");
            // no client certs
            KeyManager[] keyManagers = null;

            // Trust anyone.
            TrustManager[] trustManagers;
            trustManagers = new TrustManager[] {
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
            };
            sslContext.init(keyManagers, trustManagers, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
