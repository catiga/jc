package com.jeancoder.app.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeancoder.app.sdk.source.CommunicationSource;
import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.power.CommunicationParam;
import com.jeancoder.core.power.CommunicationPower;
import com.jeancoder.core.util.JackSonBeanMapper;

public class JCInternalMethod implements JCMethod {

	public static Object param(String name) {
		return CommunicationSource.getParameter(name);
	}
	
	public static <T> T call(final Class<T> claz, String app, String endpoint, Map<String, Object> param_dic) {
		CommunicationPower systemCaller = CommunicationSource.getCommunicatorNative(app);
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic!=null&&!param_dic.isEmpty()) {
			for(String k : param_dic.keySet()) {
				//CommunicationParam param = new CommunicationParam(k, param_dic.get(k));
				
				CommunicationParam param = new CommunicationParam(k, convert(param_dic.get(k)));
				params.add(param);
			}
		}
		try {
			String ret = systemCaller.doworkAsString(endpoint, params);
			T obj = JackSonBeanMapper.fromJson(ret, claz);
			return obj;
		} catch (JeancoderException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static String call(String app, String endpoint, Map<String, Object> param_dic) {
		CommunicationPower systemCaller = CommunicationSource.getCommunicatorNative(app);
		List<CommunicationParam> params = new ArrayList<CommunicationParam>();
		if(param_dic!=null&&!param_dic.isEmpty()) {
			for(String k : param_dic.keySet()) {
				//CommunicationParam param = new CommunicationParam(k, param_dic.get(k));
				
				CommunicationParam param = new CommunicationParam(k, convert(param_dic.get(k)));
				params.add(param);
			}
		}
		try {
			String ret = systemCaller.doworkAsString(endpoint, params);
			return ret;
		} catch (JeancoderException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object convert(Object tmp) {
		if(tmp==null) {
			return "";
		}
		//
		if(tmp instanceof Integer) {
			return tmp;
		} else if(tmp instanceof Boolean) {
			return tmp;
		} else if(tmp instanceof Byte) {
			return tmp;
		} else if(tmp instanceof Character) {
			return tmp;
		} else if(tmp instanceof Float) {
			return tmp;
		} else if(tmp instanceof Double) {
			return tmp;
		} else if(tmp instanceof Short) {
			return tmp;
		} else if(tmp instanceof Long) {
			return tmp;
		} else if(tmp instanceof String) {
			return tmp;
		} 
		//开始判断是否为集合
		if(tmp.getClass().isArray()) {
			return JackSonBeanMapper.listToJson(Arrays.asList(tmp));
		} else if(tmp instanceof List) {
			return JackSonBeanMapper.listToJson((List)tmp);
		} else if(tmp instanceof Map) {
			return JackSonBeanMapper.mapToJson((Map)tmp);
		} else {
			return JackSonBeanMapper.toJson(tmp);
		}
	}
	
	public static void main(String[] argc) {
		Object i = 0;
		i = new Object[]{"tmp", 123};
		
		System.out.println(JackSonBeanMapper.listToJson(Arrays.asList(i)));
		
		System.out.println(i.getClass().isArray());
		i = new ArrayList<>();
		System.out.println(i instanceof Collection);
		i = new HashMap<>();
		System.out.println(i instanceof Map);
		
	}
}
