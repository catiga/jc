package com.jeancoder.root.server.state;

import java.util.LinkedList;
import java.util.List;

public class RequestStateHolder {

	private static List<RequestStateModel> _list_ = new LinkedList<>();
	
	public final static RequestStateHolder INSTANCE = new RequestStateHolder();
	
	private static Long totalDataLength = 0l;
	
	private RequestStateHolder() {
	}
	
	public void add(RequestStateModel obj) {
		synchronized (_list_) {
			totalDataLength += obj.length();
			_list_.add(obj);
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
			_list_.clear();
			return _list_;
		}
	}
}
