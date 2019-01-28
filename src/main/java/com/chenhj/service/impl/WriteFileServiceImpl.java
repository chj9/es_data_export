/**
 * 
 */
package com.chenhj.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class WriteFileServiceImpl implements IWriteFileService{
	// 分文件导出
	private volatile static Integer index=null; // 起始文件下标
	// 分文件导出
	private static volatile Map<Integer, Integer> jsonIndex = new HashMap<Integer, Integer>();
	private String  basePath;
	private String  fileName;
	private String  max_length_file;
	private String query;
	private String  dataLayout;
	private String split_method;
	private boolean need_split_file;
	private WriteData2File writeData2File;
	public WriteFileServiceImpl() throws Exception{
		this.writeData2File = new WriteData2File();
		this.basePath = Config.FILE_CONFIG.getFilepath();
		this.fileName = Config.FILE_CONFIG.getFilename();
		this.max_length_file = Config.FILE_CONFIG.getMax_length_file();
		this.query = Config.ES_CONFIG.getQuery();
		this.dataLayout= Config.FILE_CONFIG.getDatalayout();
		this.query = EncryUtil.encry(query, "MD5");
		this.need_split_file = Config.FILE_CONFIG.getNeed_split_file();
		this.split_method = Config.FILE_CONFIG.getSplit_method();
	}
	@Override
	public synchronized void write2File(List<JSONObject> list) throws Exception {
		try {
			String filePath = "";
			/*******************此处选出标记的文件*************************/
			//判断是否需要分割
			if(need_split_file){
				switch (split_method) {
				case "disk":
					fileName = splitFile();
					break;
				case "amount":
					int size = list.size();
					fileName =fileName+"_"+sedAndGetIndex(size);
					break;
				default:
					break;
				}
			}
			fileName = parserFileName(fileName, dataLayout);
			filePath = basePath +File.separator+fileName;
			/*********************************************/
			writeData2File.toWrite(list, filePath,dataLayout);
		} catch (Exception e) {
			throw e;
		}
	}
	//以文件条数分割算法
	// 获取要写入的文件下标
	public int sedAndGetIndex(int size) {
		if (jsonIndex.size() == 0) {
			jsonIndex.put(0, size);
			return 0;
		}
		if(index==null){
			index = 0;
		}
		int count = jsonIndex.get(index);
		
		if (count >= Integer.valueOf(max_length_file)) {
			jsonIndex.put(++index, size);
		} else {
			jsonIndex.put(index, count + size);
		}
		return index;
	}
	/**
	 * 以文件大小进行文件切割算法
	 * @throws Exception 
	 */
	public String splitFile() throws Exception{
		String fileName = "";
		//String flagStr = "";
		//KB转B
		long max_size = Long.valueOf(max_length_file)*1024;
		//String flagFilePath = basePath +File.separator+flagFileName;
		 long fileSize =  FileUtil.getFileSize("");
			//3,114,3f5ea8e4e6cfb52f90310413623f25f9
		//	String flag  = FileUtil.fileRead(flagFilePath);
			//查看是否是第一批数据
			if(index!=null){
				//String flags[] = flag.split(",");
				//index = Integer.valueOf(flags[0]);
				String filePath = basePath +File.separator+this.fileName+"_"+index;
				//获取当前文件大小
				fileSize =  FileUtil.getFileSize(filePath);
				if (fileSize >= max_size) {
					++index;
				}else if(fileSize == -1){
					index =0;
				}
			}else{
				index =0;
			}
			fileName = this.fileName+"_"+index;
			//flagStr = index+Constant.COMMA_SIGN+query;
			//将标记写入日志中,该方法用于多文件切割时候用到
			//FileUtil.clearInfoForFile(flagFilePath);
			//FileUtil.writeFile(flagFilePath,flagStr);
			return fileName;
	} 
	private  String parserFileName(String  fileName,String fileType) {
		try {
			switch (fileType) {
					case Constant.SQL:
						fileName= fileName+".sql";
						break;
					case Constant.CSV:
						fileName= fileName+".csv";
						break;
					default:
						break;
				}
		} catch (Exception e) {
			throw (e);
		}
		return fileName;
	}
	public static void main(String[] args) {
		int i = 0;
		i++;
		System.out.println(i);
	}
}
