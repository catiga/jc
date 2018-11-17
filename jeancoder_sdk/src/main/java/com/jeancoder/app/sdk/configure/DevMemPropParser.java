package com.jeancoder.app.sdk.configure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;

public abstract class DevMemPropParser extends DevPropParser {

	public DevMemPropParser(String patternString) {
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
		return PropType.MEM;
	}

	@Override
	public PropItem getInstance(String id) {
		PropItem instance = new DevMemProp();
		instance.setId(id);
		instance.setIsDefault(true);
		return instance;
	}

	public static void main(String[] args) {
		Pattern p = Pattern.compile("mem.([^.]*).port");
		String s = "mem.cache.port";
		Matcher m = p.matcher(s);
		m.find();
		System.out.println(m.groupCount());
		System.out.println(m.group(1));
	}
}
