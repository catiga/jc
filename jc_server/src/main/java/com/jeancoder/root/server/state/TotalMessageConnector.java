package com.jeancoder.root.server.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jc.proto.msg.GeneralMsg;

public class TotalMessageConnector {

	private static final Map<String, GeneralMsg> _MESSAGE_CENTS_ = new ConcurrentHashMap<String, GeneralMsg>();
	
	public static boolean checkMsgId(String msg_id) {
		synchronized(_MESSAGE_CENTS_) {
			return _MESSAGE_CENTS_.containsKey(msg_id);
		}
	}
	
	public static void syncMsg(String msg_id, GeneralMsg message) {
		synchronized (_MESSAGE_CENTS_) {
			GeneralMsg empty = _MESSAGE_CENTS_.get(msg_id);
			if(empty==null) {
				throw new RuntimeException("MESSAGE WAS NOT INITIALIZED CORRECTLY");
			}
			if(!empty.isEmpty()) {
				throw new RuntimeException("MESSAGE REPEAT, COULD NOT BE SYNCED.");
			}
			_MESSAGE_CENTS_.put(msg_id, message);
		}
	}
	
	public static boolean addMsg(String msg_id) {
		synchronized (_MESSAGE_CENTS_) {
			GeneralMsg empty = _MESSAGE_CENTS_.get(msg_id);
			if(empty!=null) {
				return false;
			}
			_MESSAGE_CENTS_.put(msg_id, GeneralMsg.EMPTY);
			return true;
		}
	}
	
	public static GeneralMsg consumeMsg(String msg_id) {
		synchronized (_MESSAGE_CENTS_) {
			GeneralMsg msg = _MESSAGE_CENTS_.get(msg_id);
			if(msg!=null&&!msg.isEmpty()) {
				_MESSAGE_CENTS_.remove(msg_id);
			}
			return msg;
		}
	}
}
