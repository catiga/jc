package com.jeancoder.core.cl;

import java.net.URL;
import java.net.URLClassLoader;

public abstract class JClassLoader extends URLClassLoader {

	public JClassLoader(URL[] urls) {
		super(urls);
	}

	public JClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
}
