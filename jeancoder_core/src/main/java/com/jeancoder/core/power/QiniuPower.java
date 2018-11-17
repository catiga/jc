package com.jeancoder.core.power;

import java.io.InputStream;

import com.jeancoder.core.power.support.QiniuUploadResult;

public interface QiniuPower {
	public QiniuUploadResult upload(InputStream is,String fileName);
	public QiniuUploadResult upload(InputStream is,String bucket,String fileName);
	
	public String token(String bucket);
}
