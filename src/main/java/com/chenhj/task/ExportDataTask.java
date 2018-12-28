/**
 * 
 */
package com.chenhj.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.constant.Pool;
import com.chenhj.service.IEsActionService;
import com.chenhj.service.impl.EsActionServiceImpl;
/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: IncrementDataTask.java
* @Description: ES数据导出任务线程实现类
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年8月6日 下午3:44:51 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月6日     chenhj          v1.0.0               修改原因
*/
public class ExportDataTask implements Runnable{
	private static final Logger logger = LoggerFactory.getLogger(ExportDataTask.class);
	
	private IEsActionService esActionService;
	private String srcollId;
	private  List<JSONObject> list = null;
	//启用标志
	private boolean  jdbcEnabled;
	private boolean  fileEnabled;
	/**
	 * @param scroll_id
	 * @param list
	 * @throws Exception
	 */
	public ExportDataTask(String scroll_id,List<JSONObject> list) throws Exception{
		esActionService = new EsActionServiceImpl();
		this.srcollId = scroll_id;
		this.list= list;
		this.jdbcEnabled=Config.JDBC_CONFIG.isEnabled();
		this.fileEnabled =Config.FILE_CONFIG.isEnabled();
	}
	@Override
	public void run() {
		int count = 0;
		logger.info(Thread.currentThread().getName()+"开始拉取数据到本地....");
		if(list==null||list.isEmpty()){
			return;
		}
		while (true) {
			try {  
				 if(list!=null&&!list.isEmpty()){
					   count = count+list.size();
					   //写文件
					   if(fileEnabled){
						   Pool.WRITE_FILE_POOL.addExecuteTask(new Write2FileTask(list));
					   }
					   //写DB
					   if(jdbcEnabled){
						   Pool.WRITE_DB_POOL.addExecuteTask(new Write2DbTask(list));
					   }
				  }else{
					  esActionService.clearSrcoll(srcollId);
					  logger.info(Thread.currentThread().getName()+"线程拉取完成.数据条数:"+count);
					  break;
				  }
				  list = esActionService.executeQuery(srcollId,null);
			//该异常一般都是对方的url无法访问了,这个时候静默一段时间 
			}catch (Exception e) {
				logger.error("数据处理异常:",e);
			}
		}
		return;
	}
}
