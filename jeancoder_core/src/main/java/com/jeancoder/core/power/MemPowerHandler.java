package com.jeancoder.core.power;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeancoder.core.exception.JeancoderException;
import com.jeancoder.core.util.MemCodeUtil;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MemPowerHandler extends PowerHandler implements MemPower {
	
	private static Logger logger = LoggerFactory.getLogger(MemPowerHandler.class.getName());

	private MemPowerConfig config;

	private static MemCachedClient _client_instance_ = null;
	
	private String k(String k) {
		return MemCodeUtil.getk(k);
	}

	@Override
	public void init(PowerConfig defconfig) throws JeancoderException {
		config = (MemPowerConfig) defconfig;
		if (_client_instance_ == null) {
			synchronized (this) {
				if (_client_instance_ == null) {
					String[] servers = { config.getServer() };
					SockIOPool pool = SockIOPool.getInstance();
					pool.setServers(servers);
					pool.setFailover(true);
					pool.setInitConn(10);
					pool.setMinConn(5);
					pool.setMaxConn(250);
					pool.setMaintSleep(30);
					pool.setNagle(false);
					pool.setSocketTO(3000);
					pool.setAliveCheck(true);
					pool.initialize();
					_client_instance_ = new MemCachedClient();
				}
			}
		}
	}

	@Override
	public boolean setUntil(String k, Object v, Long exp) {
		if(exp==null||exp.equals(0l)||exp<0l) {
			return false;
		}
		Date exp_time = new Date(System.currentTimeMillis() + exp*1000);
		return _client_instance_.set(k(k), v, exp_time);
	}

	@Override
	public boolean set(String k, Object e) {
		return _client_instance_.set(k(k), e);
	}

	@Override
	public Object get(String k) {
		logger.info(config.getServer() + ":::" + config.getId());
		return _client_instance_.get(k(k));
	}

	@Override
	public Object delete(String k) {
		Object value = _client_instance_.get(k(k));
		_client_instance_.delete(k(k));
		return value;
	}
	
	 
	
	
	

	@Override
	public String getAsString(String k) {
		Object value = _client_instance_.get(k(k));
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	@Override
	public String deleteAsString(String k) {
		Object value = _client_instance_.get(k(k));
		_client_instance_.delete(k(k));
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	@Override
	public boolean setAsString(String k, String e) {
		return _client_instance_.set(k(k), e);
	}

	@Override
	public boolean setUntilAsString(String k, String v, Long exp) {
		if(exp==null||exp.equals(0l)||exp<0l) {
			return false;
		}
		Date exp_time = new Date(System.currentTimeMillis() + exp*1000);
		return _client_instance_.set(k(k), v, exp_time);
	}

}
