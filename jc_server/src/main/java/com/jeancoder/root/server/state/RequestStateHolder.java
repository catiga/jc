package com.jeancoder.root.server.state;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jeancoder.root.server.util.RemoteUtil;

public class RequestStateHolder {
	
	private static List<RequestStateModel> _list_ = new LinkedList<>();
	
	public final static RequestStateHolder INSTANCE = new RequestStateHolder();
	
	ScheduledExecutorService scheduExec = Executors.newScheduledThreadPool(10);
	
	private static Long totalDataLength = 0l;
	
	private RequestStateHolder() {
	}
	
	public void add(RequestStateModel obj) {
		synchronized (_list_) {
			totalDataLength += obj.length();
			_list_.add(obj);
			if(totalDataLength>1024L) {
				final List<RequestStateModel> upload_date = new LinkedList<>(_list_);
				_list_.clear();
				//直接进行上传操作
				scheduExec.schedule(new Runnable() {
					
					@Override
					public void run() {
						try {
							RemoteUtil.uploadPerfData(upload_date);
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}, 0, TimeUnit.MILLISECONDS);
			} else if(totalDataLength>GlobalStateHolder.INSTANCE.cachedMaxSize()) {
				//TODO 清空，暂时这么处理
				_list_.clear();
			}
		}
	}
	
	public List<RequestStateModel> requests() {
		return _list_;
	}
	
	public Long length() {
		return totalDataLength;
	}
	
	public List<RequestStateModel> trigger() {
		synchronized (_list_) {
			List<RequestStateModel> str_1 = new LinkedList<RequestStateModel>(_list_);
			_list_.clear();
			return str_1;
		}
	}
	
}
