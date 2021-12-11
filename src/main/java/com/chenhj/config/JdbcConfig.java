/**
 * 
 */
package com.chenhj.config;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.chenhj.init.InitConfig;
import com.chenhj.util.SqlParser;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: JdbcConfig.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午3:45:54 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public class JdbcConfig {
  private String  jdbc_driver_library;
  private String jdbc_driver_class;
  private String   jdbc_connection_string;
  private String   jdbc_user;
  private String  jdbc_password;
  //是否启用
  private boolean   enabled = false;
  private Integer  jdbc_size = 10000;
  private String jdbc_template;
  
  private String tableName;
  
  private Map<String,Integer> fieldMap;
  
  private int jdbc_write_thread_size = 1;
  
  
  
  public String getTableName() {
	return tableName;
}
public Map<String, Integer> getFieldMap() {
	return fieldMap;
}
public void setFieldMap(Map<String, Integer> fieldMap) {
	this.fieldMap = fieldMap;
}
public int getJdbc_write_thread_size() {
	return jdbc_write_thread_size;
}
public void setJdbc_write_thread_size(int jdbc_write_thread_size) {
	this.jdbc_write_thread_size = jdbc_write_thread_size;
}
public String getJdbc_template() {
	return jdbc_template;
  }
	public void setJdbc_template(String jdbc_template) {
		this.jdbc_template = jdbc_template;
	}
	public String getJdbc_driver_library() {
		return jdbc_driver_library;
	}
	public void setJdbc_driver_library(String jdbc_driver_library) {
		this.jdbc_driver_library = jdbc_driver_library;
	}
	public String getJdbc_driver_class() {
		return jdbc_driver_class;
	}
	public void setJdbc_driver_class(String jdbc_driver_class) {
		this.jdbc_driver_class = jdbc_driver_class;
	}
	public String getJdbc_connection_string() {
		return jdbc_connection_string;
	}
	public void setJdbc_connection_string(String jdbc_connection_string) {
		this.jdbc_connection_string = jdbc_connection_string;
	}
	public String getJdbc_user() {
		return jdbc_user;
	}
	public void setJdbc_user(String jdbc_user) {
		this.jdbc_user = jdbc_user;
	}
	public String getJdbc_password() {
		return jdbc_password;
	}
	public void setJdbc_password(String jdbc_password) {
		this.jdbc_password = jdbc_password;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Integer getJdbc_size() {
		return jdbc_size;
	}
	public void setJdbc_size(Integer jdbc_size) {
		this.jdbc_size = jdbc_size;
	}
	@Override
	public  String toString(){
		return JSON.toJSONString(this);  
	}
	public  void validation() throws IllegalAccessException{
		if(enabled){
			InitConfig.requireNonNull(jdbc_driver_library, "jdbc_driver_library 不能为null");
			InitConfig.requireNonNull(jdbc_driver_class, "jdbc_driver_class不能为空");
			InitConfig.requireNonNull(jdbc_connection_string, "jdbc_connection_string不能为空");
			InitConfig.requireNonNull(jdbc_user, "jdbc_user不能为空");
			InitConfig.requireNonNull(jdbc_password, "jdbc_password不能为空");
			InitConfig.requireNonNull(jdbc_template, "jdbc_template不能为空");
			
			//获得参数的标志位
			fieldMap = SqlParser.getConfigParent(jdbc_template);
			jdbc_template=SqlParser.toLegalSql(jdbc_template);
			//验证sql合法性
			if(!SqlParser.isInsertSql(jdbc_template)){
				throw new IllegalAccessException("SQL jdbc_template 只支持insert和update");
			};
			tableName = SqlParser.getTableName();
			InitConfig.requireNonNull(tableName, "tableName不能为空");
		}
	}  
	
}
