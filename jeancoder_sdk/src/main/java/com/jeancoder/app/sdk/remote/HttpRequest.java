package com.jeancoder.app.sdk.remote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;
import com.jeancoder.core.power.localdns.UrlAddress;
import com.jeancoder.core.security.BZX509TrustManager;

public class HttpRequest {
	
	static {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
	}
	
	private final static HttpRequest _instance_ = new HttpRequest();
	
	private HttpRequest() {}
	
	public static HttpRequest instance() {
		return _instance_;
	}
	
	JCLogger  Logger  = JCLoggerFactory.getLogger(HttpRequest.class);

	public String getResponseString(String url, String param, HttpMethod method, RequestCert cert) {
		if(cert==null) {
			return doRequest(url, param, method);
		} else {
			try {
				return doRequest(url, param, method, cert);
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	private String doRequest(String uri, String params, HttpMethod method, RequestCert cert) throws Exception {
		String cert_type = cert.cert_type;
		String cert_file = cert.cert_file_path;
		String cert_pass = cert.cert_passwd;
		System.out.println("cert_type=" + cert_type + ",cert_file=" + cert_file + ",cert_pass=" + cert_pass);
		KeyStore keyStore = KeyStore.getInstance(cert_type);
		FileInputStream instream = new FileInputStream(new File(cert_file));
		try {
			keyStore.load(instream, cert_pass.toCharArray());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		//SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, "10016225".toCharArray()).build();
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, cert_pass.toCharArray()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,
				new String[] { "TLSv1" },
				null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		
		HttpPost httppost = new HttpPost(uri);
		StringEntity se = new StringEntity(params);  
        httppost.setEntity(se);
        CloseableHttpResponse response = httpclient.execute(httppost);
		
        HttpEntity entity = response.getEntity();
		
        Logger.info("----------------------------------------");
        Logger.info(response.getStatusLine().getStatusCode() + "");
		if (entity != null) {
			Logger.info("Response content length: " + entity.getContentLength());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String text;
			StringBuffer ret_buffer = new StringBuffer();
			while ((text = bufferedReader.readLine()) != null) {
				ret_buffer.append(text);
			}
			return ret_buffer.toString();
		}
		return null;
	}
	
	private String doRequest(String uri, String params, HttpMethod method) {
		String fullUrl = uri;
		BufferedReader remote_ret = null;
		try {
			if(params != null) {
				if(method==HttpMethod.GET) {
					if(fullUrl.indexOf("?") != -1) {
						if(fullUrl.endsWith("?")) {
							fullUrl += params;
						}else {
							fullUrl += "&"+params;
						}
					}else {
						fullUrl += "?"+params;
					}
				}
			}
			UrlAddress ua = new UrlAddress(fullUrl);
			
			//URL url = new URL(ua.requestPath());
			URL url = new URL(ua.toString());
			HttpURLConnection conn = null;
			if(ua.isHttps()) {
				SSLSocketFactory  ssf= BZX509TrustManager.getSSFactory();
				conn = (HttpsURLConnection)url.openConnection();
				((HttpsURLConnection)conn).setSSLSocketFactory(ssf);
			} else {
				conn = (HttpURLConnection)url.openConnection();
			}
			
			// 注意 cookie 必须在设置 setRequestMethod 之前先设置
			//conn.setRequestProperty("mode", "NETWORK");
			//conn.setRequestProperty("Host", ua.getHost());
			
			if(method==HttpMethod.POST) {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				if(params!=null) {
					OutputStream os = conn.getOutputStream();
					os.write(params.getBytes(Charset.forName("UTF-8")));
					os.flush();					
				}
			} else {
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
			}
			
			int res_code = -1;
			
			if(ua.isHttps()) {
				res_code = ((HttpsURLConnection)conn).getResponseCode();
			} else {
				res_code = conn.getResponseCode();
			}
			
			if(res_code>=200&&res_code<=299) {
				remote_ret = new BufferedReader(new InputStreamReader(conn.getInputStream(),Charset.forName("UTF-8")));
			} else {
				if(ua.isHttps()) {
					remote_ret = new BufferedReader(new InputStreamReader(((HttpsURLConnection)conn).getErrorStream(),Charset.forName("UTF-8")));
				} else {
					remote_ret = new BufferedReader(new InputStreamReader((conn).getErrorStream(),Charset.forName("UTF-8")));
				}
			}
			
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line = remote_ret.readLine()) != null){
				buffer.append(line);
			}
			return buffer.toString();
		}catch(Exception e){
			Logger.error(e.getMessage(), e);
			throw new SdkRuntimeException(e.getMessage(), e);
		} finally {
			if(remote_ret!=null) {
				try {
					remote_ret.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
