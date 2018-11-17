package com.jeancoder.root.container;

import java.lang.reflect.Constructor;

import com.jeancoder.app.sdk.source.jeancoder.SysSource;
import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.core.cl.CLHandler;
import com.jeancoder.root.container.core.BootContainer;
import com.jeancoder.root.container.loader.AppClassLoader;
import com.jeancoder.root.container.loader.BootClassLoader;
import com.jeancoder.root.container.loader.TypeDefClassLoader;
import com.jeancoder.root.container.model.JCAPP;

import groovy.lang.Binding;
import groovy.lang.Script;

public class AppClassLoaderTest4 {

	public static void main(String[] argc) throws Exception {
		BootClassLoader root_class = new BootClassLoader(Thread.currentThread().getContextClassLoader());
		JCAPP appins = new JCAPP();
		BootContainer bc = new BootContainer(appins);
		bc.onStart();

		TypeDefClassLoader tyd = new TypeDefClassLoader(root_class, appins);
		AppClassLoader app_class = tyd.getAppClassLoader();
		SysSource.setClassLoader(app_class);

		AppLoader gcl = (AppLoader) app_class;
		for (CLHandler c : gcl.getAppClasses()) {

			System.out.println(c.getBindClass().getName());
		}

		Class testGroovyClass = app_class.findClass("com.jeancoder.project.entry.project.test");

		Class[] loaded_class = app_class.getLoadedClasses();
		System.out.println(loaded_class);

		Binding context = new Binding();
		Constructor constructor = testGroovyClass.getConstructor(Binding.class);
		// Script script = (Script) constructor.newInstance(context);
		Script script = (Script) testGroovyClass.newInstance();
		Object result = script.run();
		System.out.println(result);

		loaded_class = app_class.getLoadedClasses();
		System.out.println(loaded_class);
	}

}