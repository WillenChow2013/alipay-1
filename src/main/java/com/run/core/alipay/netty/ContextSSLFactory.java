package com.run.core.alipay.netty;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

public class ContextSSLFactory {
    private static final SSLContext SSL_CONTEXT_S;

    private static final SSLContext SSL_CONTEXT_C;

    private static final String SSL_PASSWORD = "longshine";

    static {
        SSLContext sslContext = null;
        SSLContext sslContext2 = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext2 = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        try {
            if (getKeyManagersServer() != null && getTrustManagersServer() != null) {
                sslContext.init(getKeyManagersServer(), getTrustManagersServer(), null);
            }
            if (getKeyManagersClient() != null && getTrustManagersClient() != null) {
                sslContext2.init(getKeyManagersClient(), getTrustManagersClient(), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        sslContext.createSSLEngine().getSupportedCipherSuites();
        sslContext2.createSSLEngine().getSupportedCipherSuites();
        SSL_CONTEXT_S = sslContext;
        SSL_CONTEXT_C = sslContext2;
    }

    public ContextSSLFactory() {

    }

    public static SSLContext getSslContext() {
        return SSL_CONTEXT_S;
    }

    public static SSLContext getSslContext2() {
        return SSL_CONTEXT_C;
    }

    private static TrustManager[] getTrustManagersServer() {
        KeyStore ks = null;
        TrustManagerFactory keyFac = null;

        TrustManager[] kms = null;
        try {

            //获取证书路径
            Resource serverPrivateKeyResource = new ClassPathResource("tserver.keystore");
            File fileServerPrivateKey = serverPrivateKeyResource.getFile();

            // 获得KeyManagerFactory对象. 初始化位默认算法
            keyFac = TrustManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(fileServerPrivateKey), SSL_PASSWORD.toCharArray());
            keyFac.init(ks);
            kms = keyFac.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kms;
    }

    private static TrustManager[] getTrustManagersClient() {
        KeyStore ks = null;
        TrustManagerFactory keyFac = null;

        TrustManager[] kms = null;
        try {

            Resource trustKeyResource = new ClassPathResource("tserver.keystore");
            File fileTrusttKey = trustKeyResource.getFile();

            // 获得KeyManagerFactory对象. 初始化位默认算法
            keyFac = TrustManagerFactory.getInstance("SunX509");

            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(fileTrusttKey), SSL_PASSWORD.toCharArray());
            keyFac.init(ks);
            kms = keyFac.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kms;
    }

    private static KeyManager[] getKeyManagersServer() {
        KeyStore ks = null;
        KeyManagerFactory keyFac = null;

        KeyManager[] kms = null;
        try {
            Resource serverPrivateKeyResource = new ClassPathResource("kserver.keystore");
            File fileServerPrivateKey = serverPrivateKeyResource.getFile();
            // 获得KeyManagerFactory对象. 初始化位默认算法
            keyFac = KeyManagerFactory.getInstance("SunX509");

            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(fileServerPrivateKey), SSL_PASSWORD.toCharArray());
            keyFac.init(ks, SSL_PASSWORD.toCharArray());
            kms = keyFac.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kms;
    }

    private static KeyManager[] getKeyManagersClient() {
        KeyStore ks = null;
        KeyManagerFactory keyFac = null;

        KeyManager[] kms = null;
        try {
            Resource trustKeyResource = new ClassPathResource("tserver.keystore");
            File fileTrusttKey = trustKeyResource.getFile();
            // 获得KeyManagerFactory对象. 初始化位默认算法
            keyFac = KeyManagerFactory.getInstance("SunX509");

            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(fileTrusttKey), SSL_PASSWORD.toCharArray());
            keyFac.init(ks, SSL_PASSWORD.toCharArray());
            kms = keyFac.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kms;
    }
}
