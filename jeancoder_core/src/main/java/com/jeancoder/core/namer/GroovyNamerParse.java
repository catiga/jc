package com.jeancoder.core.namer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jeancoder.core.common.Common;
import com.jeancoder.core.exception.AppParseException;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.proc.GroovyDynamicResource;
import com.jeancoder.core.resource.proc.GroovyMemoryDynamicResource;
import com.jeancoder.core.resource.proc.InterceptorGroovyDynamicResource;
import com.jeancoder.core.resource.proc.InterceptorGroovyMemoryDynamicResource;
import com.jeancoder.core.resource.proc.Resource;
import com.jeancoder.core.resource.proc.ResourceBundle;
import com.jeancoder.core.resource.proc.StaticResource;
import com.jeancoder.core.resource.proc.ViewResource;
import com.jeancoder.core.resource.type.DynamicResourceType;
import com.jeancoder.core.util.FileUtil;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

 
/**
 * 解析器
 * 如果载入的是本地磁盘，nam在路径下面进行全扫描
 * 如果载入类型是内存，则还应该去内存中进行扫描
 * @author huangjie
 *
 */
public class GroovyNamerParse extends NamerParse {
	
	public GroovyNamerParse(){
	}
	
	@Override
	public Application parse(NamerApplication namerApplication, String filePath) {
		Application application = new Application();
		application.setApp(namerApplication);
		Binding binding = new Binding();
		GroovyShell shell = new GroovyShell(binding);
		// 组织路径 com.developerCode.appCode
		String organization = Common.COM + File.separator + namerApplication.getDeveloperCode() + File.separator + namerApplication.getAppCode();
		try {
			File file = new File(new URI(filePath));
			String appPath = file.getPath();
			//如果 当前载入方式是载入内存中，则forEachDir不会去扫描src/main/java 下的grrovy脚步片段
			//注意： ready 下面的脚本因为需要添加 Classpath, 所有统一在此进行扫描加载
			forEachDir(appPath, organization, application, file, shell, namerApplication.getInstallWay());
			
			//如果 当前载入方式是载人内存， 则需要扫描内存中的grrovy脚步片段 
			if (InstallWay.MEMORY.equals(namerApplication.getInstallWay())) {
				parseMemory(appPath, organization,application, shell, namerApplication.getFetchWay());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppParseException("SdkGroovyNamerParse parse failure", e);
		}
		application.setClassLoader(shell.getClassLoader());
		return application;
	}
	
	/**
	 * 
	 * @param appPath 项目APP 路径
	 * @param application
	 * @param shell
	 * @param sourcedir 当前目录
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws ClassNotFoundException 
	 */
	public void forEachDir(String appPath , String organization, Application application, File sourcedir, GroovyShell shell, InstallWay installWay) throws IOException, URISyntaxException, ClassNotFoundException {
		if(sourcedir.listFiles() == null) {
			return;
		}
		for (File file : sourcedir.listFiles()) {
			if(file.isDirectory()) {
				forEachDir(appPath, organization, application, file, shell, installWay);
				continue;
			}
			String type = "";
			Resource resource = null;
			
			if (file.getPath().indexOf(appPath + File.separator + Common.MAVEN_STANDARD_SOURCE_CODE_PATH + File.separator + organization) != 0 && file.getPath().indexOf(appPath + File.separator + Common.VIEW) != 0
					&& file.getPath().indexOf(appPath + File.separator + Common.STATIC) != 0) {
				//  不在 appPath/src/main/java/ 和 appPath/template/  appPath/static/ 下的文件不进行扫描
				continue;
			}
			if (FileUtil.isGroovyFile(file.getPath()) && file.getPath().indexOf(organization + Common.ENTRY_PATH) >= 0 && InstallWay.DISK.equals(installWay)) {
				// Controller 资源
				resource = getGroovyDynamicResource(file, File.separator + organization + Common.ENTRY_PATH);
				((GroovyDynamicResource)resource).setShell(shell);
				type = Common.ENTRY;
			} else if (FileUtil.isGroovyFile(file.getPath()) && file.getPath().indexOf(organization + Common.INTERNAL_PATH) >= 0 && InstallWay.DISK.equals(installWay)) {
				//内部资源
				resource = getGroovyDynamicResource(file, File.separator + organization + Common.INTERNAL_PATH);
				((GroovyDynamicResource)resource).setShell(shell);
				type = Common.INTERNAL;
			} else if(FileUtil.isGroovyFile(file.getPath()) && file.getPath().indexOf(organization + Common.READY_PATH) >= 0) {
				// 预加载的资源
				shell.getClassLoader().addClasspath(appPath + File.separator + Common.MAVEN_STANDARD_SOURCE_CODE_PATH);
				shell.getClassLoader().loadClass(FileUtil.getClassName(file.getPath()));
				String resid = FileUtil.getResid(organization, file.getPath());
		        GroovyDynamicResource dynamicResource = new GroovyDynamicResource(file.getPath(), new DynamicResourceType());
		        dynamicResource.setResId(resid);
		        resource = dynamicResource;
		        ((GroovyDynamicResource)resource).setShell(shell);
		        type = Common.READY;
			} else if(FileUtil.isGroovyFile(file.getPath()) && file.getPath().indexOf(organization + Common.INTERCEPTOR_PATH) >= 0 && InstallWay.DISK.equals(installWay)) {
				// 预加载的资源 拦截器
				//resource = getGroovyDynamicResource(file, File.separator + organization + Common.INTERCEPTOR_PATH, shell);
				resource = getInterceptorResource(file, File.separator + organization + Common.INTERCEPTOR_PATH);
				((GroovyDynamicResource)resource).setShell(shell);
				type = Common.INTERCEPTOR;
			} else if (file.getPath().equals(appPath + File.separator + Common.MAVEN_STANDARD_SOURCE_CODE_PATH + File.separator + organization + Common.INITIAL_PATH) && InstallWay.DISK.equals(installWay)) {
				// init 脚本 System.out.println(organization + Common.INIT_PATH);
				resource = getGroovyDynamicResource(file, File.separator + organization + File.separator);
				((GroovyDynamicResource)resource).setShell(shell);
				type = Common.INITIAL;
			} else if (file.getPath().indexOf(appPath + Common.STATIC_PATH) >= 0) {
				// 静态文件资源 js css
				resource = getStaticResource(file, appPath);
				type = Common.STATIC;
			} else if (file.getPath().indexOf(appPath + Common.VIEW_PATH) >= 0) {
				//  普通视图资源
				resource = getViewResource(file, appPath);
				type = Common.VIEW;
			}else{
				continue;
			}
			ResourceBundle rb = application.getBundlesByType(type);
			if (rb == null) {
				rb = new ResourceBundle();
			}
			rb.addResources(resource);
			application.addBundles(type, rb);
		}
	}
 
	/**
	 * 解释内存中的内容
	 * @param organization
	 * @param appPath
	 * @param application
	 * @param shell
	 * @throws IOException 
	 */
	public void parseMemory(String appPath, String organization, Application application, GroovyShell shell, FetchWay fetchWay) throws IOException{
		Map<String, String> resourceMap = MemoryPool.getResourceMap(application.getAppCode());
		Iterator<Entry<String,String>> resourceIterator = resourceMap.entrySet().iterator();
		while (resourceIterator.hasNext()) {
			Map.Entry<String, String> entry = resourceIterator.next();
			String filePath = entry.getKey();
			if (filePath.indexOf(appPath + File.separator + Common.MAVEN_STANDARD_SOURCE_CODE_PATH + File.separator + organization) != 0 ) {
				//  不在 appPath/src/main/java/ 下的文件不进行扫描
				continue;
			}
			if (!FileUtil.isGroovyFile(filePath)) {
				continue;
			}
			String type = "";
			Resource resource = null;
			if (filePath.indexOf(organization + Common.ENTRY_PATH) >= 0) {
				// Controller 资源
				resource = getGroovyMemoryDynamicResource(application.getAppCode(), filePath, File.separator + organization + Common.ENTRY_PATH);
				((GroovyDynamicResource)resource).setShell(shell);
				type = Common.ENTRY;
			} else if (filePath.indexOf(organization + Common.INTERNAL_PATH) >= 0) {
				//内部资源
				resource = getGroovyMemoryDynamicResource(application.getAppCode(), filePath, File.separator + organization + Common.INTERNAL_PATH);
				((GroovyDynamicResource)resource).setShell(shell);
				type = Common.INTERNAL;
			} else if (filePath.indexOf(organization + Common.READY_PATH) >= 0) {
//				// 预加载的资源 不需要处理
			} else if (filePath.indexOf(organization + Common.INTERCEPTOR_PATH) >= 0) {
				// 预加载的资源 拦截器
				resource = getInterceptorMemoryResource(application.getAppCode(), filePath, File.separator + organization + Common.INTERCEPTOR_PATH);
				((InterceptorGroovyMemoryDynamicResource)resource).setShell(shell);
				type = Common.INTERCEPTOR;                   
			} else if (filePath.equals(appPath + File.separator + Common.MAVEN_STANDARD_SOURCE_CODE_PATH + File.separator + organization + Common.INITIAL_PATH)) {
				// init 脚本 System.out.println(organization + Common.INIT_PATH);
				resource = getGroovyMemoryDynamicResource(application.getAppCode(), filePath, File.separator + organization + File.separator);
				((GroovyDynamicResource)resource).setShell(shell);
				type = Common.INITIAL;
			} 
			ResourceBundle rb = application.getBundlesByType(type);
			if (rb == null) {
				rb = new ResourceBundle();
			}
			rb.addResources(resource);
			application.addBundles(type, rb);
			// 原文件为zip包 解压后载入内存需要删除原ready 包下的文件
			String readyPath = appPath + File.separator + Common.MAVEN_STANDARD_SOURCE_CODE_PATH + File.separator + organization + Common.READY_PATH;
			if (FetchWay.LOCAL.equals(fetchWay) || FetchWay.MEMORY.equals(fetchWay)) {
				FileUtil.deletefile(new File(readyPath));
			}
	    }  
	}
	
	// TODO 想办法把 资源转换抽象出来 黄杰  TODO 6/19
	private ViewResource getViewResource(File entry, String appPath) throws IOException{
        ViewResource viewResource = new ViewResource(entry.getPath(), new DynamicResourceType());
        viewResource.setResId(FileUtil.getResid(appPath + File.separator, entry.getPath()));
        return viewResource;
	}
	private StaticResource getStaticResource(File entry, String appPath) throws IOException{
		StaticResource staticResource = new StaticResource(entry.getPath(), new DynamicResourceType());
		staticResource.setResId(FileUtil.getResidSuffix(appPath + File.separator, entry.getPath()));
        return staticResource;
	}
	private GroovyDynamicResource getGroovyDynamicResource(File entry, String organization) throws IOException{
		String resid = FileUtil.getResid(organization, entry.getPath());
        GroovyDynamicResource dynamicResource = new GroovyDynamicResource(entry.getPath(), new DynamicResourceType());
        dynamicResource.setResId(resid);
        return dynamicResource;
	}
 
	private GroovyMemoryDynamicResource getGroovyMemoryDynamicResource(String appCode, String filePath, String organization) throws IOException{
		GroovyMemoryDynamicResource gmdr = new  GroovyMemoryDynamicResource(filePath, new DynamicResourceType());
		String resid = FileUtil.getResid(organization, filePath);
		gmdr.setResId(resid);
		gmdr.setAppCode(appCode);
		return gmdr;
	}
	
	/* 添加拦截器资源构造方法 */
	private InterceptorGroovyDynamicResource getInterceptorResource(File entry, String organization) throws IOException {
		String resid = FileUtil.getResid(organization, entry.getPath());
		InterceptorGroovyDynamicResource resource = new InterceptorGroovyDynamicResource(entry.getPath(), new DynamicResourceType());
		resource.setResId(resid);
		resource.init();
		return resource;
	}
	
	private InterceptorGroovyMemoryDynamicResource getInterceptorMemoryResource(String appCode, String filePath, String organization) throws IOException{
		String resid = FileUtil.getResid(organization, filePath);
		InterceptorGroovyMemoryDynamicResource gmdr = new  InterceptorGroovyMemoryDynamicResource(filePath, new DynamicResourceType());
		gmdr.setAppCode(appCode);
		gmdr.setResId(resid);
		gmdr.init();
		return gmdr;
	}

	public static void main(String[] arg) {
//		Binding binding = new Binding();
//		GroovyShell shell = new GroovyShell(binding);
//		shell.getClassLoader().addClasspath("/Users/huangjie/workspace/mixture-system" + File.separator + Common.MAVEN_STANDARD_SOURCE_CODE_PATH);
//		try {
//			System.out.println(new PowerIn());
//			Object o = shell.getClassLoader().loadClass("com.jeancoder.system.ready.PowerIn");
//			Object o1 = shell.getClassLoader().loadClass("com.jeancoder.system.ready.dto.ApplicationConfigDto");
//			System.out.println(o);
//			System.out.println(o1);
//			System.out.println(new PowerIn());
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
