package com.jeancoder.root.container.loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.cl.JCLoader;

/**
 * sys lib loader
 * @author jackielee
 *
 */
public class BootClassLoader extends URLClassLoader implements JCLoader {
	
	protected static Logger logger = LoggerFactory.getLogger(BootClassLoader.class);
	
	final public static String SYS_LIBS = "/Users/jackielee/Desktop/logs";
	
	static {
		ClassLoader.registerAsParallelCapable();
	}
	
	public BootClassLoader(ClassLoader parent) {
		super(EMPTY_URL_ARRAY, parent);
		registerSysJars(SYS_LIBS);
	}
	
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> aClass = findLoadedClass(name);
		if(aClass != null){
			return aClass;
		}
		return super.findClass(name);	//for loaded class
//		return super.loadClass(name);	//for not loaded class
	}
	
	public void countPathUrls(String path, List<URL> waiting) {
		File basePath = new File(path);
	    if(basePath.exists()) {
	    	if(basePath.isDirectory()) {
		    	File[] files = basePath.listFiles();
		    	for(File f : files) {
		    		countPathUrls(f.getPath(), waiting);
		    	}
	    	} else {
		    	try {
	    			waiting.add(basePath.toURI().toURL());
	    		} catch(IOException ioex) {
	    		}
		    }
	    }
	}
	
	final public List<URL> registerSysJars(String jarPath) {
		List<URL> waiting = new ArrayList<>();
		countPathUrls(jarPath, waiting);
	    
	    Method method = null;
	    try {
	        method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
	    } catch (NoSuchMethodException | SecurityException e1) {
	        e1.printStackTrace();
	    }
	    boolean accessible = method.isAccessible();
	    try {
	        method.setAccessible(true);
	        for(URL jarFile : waiting) {
		        method.invoke(this, jarFile);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        method.setAccessible(accessible);
	    }
	    
	    return waiting;
	}

}
