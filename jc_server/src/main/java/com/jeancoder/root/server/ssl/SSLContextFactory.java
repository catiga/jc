package com.jeancoder.root.server.ssl;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
 
public class SSLContextFactory  {
    public static SSLContext getSslContext() throws Exception {
        char[] passArray = "123456".toCharArray();
        SSLContext sslContext = SSLContext.getInstance("TLSv1");
        KeyStore ks = KeyStore.getInstance("JKS");
        //加载keytool 生成的文件
        //FileInputStream inputStream = new FileInputStream("/Users/jackielee/Desktop/test/ssl/server.jck");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ssl/server.jck");
        ks.load(inputStream, passArray);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, passArray);
        sslContext.init(kmf.getKeyManagers(), null, null);
        inputStream.close();
        return sslContext;
    }
}
