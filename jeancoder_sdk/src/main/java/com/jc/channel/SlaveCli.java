package com.jc.channel;

import java.util.Enumeration;

import com.jc.shell.JCShellFac;
import com.jc.shell.ShellServer;

public class SlaveCli {
	
	static final SlaveCli instance = new SlaveCli();
	
	public static SlaveCli instance() {
		return instance;
	}
	
	public Enumeration<ShellServer> servers() {
		return JCShellFac.instance().servers();
	}
}
