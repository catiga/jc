package com.jeancoder.core.context;

import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.http.JCResponse;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.MemPower;
import com.jeancoder.core.power.QiniuPower;
import com.jeancoder.core.result.Result;

public interface ApplicationContext {
	abstract JCRequest getRequest();
	abstract JCResponse getResponse();
	abstract DatabasePower getDatabase();
	abstract Result getResult();
	abstract void setResult(Result result);
	abstract ClassLoader getClassLoader();
	
	abstract MemPower getMemPower();
	abstract QiniuPower getQiniuPower();
}
