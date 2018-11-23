package com.jeancoder.cap.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public abstract class DevSysConfigPropParser extends DevPropParser {

	public DevSysConfigPropParser(String patternString) {
		super(patternString);
		// TODO Auto-generated constructor stub
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
		return PropType.SYS;
	}

	@Override
	public PropItem getInstance(String id) {
		DevSysConfigProp instance = new DevSysConfigProp();
		instance.setId(id);
		instance.setIsDefault(true);
		return instance;
	}

}
