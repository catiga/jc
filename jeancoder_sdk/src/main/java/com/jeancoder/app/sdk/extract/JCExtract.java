package com.jeancoder.app.sdk.extract;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

import com.jeancoder.app.sdk.source.LoggerSource;
import com.jeancoder.app.sdk.source.RequestSource;
import com.jeancoder.core.http.JCRequest;
import com.jeancoder.core.log.JCLogger;

public class JCExtract {

	private static final JCLogger LOGGER = LoggerSource.getLogger(JCExtract.class.getName());
	
	@SuppressWarnings("deprecation")
	public static <T> T fromRequest(Class<T> mapclass) {
		JCRequest req = RequestSource.getRequest();
		T instance = null;
		try {
			instance = mapclass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assert instance!=null;
		Field[] fields = instance.getClass().getDeclaredFields();
		for(Field f : fields) {
			String param_value = req.getParameter(f.getName());
			if(param_value==null) {
				continue;
			}
			try {
				PropertyDescriptor pd = new PropertyDescriptor(f.getName(), instance.getClass());
				String set_method_name = pd.getWriteMethod().getName();
				Method set_method = instance.getClass().getDeclaredMethod(set_method_name, f.getType());
				Object value = transfer(f, param_value);
				
				set_method.invoke(instance, value);
			} catch(Exception rex) {
				LOGGER.error(f.getName() + "=" + param_value + ", type miss match:" + f.getGenericType().getTypeName(), rex);
			}
		}
		
		return instance;
	}
	
	
	
	
	
	protected static Object transfer(Field f, String param_value) throws ClassNotFoundException {
		String type_name = f.getGenericType().getTypeName();
		return transfer(type_name, param_value);
	}
	
	protected static Object transfer(String type_name, String param_value) throws ClassNotFoundException {
		Object ret_val = null;
		if(type_name.equals(BigInteger.class.getName())) {
			ret_val = BigInteger.valueOf(Long.valueOf(param_value));
		} else if(type_name.equals(Long.class.getName())) {
			ret_val = Long.valueOf(param_value);
		} else if(type_name.equals(Integer.class.getName())) {
			ret_val = Integer.valueOf(param_value);
		} else if(type_name.equals(Float.class.getName())) {
			ret_val = Float.valueOf(param_value);
		} else if(type_name.equals(Double.class.getName())) {
			ret_val = Double.valueOf(param_value);
		} else if(type_name.equals(Short.class.getName())) {
			ret_val = Short.valueOf(param_value);
		} else if(type_name.equals(Byte.class.getName())) {
			ret_val = Byte.valueOf(param_value);
		} else if(type_name.equals(Boolean.class.getName())) {
			ret_val = Integer.valueOf(param_value)>0?true:false;
		} else if(type_name.equals(String.class.getName())) {
			ret_val = param_value;
		} else if(type_name.equals(BigDecimal.class.getName())) {
			ret_val = new BigDecimal(param_value.toString());
		} else if(type_name.equals(Date.class.getName())) {
			//TODO
			
		} else if(type_name.equals(Timestamp.class.getName())) {
			//TODO
			
		}
			
		return ret_val;
	}
	
}
