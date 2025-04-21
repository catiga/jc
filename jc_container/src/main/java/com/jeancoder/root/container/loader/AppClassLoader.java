package com.jeancoder.root.container.loader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.cl.AppLoader;
import com.jeancoder.core.cl.CLHandler;
import com.jeancoder.core.cl.JCLoader;
import com.jeancoder.root.container.JCAppContainer;

import groovy.lang.GroovyClassLoader;

public class AppClassLoader extends GroovyClassLoader implements JCLoader, AppLoader {

	protected static Logger logger = LoggerFactory.getLogger(BootClassLoader.class);
	
	TypeDefClassLoader parent = null;

	final public static String BIN_TARGET = "target/classes";
	
	final public static String SOURCE_TARGET = "src/main/java";
	
	protected final Map<String, CLHandler> resourceEntries = new ConcurrentHashMap<>();
	
	private List<URL> localRepositories = new ArrayList<>();
	
	final List<CLHandler> APP_CL_HANDLERS = new ArrayList<>();

	@Override
	public JCAppContainer getContextEnv() {
		return parent.getContextEnv();
	}
	
	protected AppClassLoader(TypeDefClassLoader parent) {
		this(EMPTY_URL_ARRAY, parent);
	}

	private void addURLCompatible(ClassLoader classLoader, URL url) throws Exception {
		try {
			// 尝试访问 "ucp" 字段（URLClassPath），适用于 JDK 9+
			Field ucpField = null;
			Class<?> clazz = classLoader.getClass();
			while (clazz != null) {
				try {
					ucpField = clazz.getDeclaredField("ucp");
					break;
				} catch (NoSuchFieldException e) {
					clazz = clazz.getSuperclass(); // 向上找
				}
			}

			if (ucpField == null) {
				throw new IllegalStateException("Cannot find 'ucp' field in classloader hierarchy.");
			}

			ucpField.setAccessible(true);
			Object ucp = ucpField.get(classLoader);

			Method addURL = ucp.getClass().getDeclaredMethod("addURL", URL.class);
			addURL.setAccessible(true);
			addURL.invoke(ucp, url);
		} catch (Exception e) {
			logger.error("Failed to inject URL into classloader. You may need to add VM option: --add-opens java.base/java.net=ALL-UNNAMED", e);
			throw e;
		}
	}
	
	public void init() throws Exception {
		String bin_target = parent.getAppins().getBin_base();
		if(bin_target==null) {
			bin_target = BIN_TARGET;
		}
		File loading_path = new File(parent.getAppins().getApp_base() + "/" + bin_target);
		if(loading_path.exists()&&loading_path.isDirectory()&&loading_path.canRead()) {
			this.localRepositories.add(loading_path.toURI().toURL());
		}
		if(!this.localRepositories.isEmpty()) {
			for (URL jar : localRepositories) {
				try {
					this.addURL(jar);
					logger.info("add url for classloader success.");
				} catch (Exception e) {
					logger.error("Current ClassLoader is not URLClassLoader. Skipping dynamic addURL.", e);
				}
			}
//			Method method = null;
//		    try {
//		        method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//		    } catch (NoSuchMethodException | SecurityException e1) {
//		        e1.printStackTrace();
//		    }
//		    @SuppressWarnings("deprecation")
//			boolean accessible = method.isAccessible();
//		    try {
//		        method.setAccessible(true);
//		        for(URL jarFile : localRepositories) {
//			        method.invoke(this, jarFile);
//		        }
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		    } finally {
//		        method.setAccessible(accessible);
//		    }
		}

		this.registerAppClasses();
		//this.addClasspath(parent.getAppins().getApp_base() + "/" + BIN_TARGET);
		//this.registerAppClasses(parent.getAppins().getApp_base() + "/" + BIN_TARGET);
	}
	
	@Override
    public URL[] getURLs() {
        ArrayList<URL> result = new ArrayList<>();
        result.addAll(localRepositories);
        result.addAll(Arrays.asList(super.getURLs()));
        return result.toArray(new URL[result.size()]);
    }
	
	public AppClassLoader(URL[] urls, TypeDefClassLoader parent) {
		super(parent);
		if(parent==null || !(parent instanceof TypeDefClassLoader)) {
			throw new RuntimeException("invalid parent classloader, please restart.");
		}
		this.parent = parent;
		try {
			init();
		} catch (Exception e1) {
			logger.error("appclassloader init error:", e1);
		}
	}

	@Override 
	public Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> claz = findLoadedClass(name);
		if (name.contains("JC.")) {
			logger.info("here");
		}
		if (claz == null) {
			try {
				claz = parent.findClass(name);
			} catch (ClassNotFoundException clz_not) {
				//pass this exception to parent level
				logger.debug("ignore error, pass to parent. {}", name);
			}
		}
		if (claz == null) {
			claz = super.findClass(name);
		}
		return claz;
	}
	
	protected void registerAppClasses() {
		for(URL clp : this.localRepositories) {
			registerAppClasses(clp.getFile(), clp.getFile());
		}
	}

	int i = 0;
	
	public CLHandler[] getAppClasses() {
		if(APP_CL_HANDLERS.isEmpty()) {
			synchronized (APP_CL_HANDLERS) {
				if(APP_CL_HANDLERS.isEmpty()) {
					resourceEntries.forEach((k, v) ->{
						APP_CL_HANDLERS.add(v);
					});
				}
			}
		}
		return APP_CL_HANDLERS.toArray(new CLHandler[APP_CL_HANDLERS.size()]);
	}

	protected void registerAppClasses(String root_path, String loading_path) {
		File pathFile = new File(loading_path);
		if (pathFile.exists()&&pathFile.canRead()) {
			if (pathFile.isDirectory()) {
				for (File file_item : pathFile.listFiles()) {
					registerAppClasses(root_path, file_item.getPath());
				}
			} else {
				
				if (pathFile.getPath().endsWith(".class")) {
					try {
						ioClass(root_path, pathFile);
					} catch (IOException e) {
					}
				} else if (pathFile.getPath().endsWith(".xml") || pathFile.getPath().endsWith(".json")
						|| pathFile.getPath().endsWith(".conf") || pathFile.getPath().endsWith(".properties")) {
					String line = null;
					try {
						BufferedReader buf = new BufferedReader(new FileReader(pathFile));
						StringBuilder content = new StringBuilder();
						while((line = buf.readLine())!=null) {
							content.append(line+"\n\r");
						}
						this.getContextEnv().addConfig(pathFile.getName(), content.toString());
						buf.close();
					}catch(Exception e){
					}
				}
			}
		}
	}
	
	protected void ioClass(String root_path, File fio) throws IOException {
		String class_file = fio.getPath().substring(root_path.length());
		class_file = class_file.substring(0, class_file.lastIndexOf(".")).replace("/", ".");
		try {
			Class<?> class_obj = this.findClass(class_file);
			synchronized (resourceEntries) {
				resourceEntries.put(class_obj.getName(), new CLHandler(class_obj));
			}
		} catch (ClassNotFoundException e) {
			logger.error(class_file + " not found.", e);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void ioClass(File fio) throws IOException {
		BufferedInputStream bis = null; ByteArrayOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(fio));
			bos = new ByteArrayOutputStream();
			for (;;) {
				int i = bis.read();
				if (i == -1) {
					break;
				}
				bos.write(i);
			}
			
			try {
				Class dfcz = this.defineClass(null, bos.toByteArray());
				this.setClassCacheEntry(dfcz);
			} catch (java.lang.ClassFormatError er) {
			}
		} finally {
			if(bis!=null)
				bis.close();
			if(bos!=null)
				bos.close();
		}
	}

}
