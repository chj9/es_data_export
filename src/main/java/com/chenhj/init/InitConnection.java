/**
 * 
 */
package com.chenhj.init;

import org.apache.commons.lang3.StringUtils;

import com.chenhj.config.Config;
import com.chenhj.dao.ConnectionManager;
import com.chenhj.es.Rest;
import com.chenhj.util.kafka.KafkaUtil;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: InitConnection.java
* @Description: 测试连接是否正常
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午7:37:16 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public class InitConnection {
	private static boolean  jdbcEnabled;
	private static boolean  kafkaEnabled;
	public static void init() throws Exception{
		 initEs();
		//如果启用了DB,检查DB连接
		jdbcEnabled=Config.JDBC_CONFIG.isEnabled();
		kafkaEnabled = Config.Kafka_CONFIG.isEnabled();
		if(jdbcEnabled){
			ConnectionManager dbp =ConnectionManager.getInstance();
			boolean flag = dbp.isValid();
			if(!flag){
				throw new Exception("DB 连接失败");
			}
			String tableName = Config.JDBC_CONFIG.getTableName();
			boolean existTable = dbp.validateTableNameExist(tableName);
			if(!existTable){
				throw new IllegalAccessException(tableName+"表不存在,请检查jdbc_template是否书写正确！！！");
			}
			dbp = null;
		}
		if(kafkaEnabled){
			initKafka();
		}
	}
    /**
     * 初始化ES的连接
     * @throws IllegalAccessException 
     */
    private static  void initEs() throws Exception{
    	String ips = Config.ES_CONFIG.getHosts();
    	String username = Config.ES_CONFIG.getUsername();
    	String password = Config.ES_CONFIG.getPassword();
    	//ES初始化
    	Rest rest = Rest.Client.setHttpHosts(ips.split(","));
		if(StringUtils.isNotBlank(username)&&StringUtils.isNotBlank(password)){
			rest.validation(username, password);
		}
		rest.build();
    }
    /**
     * 初始化Kafka的连接
     * @throws IllegalAccessException 
     */
    private static  void initKafka() throws Exception{
    	  KafkaUtil.validation();
    }
}
