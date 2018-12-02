package com.jeancoder.root.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class JCWriter extends PrintWriter {
	
	public JCWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
		this.needFlush = autoFlush;
		this.output = out;
	}

	OutputStream output;
	
	static final String DEFAULT_ENCODE = "UTF-8";

	public OutputStream getStream() {
		return output;
	}
	
	boolean needFlush;

	@Override
	public void flush() {
		try {
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void print(Object obj) {
		try {
			((JcServletOutputStream)output).write(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
