package com.jeancoder.jdbc;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jeancoder.jdbc.bean.JCBean;
import com.jeancoder.jdbc.bean.JCID;
import com.jeancoder.jdbc.bean.JCNotColumn;
import com.jeancoder.jdbc.exception.IDEmptyException;
import com.jeancoder.jdbc.exception.IDNotFoundException;
import com.jeancoder.jdbc.sql.SqlParser;

public class JCME2SQL {
	
	static SimpleDateFormat _sdf_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static <T> SqlParser generateInsert(T entity) {
		Annotation[] ans = entity.getClass().getAnnotations();
		String tbname = null;
		for(Annotation an : ans) {
			if(an instanceof JCBean) {
				tbname = ((JCBean)an).tbname();
				break;
			}
		}
		assert tbname!=null;
		Field[] fields = entity.getClass().getDeclaredFields();
		StringBuffer buf = new StringBuffer();
		StringBuffer val = new StringBuffer();
		buf.append("insert into ");
		buf.append(tbname);
		buf.append("(");
		val.append("values (");
		for(Field f : fields) {
			if(f.getName().startsWith("$")||f.getName().equals("metaClass")) {
				continue;
			}
			try {
				JCNotColumn not_column = f.getAnnotation(JCNotColumn.class);
				if(not_column!=null) {
					continue;
				}
				PropertyDescriptor pd = new PropertyDescriptor(f.getName(), entity.getClass());
				Method read_method = pd.getReadMethod();
				Object value = read_method.invoke(entity);
				if(value==null) {
					continue;
				}
				val.append(convert(value) + ",");
			}catch(Exception e) {
				continue;
			}
			buf.append(f.getName() + ",");
		}
		String sql = buf.substring(0, buf.length() - 1) + ")" + " " + val.substring(0, val.length() - 1) + ")";
		
		SqlParser par = new SqlParser(sql);
		return par;
	}
	
	public static <T> SqlParser fullUpdate(T entity) {
		Annotation[] ans = entity.getClass().getAnnotations();
		String tbname = null;
		
		for(Annotation an : ans) {
			if(an instanceof JCBean) {
				tbname = ((JCBean)an).tbname();
			} 
		}
		assert tbname!=null;
		
		Field[] fields = entity.getClass().getDeclaredFields();
		StringBuffer buf = new StringBuffer();
		buf.append("update ");
		buf.append(tbname + " ");
		buf.append("set ");
		
		String idname = null;
		Object idvalue = null;
		for(Field f : fields) {
			if(f.getName().startsWith("$")||f.getName().equals("metaClass")) {
				continue;
			}
			
			try {
				JCNotColumn not_column = f.getAnnotation(JCNotColumn.class);
				if(not_column!=null) {
					continue;
				}
				PropertyDescriptor pd = new PropertyDescriptor(f.getName(), entity.getClass());
				Method read_method = pd.getReadMethod();
				Object value = read_method.invoke(entity);
//				
				value = convert(value);
				
				JCID jcid = f.getAnnotation(JCID.class);
				if(jcid!=null) {
					idname = f.getName();
					idvalue = value;
				} else {
					buf.append(f.getName() + "=" + value + ",");
				}
			}catch(Exception e) {
				continue;
			}
		}
		if(idname==null) {
			throw new IDNotFoundException(entity.getClass().getName());
		}
		if(idvalue==null) {
			throw new IDEmptyException(entity.getClass().getName());
		}
		String sql = buf.substring(0, buf.length() - 1);
		sql = sql + " where " + idname + "=" + idvalue;
		SqlParser par = new SqlParser(sql);
		return par;
	}
	
	public static String convert(Object value) {
		String ret = null;
		if(value==null) {
			ret = "NULL";
		} else {
			if(value instanceof String) {
				value = "'" + value + "'";
			} else if(value instanceof Date) {
				value = "'" + _sdf_.format(value) + "'";
			}
			ret = value.toString();
		}
		return ret;
	}
	
}
