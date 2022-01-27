/**
 * 
 */
package com.chenhj.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.constant.Constant;
import com.chenhj.util.FileUtil;
import com.chenhj.util.SqlParser;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: WriteData2File.java
* @Description: 根据文件类型写文件
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月11日 下午4:57:10 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月11日     chenhj          v1.0.0               修改原因
*/
public class WriteData2File {
	 private String  customFieldName;
	 private boolean isLineFeed;
	 private String  fieldSplit;
	 private  String  fieldSort;
	 private boolean needFieldName;
	 private String sql_format;
	 
	 public  WriteData2File(){
		 customFieldName= Config.FILE_CONFIG.getCustom_field_name();
		 isLineFeed = Config.FILE_CONFIG.getLinefeed();
		 fieldSplit=  Config.FILE_CONFIG.getField_split();
		 fieldSort = Config.FILE_CONFIG.getField_sort();
		 needFieldName  =Config.FILE_CONFIG.getNeed_field_name();
		 sql_format = Config.FILE_CONFIG.getSql_format();
	 }
	/**
	 *写文件工具类
	 * @param list 数据list
	 * @param filePath 文件路径
	 * @param fileType 文件类型
	 * @throws IOException 
	 */
	public  void toWrite(List<JSONObject> list,String filePath,String fileType) throws IOException{
		//获取数据字符串集合
		String str =getJsonStr(list,fileType);
		FileUtil.writeFile(filePath,str);
	} 
	private  String getJsonStr(List<JSONObject> dataList,String fileType) {
		StringBuilder sb = new StringBuilder();
		try {
			for (JSONObject data : dataList) {
				switch (fileType) {
					case Constant.JSON:
						sb.append(data.toJSONString());
						if(isLineFeed){
							sb.append("\r\n");
						}
						break;
					case Constant.TXT:
						sb.append(txtHandler(data,fieldSplit));
						if(isLineFeed){
							sb.append("\r\n");
						}
						break;
					case Constant.SQL:
						sb.append(sqlHandler(data));
						sb.append("\r\n");
						break;
					default:
						break;
				}
			}
		} catch (Exception e) {
			throw (e);
		}
		return sb.toString();
	}
	private  String sqlHandler(JSONObject json){
		String sql = SqlParser.replaceToValue(sql_format, json);
		return sql;
	}
	private   String txtHandler(JSONObject json,String split){
		List<String> list = new ArrayList<>();
		List<Map<String,Object>> listMap = fieldSort(json);

		if(!listMap.isEmpty()){
			for (Map<String, Object> map : listMap) {
				for (Entry<String, Object> entry : map.entrySet()) {
					String fieldName =entry.getKey();
					if(StringUtils.isNoneEmpty(customFieldName)){
						fieldName = replaceKey(fieldName);
					}
					Object fieldValue =entry.getValue();
					if(needFieldName){
						list.add(fieldName+"="+fieldValue);
					}else{
						list.add(fieldValue+"");
					}
				}
	        }
		}else{
			for (Entry<String, Object> entry : json.entrySet()) {
				String fieldName =entry.getKey();
				if(StringUtils.isNoneEmpty(customFieldName)){
					fieldName = replaceKey(fieldName);
				}
				Object fieldValue =entry.getValue();
				if(needFieldName){
					list.add(fieldName+"="+fieldValue);
				}else{
					list.add(fieldValue+"");
				}
	        }
		}
		return StringUtils.join(list,split);
	}
	private  List<Map<String,Object>> fieldSort(JSONObject json){
		List<Map<String,Object>> listMap = new  ArrayList<>();
		//字段有序读出
		if(StringUtils.isNoneEmpty(fieldSort)){
			String fields[] = fieldSort.split(",");
			for(String field:fields){
				Map<String,Object> map = new HashMap<>();
				Object value = json.get(field);
				map.put(field, value);
				listMap.add(map);
			}
		}
		return listMap;
	}
	/**
	 * 特殊需求,有的需要替换key
	 * @param oldkey
	 * @return
	 */
	private  String replaceKey(String oldkey){
		String keySet[] = customFieldName.split(",");
		for(String key:keySet){
			String keys[] = key.split(":");
			if(keys[0].equals(oldkey)){
				oldkey = keys[1];
				break;
			}
		}
		return oldkey;
	}
}
