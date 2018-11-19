/**
 * 
 */
package com.chenhj.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.constant.Constant;
import com.chenhj.service.IDataToFileService;
import com.chenhj.util.FileUtil;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: DataToFileServiceImpl.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年11月17日 下午5:13:01 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年11月17日     chenhj          v1.0.0               修改原因
*/
public class DataToFileServiceImpl implements IDataToFileService{
	// 分文件导出
	Map<Integer, Integer> jsonIndex = new HashMap<Integer, Integer>();
	private  static boolean  firstRun = true;
	int index = 0; // 起始文件下标
	String  basePath = Constant.GLOBAL_CONFIG.get("filePath");
	String  fileName = Constant.GLOBAL_CONFIG.get("fileName");
	String  fileSize = Constant.GLOBAL_CONFIG.get("fileSize");
	boolean isLineFeed = Boolean.valueOf( Constant.GLOBAL_CONFIG.get("isLineFeed"));
	String  fieldSplit= Constant.GLOBAL_CONFIG.get("fieldSplit");
	String  fieldSort = Constant.GLOBAL_CONFIG.get("fieldSort");
	boolean needFieldName  =Boolean.valueOf( Constant.GLOBAL_CONFIG.get("needFieldName"));
	String  customFieldName= Constant.GLOBAL_CONFIG.get("customFieldName");
	String  dataLayout= Constant.GLOBAL_CONFIG.get("dataLayout");
	String flagFileName = "flag_montnets";
	
	@Override
	public void write2File(List<JSONObject> list) throws Exception {
		try {
			int dataSize = list.size();
			String flagFilePath = basePath +File.separator+flagFileName;
			String filePath = "";
			String flagStr = "";
			/*******************此处选出标记的文件*************************/
			if(StringUtils.isNoneEmpty(fileSize)){
				int num = Integer.valueOf(fileSize);
				if(firstRun){
					try {
						String flag  = FileUtil.fileRead(flagFilePath);
						//查看是否是第一批数据
						if(StringUtils.isNoneEmpty(flag)){
							String flags[] = flag.split(",");
							index = Integer.valueOf(flags[0]);
							Integer count = Integer.valueOf(flags[1].trim());
							if (count >= num) {
								jsonIndex.put(++index, dataSize);
							} else {
								jsonIndex.put(index, count + dataSize);
							}
						}else{
							index = 0;
							jsonIndex.put(0, dataSize);
						}
					} catch (FileNotFoundException e) {
						index = 0;
						jsonIndex.put(0, dataSize);
					}
					firstRun = false;
					filePath = basePath +File.separator+fileName+"_"+index;
				}else if(!firstRun){
					index = sedAndGetIndex(dataSize,num);
				}
				flagStr = index+","+jsonIndex.get(index);
			}else{
				filePath = basePath +File.separator+fileName;
			}
			/*********************************************/
			String str =getJsonStr(list);
			FileUtil.writeFile(filePath,str);
			//该批数据写完,将标记写入日志中\
			if(StringUtils.isNoneEmpty(flagStr)){
				FileUtil.clearInfoForFile(flagFilePath);
				FileUtil.writeFile(flagFilePath,flagStr);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	// 获取要写入的文件下标
	public int sedAndGetIndex(int size,int num) {
		
		if (jsonIndex.size() == 0) {
			jsonIndex.put(0, size);
			return 0;
		}
		int count = jsonIndex.get(index);
		if (count >= num) {
			jsonIndex.put(++index, size);
		} else {
			jsonIndex.put(index, count + size);
		}
		return index;
	}
	public String getJsonStr(List<JSONObject> dataList) {
		StringBuilder sb = new StringBuilder();
		try {
			for (JSONObject data : dataList) {
				if(Constant.JSON.equals(dataLayout)){
					sb.append(data.toJSONString());
				}else if(Constant.TXT.equals(dataLayout)){
					sb.append(dealTxt(data));
				}
				if(isLineFeed){
					sb.append("\r\n");
				}
			}
		} catch (Exception e) {
			throw (e);
		}
		return sb.toString();
	}
	public  String dealTxt(JSONObject json){
		List<String> list = new ArrayList<>();
		
		List<Map<String,Object>> listMap = new ArrayList<>();
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

		return StringUtils.join(list,fieldSplit);
	}
	public String replaceKey(String oldkey){
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
