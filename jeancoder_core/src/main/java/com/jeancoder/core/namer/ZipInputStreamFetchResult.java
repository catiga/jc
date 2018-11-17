package com.jeancoder.core.namer;

import java.util.zip.ZipInputStream;

public class ZipInputStreamFetchResult implements IFetchResult {
	
	private ZipInputStream zis = null;
	
	@Override
	public void setFetchResult(Object result) {
		this.zis = (ZipInputStream)result;
	}

	@Override
	public Object getFetchResult() {
		return zis;
	}

}
