package com.jeancoder.root.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

import com.jeancoder.root.env.ChannelContextWrapper;
import com.jeancoder.root.vm.JCVMDelegatorGroup;

public class JcServletOutputStream extends ServletOutputStream {

	ChannelContextWrapper ctx = JCVMDelegatorGroup.instance().getDelegator().getCurrentContext();
	
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	@Override
	public void write(int b) throws IOException {
		if(ctx==null) {
			throw new IOException("channel context null, maybe closed");
		}
		bos.write(b);
	}
	
	public byte[] getData() {
		byte[] data = bos.toByteArray();
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void flush() {
		//ctx.getContext().flush();
	}
	
}
