package com.jeancoder.root.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EventListener;
import java.util.EventObject;

import com.jeancoder.core.http.JCResponse;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.result.ResultType;

public class RunnerResultListener<T extends Result> implements EventListener {

	@SuppressWarnings("unchecked")
	public Object handleEvent(EventObject event) {
		if(event==null) {
			return null;
		}
		if(event instanceof TouchIOEvent) {
			JCResponse response = JCThreadLocal.getResponse();
			
			ResultType type = ((TouchIOEvent<T>)event).getResult().getResultType();
			String value = ((TouchIOEvent<T>)event).getResult().getResult();
			
			if(value!=null&&type.equals(ResultType.GENERAL_IO)) {
				try {
					BufferedInputStream fis = new BufferedInputStream(new FileInputStream(new File(value)));
					ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
		            byte[] b = new byte[1000];  
		            int n;  
		            while ((n = fis.read(b)) != -1) {  
		                bos.write(b, 0, n);  
		            }  
		            fis.close();  
		            bos.close();
		            for(byte bb : b) {
		            	response.getWriter().write(bb);
		            }
				}catch(IOException ioex) {
				}
			}
		}
		return null;
	}
}
