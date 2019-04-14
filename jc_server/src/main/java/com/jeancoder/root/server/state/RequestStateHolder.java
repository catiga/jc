package com.jeancoder.root.server.state;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.root.server.queue.VsConsumer;

public class RequestStateHolder {
	
	public static final Integer _DEFA_SIZE_ = 200*1024*1024;	//默认队列大小
	
	protected static Logger logger = LoggerFactory.getLogger(RequestStateHolder.class);
	
	//public final Queue<RequestStateModel> VSQUEUE = new LinkedList<>();
	//public final Queue<RequestStateModel> VSQUEUE = new ArrayBlockingQueue<RequestStateModel>(_DEFA_SIZE_);
	
	private LinkedBlockingQueue<RequestStateModel> VSQUEUE = null; //new LinkedBlockingQueue<>(_DEFA_SIZE_);
	
	private static RequestStateHolder INSTANCE = null;;
	
	private Long totalDataLength = 0L;
	
	private volatile Long nowCapacity = 0L;
	
	private RequestStateHolder() {
		VSQUEUE = new LinkedBlockingQueue<>(_DEFA_SIZE_);
		Thread thread = new Thread(new VsConsumer());
		thread.start();
	}
	
	public static final RequestStateHolder getInstance() {
		if(INSTANCE==null) {
			synchronized (RequestStateHolder.class) {
				if(INSTANCE==null) {
					INSTANCE = new RequestStateHolder();
				}
			}
		}
		return INSTANCE;
	}
	
	public RequestStateModel popup() {
		RequestStateModel p = null;
		try {
			p = VSQUEUE.take();
		} catch (InterruptedException e) {
			logger.error("TAKE QUEUE INTERRUPTED:", e);
		}	//阻塞
		return p;
	}
	
	public void add(RequestStateModel obj) {
//		logger.debug("PREPARE_UD=" + GlobalStateHolder.INSTANCE.cachedMinSize() + "," + GlobalStateHolder.INSTANCE.cachedMaxSize() + "," + GlobalStateHolder.INSTANCE.inExCallTimeout());
//		logger.debug("EX_DATE_SIZE=" + totalDataLength + ", MIN_SIZE=" + GlobalStateHolder.INSTANCE.cachedMinSize());
//		synchronized (VSQUEUE) {
////			VSQUEUE.add(obj);
//			boolean add_result = VSQUEUE.offer(obj);
//			if(add_result) {
//				//if the queue has been full, not insert again
//				totalDataLength += obj.length();
//			}
//			if(totalDataLength>GlobalStateHolder.INSTANCE.cachedMaxSize()) {
//				//TODO 清空，暂时这么处理
//				VSQUEUE.clear();
//				totalDataLength = 0L;
//			}
//		}
	}
	
	public void addNew(RequestStateModel obj) {
		logger.info("PREPARE_VSSWITCH=" + GlobalStateHolder.INSTANCE.getVsSwitch() + ", VSEXETIMEOUT=" + GlobalStateHolder.INSTANCE.getInternalExecuteTimeout());
		boolean add_result = VSQUEUE.offer(obj);	//会丢失元素，无所谓
		if(add_result) {
			totalDataLength += obj.length();
			nowCapacity++;
		}
//		if(nowCapacity>_DEFA_SIZE_) {
//			//TODO 清空，暂时这么处理
//			VSQUEUE.clear();
//			totalDataLength = 0L;
//		}
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
