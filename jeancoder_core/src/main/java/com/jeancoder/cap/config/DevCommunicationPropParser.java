package com.jeancoder.cap.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public abstract class DevCommunicationPropParser extends DevPropParser {

	public DevCommunicationPropParser(String patternString) {
		super(patternString);
	}

	@Override
	public String getId(String pkey) {
		Pattern p = Pattern.compile(getPatternString());
		Matcher m = p.matcher(pkey);
		if(m.find()) {
			return m.group(1);
		}
		return null;
	}

	@Override
	public PropType getType() {
		return PropType.COMMUNICATION;
	}

	@Override
	public PropItem getInstance(String id) {
		DevCommunicationProp instance = new DevCommunicationProp();
		instance.setId(id);
		instance.setIsDefault(false);
		return instance;
	}
}
