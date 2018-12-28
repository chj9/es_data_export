/**
 * 
 */
package com.chenhj.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.constant.Constant;
import com.chenhj.service.IWriteFileService;
import com.chenhj.util.EncryUtil;
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
public class WriteDbServiceImpl implements IWriteFileService{
	// 分文件导出
	private  static Map<Integer, Integer> jsonIndex = new HashMap<Integer, Integer>();
	private  static boolean  firstRun = true;
	private  static int index = 0; // 起始文件下标
	String  basePath = Config.FILE_CONFIG.getFilepath();
	String  fileName = Config.FILE_CONFIG.getFilename();
	String  fileSize = Config.FILE_CONFIG.getFilesize();

	String  flagFileName = ".es_data_export";
	String query = Config.ES_CONFIG.getQuery();
	String  dataLayout= Config.FILE_CONFIG.getDatalayout();

	@Override
	public  void write2File(List<JSONObject> list) throws Exception {
		try {
			String filePath = "";
			/*******************此处选出标记的文件*************************/
			//判断是否需要分割
			if(StringUtils.isNoneEmpty(fileSize)){
				int dataSize = list.size();
			 	filePath = splitFile(dataSize);
			}else{
				filePath = basePath +File.separator+fileName;
			}
			/*********************************************/
			WriteData2File.toWrite(list, filePath,dataLayout);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 文件切割算法
	 * @throws Exception 
	 */
	public String splitFile(int dataSize) throws Exception{
		String filePath = "";
		String flagStr = "";
		String flagFilePath = basePath +File.separator+flagFileName;
			int num = Integer.valueOf(fileSize);
			query = EncryUtil.encry(query, "MD5");
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
			}else if(!firstRun){
				index = sedAndGetIndex(dataSize,num);
			}
			filePath = basePath +File.separator+fileName+"_"+index;
			flagStr = index+Constant.COMMA_SIGN+jsonIndex.get(index)+Constant.COMMA_SIGN+query;
			//将标记写入日志中,该方法用于多文件切割时候用到
			FileUtil.clearInfoForFile(flagFilePath);
			FileUtil.writeFile(flagFilePath,flagStr);
			return filePath;
	} 
	/**
	 * 获取要写入的文件下标
	 * @param size 数据长度
	 * @param num 单个文件最大长度
	 * @return
	 */
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

}
