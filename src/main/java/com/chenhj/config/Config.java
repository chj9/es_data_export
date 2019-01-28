/**
 * 
 */
package com.chenhj.config;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: Config.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月28日 下午4:28:44 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月28日     chenhj          v1.0.0               修改原因
*/
public enum Config {
	EXPOST;
	public static EsConfig ES_CONFIG;
	public static JdbcConfig JDBC_CONFIG;
	public static FileConfig FILE_CONFIG;
	public static KafkaConfig Kafka_CONFIG;
	public static CommonConfig COMMON_CONFIG;
	public static QuartzConfig QUARTZ_CONFIG;
	
}
