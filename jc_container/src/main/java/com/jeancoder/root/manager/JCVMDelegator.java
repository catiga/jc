package com.jeancoder.root.manager;

import com.jeancoder.root.container.JCVM;
import com.jeancoder.root.container.core.StandardVM;

public class JCVMDelegator {

	private final static JCVMDelegator instance = new JCVMDelegator();
	
	public static final JCVMDelegator delegate() {
		return instance;
	}
	
	public final JCVM getVM() {
		return StandardVM.getVM();
	}
}
