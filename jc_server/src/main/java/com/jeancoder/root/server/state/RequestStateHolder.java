package com.jeancoder.root.server.state;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.queue.VsConsumer;

public class RequestStateHolder {
	
	protected static Logger logger = LoggerFactory.getLogger(RequestStateHolder.class);
	
	//public final Queue<RequestStateModel> VSQUEUE = new LinkedList<>();
	public final Queue<RequestStateModel> VSQUEUE = new LinkedBlockingQueue<RequestStateModel>();
	
	public final static RequestStateHolder INSTANCE = new RequestStateHolder();
	
	private Long totalDataLength = 0l;
	
	private RequestStateHolder() {
		Thread thread = new Thread(new VsConsumer());
		thread.start();
	}
	
	public RequestStateModel popup() {
		synchronized(VSQUEUE) {
			RequestStateModel p = VSQUEUE.poll();
			if(p!=null) {
				totalDataLength = totalDataLength - p.length();
			}
			return p;
		}
	}
	
	public void add(RequestStateModel obj) {
		logger.debug("PREPARE_UD=" + GlobalStateHolder.INSTANCE.cachedMinSize() + "," + GlobalStateHolder.INSTANCE.cachedMaxSize() + "," + GlobalStateHolder.INSTANCE.inExCallTimeout());
		logger.debug("EX_DATE_SIZE=" + totalDataLength + ", MIN_SIZE=" + GlobalStateHolder.INSTANCE.cachedMinSize());
		synchronized (VSQUEUE) {
//			VSQUEUE.add(obj);
			boolean add_result = VSQUEUE.offer(obj);
			if(add_result) {
				//if the queue has been full, not insert again
				totalDataLength += obj.length();
			}
			
			
			
//			if(totalDataLength>GlobalStateHolder.INSTANCE.cachedMinSize()) {
//				final List<RequestStateModel> upload_date = new LinkedList<>(_list_);
//				_list_.clear();
//				totalDataLength = 0l;
//				//直接进行上传操作
//				scheduExec.schedule(new Runnable() {
//					
//					@Override
//					public void run() {
//						try {
//							RemoteUtil.uploadPerfData(upload_date);
//						}catch(Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}, 0, TimeUnit.MILLISECONDS);
//			}
			if(totalDataLength>GlobalStateHolder.INSTANCE.cachedMaxSize()) {
				//TODO 清空，暂时这么处理
				VSQUEUE.clear();
				totalDataLength = 0L;
			}
		}
	}
	
	public Queue<RequestStateModel> requests() {
		return VSQUEUE;
	}
	
	public Long length() {
		return totalDataLength;
	}
	
	public List<RequestStateModel> trigger() {
		synchronized (VSQUEUE) {
			List<RequestStateModel> str_1 = new LinkedList<RequestStateModel>(VSQUEUE);
			VSQUEUE.clear();
			totalDataLength = 0L;
			return str_1;
		}
	}
	
}
