package com.jeancoder.core.namer;

import com.jeancoder.core.resource.proc.Application;

/**
 * @author huangjie
 *
 */
public interface INamerParse {
	
	public Application parse(NamerApplication namerApplication, String inputStream);
}
