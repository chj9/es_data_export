/**
 * 
 */
package com.chenhj.constant;

import java.util.concurrent.ExecutorService;

import com.chenhj.thread.ThreadPoolManager;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: Pool.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月7日 下午2:23:35 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月7日     chenhj          v1.0.0               修改原因
*/
public enum Pool {
	INSTANCE;
	/**
	 * 写文件的线程
	 */
	public static ThreadPoolManager WRITE_FILE_POOL = null;
	/**
	 * 写数据库线程池
	 */
	public static ThreadPoolManager WRITE_DB_POOL = null;
    //public  static CountDownLatch LATCH = null;
    public static ExecutorService EXECPool=null;
   // public static ConnectionManager DB_POOL_Connection = null;
}
