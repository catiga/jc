package com.jeancoder.core.power;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.http.JCCookie;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;
import com.jeancoder.core.power.localdns.UrlAddress;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.security.BZX509TrustManager;
import com.jeancoder.core.util.FileUtil;
import com.jeancoder.core.util.JackSonBeanMapper;
import com.jeancoder.core.util.StringUtil;
import com.jeancoder.root.container.ContainerContextEnv;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.container.JCAppContainer;
import com.jeancoder.root.env.RunnerResult;
import com.jeancoder.root.vm.JCVMDelegatorGroup;
import com.jeancoder.root.vm.VMDelegate;

/**
 * 用于两个应用间互相通信
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月8日
 */
public class CommunicationPowerHandler extends PowerHandler implements CommunicationPower{
	static {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
	}
	
	JCLogger  Logger  = JCLoggerFactory.getLogger(CommunicationPowerHandler.class);
	//域名
	private String domain;
	//调用方式
	private CommunicationWorkMode mode;
	//部署模式
	private Integer deploy = 0;
	
	//临时解决方案  用来标识访问内部资源
	private String rootPath = "";

	@Override
	public void init(PowerConfig config) throws JeancoderException {
		CommunicationPowerConfig cfg = (CommunicationPowerConfig)config;
		this.domain = cfg.getDomain();
		this.mode = cfg.getMode();
		this.deploy = cfg.getDeploy()>0?1:0;
	}
	
	/**
	 * 通信模式为网络时 默认GET
	 * @param path
	 * @param params
	 */
	public String doworkAsString(String path, List<CommunicationParam> params) {
		if (path.indexOf("/") == 0) {
			path = path.replaceFirst("/", "");
		}
		if (!StringUtil.isEmpty(getRootPath())) {
			path = getRootPath() + "/" + path ;
		}
		if (CommunicationWorkMode.NATIVE.equals(mode)) {
			return doRequest(path, params);
		} 
		return doRequest(path, params, CommunicationMethod.POST);
	}
	
	/**
	 * 通信模式为网络时 可以制定GET或POST
	 * @param path
	 * @param params
	 * @param method
	 */
	public String doworkAsString(String path,List<CommunicationParam> params,CommunicationMethod method) {
		if(method == null || CommunicationMethod.GET.equals(method)) {
			//return doGetAsString(path,params);
			return doRequest(path, params, CommunicationMethod.GET);
		}else if(CommunicationMethod.POST.equals(method)) {
			//return doPostAsString(path,params);
			return doRequest(path, params, CommunicationMethod.POST);
		}
		return null;
	}
	
