package com.jc.channel;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jc.proto.msg.GeneralMsg;

@Deprecated
@SuppressWarnings("serial")
public class MessageExchangeCents implements Serializable {

	private final Map<String, GeneralMsg> _MESSAGE_CENTS_ = new ConcurrentHashMap<String, GeneralMsg>();
	
	private MessageExchangeCents() {}
	
	static private MessageExchangeCents __instance__ = null;
	
	public static MessageExchangeCents getExchanger() {
		if(__instance__==null) {
			synchronized (MessageExchangeCents.class) {
				if(__instance__==null) {
					__instance__ = new MessageExchangeCents();
				}
			}
		}
		return __instance__;
	}
	
	public boolean add(String msg_id) {
		synchronized (_MESSAGE_CENTS_) {
			GeneralMsg exist = _MESSAGE_CENTS_.get(msg_id);
			if(exist==null) {
				_MESSAGE_CENTS_.put(msg_id, GeneralMsg.EMPTY);	//pre reserve
				return true;
			}
			return false;
		}
	}
}
