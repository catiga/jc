package com.jc.proto.msg;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public abstract class GeneralMsg  implements Serializable {
	
	protected String unionid;
    
    private MsgType type;
    
    //必须唯一，否者会出现channel调用混乱
    private String clientId;

    //初始化客户端id和消息全局ID
    public GeneralMsg() {
        this.clientId = Constants.getClientId();
        if(unionid==null) {
        	unionid = UUID.randomUUID().toString().replace("-", "");
        }
    }

    public String getUnionid() {
		return unionid;
	}
    
    public void resetUnionid(String unionid) {
    	if(unionid!=null) {
    		this.unionid = unionid;
    	} else {
    		this.unionid = UUID.randomUUID().toString().replace("-", "");
    	}
    }

	public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }
}
