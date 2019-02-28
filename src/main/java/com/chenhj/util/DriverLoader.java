/**
 * 
 */
package com.chenhj.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.JdbcConstants;
/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: DriverLoader.java
* @Description: 动态加载驱动jar包
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午4:21:35 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public class DriverLoader {
	private static final Logger logger = LoggerFactory.getLogger(DriverLoader.class);
	private static URLClassLoader loader;
	private static String driverClass;
	 /**
	  * 加载对应路径jar包里的对应驱动
	  * @param fname  对应路径  如: lib4/ojdbc14.jar
	  * @param driver  驱动名  如: oracle.jdbc.driver.OracleDriver
	  * @return 加载到的驱动    java.sql.Driver
	  * @throws Exception
	  * @author tangxr
	  */
	 public static Driver getDriverLoaderByName (String fname,String driver)throws Exception {
		 setDriverClassName(driver);
		  if(StringUtils.isBlank(fname)){
			  logger.error("对应的驱动路径不存在,请确认.");
			  return null;
		  }
		  if(StringUtils.isBlank(driverClass)){
			  logger.error("对应的驱动类的名字不存在.");
			  return null;
		  }
		  File file = new File(fname);
		  if(!file.exists()){
			  logger.error("对应的驱动jar不存在.");
			  return null;
		  }
		  loader = new URLClassLoader(new URL[] { file.toURI().toURL() });
		  loader.clearAssertionStatus();
		  return (Driver) loader.loadClass(driverClass).newInstance();
	 }
	 public static void setDriverClassName(String driver) {
	        if (driver != null && driver.length() > 256) {
	            throw new IllegalArgumentException("driverClassName length > 256.");
	        }

	        if (JdbcConstants.ORACLE_DRIVER2.equalsIgnoreCase(driver)) {
	        	driver = "oracle.jdbc.OracleDriver";
	            logger.warn("oracle.jdbc.driver.OracleDriver is deprecated.Having use oracle.jdbc.OracleDriver.");
	        }
	        driverClass = driver;
	 }
}
