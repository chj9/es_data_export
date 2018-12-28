package com.chenhj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenhj.constant.Constant;
import com.chenhj.init.InitConfig;
import com.chenhj.init.InitConnection;
import com.chenhj.init.LogBack;
import com.chenhj.init.ThreadPool;
import com.chenhj.job.ThreadUtil;
/**
 *ES数据导出入口类
 */
public class App 
{
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )  {
    	try {
    		LogBack.init();
        	logger.info("Log Config Load the success...");	
        	//读取配置文件
        	InitConfig.init();
    		logger.info("Config Load the success...");	
    		//初始化连接
    		InitConnection.init();
    		logger.info("ElasticSearch Client Load the success...");	
    		//线程池初始化
    		ThreadPool.init();
    		logger.info("ThreadPool Load the success...");
        	new ThreadUtil().startConsume();
        	logger.info("Running Success,Version:"+Constant.VERSION);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Running Failed",e);
			logger.error("Running Failed",e);
			System.exit(-1);
		}
    	
    }


}