	private String doRequest(String path,List<CommunicationParam> params, CommunicationMethod method) {
		String fullUrl = FileUtil.pathsJoint(domain,this.getId(),path);
		BufferedReader remote_ret = null;
		String schema = ContainerContextEnv.getSchema();
		StringBuffer paramsStr = new StringBuffer();
		
		if(params != null) {
			
			for(CommunicationParam param : params) {
				if(paramsStr.length() > 0) {
					paramsStr.append("&");
				}
				String param_value = null;
				try {
					param_value = URLEncoder.encode(String.valueOf(param.getValue()), "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(param_value!=null) {
					paramsStr.append(param.getName()+"="+param_value);
				}
			}
			if(method==CommunicationMethod.GET) {
				if(fullUrl.indexOf("?") != -1) {
					if(fullUrl.endsWith("?")) {
						fullUrl += paramsStr;
					}else {
						fullUrl += "&"+paramsStr;
					}
				}else {
					fullUrl += "?"+paramsStr;
				}
			}
		}
		
		try {
			System.out.println("==========req_api_url=" + fullUrl);
			UrlAddress ua = new UrlAddress(fullUrl);
			System.out.println("***********original_protocol_is:::" + ua.getProtocol());
			if(schema!=null) {
				ua.changeProto(schema);
				System.out.println("***********change_protocol_to:::" + schema);
			}
			URL url = null;
			if(method==CommunicationMethod.POST) {
				url = new URL(ua.requestPath(this.deploy));
			} else {
				url = new URL(ua.toString(this.deploy));
			}
			HttpURLConnection conn = null;
			if(ua.isHttps()) {
				SSLSocketFactory  ssf= BZX509TrustManager.getSSFactory();
				conn = (HttpsURLConnection)url.openConnection();
				((HttpsURLConnection)conn).setSSLSocketFactory(ssf);
			} else {
				conn = (HttpURLConnection)url.openConnection();
			}
			
			// 注意 cookie 必须在设置 setRequestMethod 之前先设置
			conn.setRequestProperty("Cookie", getCookies());
			conn.setRequestProperty("mode", mode.toString());
			if(this.deploy==1) {
				conn.setRequestProperty( "User-agent", "Mozilla/9.0 (compatible; MSIE 10.0; Windows NT 8.1; .NET CLR 2.0.50727)" );
				conn.setRequestProperty("Host", ua.getHost());
			}
			
			if(method==CommunicationMethod.POST) {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				//if(ua.getParam()!=null) {
				if(paramsStr!=null&&paramsStr.length()>0) {
					OutputStream os = conn.getOutputStream();
					//os.write(ua.getParam().getBytes(Charset.forName("UTF-8")));
					os.write(paramsStr.toString().getBytes(Charset.forName("UTF-8")));
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
			Logger.error(fullUrl, e);
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
	
	
	/**
	 * 
	 * @param path
	 * @param params
	 * @return
	 */
	private String doRequest(String path, List<CommunicationParam> params) {
		Map<String, Object> parameterMap = JCThreadLocal.getNativeParameter();
//		JCRequest reqeust = JCThreadLocal.getRequest();
//		JCResponse response = JCThreadLocal.getResponse();
//		Result  result =  JCThreadLocal.getResult();
//		
//		JCThreadLocal.setRequest(null);
//		JCThreadLocal.setResponse(null);
		JCThreadLocal.setNativeParameter(getParameterMap(params));
//		JCThreadLocal.setResponse(null);
//		JCThreadLocal.setCode(this.getId());
		
		VMDelegate wd = JCVMDelegatorGroup.instance().getDelegator();	
		ContainerMaps cm = wd.getVM().getContainers();
		try {
			Enumeration<JCAppContainer> container  = cm.getByCode(this.getId());
			JCAppContainer jcAppContainer = container.nextElement();
			RunnerResult<Result> runnerResult = jcAppContainer.execute(path);
			return JackSonBeanMapper.toJson(runnerResult.getResult().getData());
		} catch (Exception e) {
			Logger.error("",e);
			throw e;
		} finally {
//			JCThreadLocal.setResult(result);
			JCThreadLocal.setNativeParameter(parameterMap);
//			JCThreadLocal.setRequest(reqeust);
//			JCThreadLocal.setResponse(response);
		} 
	}
	

	
	
	
	/**
	 * 通过htttp协议跳转的时候需要把cookies带着访问
	 * @return
	 */
	private String getCookies() {
		JCRequest request = JCThreadLocal.getRequest();
		if (request == null) {
			return "";
		}
		JCCookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (JCCookie jcCookie : cookies) {
			if (sb.length() != 0) {
				sb.append(";");
			}
			sb.append(jcCookie.getName() + "=" + jcCookie.getValue());
		}
		return sb.toString();
	}
	
	
	//TODO 应该根据模式判断的 
	private String doGetAsString(String path,List<CommunicationParam> params) {
		String fullUrl = FileUtil.pathsJoint(domain,this.getId(),path);
		try {
			if(params != null) {
				StringBuffer paramsStr = new StringBuffer();
				for(CommunicationParam param : params) {
					if(paramsStr.length() > 0) {
						paramsStr.append("&");
					}
					paramsStr.append(param.getName()+"="+URLEncoder.encode(String.valueOf(param.getValue()), "utf-8"));
				}
				if(fullUrl.indexOf("?") != -1) {
					if(fullUrl.endsWith("?")) {
						fullUrl += paramsStr;
					}else {
						fullUrl += "&"+paramsStr;
					}
				}else {
					fullUrl += "?"+paramsStr;
				}
			}
			
			UrlAddress ua = new UrlAddress(fullUrl);
			
			URL url = new URL(ua.toString(this.deploy));
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 注意 cookie 必须在设置 setRequestMethod 之前先设置
			conn.setRequestProperty("Cookie", getCookies());
			
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			conn.setRequestProperty("mode", mode.toString());
			conn.setRequestProperty("Host", ua.getHost());
			
			long start = System.currentTimeMillis();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));  
			StringBuilder sb = new StringBuilder();
			long end = System.currentTimeMillis();
			if((end - start)>1*1000) {
				System.out.println("not normal=" + (end-start)/1000 + "秒");
				System.out.println(fullUrl);
			}
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			return sb.toString();
		}catch(Exception e){
			Logger.error(e.getMessage(), e);
			throw new SdkRuntimeException(e.getMessage(), e);
		}
	}
	//TODO 应该根据模式判断的 
	@SuppressWarnings("unused")
	private String doPostAsString(String path,List<CommunicationParam> params) {
		return doGetAsString(path,params);
	}
	
	
	private Map<String,Object> getParameterMap(List<CommunicationParam> params) {
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		if (params == null || params.isEmpty()) {
			return paramsMap;
		}
		for (CommunicationParam param : params) {
			paramsMap.put(param.getName(), param.getValue());
		}
		return paramsMap;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}
