package com.jeancoder.core.resource.proc;

import java.io.File;

import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.resource.type.ResourceType;

import groovy.lang.GroovyShell;

@SuppressWarnings("serial")
public class GroovyDynamicResource extends DynamicResource {
	
	private GroovyShell shell;
	
	public GroovyDynamicResource(String rescontent, ResourceType restype) {
		super(rescontent, restype);
	}

	public GroovyShell getShell() {
		return shell;
	}

	public void setShell(GroovyShell shell) {
		this.shell = shell;
	}
	
	@Override
	public Object getResult(){
		try{
			File file = new File(getRescontent());
			return shell.evaluate(file);
		} catch (Exception e) {
			throw new SdkRuntimeException("", e);
		}
	}
	
}
