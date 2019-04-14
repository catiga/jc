package com.jeancoder.root.server.queue;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.state.RequestStateHolder;
import com.jeancoder.root.server.state.RequestStateModel;
import com.jeancoder.root.server.util.RemoteUtil;

public class VsConsumer implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(VsConsumer.class);
	
	static final Long UPD_DIFF = 30*1000L;	//默认上传时间间隔，五分钟
	
	@Override
	public void run() {
		List<RequestStateModel> data = new ArrayList<RequestStateModel>();
		while(true) {
			try {
				RequestStateModel e = RequestStateHolder.getInstance().popup();
				if(e!=null) {
					data.add(e);
					RemoteUtil.uploadPerfData(data);
					data.clear();
				}
			} catch(Exception e) {
				logger.error("CONSUME VSDATA EXCEPTION:", e);
			}
		}
	}

}
