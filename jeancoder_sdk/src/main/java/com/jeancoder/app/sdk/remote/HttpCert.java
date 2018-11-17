package com.jeancoder.app.sdk.remote;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpCert {
	/**
	 * 获得KeyStore.
	 * 
	 * @param keyStorePath
	 *            密钥库路径
	 * @param password
	 *            密码
	 * @return 密钥库
	 * @throws Exception
	 */
	public static KeyStore getKeyStore(String cert_type, String password, String keyStorePath) throws Exception {
		// 实例化密钥库 KeyStore用于存放证书，创建对象时 指定交换数字证书的加密标准
		// 指定交换数字证书的加密标准
		// KeyStore ks = KeyStore.getInstance("PKCS12");
		KeyStore ks = KeyStore.getInstance(cert_type);
		// 获得密钥库文件流
		FileInputStream is = new FileInputStream(keyStorePath);
		// 加载密钥库
		ks.load(is, password.toCharArray());
		// 关闭密钥库文件流
		is.close();
		return ks;
	}

	/**
	 * 获得SSLSocketFactory.
	 * 
	 * @param password
	 *            密码
	 * @param keyStorePath
	 *            密钥库路径
	 * @param trustStorePath
	 *            信任库路径
	 * @return SSLSocketFactory
	 * @throws Exception
	 */
	public static SSLContext getSSLContext(String cert_type, String password, String keyStorePath,
			String trustStorePath) throws Exception {
		// 实例化密钥库 KeyManager选择证书证明自己的身份
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// 获得密钥库
		KeyStore keyStore = getKeyStore(cert_type, password, keyStorePath);
		// 初始化密钥工厂
		keyManagerFactory.init(keyStore, password.toCharArray());
		KeyManager[] keys = keyManagerFactory.getKeyManagers();

		TrustManager[] trusts = null;
		if (trustStorePath != null) {
			// 实例化信任库 TrustManager决定是否信任对方的证书
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			// 获得信任库
			KeyStore trustStore = getKeyStore(cert_type, password, trustStorePath);
			// 初始化信任库
			trustManagerFactory.init(trustStore);
			trusts = trustManagerFactory.getTrustManagers();
		} else {
			X509TrustManager x509TrustManager = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}
			};
			trusts = new TrustManager[] { x509TrustManager };
		}

		// 实例化SSL上下文
		SSLContext ctx = SSLContext.getInstance("TLSv1");
		// 初始化SSL上下文
		ctx.init(keys, trusts, null);
		// 获得SSLSocketFactory
		return ctx;
	}

	/**
	 * 初始化HttpsURLConnection.
	 * 
	 * @param password
	 *            密码
	 * @param keyStorePath
	 *            密钥库路径
	 * @param trustStorePath
	 *            信任库路径
	 * @throws Exception
	 */
	public static void initHttpsURLConnection(String cert_type, String password, String keyStorePath,
			String trustStorePath) throws Exception {
		// 声明SSL上下文
		SSLContext sslContext = null;
		// 实例化主机名验证接口
		// HostnameVerifier hnv = new MyHostnameVerifier();
		try {
			sslContext = getSSLContext(cert_type, password, keyStorePath, trustStorePath);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		// HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	/**
	 * 发送请求.
	 * 
	 * @param httpsUrl
	 *            请求的地址
	 * @param xmlStr
	 *            请求的数据
	 */
	public static void post(String httpsUrl, String xmlStr) {
		HttpsURLConnection urlCon = null;
		try {
			urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			urlCon.setRequestMethod("POST");
			urlCon.setRequestProperty("Content-Length", String.valueOf(xmlStr.getBytes().length));
			urlCon.setUseCaches(false);
			// 设置为gbk可以解决服务器接收时读取的数据中文乱码问题
			urlCon.getOutputStream().write(xmlStr.getBytes("gbk"));
			urlCon.getOutputStream().flush();
			urlCon.getOutputStream().close();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试方法.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// 密码
		String password = "1513094861";
		// 密钥库
		String keyStorePath = "/Users/jackielee/Desktop/tmp/apiclient_cert.p12";
		// 信任库
		String trustStorePath = keyStorePath;
		// 本地起的https服务
		String httpsUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
		// 传输文本
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruitShop><fruits><fruit>&lt;kind>萝卜</kind></fruit><fruit><kind>菠萝</kind></fruit></fruits></fruitShop>";
		HttpCert.initHttpsURLConnection("PKCS12", password, keyStorePath, trustStorePath);
		// 发起请求
		HttpCert.post(httpsUrl, xmlStr);
	}
}