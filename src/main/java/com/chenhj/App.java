package com.chenhj;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.constant.ApplicationConfig;
import com.chenhj.constant.Constant;
import com.chenhj.constant.Pool;
import com.chenhj.init.LogBack;
import com.chenhj.init.Rest;
import com.chenhj.init.ThreadPool;
import com.chenhj.job.ScrollMultJob;
import com.chenhj.task.ExportDataTask;
import com.chenhj.task.MonitorTask;
import com.chenhj.util.PropertiesAutoSerialize;
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
    		PropertiesAutoSerialize.init(Constant.CONFIG_NAME,ApplicationConfig.class);
    		//验证配置
    		ApplicationConfig.validation();
    		logger.info("Config Load the success...");	
    		//初始化ES
    		initEs();
    		logger.info("ElasticSearch Client Load the success...");	
    		//线程池初始化
    		ThreadPool.init();
    		logger.info("ThreadPool Load the success...");
        	exportDataTask();
        	logger.info("Running Success,Version:"+Constant.VERSION);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Running Failed",e);
			logger.error("Running Failed",e);
			System.exit(-1);
		}
    	
    }
    /**
     * 导出数据线程
     * @throws Exception 
     */
    private static void exportDataTask() throws Exception{
 		 try {
 			int threadSize = ApplicationConfig.getRunThreadSize();
 	       //写文件的线程,单线程操作
 	       logger.info("ThreadPool Size:"+threadSize);
 	       String scrollId;
    	   //执行任务
 		   for(int i=0;i<threadSize;i++){
 			   ScrollMultJob sJob = new ScrollMultJob();
 			   List<JSONObject> list = sJob.executeJob(ApplicationConfig.getScrollQuery(i,threadSize));
 			   scrollId = sJob.getSrcollId();
 			   ExportDataTask task = new ExportDataTask(scrollId,list);
 			   Pool.EXECPool.execute(task);
 		   }
 		  //开启监控线程池线程
 		  monitor();
 		} catch (Exception e) {
 			//关闭线程池
 			if(Pool.EXECPool!=null){
 				Pool.EXECPool.shutdown();
 			}
 			throw e;
 		}
    }
    /**
     * 监控线程池(只有在拉取数据的线程结束后才开启这个监控线程)
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void monitor() throws InterruptedException, ExecutionException{
		   MonitorTask monitorTask = new MonitorTask();
		  //1.执行 Callable 方式，需要 FutureTask 实现类的支持，用于接收运算结果。
	       FutureTask<Byte> result = new FutureTask<>(monitorTask);
	       new Thread(result).start();
	       Byte status = result.get();
	       if(status==Constant.SUCCESS){
	    	   logger.info("Process Exits...");
	    	   System.exit(-1);
	       }
    }
    /**
     * 初始化ES的连接
     * @throws IllegalAccessException 
     */
    public static  void initEs() throws Exception{
    	String ips = ApplicationConfig.getEsserver();
    	String username = ApplicationConfig.getEsusername();
    	String password = ApplicationConfig.getEspassword();
    	//ES初始化
    	Rest rest = Rest.Client.setHttpHosts(ips.split(","));
		if(StringUtils.isNotBlank(username)&&StringUtils.isNotBlank(password)){
			rest.validation(username, password);
		}
		rest.build();
    }
}
