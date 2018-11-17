package com.jeancoder.app.sdk.rendering;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.jeancoder.app.sdk.util.FileSysUtil;
import com.jeancoder.core.exception.SdkRuntimeException;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.log.JCLoggerFactory;
import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.util.HttpUtil;

public class WelcomeApplicationRendering implements Rendering {
	
	private static final JCLogger Logger = JCLoggerFactory.getLogger(WelcomeApplicationRendering.class);
	
    public void process(HttpServletRequest request, HttpServletResponse response, Result result) 
         throws Exception {
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType(HttpUtil.HTML);
    	WebContext ctx = new WebContext(request, response, request.getServletContext(),request.getLocale());
        Iterator<Entry<String, Object>>  dataMap = result.getDataMap().entrySet().iterator();  
        while(dataMap.hasNext()){  
        	Map.Entry<String,Object> entry = dataMap.next();  
        	ctx.setVariable(entry.getKey(), entry.getValue());
        }
        
        //封装的静态资源路径 6/21 huangjie
        ctx.setVariable("static", "/" + JCThreadLocal.getCode() + "/static");
        ctx.setVariable("contextPath", JCThreadLocal.getRequest().getContextPath());
        ctx.setVariable("JCrequest", JCThreadLocal.getRequest());
        
        String path = FileSysUtil.getFilePath(result.getResult());
        String name = FileSysUtil.getFileName(result.getResult());
        /**
         * 使用Thymeleaf引擎加载模板文件welcome.html
         */
        try {
	        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(request.getServletContext());
	        templateResolver.setTemplateMode("XHTML");
	        templateResolver.setPrefix(path + File.separator);
	        templateResolver.setSuffix(".html");
	        templateResolver.setCacheTTLMs(3600000L);
	        templateResolver.setCharacterEncoding("UTF-8");
	        TemplateEngine templateEngine = new TemplateEngine();
	        templateEngine.setTemplateResolver(templateResolver);
	        templateEngine.process(name, ctx, response.getWriter());
        } catch (Exception e) {
        	Logger.error("渲染 " +  path + File.separator + name + "出现异常" , e);
        	throw new SdkRuntimeException(e.getMessage(), e);
        }
    }
}