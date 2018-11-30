package com.jeancoder.root.io.http;

import java.io.File;

import org.apache.commons.fileupload.FileItem;

public class UploadFile {

	String contentType;
	
	String fieldName;
	
	String fileName;
	
	long fileSize;
	
	boolean isFormField;
	
	private File file;
	
	int sizeThreshold = 10240;
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public String getContentType() {
		return contentType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFileName() {
		return fileName;
	}

	public UploadFile(String fieldName,
            String contentType, boolean isFormField, String fileName, long fileSize, File attch_file) {
		this.file = attch_file;
		this.fieldName = fieldName;
		this.contentType = contentType;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.isFormField = isFormField;
//		file = new DiskFileItem(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
	}
	
	public FileItem toItem() {
		if(file!=null&&file.exists()) {
			return new JCFileItem(fieldName, contentType, isFormField, fileName, fileSize, sizeThreshold, file);
		}
		return null;
	}
}
