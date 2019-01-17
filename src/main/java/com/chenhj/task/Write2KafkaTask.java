/**
 * 
 */
package com.chenhj.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.service.IWriteKafkaService;
import com.chenhj.service.impl.WriteKafkaServiceImpl;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: Write2FileTask.java
* @Description: 写文件任务类
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
public class Write2KafkaTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Write2KafkaTask.class);
	private  List<JSONObject> list = null;
	private IWriteKafkaService kafkaService;
	public Write2KafkaTask(List<JSONObject> list) throws Exception {
		this.list = list;
		kafkaService = new WriteKafkaServiceImpl();
	}
	@Override
	public void run() {
		 try {
			 kafkaService.write2Kafka(list);
		} catch (Exception e) {
			logger.error("Write Kafka fail:",e);
		}
	}
}
