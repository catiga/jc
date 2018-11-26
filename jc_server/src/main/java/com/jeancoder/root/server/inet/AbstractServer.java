package com.jeancoder.root.server.inet;

import com.jeancoder.root.manager.JCVMDelegator;
import com.jeancoder.root.vm.JCVM;

public abstract class AbstractServer implements JCServer {

	private JCVM jcvm;
	
	protected AbstractServer() {
		this.jcvm = JCVMDelegator.delegate().getVM();
	}

	public JCVM getVM() {
		return jcvm;
	}

}
