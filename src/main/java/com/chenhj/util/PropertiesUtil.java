package com.chenhj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: GetProperties.java
* @Description: 该类的功能描述
*获取配置文件
* @version: v1.0.0
* @author: chenhj
* @date: 2018年6月9日 下午1:54:50 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年6月9日     chenhj          v1.0.0               修改原因
 */
public class PropertiesUtil {
	private  Map<String,String> appSettings = new HashMap<String,String>();
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);
	private  String pathName;
	/**
	 * 初始化系统默认参数
	 */
	public PropertiesUtil(String pathName){
		this.pathName=pathName;
	}
	public PropertiesUtil(){
	}
	private  void init(){
		InputStream in = null;
		try{
			//获取resource中的配置
			File file =new File(pathName);
			if(file.exists()){
				in=new FileInputStream(file);
			}else{
				in=PropertiesUtil.class.getClassLoader().getResourceAsStream(pathName);
			}
			//获取项目同级的配置
			//
			Properties prop = new Properties();
			prop.load(new InputStreamReader(in, "utf-8"));
			Set<Entry<Object, Object>> buf = prop.entrySet();
			Iterator<Entry<Object, Object>> it = buf.iterator();
			while(it.hasNext()){
				Entry<Object, Object> t = it.next();
				appSettings.put((String)t.getKey(), (String)t.getValue());
			}
			
		}catch(IOException e){
			LOG.error("加载配置文件失败!",e);
		}finally{
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
					LOG.error("加载系统参数失败!",e);
				}
			}
		}
	}
	
	/**
	 * 获取配置文件
	 * @return
	 */
	public synchronized  Map<String, String> loadProperties() {
		if(null==pathName||"".equals(pathName)){
			throw new NullPointerException("Properties file path can not null");
		}
		if(null == appSettings || appSettings.isEmpty()){
			init();
		}
		return appSettings;
	}
	public synchronized  Map<String, String> loadProperties(String pathName) {
		this.pathName=pathName;
		if(null == appSettings || appSettings.isEmpty()){
			init();
		}
		return appSettings;
	}
}
