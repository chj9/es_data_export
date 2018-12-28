/**
 * 
 */
package com.chenhj.init;

import java.util.concurrent.Executors;

import com.chenhj.config.Config;
import com.chenhj.constant.Pool;
import com.chenhj.thread.ThreadPoolManager;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: ThreadPool.java
* @Description: 线程池初始化
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月7日 下午2:20:13 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月7日     chenhj          v1.0.0               修改原因
*/
public class ThreadPool {
	
	public static void init() throws Exception{
		   //写文件线程池
		   Pool.WRITE_FILE_POOL = ThreadPoolManager.newInstance(1).build();
		   //拉取数据线程池
		   Pool.EXECPool = Executors.newFixedThreadPool(Config.COMMON_CONFIG.getThread_size()); 
		   
		   Pool.WRITE_DB_POOL = ThreadPoolManager.newInstance(Config.JDBC_CONFIG.getJdbc_write_thread_size()).build();
 	       //计数器
		  // Pool.LATCH = new CountDownLatch(threadSize);
	}
}
