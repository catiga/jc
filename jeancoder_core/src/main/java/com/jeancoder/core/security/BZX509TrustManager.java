package com.jeancoder.core.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BZX509TrustManager implements X509TrustManager {

	public BZX509TrustManager(){}
	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static SSLSocketFactory getSSFactory() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException{
		TrustManager[] tm = { new BZX509TrustManager()};
		//SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tm, new java.security.SecureRandom());
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		return  ssf;
	}

}
