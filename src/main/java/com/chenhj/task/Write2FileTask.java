/**
 * 
 */
package com.chenhj.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.service.IDataToFileService;
import com.chenhj.service.impl.DataToFileServiceImpl;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: Write2FileTask.java
* @Description: 写数据任务类
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
public class Write2FileTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Write2FileTask.class);
	private IDataToFileService dataToFileService;
	private  List<JSONObject> list = null;
	public Write2FileTask(List<JSONObject> list) {
		this.list = list;
		dataToFileService = new DataToFileServiceImpl();
	}
	@Override
	public void run() {
		 try {
			dataToFileService.write2File(list);
		} catch (Exception e) {
			logger.error("文件写失败",e);
		}
	}
     
}
