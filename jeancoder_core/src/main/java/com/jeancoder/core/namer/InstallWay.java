package com.jeancoder.core.namer;

public enum InstallWay {
	//内存
	MEMORY(MemoryNamerLoad.class)
	//磁盘
	,DISK(DiskNamerLoad.class);
	
	private Class<? extends NamerLoad> clazz;
	private InstallWay(Class<? extends NamerLoad> clazz){
		this.clazz = clazz;
	}
	
	@SuppressWarnings("deprecation")
	public NamerLoad getInstance() throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
}
