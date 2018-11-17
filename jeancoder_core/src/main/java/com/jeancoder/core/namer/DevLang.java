package com.jeancoder.core.namer;

/**
 * 应用开发所用语言
 * @author wow zhang_gh@cpis.cn
 * @date 2018年5月28日
 */
public enum DevLang {
	// 正式环境下使用
	GROOVY(GroovyNamerParse.class),
	// 开发环境使用
	SDK_GROOVY(SdkGroovyNamerParse.class)
	;
	
	private Class<? extends NamerParse> clazz;
	private DevLang(Class<? extends NamerParse> clazz){
		this.clazz = clazz;
	}
	
	public NamerParse getInstance() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
	public static DevLang getDevLang(String devLang) {
		if ("groovy".equals(devLang)) {
			return GROOVY;
		}
		return null;
	}
}
