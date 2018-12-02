package com.jeancoder.root.manager;

import java.lang.reflect.Method;

import com.jeancoder.root.env.ChannelContextWrapper;
import com.jeancoder.root.env.StandardVM;
import com.jeancoder.root.vm.JCVM;
import com.jeancoder.root.vm.JCVMDelegatorGroup;
import com.jeancoder.root.vm.VMDelegate;

public class JCVMDelegator implements VMDelegate {

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
	
	public ChannelContextWrapper getCurrentContext() {
		return CONTEXT_ENV.get();
	}
	
	public static final JCVMDelegator delegate() {
		JCVMDelegatorGroup group = JCVMDelegatorGroup.instance();
		if(group!=null&&group.getDelegator()==null) {
			synchronized(group) {
				Method method = null;
			    try {
			        method = JCVMDelegatorGroup.class.getDeclaredMethod("setDelegator", VMDelegate.class);
			    } catch (NoSuchMethodException | SecurityException e1) {
			        e1.printStackTrace();
			    }
			    boolean accessible = method.isAccessible();
			    try {
			        method.setAccessible(true);
			        method.invoke(group, instance);
			    } catch (Exception e) {
			        e.printStackTrace();
			    } finally {
			        method.setAccessible(accessible);
			    }
			}
		}
		return instance;
	}
	
	public String delegatedId() {
		return getVM().meId();
	}
	
	public final JCVM getVM() {
		return StandardVM.getVM();
	}
}
