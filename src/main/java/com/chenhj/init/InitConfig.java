/**
 * 
 */
package com.chenhj.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.chenhj.config.CommonConfig;
import com.chenhj.config.Config;
import com.chenhj.config.EsConfig;
import com.chenhj.config.FileConfig;
import com.chenhj.config.JdbcConfig;
import com.chenhj.constant.Constant;
import com.chenhj.util.PropertiesUtil;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: InitConfig.java
* @Description: 初始化配置
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午4:34:38 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public class InitConfig {
	 private static Map<String, String> map=null;
	 public static void main(String[] args) throws Exception {
		 InitConfig.init();
	}
	 public static void init() throws Exception {
		 map = new PropertiesUtil(Constant.CONFIG_NAME).loadProperties();
		 if(map!=null){
			 String value;
			 String prefix;
			 String field;
			 EsConfig esConfig = new EsConfig();
			 FileConfig fileConfig = new FileConfig();
			 JdbcConfig jdbcConfig = new JdbcConfig();
			 CommonConfig commonConfig = new CommonConfig();
			 //加载配置文件的参数
			 for(Map.Entry<String, String> entry : map.entrySet()){
		            value  = entry.getValue();
		            String keys[] = entry.getKey().split(Constant.DOT);
		           //点号隔开
		            prefix = keys[0];
		            if(keys.length!=2){
		            	continue;
		            }
		            field = keys[1];
		            switch (prefix) {
						case Constant.ELASTICSEARCH:
							setConfig(esConfig,field,value);
							break;
						case Constant.FILE:
							setConfig(fileConfig,field,value);
							break;
						case Constant.DB:
							setConfig(jdbcConfig,field,value);
							break;
						case Constant.COMMON:
							setConfig(commonConfig,field,value);
							break;
						default:
							break;
						}
		      } 
			 //验证配置文件的参数
			 esConfig.validation();
			 fileConfig.validation();
			 jdbcConfig.validation();
			 //赋值全局变量
			 Config.ES_CONFIG = esConfig;
			 Config.FILE_CONFIG = fileConfig;
			 Config.JDBC_CONFIG = jdbcConfig;
			 Config.COMMON_CONFIG = commonConfig;
		 }
	 }
	 private static  void setConfig(Object obj,String key,String value) throws Exception{
		 Field field; 
         if (null == (field = getField(obj.getClass(), key))) {
             return;
         }
         //不为静态不设置
         if (Modifier.isStatic(field.getModifiers())) {
        	 return;
         }
         if (Modifier.isFinal(field.getModifiers())) {
        	 return;
         }
    	 field.setAccessible(true);
         setField(obj,field, value);
	 } 
	    /**
	     * 通过反射获取待转类clazz中指定字段名的字段,如果字段不存在则返回null
	     *
	     * @param fieldName 去查找待转类中的指定字段
	     * @return 返回指定的字段
	     */
	    private static Field getField(Class<?> clazz,String fieldName) {
	        try {
	            return clazz.getDeclaredField(fieldName);
	        } catch (Exception ignored) {
	        }
	        return null;
	    }
	    /**
	     * 对指定的字段进行设置值,目前仅支持字段类型:
	     * String,boolean,byte,char,short,int,long,float,double
	     *
	     * @param field 指定的字段
	     * @param value 设置值
	     * @throws IllegalAccessException 
	     * @throws IllegalArgumentException 
	     */
	    private static void setField(Object object,Field field, String value) throws Exception {
	        Class<?> type = field.getType();
	        Object par = null;
	            if (String.class.equals(field.getType())) {
	                par = value;
	            } else if (int.class.equals(type) || Integer.class.equals(type)) {
	                par = Integer.valueOf(value);
	            } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
	                par = Boolean.valueOf(value);
	            } else if (long.class.equals(type) || Long.class.equals(type)) {
	                par = Long.valueOf(value);
	            } else if (double.class.equals(type) || Double.class.equals(type)) {
	                par = Double.valueOf(value);
	            } else if (float.class.equals(type) || Float.class.equals(type)) {
	                par = Float.valueOf(value);
	            } else if (short.class.equals(type) || Short.class.equals(type)) {
	                par = Short.valueOf(value);
	            } else if (byte.class.equals(type) || Byte.class.equals(type)) {
	                par = Byte.valueOf(value);
	            } else if (char.class.equals(type)) {
	                par = value.charAt(0);
	            }
	            field.set(object, par);
             }
	public static void requireNonNull(Object obj,String msg){
			if(obj==null){
				throw new NullPointerException(msg);
			}
			if(obj instanceof String){
				String mss = String.valueOf(obj);
				if(StringUtils.isBlank(mss)){
					throw new NullPointerException(msg);
				}
			}
			
		}
	
}
