/**
 * 
 */
package com.chenhj.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.dao.DbDao;
import com.chenhj.dao.impl.DbDaoImpl;
/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: Write2FileTask.java
* @Description: 写DB任务类
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月6日 上午10:36:56 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月6日     chenhj          v1.0.0               修改原因
*/
public class Write2DbTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Write2DbTask.class);
	private DbDao dbDao; 
	private  List<JSONObject> list = null;
	public Write2DbTask(List<JSONObject> list) {
		this.list = list;
		dbDao = new DbDaoImpl();
	}
	@Override
	public void run() {
		 try {
			 dbDao.insert(list);
		} catch (Exception e) {
			logger.error("Write DB fail:",e);
		}
	}
}
