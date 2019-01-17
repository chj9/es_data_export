/**
 * 
 */
package com.chenhj.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.constant.Constant;
import com.chenhj.init.InitConfig;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: KafkaConfig.java
* @Description: kafka配置文件
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
public class KafkaConfig {

	private  String hosts;

	private String topic;
	
	private String add_value;
	
	private String replace_key;
	
	private Integer write_thread_size =1;
	
	private Integer send_size = 1000;
	
	private Integer delay = 0;
	
	private JSONObject add_value_JSON;
	
	private Map<String,String> replace_key_Map;
	
	
	
	public JSONObject getAdd_value_JSON() {
		return add_value_JSON;
	}
	public void setAdd_value_JSON(JSONObject add_value_JSON) {
		this.add_value_JSON = add_value_JSON;
	}
	
	
	public Map<String, String> getReplace_key_Map() {
		return replace_key_Map;
	}
	public void setReplace_key_Map(Map<String, String> replace_key_Map) {
		this.replace_key_Map = replace_key_Map;
	}
	public String getAdd_value() {
		return add_value;
	}
	public void setAdd_value(String add_value) {
		this.add_value = add_value;
	}
	public String getReplace_key() {
		return replace_key;
	}
	public void setReplace_key(String replace_key) {
		this.replace_key = replace_key;
	}
	public Integer getDelay() {
		return delay;
	}
	public void setDelay(Integer delay) {
		this.delay = delay;
	}
	public Integer getSend_size() {
		return send_size;
	}
	public void setSend_size(Integer send_size) {
		this.send_size = send_size;
	}
	private boolean enabled = false;

	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public Integer getWrite_thread_size() {
		return write_thread_size;
	}
	public void setWrite_thread_size(Integer write_thread_size) {
		this.write_thread_size = write_thread_size;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public  String toString(){
		return JSON.toJSONString(this);  
	}
	public  void validation(){
		InitConfig.requireNonNull(hosts, "kafka hosts 不能为空");
		InitConfig.requireNonNull(topic, "topic不能为空");
		if(StringUtils.isNotBlank(add_value)){
			add_value_JSON = JSON.parseObject(add_value);
		}
		if(StringUtils.isNotBlank(replace_key)){
			String fields[] = replace_key.split(Constant.COMMA_SIGN);
			replace_key_Map = new HashMap<String,String>();
			for(String field:fields){
				String old_new[] = field.split(Constant.COLON);
				replace_key_Map.put(old_new[0], old_new[1]);
			}
			
		}
	}
}
