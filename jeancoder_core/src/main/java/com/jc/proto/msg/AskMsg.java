package com.jc.proto.msg;

@SuppressWarnings("serial")
public class AskMsg extends GeneralMsg {
	
    public AskMsg() {
        super();
        setType(MsgType.ASK);
    }
    
    private AskParams params;

    public AskParams getParams() {
        return params;
    }

    public void setParams(AskParams params) {
        this.params = params;
    }
}
