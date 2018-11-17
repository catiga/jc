package com.jeancoder.core.resource.proc;


import com.jeancoder.core.resource.type.ResourceType;
import com.jeancoder.core.result.Result;


/**
 * 静态资源  css  js  不需要特殊的模板 渲染
 * @author huangjie
 *
 */
@SuppressWarnings("serial")
public class StaticResource extends AbstractResource {

	public StaticResource(String respath, ResourceType restype) {
		super(respath, restype);
	}

	@Override
	public Object getResult() {
		return  new Result().setStaticName(getRescontent());
	}
	
}
