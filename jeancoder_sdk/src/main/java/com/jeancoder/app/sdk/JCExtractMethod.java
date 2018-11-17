package com.jeancoder.app.sdk;

import com.jeancoder.app.sdk.extract.JCExtract;

public class JCExtractMethod implements JCMethod {

	public static <T> T fromRequest(Class<T> form) {
		return JCExtract.fromRequest(form);
	}
}
