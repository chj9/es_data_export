/**
 * 
 */
package com.chenhj.config;

import org.quartz.CronExpression;

import com.alibaba.fastjson.JSON;
import com.chenhj.init.InitConfig;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: QuartzConfig.java
* @Description:定时器配置
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午7:03:23 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public class QuartzConfig {
	
	private boolean  enabled = false;
	
	private String schedule;

	
	
	public boolean isEnabled() {
		return enabled;
	}



	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}



	public String getSchedule() {
		return schedule;
	}



	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public  void validation() throws IllegalArgumentException{
		if(enabled){
			InitConfig.requireNonNull(schedule, "schedule 不能为null");
			if(!CronExpression.isValidExpression(schedule)){
				throw new IllegalArgumentException("schedule config error(schedule配置参数值错误)");
			}
		}
	}  
	public  String toString(){
		return JSON.toJSONString(this);  
	}
}
