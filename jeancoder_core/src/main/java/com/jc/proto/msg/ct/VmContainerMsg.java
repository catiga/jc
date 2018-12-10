package com.jc.proto.msg.ct;

import java.util.Map;

import com.jc.proto.msg.SyncMsg;
import com.jeancoder.root.bean.ContainerBean;
import com.jeancoder.root.container.ContainerMaps;
import com.jeancoder.root.container.core.BCID;

@SuppressWarnings("serial")
public class VmContainerMsg extends SyncMsg {
	
	Map<BCID, ContainerBean> conthos;

	@Override
	public Object getResData() {
		return conthos;
	}

	public void setConthos(Map<BCID, ContainerBean> conthos) {
		this.conthos = conthos;
	}
	
	public VmContainerMsg(ContainerMaps hos) {
		super();
		this.conthos = ContainerBean.cons(hos);
	}
	
}
