package com.jeancoder.root.io.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import io.netty.util.CharsetUtil;

public class RequestParser {

	private static Logger logger = LoggerFactory.getLogger(RequestParser.class.getName());
	
	DiskFileItemFactory factory = new DiskFileItemFactory();
	
	public static void main(String[] argc) {
		String full_uri = "/general_api/ypcall/sp/info/res?=&partner=11110002&ver=1.0&sign=f684468164e1c9d79e122f91416f8ce0";
		RequestParser par = new RequestParser();
		par.params(full_uri);
	}
	
	/**
	 * dispost get parameter urldecode
	 * @param full_uri
	 * @return
	 */
	Map<String, String[]> params(String full_uri) {
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		if(full_uri.indexOf("?")<0) {
			return parameters;
		}
		String[] params = full_uri.substring(full_uri.indexOf("?") + 1).trim().split("&");
		for(String k : params) {
			String[] key_values = k.split("=");
			if(key_values!=null&&key_values.length>0) {
				String key = key_values[0].trim();
				String value = "";
				if(key_values.length>1) {
					value = key_values[1];
				}
				if(value!=null&&!value.equals("")) {
					try {
						value = URLDecoder.decode(value, CharsetUtil.UTF_8.name());
					} catch (UnsupportedEncodingException e) {
						logger.error("PARAMETER VALUE:" + value, e);
					}
				}
				String[] exist_values = parameters.get(key);
				if(exist_values!=null&&exist_values.length>0) {
					exist_values = Arrays.copyOf(exist_values, exist_values.length + 1);
					exist_values[exist_values.length - 1] = value;
					//inner_list = Arrays.asList(exist_values);
				} else {
					//inner_list = new ArrayList<String>();
					exist_values = new String[]{value};
				}
				parameters.put(key, exist_values);
			}
		}
		return parameters;
	}
	
	/**
	 * 解析请求参数
	 * 
	 * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
	 *
	 * @throws BaseCheckedException
	 * @throws IOException
	 */
	public ReqTotal parse(FullHttpRequest fullReq) throws IOException {
		String full_uri = fullReq.uri();
		Map<String, String[]> parameters = this.params(full_uri);
		List<UploadFile> files = new ArrayList<UploadFile>();
		
//		full_params.entrySet().forEach(entry -> {
//			List<String> values = entry.getValue();
//			parameters.put(entry.getKey(), values.toArray(new String[values.size()]));
//		});
		
		HttpMethod method = fullReq.method();
		if (HttpMethod.POST == method) {
			// 是POST请求
			HttpPostRequestDecoder decoder = null;
			try {
				decoder = new HttpPostRequestDecoder(fullReq);
				
				//decoder.offer(fullReq);	// Not nessary code
				List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
				for (InterfaceHttpData parm : parmList) {
					HttpDataType data_type = parm.getHttpDataType();
					if (data_type == HttpDataType.FileUpload) {
						MixedFileUpload fileUpload = (MixedFileUpload) parm;
						String fileName = fileUpload.getFilename();
						String contentType = fileUpload.getContentType();
						String formField = fileUpload.getName();
						Long file_size = -1l;
						if(!fileUpload.isInMemory()) {
							file_size = fileUpload.getFile().length();
						} else {
							file_size = fileUpload.length();
						}
						if (fileUpload.isCompleted()) {
							// 暂存到磁盘
							String tmp_dir = System.getProperty("java.io.tmpdir");
							//tmp_dir = "/Users/jackielee/Desktop/test";
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
			} finally {
				if(decoder!=null) {
					try {
						// Release Memory manually
						decoder.destroy();
					} catch(Exception e) {
						logger.error("Http_Post_Decoder memory release error:" + e.toString());
					}
				}
			}
		}
		return new ReqTotal(parameters, files);
	}
}
