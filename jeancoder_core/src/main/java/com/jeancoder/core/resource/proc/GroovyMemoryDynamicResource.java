package com.jeancoder.core.resource.proc;

import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.namer.MemoryPool;
import com.jeancoder.core.resource.type.ResourceType;

@SuppressWarnings("serial")
public class GroovyMemoryDynamicResource extends GroovyDynamicResource implements MemoryDynamicResource {
	
	private String appCode;
	
	public GroovyMemoryDynamicResource(String rescontent, ResourceType restype) {
		super(rescontent, restype);
	}

	@Override
	public Object getResult(){
		try{
			return getShell().evaluate(MemoryPool.getResource(appCode, getRescontent()));
		} catch (Exception e) {
			throw new SdkRuntimeException("", e);
		}
	}

	@Override
	public String getAppCode() {
		return appCode;
	}

	@Override
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	 
}
