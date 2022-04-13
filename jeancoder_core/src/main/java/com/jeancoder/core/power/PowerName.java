package com.jeancoder.core.power;

/**
 * 类型枚举
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月8日
 */
public enum PowerName {
	/**
	 * 关系型数据库
	 */
	DATABASE(DatabasePowerHandler.class)
	/**
	 * 应用间通信
	 */
	,COMMUNICATION(CommunicationPowerHandler.class)
	/**
	 * 七牛云存储
	 */
	,QINIU(QiniuPowerHandler.class)
	/**
	 * mem缓存
	 */
	,MEM(MemPowerHandler.class)
	;
	
	private Class<? extends PowerHandler> clazz;
	private PowerName(Class<? extends PowerHandler> clazz){
		this.clazz = clazz;
	}
	
	@SuppressWarnings("deprecation")
	public PowerHandler getInstance() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

	public Class<?> getClazz() {
		return clazz;
	}
}
