package com.jeancoder.root.container;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

import com.jeancoder.root.container.loader.AppClassLoader;
import com.jeancoder.root.container.loader.BootClassLoader;
import com.jeancoder.root.container.loader.TypeDefClassLoader;
import com.jeancoder.root.env.JCAPP;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class AppClassLoaderTest {

	@SuppressWarnings({ "resource", "rawtypes", "unchecked", "unused" })
	public static void main(String[] argc) throws Exception {
		BootClassLoader root_class = new BootClassLoader(Thread.currentThread().getContextClassLoader());
		JCAPP appins = new JCAPP();
		TypeDefClassLoader tyd = new TypeDefClassLoader(root_class, appins);
		AppClassLoader app_class = tyd.getAppClassLoader();
		File welcome = new File("/Users/jackielee/Documents/dev_workspace/92yp_app_project/src/main/java/com/jeancoder/project/entry/welcome.groovy");

		System.out.println(ClassLoader.getSystemClassLoader());
		
		String class_name_result = "com.jeancoder.core.result.Result";
		
		Class cz = app_class.findClass(class_name_result);
		System.out.println(cz);
		
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream("/Users/jackielee/Documents/dev_workspace/92yp_app_project/target/classes/com/jeancoder/project/entry/welcome.class"));  
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	    for(;;){  
	        int i = bis.read();  
	        if( i == -1){  
	            break;
	        }  
	        bos.write(i);  
	    }
	    bis.close();  
	    bos.close(); 
	    
	    //Class testGroovyClass = app_class.defineClass(null, bos.toByteArray());
	    
	    Class testGroovyClass = app_class.findClass("com.jeancoder.project.entry.welcome");
	    GroovyObject instance = (GroovyObject)testGroovyClass.newInstance();
	    System.out.println(instance);
	    GroovyShell shell = new GroovyShell(app_class);
	    
	    shell.evaluate(welcome);

	    Class[] loaded_class = app_class.getLoadedClasses();
        System.out.println(loaded_class);
        
	    Binding context = new Binding();
	    Constructor constructor = testGroovyClass.getConstructor(Binding.class);
        //Script script = (Script) constructor.newInstance(context);
	    Script script = (Script)testGroovyClass.newInstance();
        Object result = script.run();
        System.out.println(result);
        
        loaded_class = app_class.getLoadedClasses();
        System.out.println(loaded_class);
        
        while(true) {
        	TimeUnit.MILLISECONDS.sleep(500L);
        	shell.evaluate(welcome);
        	System.out.println(result);
        }
	}
	
}
