package com.jeancoder.core.resource.proc;

import com.jeancoder.core.resource.type.ResourceType;
import com.jeancoder.core.result.Result;

@SuppressWarnings("serial")
public class ViewResource extends AbstractResource {

	public ViewResource(String rescontent, ResourceType restype) {
		super(rescontent, restype);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getResult() {
		return new Result().setView(getRescontent());
	}

}
