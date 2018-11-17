package com.jeancoder.core.power;

import java.io.InputStream;

import com.google.gson.Gson;
import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.power.support.QiniuUploadResult;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

public class QiniuPowerHandler extends PowerHandler implements QiniuPower {

	private QiniuPowerConfig config;

	@Override
	public void init(PowerConfig config) throws JeancoderException {
		QiniuPowerConfig qiniuConfig = (QiniuPowerConfig) config;
		this.config = qiniuConfig;
	}

	@Override
	public QiniuUploadResult upload(InputStream is, String fileName) {
		QiniuUploadResult result = upload(is, config.getDefaultBucket(), fileName);
		return result;
	}

	@Override
	public QiniuUploadResult upload(InputStream is, String bucket, String fileName) {
		QiniuUploadResult result = new QiniuUploadResult();
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());
		// ...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);
		// ...生成上传凭证，然后准备上传
		String accessKey = config.getAccessKey();
		String secretKey = config.getSecretKey();
		// 默认不指定key的情况下，以文件内容的hash值作为文件名
		String key = fileName;
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket);
		try {
			Response response = uploadManager.put(is, key, upToken, null, null);
			// 解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			result.setSuccess(true);
			result.setHash(putRet.hash);
			result.setKey(putRet.key);
		} catch (QiniuException ex) {
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public String token(String bucket) {
		// 构造一个带指定Zone对象的配置类
		// ...生成上传凭证，然后准备上传
		String accessKey = config.getAccessKey();
		String secretKey = config.getSecretKey();
		// 默认不指定key的情况下，以文件内容的hash值作为文件名
		Auth auth = Auth.create(accessKey, secretKey);
		
		if(bucket==null) {
			bucket = config.getDefaultBucket();
		}
		String upToken = auth.uploadToken(bucket);
		return upToken;
	}

}
