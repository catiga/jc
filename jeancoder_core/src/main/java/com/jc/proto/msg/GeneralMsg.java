package com.jc.proto.msg;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.UUID;

@SuppressWarnings("serial")
public abstract class GeneralMsg  implements MsgProto, Serializable {
	
	public static final EmptyMsg EMPTY = new EmptyMsg();
	
	protected String unionid;
    
    private MsgType type;
    
    //必须唯一，否者会出现channel调用混乱，clientId实际即为instance id，即实例ID
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
        if(clientId!=null) {
        	Constants.setClientId(clientId);
        }
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }
    
    public boolean isEmpty() {
    	if(unionid!=null&&unionid.equals(MsgType.EMPTY.toString())) {
    		return true;
    	}
    	return false;
    }
    
    public Object getResData() {
    	return null;
    }
    
	@Override
	public String digest() {
		return null;
	}

	@Override
	public String version() {
		return null;
	}

	// test method
    public static void main(String[] argc) throws Exception {
    	InetAddress ia = InetAddress.getLocalHost();
		System.out.println(ia);
    }
    
    public String LocalMacByIa(InetAddress ia) throws SocketException {
		//获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) {
			if(i!=0) {
				sb.append("-");
			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			System.out.println("每8位:"+str);
			if(str.length()==1) {
				sb.append("0"+str);
			}else {
				sb.append(str);
			}
		}
		return sb.toString();
	}
}
