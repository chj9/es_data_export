package com.chenhj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenhj.constant.Constant;
import com.chenhj.init.InitConfig;
import com.chenhj.init.InitConnection;
import com.chenhj.init.InitLogBack;
import com.chenhj.init.InitThreadPool;
import com.chenhj.job.ThreadUtil;
import com.chenhj.util.impl.ResourceBannerImpl;

/**
 *ES数据导出入口类
 */
public class App 
{
//	  | ____|  / \  / ___\ \ / / | ____\ \/ /  _ \ / _ \|  _ \_   _| 
//	  |  _|   / _ \ \___ \\ V /  |  _|  \  /| |_) | | | | |_) || |   
//	  | |___ / ___ \ ___) || |   | |___ /  \|  __/| |_| |  _ < | |   
//	  |_____/_/   \_\____/ |_|   |_____/_/\_\_|    \___/|_| \_\|_|     
	private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )  {
    	try {
    		InitLogBack.init();
        	logger.info("Log Config Load the success...");	
        	//读取配置文件
        	InitConfig.init();
    		logger.info("Config Load the success...");	
    		//初始化连接
    		InitConnection.init();
    		logger.info("ElasticSearch Client Load the success...");	
    		//线程池初始化
    		InitThreadPool.init();
    		logger.info("ThreadPool Load the success...");

        	logger.info("Running Success,Version:"+Constant.VERSION);
        	// 打印logo
        	new ResourceBannerImpl().printBanner(System.out);

			new ThreadUtil().startConsume();

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Running Failed",e);
			logger.error("Running Failed",e);
			System.exit(-1);
		}
    	
    }
    
}
