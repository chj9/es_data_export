/**
 * 
 */
package com.chenhj.constant;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
public class ApplicationConfig {
	private static String index;
	private static String type;
	private static String query;
	private static String esserver;
	private static String esusername;
	private static String espassword;
	private static boolean isLineFeed;
	private static String dataLayout;
	private static String filePath;
	private static String fileName;
	private static String fileSize;
	private static String customFieldName;
	private static String fieldSplit;
	private static String fieldSort;
	private static boolean needFieldName;
	private static String sqlFormat;
	private static String includes;
	private static int threadSize;
	
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
}
