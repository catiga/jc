package com.jeancoder.root.io.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

@SuppressWarnings("serial")
public class JCFileItem implements FileItem {

	String contentType;

	String fieldName;

	String fileName;

	File file;

	boolean isFormField;

	int sizeThreshold;

	long file_size;

	public JCFileItem(String fieldName, String contentType, boolean isFormField, String fileName, long file_size,
			int sizeThreshold, File attch_file) {
		this.contentType = contentType;
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.file = attch_file;
		this.isFormField = isFormField;
		this.sizeThreshold = sizeThreshold;
		this.file_size = file_size;
	}

	@Override
	public FileItemHeaders getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders headers) {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public String getName() {
		return this.fileName;
	}

	@Override
	public boolean isInMemory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getSize() {
		return this.file_size;
	}

	@Override
	public byte[] get() {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (Exception e) {
		}
		return buffer;
	}

	@Override
	public String getString(String encoding) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(File file) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete() {
		if (this.file != null) {
			this.file.deleteOnExit();
		}
	}

	@Override
	public String getFieldName() {
		return this.fieldName;
	}

	@Override
	public void setFieldName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFormField() {
		return this.isFormField;
	}

	@Override
	public void setFormField(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
