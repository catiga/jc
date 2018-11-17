package com.jeancoder.core.power;

import com.jeancoder.core.exception.JeancoderException;

public interface IPowerHandler {
	void init(PowerConfig config)throws JeancoderException;
}
