/**
 * 
 */
package com.chenhj.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenhj.constant.Constant;
import com.chenhj.constant.Pool;
import com.chenhj.es.Rest;
import com.chenhj.thread.ThreadPoolManager;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: MonitorTask.java
* @Description: 监控拉取数据线程池与写文件线程池
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月7日 上午11:12:34 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月7日     chenhj          v1.0.0               修改原因
*/
public class MonitorTask implements  Callable<Byte>{
	private static final Logger logger = LoggerFactory.getLogger("Monitor");
	private static final Logger log = LoggerFactory.getLogger(MonitorTask.class);
	private ThreadPoolExecutor tpe = ((ThreadPoolExecutor) Pool.EXECPool);
	private ThreadPoolManager filePool = Pool.WRITE_FILE_POOL;
	private ThreadPoolManager DbPool = Pool.WRITE_DB_POOL;
	@Override
	public Byte call() throws Exception {
	    //监控拉取数据的线程池
	    while (true) {
	    	boolean flag = getDataPool();
	    	if(flag){
	    		log.info("ES is finished scroll, Stop the scroll thread pool...");
	    		break;
	    	}
	    }
	    //关闭ES连接
	    Rest.Client.getRestClient().close();
	    //监控写文件线程是否已经结束
	    if(filePool!=null){
		    while (true) {
		    	boolean flag = writeFilePool();
		    	if(flag){
		    		log.info("File is finished writing, Stop the writing thread pool...");
		    		break;
		    	}
			}
	    }
	    //监控写DB线程是否已经结束
	    if(DbPool!=null){
		    while (true) {
		    	boolean flag = writeDbPool();
		    	if(flag){
		    		log.info("DB is finished writing, Stop the writing thread pool...");
		    		break;
		    	}
			}
	    }
		return Constant.SUCCESS;
	}
	public boolean getDataPool() throws InterruptedException{
		boolean flag = false;
		int queueSize = tpe.getQueue().size(); 
		logger.info("EXECPool>>Queue:" + queueSize);
		int activeCount = tpe.getActiveCount(); 
		logger.info("EXECPool>>Active:" + activeCount); 
		long completedTaskCount = tpe.getCompletedTaskCount(); 
		logger.info("EXECPool>>Finish:" + completedTaskCount); 
		if(activeCount==0){
			tpe.shutdown();
			if(tpe.isTerminated()){
				logger.info(">>>>>EXECPool Shutdown..."); 
				flag = true;
			 }
		}
		TimeUnit.SECONDS.sleep(3);
		return flag;
	}
	public boolean writeFilePool() throws InterruptedException{
		boolean flag = false;
	     boolean hasMoreAcquire = filePool.hasMoreAcquire();
	     boolean isTaskEnd = filePool.isTaskEnd();
			int queueSize = filePool.getNumQueue(); 
			logger.info("WRITE_FILE_POOL>>Queue:" + queueSize);
			int activeCount = filePool.getNumActive(); 
			logger.info("WRITE_FILE_POOL>>Active:" + activeCount); 
	     if(!hasMoreAcquire&&isTaskEnd&&activeCount==0){  
	    	 	filePool.shutdown();
	            logger.info(">>>>>WRITE_FILE_POOL Shutdown...");  
	            flag = true;  
	     }
	    TimeUnit.SECONDS.sleep(3);
		return flag;
	}
	public boolean writeDbPool() throws InterruptedException{
		boolean flag = false;
	     boolean hasMoreAcquire = DbPool.hasMoreAcquire();
	     boolean isTaskEnd = DbPool.isTaskEnd();
			int queueSize = DbPool.getNumQueue(); 
			logger.info("WRITE_Db_POOL>>Queue:" + queueSize);
			int activeCount = DbPool.getNumActive(); 
			logger.info("WRITE_Db_POOL>>Active:" + activeCount); 
	     if(!hasMoreAcquire&&isTaskEnd&&activeCount==0){  
	    	   DbPool.shutdown();
	            logger.info(">>>>>WRITE_Db_POOL Shutdown...");  
	            flag = true;  
	     }
	    TimeUnit.SECONDS.sleep(3);
		return flag;
	}
}
