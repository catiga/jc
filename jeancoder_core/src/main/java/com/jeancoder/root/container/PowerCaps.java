package com.jeancoder.root.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jeancoder.core.common.Common;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.IPowerHandler;
import com.jeancoder.core.power.MemPower;
import com.jeancoder.core.power.QiniuPower;

public class PowerCaps {

	final Map<String, IPowerHandler> caps = new ConcurrentHashMap<>();
	
	public void add(String key, IPowerHandler powhs) {
		caps.put(key, powhs);
	}
	
	public DatabasePower getDatabase() {
		IPowerHandler pow = caps.get(Common.DATABASE);
		if(pow==null) {
			throw new RuntimeException("DB POWER NOT GETED");
		}
		return (DatabasePower)pow;
	}
	
	public MemPower getMemPower() {
		IPowerHandler pow = caps.get(Common.MEM_POWER);
		if(pow==null) {
			throw new RuntimeException("MEM POWER NOT GETED");
		}
		return (MemPower)pow;
	}
	
	public QiniuPower getQiniu() {
		IPowerHandler pow = caps.get(Common.QINIU_POWER);
		if(pow==null) {
			throw new RuntimeException("QINIU POWER NOT GETED");
		}
		return (QiniuPower)pow;
	}
}
