package com.jeancoder.app.sdk.runner;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeancoder.core.common.Common;
import com.jeancoder.core.http.JCThreadLocal;
import com.jeancoder.core.resource.proc.Application;
import com.jeancoder.core.resource.proc.Resource;
import com.jeancoder.core.result.Result;
import com.jeancoder.core.result.ResultType;

public class Runner {
	
	public static Result  run(Application application, String resourceId) throws IOException {
		Resource resource = getResource(application, resourceId);
		if (resource == null) {
			return null;
		}
		// 如果返回的数据对象不是 Result 则默认设置 DATA类型的Result
		Object resultObj = resource.getResult();
		if (resultObj == null){
			return new Result();
		}
		if (!(resultObj instanceof Result)) {
			return new Result().setData(resultObj);
		}
		return (Result)resultObj;
	}
	
	
	/**
	 * 对得到的Result进行换换
	 * @param application
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	public static Result getResult(Application application, HttpServletRequest req, HttpServletResponse res) throws IOException {
		Result result =  JCThreadLocal.getResult();
		if (result == null) {
			return null;
		}
		
		// 如果是视图资源需要 需要在 template 下查找得到视图资源对应的绝对路径
		if (ResultType.VIEW_RESOURCE.equals(result.getResultType())) {
			//如果返回的是 视图资源，则根据视图资源id 找到视图资源
			Resource viewResource = application.getResource(Common.VIEW, Common.VIEW + "/" + result.getResult());
			if (viewResource == null) {
				res.setContentType("text/html;charset=UTF-8");
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			// 将原视图资源id 换成 资源绝对路径路径
			result.setView(((Result)viewResource.getResult()).getResult() );
		} 
		return result;
	}
	
	private static Resource getResource(Application application, String resourceId){
		if(resourceId.indexOf(Common.STATIC) == 0) {
			return application.getResource(Common.STATIC, resourceId);
		}
		return application.getResource(Common.ENTRY, resourceId);
	}
	
}
