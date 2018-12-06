package com.chenhj;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.constant.ApplicationConfig;
import com.chenhj.constant.Constant;
import com.chenhj.init.Rest;
import com.chenhj.job.EsInfoJob;
import com.chenhj.job.ScrollMultJob;
import com.chenhj.task.ExportDataTask;
import com.chenhj.util.PropertiesAutoSerialize;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 *ES数据导出入口类
 */
public class App 
{
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )  {
    	try {
    		initLogBack();
        	logger.info("日志加载成功...");	
        	//读取配置文件
    		PropertiesAutoSerialize.init(Constant.CONFIG_NAME,ApplicationConfig.class);
    		initEs();
        	exportDataTask();
        	logger.info("程序启动成功,版本号:"+Constant.VERSION);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("程序启动失败");
			System.exit(-1);
		}
    	
    }
    /**
     * 导出数据线程,后期根据index的shard的数量进行多线程获取
     * @throws Exception 
     */
    private static void exportDataTask() throws Exception{
 	       ExecutorService exec = null;
 	      
 		 try {
 		   int threadSize = getThreadSize();
 	       exec = Executors.newFixedThreadPool(threadSize);  
 	       //写文件的线程,单线程操作
 	       logger.info("拉取数据线程数:"+threadSize);
 	       String scrollId;
    	   //执行任务
 		   for(int i=0;i<threadSize;i++){
 			   ScrollMultJob sJob = new ScrollMultJob();
 			  List<JSONObject> list = sJob.executeJob(ApplicationConfig.getScrollQuery(i,threadSize));
 			   scrollId = sJob.getSrcollId();
 			   System.out.println(scrollId);
 			   ExportDataTask task = new ExportDataTask(scrollId,list);
 			   exec.execute(task);
 		   }
 		 // exec.shutdown();
 		 // exec.awaitTermination(1,TimeUnit.HOURS);
 		} catch (Exception e) {
 			//关闭线程池
 			if(exec!=null){
 			     exec.shutdown();
 			}
 			throw e;
 		}
    }
    private static int getThreadSize() throws Exception{
    	   EsInfoJob esInfo = new EsInfoJob();
    	   //索引分片数
    	   int share = esInfo.getIndexShards(ApplicationConfig.getIndex());//优先级2
		   //配置最大线程
    	   int threadSize = ApplicationConfig.getThreadSize(); //优先级1
		   //当前机器CPU数
    	   int nowCpu = Runtime.getRuntime().availableProcessors(); //优先级3
	    	//如果分区数小于最大线程数,则线程数取分区的数量
	       if(share<threadSize){
	    	    threadSize = share;
	    	}
	       if(nowCpu<threadSize){
	    	   threadSize = nowCpu;
	       }
	     return threadSize;
    }
    /**
     * 初始化ES的连接
     */
    public static  void initEs(){
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
    /**
     * 加载日志配置文件(不放在resources下的时候使用)
     * @throws IOException
     * @throws JoranException
     */
 	private static void initLogBack() throws IOException, JoranException {
 		//String configFilepathName = FilePathHelper.getFilePathWithJar("logback.xml");
        File file = new File(Constant.LOGBACK_CONFIG_NAME);
        if(!file.exists()){
        	throw new NullPointerException("logback.xml为空");
        }
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        //注意包不要引错
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        loggerContext.reset();
        try {
            joranConfigurator.doConfigure(file);
        } catch (Exception e) {
     	   logger.error("Load logback config file error. Message:{}",e);
        	throw e;
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);

        
 	}
}
