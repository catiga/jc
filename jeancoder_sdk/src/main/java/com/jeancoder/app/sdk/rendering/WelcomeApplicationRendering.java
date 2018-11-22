package com.jeancoder.app.sdk.rendering;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.io.http.JCServletContext;

import io.netty.channel.ChannelHandlerContext;

public class WelcomeApplicationRendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	public WelcomeApplicationRendering(ChannelHandlerContext context) {
		super(context);
	}

	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		WebContext ctx = new WebContext(request, response, new JCServletContext());
		Iterator<Entry<String, Object>> dataMap = result.getDataMap().entrySet().iterator();
		while (dataMap.hasNext()) {
			Map.Entry<String, Object> entry = dataMap.next();
			ctx.setVariable(entry.getKey(), entry.getValue());
		}
		Enumeration<String> attribute_names = request.getAttributeNames();
		while(attribute_names.hasMoreElements()) {
			String next_ele = attribute_names.nextElement();
			if(!ctx.containsVariable(next_ele)) {
				ctx.setVariable(next_ele, request.getAttribute(next_ele));
			}
		}

		ctx.setVariable("static", "/" + JCThreadLocal.getCode() + "/static");
		ctx.setVariable("contextPath", JCThreadLocal.getRequest().getContextPath());
		ctx.setVariable("JCrequest", JCThreadLocal.getRequest());
		ctx.setVariable("pub_bucket", "https://cdn.iplaysky.com/static/");

		JCAPP apps = this.runningResult.getAppins();
		String path = apps.getApp_base() + "/template/";
		String name = result.getResult();
		if (result.getResult().indexOf("/") > -1) {
			path = path + name.substring(0, name.lastIndexOf("/")) + "/";
			name = name.substring(name.lastIndexOf("/") + 1);
		}
		
		FileTemplateResolver resolver = new FileTemplateResolver();
		resolver.setPrefix(path);// 模板所在目录，相对于当前classloader的classpath。
		resolver.setSuffix(".html");// 模板文件后缀
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(resolver);
		// 渲染模板
		String html = templateEngine.process(name, ctx);

		this.writeHtmlResponse(html, true);
		return html;
	}
}