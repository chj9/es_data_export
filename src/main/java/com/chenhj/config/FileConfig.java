/**
 * 
 */
package com.chenhj.config;

import com.alibaba.fastjson.JSON;
import com.chenhj.constant.Constant;
import com.chenhj.init.InitConfig;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: FileConfig.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午3:45:01 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public class FileConfig {
	//是否启用
	private boolean   enabled = false;
	//换行
	private  Boolean linefeed=true;
	//数据类型
	private  String datalayout;
	//文件路径
	private  String filepath;
	private  String filename;
	private  String max_filesize;
	private  String custom_field_name;
	private  String field_split=Constant.COMMA_SIGN;
	private  String field_sort;
	private  Boolean need_field_name=false;
	private  String sql_format;
	
	private String csv_headers;
	
	
	
	public String getCsv_headers() {
		return csv_headers;
	}
	public void setCsv_headers(String csv_headers) {
		this.csv_headers = csv_headers;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Boolean getLinefeed() {
		return linefeed;
	}
	public void setLinefeed(Boolean linefeed) {
		this.linefeed = linefeed;
	}
	public String getDatalayout() {
		return datalayout;
	}
	public void setDatalayout(String datalayout) {
		this.datalayout = datalayout;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getMax_filesize() {
		return max_filesize;
	}
	public void setMax_filesize(String max_filesize) {
		this.max_filesize = max_filesize;
	}
	public String getCustom_field_name() {
		return custom_field_name;
	}
	public void setCustom_field_name(String custom_field_name) {
		this.custom_field_name = custom_field_name;
	}
	public String getField_split() {
		return field_split;
	}
	public void setField_split(String field_split) {
		this.field_split = field_split;
	}
	public String getField_sort() {
		return field_sort;
	}
	public void setField_sort(String field_sort) {
		this.field_sort = field_sort;
	}
	public Boolean getNeed_field_name() {
		return need_field_name;
	}
	public void setNeed_field_name(Boolean need_field_name) {
		this.need_field_name = need_field_name;
	}
	public String getSql_format() {
		return sql_format;
	}
	public void setSql_format(String sql_format) {
		this.sql_format = sql_format;
	}
	@Override
	public  String toString(){
		return JSON.toJSONString(this);  
	}
	public  void validation(){
		if(enabled){
			InitConfig.requireNonNull(linefeed, "linefeed 不能为null");
			InitConfig.requireNonNull(datalayout, "datalayout不能为空");
			InitConfig.requireNonNull(filepath, "filepath数据存储文件路径不能为空");
			InitConfig.requireNonNull(filename, "filename数据存储文件名不能为空");
		}
	}
}
