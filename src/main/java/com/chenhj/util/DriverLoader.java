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
	 /**
	  * 加载对应路径jar包里的对应驱动
	  * @param fname  对应路径  如: lib4/ojdbc14.jar
	  * @param dname  驱动名  如: oracle.jdbc.driver.OracleDriver
	  * @return 加载到的驱动    java.sql.Driver
	  * @throws Exception
	  * @author tangxr
	  */
	 public static Driver getDriverLoaderByName (String fname,String dname)throws Exception {
		  if(StringUtils.isBlank(fname)){
			  logger.error("对应的驱动路径不存在,请确认.");
			  return null;
		  }
		  if(StringUtils.isBlank(dname)){
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
		  return (Driver) loader.loadClass(dname).newInstance();
  }
}
