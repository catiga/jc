package com.jeancoder.core.rendering;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.result.Result;
import com.jeancoder.root.env.JCAPP;
import com.jeancoder.root.io.http.ContentTypes;

import io.netty.channel.ChannelHandlerContext;
import sun.net.www.URLConnection;

public class StaticRendering<T extends Result> extends DefaultRendering<T> implements Rendering {

	private static Logger logger = LoggerFactory.getLogger(StaticRendering.class);
	
	public StaticRendering(ChannelHandlerContext context) {
		super(context);
	}

	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) {
		super.process(request, response);
		Result result = this.runningResult.getResult();
		
		JCAPP apps = this.runningResult.getAppins();
		//String path = apps.getApp_base() + "/" + apps.getSta_base() + "/";
		String path = apps.getApp_base() + File.separator + apps.getSta_base() + File.separator;
		String name = result.getResult();
		name = path + name;
		String content_key = name.substring(name.lastIndexOf("."));
		String content_type = URLConnection.guessContentTypeFromName(name);
		if(content_type==null)
			content_type = ContentTypes.get(content_key);
		try {
			//response.setContentType(HttpUtil.getContentType(result.getResult()));
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(new File(name)));
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            byte[] buffer = bos.toByteArray();
            
			//this.writeStreamResponse(buffer, content_type, true);
            String filename = null;
            if(name.indexOf(File.separator)>-1) {
            	if(name.lastIndexOf(File.separator)==name.length()-1) {
            		String[] arr_name = name.split(File.separator);
            		filename = arr_name[arr_name.length-1];
            	} else {
            		filename = name.substring(name.lastIndexOf(File.separator) + 1);
            	}
            }
            this.writeStreamResponse(buffer, content_type, filename, true);
		} catch (Exception e) {
			logger.error(name + " rendering error:", e);
		}
		return null;
	}

	public static void main(String[] argc) throws Exception{
		String name = "user/path/info.jpg";
		System.out.println(name.length() + "===" + name.lastIndexOf("/"));
		
		if (name.indexOf(File.separator) > -1) {
			if (name.lastIndexOf(File.separator) == name.length() - 1) {
				String[] arr_name = name.split(File.separator);
				name = arr_name[arr_name.length - 2];
			} else {
				name = name.substring(name.lastIndexOf(File.separator));
			}
		}
		System.out.println(name);
		
		name = "/Users/jackielee/Documents/server_workspace/app_scm/static/static/images/back2.pg";
		Path path1 = Paths.get(name);
        String contentType1 = Files.probeContentType(path1);
        System.out.println(contentType1);
        contentType1 = URLConnection.guessContentTypeFromName(name);
        System.out.println(contentType1);
	}
}
