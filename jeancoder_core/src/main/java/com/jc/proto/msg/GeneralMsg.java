package com.jc.proto.msg;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public abstract class GeneralMsg  implements Serializable {
	
	private String unionid;
    
    private MsgType type;
    
    //必须唯一，否者会出现channel调用混乱
    private String clientId;

    //初始化客户端id
    public GeneralMsg() {
        this.clientId = Constants.getClientId();
        this.unionid = UUID.randomUUID().toString();
    }

    public String getUnionid() {
		return unionid;
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
