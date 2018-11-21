package com.jeancoder.root.manager;

import com.jeancoder.root.env.ChannelContextWrapper;
import com.jeancoder.root.env.StandardVM;
import com.jeancoder.root.vm.JCVM;

public class JCVMDelegator {

	private final static JCVMDelegator instance = new JCVMDelegator();
	
	static ThreadLocal<ChannelContextWrapper> CONTEXT_ENV = new ThreadLocal<ChannelContextWrapper>();
	
	public static void bindContext(ChannelContextWrapper context) {
		CONTEXT_ENV.set(context);
	}
	
	public static ChannelContextWrapper getContext() {
		return CONTEXT_ENV.get();
	}
	
	public static void releaseContext() {
		CONTEXT_ENV.remove();
	}
	
	public static final JCVMDelegator delegate() {
		return instance;
	}
	
	public final JCVM getVM() {
		return StandardVM.getVM();
	}
}
