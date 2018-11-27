package com.jeancoder.root.container;

public class ContainerContextEnv {
	
	public static JCAppContainer getCurrentContainer() {
		return JCAPPHolder.getContainer();
	}
	
	public static void setCurrentContainer(JCAppContainer container) {
		Thread.currentThread().setContextClassLoader((ClassLoader)container.getSignedClassLoader().getManaged());
		JCAPPHolder.setContainer(container);
	}
	
	public static void clearCurrentContainer() {
		JCAPPHolder.clearContainer();
	}

//	public static JCAppContainer getCurrentContainer() {
//		ClassLoader current_classloader = Thread.currentThread().getContextClassLoader();
//		if(current_classloader instanceof KoLoader) {
//			return ((KoLoader)current_classloader).getContextEnv();
//		}
//		throw new RuntimeException("CONTAINER CONTEXT INITED ERROR......");
//	}
//	
//	public static void replaceContainer(JCAppContainer container) {
//		if(container!=getCurrentContainer()) {
//			JCAPPHolder.setContainer(container);
//		}
//	}
//	
//	public static JCAppContainer getContainer() {
//		try {
//			return JCAPPHolder.getContainer()==null?getCurrentContainer():JCAPPHolder.getContainer();
//		} finally {
//			//JCAPPHolder.clearContainer();
//		}
//	}
//	
//	public static void clearContainer() {
//		JCAPPHolder.clearContainer();
//	}
	
}
