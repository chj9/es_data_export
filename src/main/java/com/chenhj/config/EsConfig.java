/**
 * 
 */
package com.chenhj.config;


import com.alibaba.fastjson.JSON;
import com.chenhj.init.InitConfig;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: EsConfig.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午3:46:13 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public class EsConfig {
	private  String index;
	private  String document_type;
	private  String query;
	private  String hosts;
	private  String username;
	private  String password;
	//SSL设置
	private  String ssl_type;
	private  String ssl_keystorepath;
	private  String ssl_keystorepass;
	
	private String includes;
	
	
	public String getIncludes() {
		return includes;
	}
	public void setIncludes(String includes) {
		this.includes = includes;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getDocument_type() {
		return document_type;
	}
	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSsl_type() {
		return ssl_type;
	}
	public void setSsl_type(String ssl_type) {
		this.ssl_type = ssl_type;
	}
	public String getSsl_keystorepath() {
		return ssl_keystorepath;
	}
	public void setSsl_keystorepath(String ssl_keystorepath) {
		this.ssl_keystorepath = ssl_keystorepath;
	}
	public String getSsl_keystorepass() {
		return ssl_keystorepass;
	}
	public void setSsl_keystorepass(String ssl_keystorepass) {
		this.ssl_keystorepass = ssl_keystorepass;
	}
	@Override
	public  String toString(){
		return JSON.toJSONString(this);  
	}
	public  void validation(){
		InitConfig.requireNonNull(index, "index 不能为空");
		//requireNonNull(type, "");
		//requireNonNull(query, message);
		InitConfig.requireNonNull(hosts, "hosts集群地址不能为空");
		//requireNonNull(esusername, message);
		//requireNonNull(espassword, message);
	}
}
