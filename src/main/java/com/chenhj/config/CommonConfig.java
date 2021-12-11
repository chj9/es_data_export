/**
 * 
 */
package com.chenhj.config;

import com.alibaba.fastjson.JSON;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: CommonConfig.java
* @Description: 该类的功能描述
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
public class CommonConfig {
	private int thread_size = 1;
	
	private String banner;

	public int getThread_size() {
		return thread_size;
	}
	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public void setThread_size(int thread_size) {
		this.thread_size = thread_size;
	}

	@Override
	public  String toString(){
		return JSON.toJSONString(this);  
	}
}
