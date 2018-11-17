package com.jeancoder.core.namer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MemoryPool {
	
	public static Map<String, Map<String,String>> MEMORY_POOL = new HashMap<String, Map<String,String>>();
	
	public static void addResource(String appCode, String resourceUrl, String rescontent){
		Map<String,String> resourceMap = MEMORY_POOL.get(appCode);
		if (resourceMap == null) {
			resourceMap = new LinkedHashMap<String,String>();
		}
		resourceMap.put(resourceUrl, rescontent);
		MEMORY_POOL.put(appCode, resourceMap);
	}
	
	public static String getResource(String appCode, String resourceUrl){
		Map<String,String> resourceMap = MEMORY_POOL.get(appCode);
		if (resourceMap == null) {
			return null;
		}
		return resourceMap.get(resourceUrl);
	}
	
	public static Map<String,String> getResourceMap(String appCode){
		return MEMORY_POOL.get(appCode);
	}
	
	public static void printlnAll(){
		Iterator<Entry<String, Map<String,String>>> memoryPool = MEMORY_POOL.entrySet().iterator();
		while (memoryPool.hasNext()) {  
			Map.Entry<String, Map<String,String>> entry = memoryPool.next();
			System.out.println("appCode：" + entry.getKey());
			Iterator<Entry<String,String>> resourceMap = entry.getValue().entrySet().iterator();
			while (resourceMap.hasNext()) {  
				Map.Entry<String, String> resourceBundleEntry = resourceMap.next();  
				System.out.println("ResourceUrl：" + resourceBundleEntry.getKey());
				System.out.println("Resource：" + resourceBundleEntry.getValue());
		    }  
	    }  
	}
	
	
}
