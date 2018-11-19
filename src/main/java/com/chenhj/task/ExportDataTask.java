/**
 * 
 */
package com.chenhj.task;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.constant.Constant;
import com.chenhj.pojo.HttpClientResult;
import com.chenhj.service.IDataToFileService;
import com.chenhj.service.impl.DataToFileServiceImpl;
import com.chenhj.util.HttpClientUtils;
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
	/****数据库连接*****/
	//HTTP连接状态
	private volatile boolean conneFlag = false;
	//重连接次数,达到一定次数开始休眠
	private int reconnect = 0;
	//请求头部
	private Map<String,String>  header = null;
	private Map<String, String> conf = null;
	
	private IDataToFileService dataToFileService;

	public ExportDataTask() throws Exception{
		conf = Constant.GLOBAL_CONFIG;
		dataToFileService = new DataToFileServiceImpl();
		//初始化HTTP请求头部
		initRequestHeader();
	}
	@Override
	public void run() {
		
		HttpClientResult result = null;
		String index = conf.get("index");
		String type = conf.get("type");
		String esUrl = conf.get("es.servers");
		String url1 =String.format("http://%s/%s/%s/_search?scroll=1m",esUrl,index,type);
		String url2 =String.format("http://%s/_search/scroll",esUrl);
		String query = conf.get("query");
		String includes = conf.get("includes");
		JSONObject params = new JSONObject();
		if(StringUtils.isNoneEmpty(query)){
			 params = JSON.parseObject(query);
		}
		if(StringUtils.isNoneEmpty(includes)){
			JSONObject inc = new JSONObject();
			String field[] = includes.split(",");
			inc.put("includes", field);
			params.put("_source",inc);
		}
		List<JSONObject> list = null;
		logger.info("开始拉取数据到本地....");
		while (true) {
			try {
				//如果数据库连接成功
			  if(conneFlag){		
				  	result = HttpClientUtils.doPost(url1,header,params);
					//如果请求成功
					if(result.getCode()==HttpStatus.SC_OK){
						if(StringUtils.isNotEmpty(result.getContent())){
							try {
								//服务端返回的数据
								JSONObject resultData = JSON.parseObject(result.getContent());
								JSONObject hits = resultData.getJSONObject("hits");
								JSONArray hitsData = hits.getJSONArray("hits");
								int size = hitsData.size();
								if(size>0){
									list = new ArrayList<>();
									for(int i=0;i<size;i++){
										JSONObject data = JSON.parseObject(hitsData.getJSONObject(i).getString("_source"));
										list.add(data);
									}
									dataToFileService.write2File(list);
									//获取下批数据的准备
									url1 = url2;
									params.clear();
									String scroll_id  = resultData.getString("_scroll_id");
									params.put("scroll_id", scroll_id);
									params.put("scroll", "1m");
								}else{
									logger.info("数据导出完毕,程序退出...");
									break;
								}
							} catch (JSONException e) {
								 //数据格式出错的话直接跳出本次
								 logger.error("数据格式错误:{}",result.getContent());
								 continue;
							}
						}
					}else{
						 logger.error("服务端响应异常,状态码:{},内容:{}",result.getCode(),result.getContent());
						 TimeUnit.SECONDS.sleep(120);
					}
			   }else{
				   //测试连接是否正常
				   String urls[] = esUrl.split(":");
				   String ip = urls[0];
				   if(urls.length==2){
					   Integer port = Integer.valueOf(urls[1]);
					   conneFlag =  HttpClientUtils.isHostConnectable(ip, port);
				   }else{
					   conneFlag =  HttpClientUtils.isHostConnectable(ip, 80);
				   }
				   logger.info("测试ES连接,连接状态:"+conneFlag);
				   //休眠三秒
				   TimeUnit.SECONDS.sleep(1);
			   }
			//该异常一般都是对方的url无法访问了,这个时候静默一段时间 
			}catch (SocketTimeoutException e) {
				//暂停1秒
				try {
					logger.error("数据导出异常,暂停10秒再请求",e);
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e1) {
					logger.error("线程暂停失败:{}",e);
				}
			}
			catch (ConnectException e) {
			  try { 
				 reconnect++;
				 //5次重连不行就长休眠一次
				 if(reconnect%5==0){
						//数据库初始化置为false
						conneFlag= false;
						//关闭数据库连接
						logger.info("服务端连接异常,程序开始休眠1200秒..{}",e);
						//休眠1200s
						TimeUnit.SECONDS.sleep(1200);
				   }
				//暂停1秒
				TimeUnit.SECONDS.sleep(1);
				} catch (Exception e1) {
					logger.error("线程失败:{}",e);
				}
			} catch (Exception e) {
				logger.error("数据处理异常:",e);
			}
		}
		System.exit(0);;
	}
	/**
	 * 初始化请求头
	 */
	private void initRequestHeader(){
		header = new HashMap<String, String>(16);
		header.put("Content-Type", "application/json;charset=UTF-8");
		header.put("Accept", "application/json;charset=UTF-8");
		//header.put("appId", MiConfig.mi_appId);
		//header.put("appKey", MiConfig.mi_appKey);
		//header.put("appSecret", MiConfig.mi_appSecret);
	}
}
