package com.jeancoder.core.namer;

import java.io.IOException;
import java.io.InputStream;

public class SdkFakeInputStream extends InputStream {
	
	private String appPath;

	@Override
	public int read() throws IOException {
		return 0;
	}

	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
}
