package com.jeancoder.root.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class JCWriter extends Writer {
	
	public JCWriter(OutputStream out, boolean autoFlush) {
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
	public void write(char[] cbuf, int off, int len) throws IOException {
		String buf = new String(cbuf, off, len);
		try {
			output.write(buf.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
