package com.jeancoder.cap.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jeancoder.core.configure.PropItem;
import com.jeancoder.core.configure.PropType;


public abstract class DevDatabasePropParser extends DevPropParser{

	public DevDatabasePropParser(String patternString) {
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
		return PropType.DATABASE;
	}

	@Override
	public PropItem getInstance(String id) {
		PropItem instance = new DevDatabaseProp();
		instance.setId(id);
		return instance;
	}
	
	public static void main(String[] args) {
		Pattern p = Pattern.compile("database.([^.]*).default");
		String s = "database.local.default";
		Matcher m = p.matcher(s);
		m.find();
		System.out.println(m.groupCount());
		System.out.println(m.group(1));
	}
}
