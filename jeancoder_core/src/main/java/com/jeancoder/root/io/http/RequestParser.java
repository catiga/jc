package com.jeancoder.root.io.http;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class RequestParser {

	/**
	 * 解析请求参数
	 * 
	 * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
	 *
	 * @throws BaseCheckedException
	 * @throws IOException
	 */
	public Map<String, String[]> parse(FullHttpRequest fullReq) throws IOException {
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		
		new QueryStringDecoder(fullReq.uri()).parameters().entrySet().forEach(entry -> {
			List<String> values = entry.getValue();
			parameters.put(entry.getKey(), values.toArray(new String[values.size()]));
		});

		HttpMethod method = fullReq.method();
		if (HttpMethod.POST == method) {
			// 是POST请求
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
			decoder.offer(fullReq);
			List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
			for (InterfaceHttpData parm : parmList) {
				HttpDataType data_type = parm.getHttpDataType();
				if (data_type == HttpDataType.FileUpload) {
					FileUpload fileUpload = (FileUpload) parm;
					fileUpload.get();
					String fileName = fileUpload.getFilename();
					if (fileUpload.isCompleted()) {
						// 保存到磁盘
						StringBuffer fileNameBuf = new StringBuffer();
						fileNameBuf.append(DiskFileUpload.baseDirectory).append(fileName);
						fileUpload.renameTo(new File(fileNameBuf.toString()));
					}
					//TODO build file object
				} else {
					// normal post to merge params
					Attribute data = (Attribute) parm;
					if(parameters.containsKey(data.getName())) {
						String[] values = parameters.get(data.getName());
						Vector<String> vs = new Vector<String>(Arrays.asList(values));
						vs.add(data.getValue());
						parameters.put(data.getName(), vs.toArray(new String[vs.size()]));
					} else {
						parameters.put(data.getName(), new String[]{data.getValue()});
					}
				}
			}

		}

		return parameters;
	}
}
