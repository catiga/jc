package com.jeancoder.root.io;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import com.jeancoder.root.env.ChannelContextWrapper;
import com.jeancoder.root.vm.JCVMDelegatorGroup;

public class JcServletOutputStream extends ServletOutputStream {

	ChannelContextWrapper ctx = JCVMDelegatorGroup.instance().getDelegator().getCurrentContext();
	
	@Override
	public void write(int b) throws IOException {
		if(ctx==null) {
			throw new IOException("channel context null, maybe closed");
		}
		ctx.getContext().write(b);
	}
	
	public void flush() {
		ctx.getContext().flush();
	}
	
	public void write(Object msg) throws IOException {
		if(ctx==null) {
			throw new IOException("channel context null, maybe closed");
		}
		ctx.getContext().write(msg);
	}
}
