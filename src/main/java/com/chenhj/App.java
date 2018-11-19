package com.chenhj;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenhj.constant.Constant;
import com.chenhj.task.ExportDataTask;
import com.chenhj.util.PropertiesUtil;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	 private static Map<String, String> conf = null;
    public static void main( String[] args )  {
    	try {
    		initLogBack();
        	logger.info("日志加载成功...");	
        	//读取配置文件
    		conf =new PropertiesUtil().loadProperties(Constant.CONFIG_NAME);
    		Constant.GLOBAL_CONFIG = conf;
        	exportDataTask();
        	logger.info("程序启动成功,版本号:"+Constant.VERSION);
		} catch (Exception e) {
			logger.error("程序启动失败");
		}
    	
    }
    /**
     * 增量数据处理
  * @throws Exception 
     */
    private static void exportDataTask() throws Exception{
 	       ExecutorService exec = null;
 		 try {
 	       exec = Executors.newFixedThreadPool(1);   
    	   	//执行任务,目前单线程执行
 		   for(int i=0;i<1;i++){
 			   ExportDataTask task = new ExportDataTask();
 			   exec.execute(task);
 		   }
 		} catch (Exception e) {
 			//关闭线程池
 			if(exec!=null){
 			     exec.shutdown();
 			}
 			throw e;
 		}
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
