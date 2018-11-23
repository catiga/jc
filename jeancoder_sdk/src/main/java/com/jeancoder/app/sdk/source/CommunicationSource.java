package com.jeancoder.app.sdk.source;

import java.util.Map;

import com.jeancoder.app.sdk.exception.CommunicationPowerGenerateFailedException;
import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.power.CommunicationPower;
import com.jeancoder.core.power.CommunicationPowerConfig;
import com.jeancoder.core.power.CommunicationPowerHandler;
import com.jeancoder.core.power.CommunicationWorkMode;
import com.jeancoder.core.power.PowerHandlerFactory;
import com.jeancoder.core.power.PowerName;
import com.jeancoder.core.power.localdns.UrlAddress;

public class CommunicationSource {

	/**
	 * 获取调用其他app能力
	 * @param appcode 另一个app的code 需要在application.properties中配置（例：namer.system.domain=http://localhost:8000,system为id）
	 * @return
	 */
	public static CommunicationPower getCommunicator(String appcode){
		CommunicationPowerConfig config = new CommunicationPowerConfig();
		JCRequest req = RequestSource.getRequest();
		String buff = req.getRequestURL().toString();
		UrlAddress ua = new UrlAddress(buff);
		config.setDomain(ua.requestDomain());
		config.setMode(CommunicationWorkMode.NETWORK);
		config.setId(appcode);
		config.setDefault(true);
		config.setDeploy(0);
		
		CommunicationPower power = null;
		try {
			CommunicationPowerHandler handler = (CommunicationPowerHandler)PowerHandlerFactory.generatePowerHandler(PowerName.COMMUNICATION, config,JCThreadLocal.getCode());
			power = handler;
		} catch (JeancoderException e) {
			throw new CommunicationPowerGenerateFailedException(appcode);
		}
		return power;
	}
	
	public static CommunicationPower getCommunicatorNative(String appcode){
		CommunicationPowerConfig config = new CommunicationPowerConfig();
		config.setMode(CommunicationWorkMode.NATIVE);
		config.setId(appcode);
		config.setDefault(true);
		config.setDeploy(0);
		
		CommunicationPower power = null;
		try {
			CommunicationPowerHandler handler = (CommunicationPowerHandler)PowerHandlerFactory.generatePowerHandler(PowerName.COMMUNICATION, config,JCThreadLocal.getCode());
			power = handler;
		} catch (JeancoderException e) {
			throw new CommunicationPowerGenerateFailedException(appcode);
		}
		return power;
	}
	
	
	public  static Object getParameter(String arg0) {
		Map<String,Object> parameterMap = JCThreadLocal.getNativeParameter();
		if (parameterMap == null) {
			return null;
		}
		return parameterMap.get(arg0);
	}
	
	public  static Map<String, Object>  getParameterMap() {
		return JCThreadLocal.getNativeParameter();
	}
	
}
