/**
 * 
 */
package com.chenhj.init;

import org.apache.commons.lang3.StringUtils;

import com.chenhj.config.Config;
import com.chenhj.dao.ConnectionManager;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: InitConnection.java
* @Description: 该类的功能描述
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
	public static void init() throws Exception{
		 initEs();
		//如果启用了DB,检查DB连接
		jdbcEnabled=Config.JDBC_CONFIG.isEnabled();
		if(jdbcEnabled){
			ConnectionManager dbp =ConnectionManager.getInstance();
			boolean flag = dbp.isValid();
			if(!flag){
				throw new Exception("DB 连接失败");
			}
			dbp = null;
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
}
