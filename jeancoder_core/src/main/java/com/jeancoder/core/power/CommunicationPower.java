package com.jeancoder.core.power;

import java.util.List;

import com.jeancoder.core.exception.JeancoderException;

public interface CommunicationPower {

	/**
	 * 以获取String的方式工作
	 * @param path 不需要包含appcode
	 * @param params
	 * @return
	 * @throws JeancoderException
	 */
	public String doworkAsString(String path,List<CommunicationParam> params) throws JeancoderException;
	
	/**
	 * 在mode为Network时可以指定调用方式 以获取String的方式工作
	 * @param path  不需要包含appcode
	 * @param params
	 * @param method GET or POST
	 * @return
	 * @throws JeancoderException
	 */
	public String doworkAsString(String path,List<CommunicationParam> params,CommunicationMethod method) throws JeancoderException;
}
