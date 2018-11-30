package com.jc.shell;

public class JCShellFac {

	private static ShellChannelHolder var = null;
	
	public static void init(ShellChannelHolder hods) {
		if(var==null) {
			synchronized(JCShellFac.class) {
				if(var==null) {
					var = hods;
				}
			}
		}
	}
	
	public static ShellChannelHolder instance() {
		if(var==null) {
			throw new RuntimeException("SYSTEM INIT ERROR WITH JC CHANNELS.");
		}
		return var;
	}
}
