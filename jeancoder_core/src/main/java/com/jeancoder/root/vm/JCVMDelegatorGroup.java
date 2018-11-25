package com.jeancoder.root.vm;

public class JCVMDelegatorGroup {

	private VMDelegate delegator;
	
	public VMDelegate getDelegator() {
		return delegator;
	}

	private final static JCVMDelegatorGroup instance = new JCVMDelegatorGroup();
	
	public static JCVMDelegatorGroup instance() {
		return instance;
	}
	
	private JCVMDelegatorGroup() {}
	
	protected void setDelegator(VMDelegate obj) {
		this.delegator = obj;
	}
}
