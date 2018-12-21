package com.chenhj.constant;


/**
 * 
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: Constant.java
* @Description: 程序所有常量
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年8月6日 下午3:43:46 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月6日     chenhj          v1.0.0               修改原因
 */
public enum Constant {
	INSTANCE;
	/**
	 * 配置文件
	 */
	public static final String CONFIG_NAME = "global.properties";
	public static final String LOGBACK_CONFIG_NAME = "logback.xml";
	public static final String JSON = "json";
	public static final String TXT = "txt";
	public static final String EXCEL = "excel";
	public static final String SQL = "sql";
	public static final String CSV = "csv";
	
	public static final String GET ="GET";
	public static final String POST ="POST"; 
	public static final String DELETE ="DELETE";
	public static final String ENCODE_UTF8="UTF-8";
	public static final byte SUCCESS = 1;
	 /**
     * 逗号分隔符
     */
    public static final String COMMA_SIGN=",";
    /**
 	 * 程序版本
 	 */
 	public  static final String VERSION = "V1.0.2"; 	
 	 /**
 	  * 程序信息
 	 */
 	 public  static final String VERSION_MAG = "ES数据导出工具";
 	 
 	 
}
