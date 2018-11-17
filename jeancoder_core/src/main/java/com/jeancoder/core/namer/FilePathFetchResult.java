package com.jeancoder.core.namer;


/**
 * 获取得到的只是一个文件路径结果
 * @author huangjie
 *
 */
public class FilePathFetchResult implements IFetchResult {
	
	private String path = "";
	
	@Override
	public Object getFetchResult() {
		return path;
	}

	@Override
	public void setFetchResult(Object result) {
		this.path = (String)result;
	}

}
