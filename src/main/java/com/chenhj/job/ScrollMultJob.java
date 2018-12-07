/**
 * 
 */
package com.chenhj.job;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.service.IEsActionService;
import com.chenhj.service.impl.EsActionServiceImpl;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: ScrollMultJob.java
* @Description: 多线程的时候需要得到每个线程的scrollId
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月5日 下午5:33:47 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月5日     chenhj          v1.0.0               修改原因
*/
public class ScrollMultJob {
	private String scrollId;
	private IEsActionService esActionService;
	public ScrollMultJob() throws Exception{
		esActionService = new EsActionServiceImpl();
	}
	public List<JSONObject> executeJob(String query) throws Exception{
		List<JSONObject> list = null; 
		try {
			list = esActionService.executeQuery(null, query);
			scrollId = esActionService.getSrcollId();
		} catch (Exception e) {
			throw e;
		}
		return list;
	}
	public String getSrcollId() {
		return scrollId;
	}
}
