package com.jeancoder.core.namer;

/**
 * 应用获取方式
 * @author wow zhang_gh@cpis.cn
 * @date 2018年5月28日
 */
public enum FetchWay {
	//http网络地址 流
	//NETWORK,
	//本地磁盘路径 zip包
	LOCAL(LocalNamerFetch.class)
	//开发环境SDK 文件夹
	,SDK(SdkNamerFetch.class)
	// 从缓存中获取InputStream
	,MEMORY(MemoryNamerFetch.class)
	;
	
	
	private Class<? extends NamerFetch> clazz;
	private FetchWay(Class<? extends NamerFetch> clazz){
		this.clazz = clazz;
	}
	
	public NamerFetch getInstance() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
	public static FetchWay getFetchWay(String fetchWay) {
		if ("memory".equals(fetchWay)) {
			return MEMORY;
		}
		return null;
	}
}
