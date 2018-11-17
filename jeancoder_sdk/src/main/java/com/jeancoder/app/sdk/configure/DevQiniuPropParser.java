package com.jeancoder.app.sdk.configure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public abstract class DevQiniuPropParser extends DevPropParser {

	public DevQiniuPropParser(String patternString) {
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
		return PropType.QINIU;
	}

	@Override
	public PropItem getInstance(String id) {
		PropItem instance = new DevQiniuProp();
		instance.setId(id);
		instance.setIsDefault(true);
		return instance;
	}

}
