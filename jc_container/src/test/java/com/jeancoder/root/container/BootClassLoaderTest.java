package com.jeancoder.root.container;

import java.lang.reflect.Method;
import java.net.URL;

import com.jeancoder.root.container.loader.BootClassLoader;

public class BootClassLoaderTest {

	public static void main(String[] argc) throws Exception {
		//String s = "file://Users/jackielee/Documents/dev_workspace/92yp_app_project/target/classes";
		
		//s = System.getProperty("java.class.path");
		String s = "file://Users/jackielee/Documents/new_workspace/jc_root_space/jc_parent/jc_container/target/classes";
		URL[] urls = {new URL(s)};
		
		BootClassLoader bt = new BootClassLoader(Thread.currentThread().getContextClassLoader());
		
		System.out.println(Thread.currentThread().getContextClassLoader());
		System.out.println(ClassLoader.getSystemClassLoader());
		
		Class list = bt.loadClass("com.jeancoder.root.container.core.StandardVM");
		System.out.println(list);
		Object obj = list.newInstance();
		System.out.println(obj);
		list.getDeclaredMethod("onInit").invoke(obj);
		
//		String jarName = "jeancoder-core.jar";
//		File jarUrl = new File("file:/" + SYS_LIBS + "/" + jarName);
		
//		loadJar(SYS_LIBS + "/" + jarName);

		//bt.loadSysJars(SYS_LIBS);
		
		Class jc = bt.findClass("com.jeancoder.app.sdk.JC");
		System.out.println(jc);
		Class p = bt.findClass("com.jeancoder.core.namer.NamerApplication");
		Object param = p.newInstance();
		Class<?> a = bt.findClass("com.jeancoder.core.namer.InstallerFactory");
		Object ins = a.newInstance();
		Method m = a.getDeclaredMethod("generateInstaller", p);
		m.invoke(ins, param);
	}
	
}
