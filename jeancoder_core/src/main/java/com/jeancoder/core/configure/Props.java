package com.jeancoder.core.configure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Props implements Serializable{
	private static final long serialVersionUID = -5720093093503194092L;
	
	private Map<PropType,List<PropItem>> allprop = new HashMap<PropType,List<PropItem>>();
	
	/**
	 * 存在同类型 同id就更新 不存在就添加
	 * @param propItem
	 */
	public synchronized void addProp(PropItem propItem) {
		if(!allprop.containsKey(propItem.getPropType())) {
			allprop.put(propItem.getPropType(), new ArrayList<PropItem>());
		}
		int ind = -1;
		int i=0;
		for(PropItem existsItem : getProp(propItem.getPropType())) {
			if(existsItem.getId().equals(propItem.getId())) {
				ind = i;
				return;
			}
			i++;
		}
		if(ind != -1) {
			allprop.get(propItem.getPropType()).set(ind, propItem);
		}else {
			allprop.get(propItem.getPropType()).add(propItem);
		}
	}
	
	public List<PropItem> getProp(PropType propType){
		return allprop.get(propType);
	}
	
	public PropItem getProp(PropType propType,String id) {
		if(getProp(propType) == null) {
			return null;
		}
		for(PropItem propItem : getProp(propType)) {
			if(propItem.getId().equals(id)) {
				return propItem;
			}
		}
		return null;
	}
	
	public PropItem getDefault(PropType propType) {
		if(getProp(propType) == null) {
			return null;
		}
		for(PropItem propItem : getProp(propType)) {
			if(propItem.getIsDefault()) {
				return propItem;
			}
		}
		return null;
	}
}
