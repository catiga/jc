package com.jc.proto.msg.ct;

import com.jc.proto.msg.GeneralMsg;
import com.jeancoder.root.container.ContainerMaps;

@SuppressWarnings("serial")
public class VmContainerMsg extends GeneralMsg {
	
	ContainerMaps conthos;

	public ContainerMaps getConthos() {
		return conthos;
	}

	public void setConthos(ContainerMaps conthos) {
		this.conthos = conthos;
	}
	
	public VmContainerMsg(ContainerMaps hos) {
		super();
		this.conthos = hos;
	}
}
