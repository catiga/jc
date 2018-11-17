package com.jeancoder.jdbc.template;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.jeancoder.app.sdk.source.DatabaseSource;
import com.jeancoder.app.sdk.source.LoggerSource;
import com.jeancoder.core.log.JCLogger;
import com.jeancoder.core.power.DatabasePower;
import com.jeancoder.core.power.result.JeancoderResultSet;
import com.jeancoder.jdbc.sql.SqlParser;

public abstract class GeneralJcDaoTemplate<T> {
	
	private static final JCLogger LOGGER = LoggerSource.getLogger(GeneralJcDaoTemplate.class.getName());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <X> Class<X> getSuperClassGenricType(final Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Class getSuperClassGenricType(final Class clazz,	final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}

		return (Class) params[index];
	}
	
	@SuppressWarnings("deprecation")
	protected long countSql(SqlParser par, Object...params) {
		JeancoderResultSet jrs = null;
		try {
			String count_sql = par.toCountSql();
			if(count_sql==null) {
				return 0l;
			}
			DatabasePower dp = DatabaseSource.getDatabasePower();
			jrs = dp.doQuery(count_sql, params);
			ResultSet rs = jrs.getResultSet();
			while(rs.next()) {
				return rs.getLong(1);
			}
		}catch(Exception e) {
			LOGGER.error("", e);
		}finally{
			if(jrs!=null)
				jrs.closeConnection();
		}
		return -1L;
	}
	
	protected SqlParser buildSql(Class<T> mapclass, String sql) {
		SqlParser par = new SqlParser(sql);
		return par;
	}
	
	protected Object readTypeValue(int type_code, String type_name, Field field, ResultSet rs) throws SQLException {
		String fld_clz = field.getGenericType().getTypeName();
		Object value = rs.getObject(field.getName());
		if(value==null) {
			return value;
		}
		type_name = type_name.toUpperCase();
		if(type_name.equals("TINYINT")) {
			if(!fld_clz.equals(Boolean.class.getName()))
				value = rs.getInt(field.getName());
			else
				value = rs.getBoolean(field.getName());
		} else if(type_name.equals("DATE")) {
			value = rs.getDate(field.getName());
		} else if(type_name.equals("TIME")) {
			value = rs.getDate(field.getName());
		} else if(type_name.equals("BIGINT")) {
			Long long_value = rs.getLong(field.getName());
			if(fld_clz.equals(BigInteger.class.getName())) {
				value = BigInteger.valueOf(long_value);
			} else {
				value = long_value;
			}
		}
//		if(value==null) {
//			value = rs.getObject(field.getName());
//		}
		return value;
	}
	
	protected T generateInstance(Class<T> mapclass) {
		T instance = null;
		Object ret_value = null;
		if(mapclass==Long.class) {
			ret_value = 0x0L;
		} else if(mapclass==Integer.class) {
			ret_value = 0x0;
		} else if(mapclass==Byte.class) {
			ret_value = (byte)0x0;
		} else if(mapclass==Double.class) {
			ret_value = (double)0x0;
		} else if(mapclass==Float.class) {
			ret_value = (float)0x0;
		} else if(mapclass==Short.class) {
			ret_value = (short)0x0;
		} else if(mapclass==Character.class) {
			ret_value = (char)0x0;
		} else if(mapclass==Boolean.class) {
			ret_value = false;
		} else if(mapclass==BigDecimal.class) {
			ret_value = new BigDecimal(0);
		} else if(mapclass==BigInteger.class) {
			ret_value = BigInteger.valueOf(0L);
		}
		if(ret_value!=null) {
			instance = cast(ret_value);
		} else {
			try {
				instance = mapclass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public T cast(Object obj) {
        return (T) obj;
    }
	
	protected T wrapperInstance(Class<T> mappclass, ResultSet rsindex)  throws SQLException, IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		boolean is_base = this.isBaseType(mappclass);
		T instance = null;
		if(is_base) {
			instance = wrapperBaseInstance(mappclass, rsindex);
		} else {
			instance = wrapperObjInstance(mappclass, rsindex);
		}
		return instance;
	}
	
	private T wrapperBaseInstance(Class<T> mappclass, ResultSet rsindex) throws SQLException, IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		T instance = generateInstance(mappclass);
		Object value = rsindex.getObject(1);
		instance = cast(value);
		return instance;
	}
	
	private T wrapperObjInstance(Class<T> mappclass, ResultSet rsindex) throws SQLException, IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		T instance = generateInstance(mappclass);
		ResultSetMetaData metadata = rsindex.getMetaData();
		int col_size = metadata.getColumnCount();
		for(int i=1; i<=col_size; i++) {
			String column_name = metadata.getColumnName(i);
			String column_alias_name = metadata.getColumnLabel(i);
			if(column_alias_name!=null) {
				column_name = column_alias_name;
			}
			//mapped java class
			String column_class_name = metadata.getColumnClassName(i);
			//type number
			int column_type = metadata.getColumnType(i);
			//type name
			String column_type_name = metadata.getColumnTypeName(i);
			
			LOGGER.debug(column_name + "--" + column_class_name + "--" + column_type + "--" + column_type_name);
			
			Field[] fields = instance.getClass().getDeclaredFields();
			for(Field f : fields) {
				if(f.getName().equals(column_name)) {
					PropertyDescriptor pd = new PropertyDescriptor(f.getName(), instance.getClass());
					String set_method_name = pd.getWriteMethod().getName();
					Method set_method = instance.getClass().getDeclaredMethod(set_method_name, f.getType());
					Object value = this.readTypeValue(column_type, column_type_name, f, rsindex);
					try {
						set_method.invoke(instance, value);
					} catch(RuntimeException rex) {
						LOGGER.error(f.getName() + "=" + value + ", type miss match:" + f.getGenericType().getTypeName(), rex);
					}
				}
			}
		}
		return instance;
	}
	
	final static Class<?>[] base_array = {Long.class, Integer.class, Short.class, Byte.class, Float.class, Double.class, Character.class, Boolean.class, BigInteger.class, BigDecimal.class, String.class};
	
	protected boolean isBaseType(Class<T> mapclass) {
		boolean ret = false;
		for(Class<?> c : base_array) {
			if(mapclass.getTypeName().equals(c.getTypeName())) {
				ret = true;
			}
		}
		return ret;
	}
	
}
