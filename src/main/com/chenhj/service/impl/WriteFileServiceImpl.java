/**
 * 
 */
package com.chenhj.service.impl;

import java.io.File;
import java.util.List;
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
public class WriteFileServiceImpl implements IWriteFileService{
	// 分文件导出
	private  static Integer index=null; // 起始文件下标
	String  basePath;
	String  fileName;
	String  max_filesize;
	String  flagFileName;
	String query;
	String  dataLayout;
	private WriteData2File writeData2File;
	public WriteFileServiceImpl() throws Exception{
		writeData2File = new WriteData2File();
		basePath = Config.FILE_CONFIG.getFilepath();
		fileName = Config.FILE_CONFIG.getFilename();
		max_filesize = Config.FILE_CONFIG.getMax_filesize();
		flagFileName = ".es_data_export";
		query = Config.ES_CONFIG.getQuery();
		dataLayout= Config.FILE_CONFIG.getDatalayout();
		query = EncryUtil.encry(query, "MD5");
	}
	@Override
	public  void write2File(List<JSONObject> list) throws Exception {
		try {
			String filePath = "";
			/*******************此处选出标记的文件*************************/
			//判断是否需要分割
			if(StringUtils.isNoneEmpty(max_filesize)){
			  fileName = splitFile();
			}
			fileName = parserFileName(fileName, dataLayout);
			filePath = basePath +File.separator+fileName;
			/*********************************************/
			writeData2File.toWrite(list, filePath,dataLayout);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 文件切割算法
	 * @throws Exception 
	 */
	public String splitFile() throws Exception{
		String fileName = "";
		//String flagStr = "";
		//KB转B
		long max_size = Long.valueOf(max_filesize)*1024;
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
