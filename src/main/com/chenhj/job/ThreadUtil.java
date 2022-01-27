package com.chenhj.job;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.constant.Constant;
import com.chenhj.constant.Pool;
import com.chenhj.task.ExportDataMasterTask;
import com.chenhj.task.MonitorTask;


public class ThreadUtil {
		
	private static final Logger LOG = LoggerFactory.getLogger(ThreadUtil.class);
	static int nThreads = 1;
	public ThreadUtil() throws Exception {
		nThreads = Config.COMMON_CONFIG.getThread_size();
	}
	/**
	 * 启动所有线程池
	 * @throws Exception 
	 */
	public void startConsume() throws Exception{
		try {
			if(!Config.FILE_CONFIG.isEnabled()&&!Config.JDBC_CONFIG.isEnabled()){
				LOG.info("文件也不写,DB也不入,你拉数据下来干啥....程序退出!!");
				System.exit(-1);
			}
			exportDataTask();
		} catch (Exception e) {
			throw e;
		}
	}
    /**
     * 导出数据线程
     * @throws Exception 
     */
    private static void exportDataTask() throws Exception{
 		 try {
 		   nThreads = getRunThreadSize();
 	       //写文件的线程,单线程操作
 	       LOG.info("ThreadPool Size:"+nThreads);
 	       String scrollId;
    	   //执行任务
 		   for(int i=0;i<nThreads;i++){
 			   ScrollMultJob sJob = new ScrollMultJob();
 			   List<JSONObject> list = sJob.executeJob(getScrollQuery(i,nThreads));
 			   scrollId = sJob.getSrcollId();
 			   ExportDataMasterTask task = new ExportDataMasterTask(scrollId,list);
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
	    	   LOG.info("Process Exits...");
	    	   System.exit(-1);
	       }
    }
    
	public static String getScrollQuery(Integer nowid,Integer maxid) {

		String query = Config.ES_CONFIG.getQuery();
		String includes =Config.ES_CONFIG.getIncludes();
		JSONObject params = new JSONObject();
		if(StringUtils.isNoneEmpty(query)){
			 params = JSON.parseObject(query);
		}
		if(nowid!=null&&maxid!=null&&maxid>1){
//			if(maxid<=1){
//				throw new IllegalArgumentException("max must be greater than 1");
//			}
			if(maxid<=nowid){
				throw new IllegalArgumentException("max must be greater than id");
			}
			JSONObject slice = new JSONObject();
			slice.put("id",nowid);
			slice.put("max",maxid);
			params.put("slice", slice);
		}
		if(StringUtils.isNoneEmpty(includes)){
			JSONObject inc = new JSONObject();
			String field[] = includes.split(",");
			inc.put("includes", field);
			params.put("_source",inc);
		}
		if(StringUtils.isBlank(params.getString("sort"))){
			String sort[] ={"_doc"};
			params.put("sort", sort);
		}
		return params.toJSONString();
	}
    public static int getRunThreadSize() throws Exception{
 	   EsInfoJob esInfo = new EsInfoJob();
 	   //索引分片数
 	   int share = esInfo.getIndexShards(Config.ES_CONFIG.getIndex());//优先级2
		   //配置最大线程
 	   int threadSize = nThreads; //优先级1
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
}
