/**
 * 
 */
package com.chenhj.constant;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.job.EsInfoJob;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: ApplicationConfig.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月5日 上午10:15:55 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月5日     chenhj          v1.0.0               修改原因
*/
public class ApplicationConfig{
	private static String index;
	private static String type;
	private static String query;
	private static String esserver;
	private static String esusername;
	private static String espassword;
	private static Boolean isLineFeed;
	private static String dataLayout;
	private static String filePath;
	private static String fileName;
	private static String fileSize;
	private static String customFieldName;
	private static String fieldSplit=Constant.COMMA_SIGN;
	private static String fieldSort;
	private static Boolean needFieldName;
	private static String sqlFormat;
	private static String includes;
	private static Integer threadSize;
	//SSL设置
	private static String SSL_type;
	private static String SSL_keyStorePath;
	private static String SSL_keyStorePass;
	
	public static String getSSL_type() {
		return SSL_type;
	}
	public static String getSSL_keyStorePath() {
		return SSL_keyStorePath;
	}
	public static String getSSL_keyStorePass() {
		return SSL_keyStorePass;
	}
	public static int getThreadSize() {
		return threadSize;
	}
	public static String getIndex() {
		return index;
	}
	public static String getType() {
		return type;
	}
	public static String getQuery() {
		if(StringUtils.isBlank(query)){
			query="{\"size\":1000,\"query\":{\"match_all\":{}}}";
		}
		return query;
	}
	public static String getEsserver() {
		return esserver;
	}
	public static String getEsusername() {
		return esusername;
	}
	public static String getEspassword() {
		return espassword;
	}
	public static boolean isLineFeed() {
		return isLineFeed;
	}
	public static String getDataLayout() {
		return dataLayout;
	}
	public static String getFilePath() {
		return filePath;
	}
	public static String getFileName() {
		return fileName;
	}
	public static String getFileSize() {
		return fileSize;
	}
	public static String getCustomFieldName() {
		return customFieldName;
	}
	public static String getFieldSplit() {
		return fieldSplit;
	}
	public static String getFieldSort() {
		return fieldSort;
	}
	public static boolean isNeedFieldName() {
		return needFieldName;
	}
	public static String getSqlFormat() {
		return sqlFormat;
	}
	public static String getIncludes() {
		return includes;
	}
	public static String getScrollQuery(Integer nowid,Integer maxid) {

		String query = getQuery();
		String includes =ApplicationConfig.getIncludes();
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
	 * 主要拿来校验参数是否输入正确
	 */
	public static void validation(){
		requireNonNull(index, "index 不能为空");
		//requireNonNull(type, "");
		//requireNonNull(query, message);
		requireNonNull(esserver, "esserver集群地址不能为空");
		//requireNonNull(esusername, message);
		//requireNonNull(espassword, message);
		requireNonNull(isLineFeed, "isLineFeed 不能为null");
		requireNonNull(dataLayout, "dataLayout不能为空");
		requireNonNull(filePath, "filePath数据存储文件路径不能为空");
		requireNonNull(fileName, "fileName数据存储文件名不能为空");
		//requireNonNull(fileSize, "");
		//requireNonNull(customFieldName, message);
		//requireNonNull(fieldSplit, message);
		//requireNonNull(fieldSort, message);
		//requireNonNull(needFieldName, "needFieldName 不");
		//requireNonNull(sqlFormat, message);
		//requireNonNull(includes, message);
		//requireNonNull(threadSize, message);
	}
	private static void requireNonNull(Object obj,String msg){
		if(obj==null){
			throw new NullPointerException(msg);
		}
		if(obj instanceof String){
			String mss = String.valueOf(obj);
			if(StringUtils.isBlank(mss)){
				throw new NullPointerException(msg);
			}
		}
		
	}
}
