package com.jeancoder.app.sdk.rendering;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.rendering.Rendering;
import com.jeancoder.core.result.Result;

public class  StreamRendering implements Rendering{

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response, Result result) throws Exception {
		FileInputStream fis = new FileInputStream(new File(result.getResult()));
		OutputStream os = response.getOutputStream();
		int len ;
        byte[] _byte = new byte[1024];
        while ((len = fis.read(_byte)) > 0){
        	os.write(_byte, 0, len);
        }
        os.flush();
        fis.close();
	}

}
