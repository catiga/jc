package com.jeancoder.root.io.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class RequestParser {

	private static Logger logger = LoggerFactory.getLogger(RequestParser.class);
	
	DiskFileItemFactory factory = new DiskFileItemFactory();
	
	/**
	 * 解析请求参数
	 * 
	 * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
	 *
	 * @throws BaseCheckedException
	 * @throws IOException
	 */
	public ReqTotal parse(FullHttpRequest fullReq) throws IOException {
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		List<UploadFile> files = new ArrayList<UploadFile>();
		
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
					String fileName = fileUpload.getFilename();
					String contentType = fileUpload.getContentType();
					String formField = fileUpload.getName();
					Long file_size = fileUpload.getFile().length();
					if (fileUpload.isCompleted()) {
						// 暂存到磁盘
						String tmp_dir = System.getProperty("java.io.tmpdir");
						tmp_dir = "/Users/jackielee/Desktop/test";
						tmp_dir = tmp_dir + File.separator + "tmp" + File.separator + Thread.currentThread().getId() + File.separator + System.currentTimeMillis() + "/";
						File parent_path = new File(tmp_dir);
						if(!parent_path.exists()) {
							boolean create_path_result = parent_path.setWritable(true);
							if(!create_path_result) {
								logger.error(tmp_dir + " permission not efficient");
							}
							parent_path.mkdirs();
						}
						File attch_file = new File(tmp_dir + "/" + fileName);
						boolean att_up_result = fileUpload.renameTo(attch_file);
						if(att_up_result) {
							UploadFile item = new UploadFile(formField, contentType, true, fileName, file_size, attch_file);
							files.add(item);
						}
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
		return new ReqTotal(parameters, files);
	}
}
