package com.jeancoder.core.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.power.exception.PowerNameErrorException;

/**
 * 工厂
 * @author wow zhang_gh@cpis.cn
 * @date 2018年6月8日
 */
public class PowerHandlerFactory {
	/**
	 * 仓库，用于存放已经生产好的
	 */
//	private static List<PowerHandler> warehouse = new ArrayList<PowerHandler>();
	private static Map<String, List<PowerHandler>> warehouseMap = new HashMap<String, List<PowerHandler>>();
	/**
	 * 建造一个实例 并且放在仓库中 如果仓库中已存在该实例 则直接返回
	 * @param powerName
	 * @param config
	 * @return
	 */
	public static PowerHandler generatePowerHandler(PowerName powerName,PowerConfig config,String appCode) throws JeancoderException{
		PowerHandler powerHandler = null;
		try {
			powerHandler = powerName.getInstance();
			powerHandler.init(config);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new PowerNameErrorException(powerName);
		} catch (JeancoderException e) {
			throw e;
		}
		if(powerHandler != null) {
			powerHandler.setId(config.getId());
			powerHandler.setDefault(config.isDefault());
			
			//除了一次性的通信操作器 其他的都放到仓库中
			if(!powerName.equals(PowerName.COMMUNICATION)) {
				addWarehouse(powerHandler, appCode);
			}
		}
		return powerHandler;
	}
	
	/**
	 * 根据 类型和id 从仓库中获取一个实例
	 * @return
	 */
	public static PowerHandler getPowerHandler(PowerName powerName,String id, String appCode) {
		List<PowerHandler>  list = getListByAppCode(appCode);
		for(PowerHandler powerHandler : list) {
			if((powerHandler.getClass() == powerName.getClazz() || powerHandler.getClass().equals(powerName.getClazz())) && powerHandler.getId().equals(id)){
				return powerHandler;
			}
		}
		return null;
	}
	
	/**
	 * 从仓库中获取该类型的默认实例
	 * @param powerName
	 * @return
	 */
	public static PowerHandler getPowerHandler(PowerName powerName, String appCode) {
		List<PowerHandler>  list = getListByAppCode(appCode);
		for(PowerHandler powerHandler : list) {
			if((powerHandler.getClass() == powerName.getClazz() || powerName.getClass().equals(powerName.getClazz())) && powerHandler.isDefault()){
				return powerHandler;
			}
		}
		return null;
	}
	
	/**
	 * 向仓库中添加一个实例
	 * @param powerHandler
	 */
	private static synchronized void addWarehouse(PowerHandler powerHandler, String appCode) {
		List<PowerHandler>  list = getListByAppCode(appCode);
		if (list.isEmpty()) {
			list.add(powerHandler);
			warehouseMap.put(appCode, list);
		}
		boolean beAdd = true;
		int i=0;
		Iterator<PowerHandler> iterator = list.iterator();
		while(iterator.hasNext()) {
			if(iterator.next().getId().equals(powerHandler.getId())) {
				list.set(i, powerHandler);
				beAdd = false;
				break;
			}
			i++;
		}
		
		if(beAdd) {
			list.add(powerHandler);
			warehouseMap.put(appCode, list);
		}
	}
	
	private static List<PowerHandler> getListByAppCode(String appCode){
		List<PowerHandler> warehouseList =  warehouseMap.get(appCode);
		if (warehouseList == null) {
			return new ArrayList<PowerHandler>();
		}
		return warehouseList;
	}
	
}
