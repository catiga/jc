package com.jeancoder.root.io.http;

import java.util.List;
import java.util.Map;

public class ReqTotal {

	Map<String, String[]> parameters;
	
	List<UploadFile> files;

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public List<UploadFile> getFiles() {
		return files;
	}
	
	public ReqTotal(Map<String, String[]> parameters, List<UploadFile> files) {
		this.parameters = parameters;
		this.files = files;
	}
}
