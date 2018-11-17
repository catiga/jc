package com.jeancoder.root.container;

import com.jeancoder.root.container.core.BootContainer;
import com.jeancoder.root.container.model.JCAPP;

public class AppClassLoaderTest3 {

	public static void main(String[] argc) throws Exception {
//
//		String app_base = "/Users/jackielee/Documents/dev_workspace/92yp_app_project/" + "target/classes";
//		
//		List<URL> waiting = new ArrayList<>();
//		waiting.add(new File(app_base).toURI().toURL());
//		
//		URLClassLoader url_classloader = new URLClassLoader(new URL[0]);
//
//		Method method = null;
//	    try {
//	        method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//	    } catch (NoSuchMethodException | SecurityException e1) {
//	        e1.printStackTrace();
//	    }
//	    boolean accessible = method.isAccessible();
//	    try {
//	        method.setAccessible(true);
//	        for(URL jarFile : waiting) {
//		        method.invoke(url_classloader, jarFile);
//	        }
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    } finally {
//	        method.setAccessible(accessible);
//	    }
//	    
//		Class cls = url_classloader.loadClass("com.jeancoder.project.ready.common.RetObj");
//		System.out.println(cls);
//
//		Class[] clsses = cls.getClasses();
//		System.out.println(clsses);
		

		JCAPP appins = new JCAPP();
		BootContainer bc = new BootContainer(appins);
		bc.onStart();
	}

}