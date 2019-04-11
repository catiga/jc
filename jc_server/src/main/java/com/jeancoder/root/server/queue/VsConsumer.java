package com.jeancoder.root.server.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.state.GlobalStateHolder;
import com.jeancoder.root.server.state.RequestStateHolder;
import com.jeancoder.root.server.state.RequestStateModel;
import com.jeancoder.root.server.util.RemoteUtil;

public class VsConsumer implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(VsConsumer.class);
	
	static final Long UPD_DIFF = 5*60*1000L;	//默认上传时间间隔，五分钟
	
	@Override
	public void run() {
		while(true) {
			try {
				if(GlobalStateHolder.INSTANCE.upTimeDiff()!=null && GlobalStateHolder.INSTANCE.upTimeDiff() >0l) {
					Thread.sleep(GlobalStateHolder.INSTANCE.upTimeDiff());
				} else {
					Thread.sleep(UPD_DIFF);
				}
				Queue<RequestStateModel> VSQUEUE = RequestStateHolder.INSTANCE.VSQUEUE;
				List<RequestStateModel> up_data = null;
				synchronized(VSQUEUE) {
					Long up_data_min_length = GlobalStateHolder.INSTANCE.cachedMinSize();
					
					if(RequestStateHolder.INSTANCE.length()<up_data_min_length) {
						up_data = RequestStateHolder.INSTANCE.trigger();
					} else {
						up_data = new LinkedList<>();
						Long added_length = 0L;
						for(;;) {
							RequestStateModel e = RequestStateHolder.INSTANCE.popup();
							if(e==null) {
								break;
							}
							added_length += e.length();
							up_data.add(e);
							if(added_length>=up_data_min_length) {
								break;
							}
						}
					}
				}
				if(up_data!=null && !up_data.isEmpty()) {
					RemoteUtil.uploadPerfData(up_data);
				}
			} catch(Exception e) {
				logger.error("CONSUME VSDATA EXCEPTION:", e);
			}
		}
	}

}
