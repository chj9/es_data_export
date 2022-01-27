package com.chenhj.dao.impl;




import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.dao.ConnectionManager;
import com.chenhj.dao.DbDao;
/**
 * 
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: DBDaoImpl.java
* @Description: Mysql数据库操作实现类 
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年8月6日 下午3:38:20 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月6日     chenhj          v1.0.0               修改原因
 */
public class DbDaoImpl implements DbDao {
	
	private static final Logger logger = LoggerFactory.getLogger(DbDaoImpl.class);
	private static ConnectionManager dbp = ConnectionManager.getInstance();
	private  String sql = Config.JDBC_CONFIG.getJdbc_template();
	private  Integer insertSize = Config.JDBC_CONFIG.getJdbc_size();
	private  Map<String,Integer> fieldMap = Config.JDBC_CONFIG.getFieldMap();
	@Override
	public synchronized void insert(List<JSONObject> list) throws SQLException {
		//该sql语句是如果库中存在直接覆盖
 		PreparedStatement  statement = null;
		Connection conn = null;
		try {
			conn= dbp.getConnection();
			//创建连接池，以add语句为例
			statement = conn.prepareStatement(sql);
			//关闭连接的自动提交
			conn.setAutoCommit(false);
			//判断条数
			int size = list.size();
			List<JSONObject> listTemp = null;
			//分批存入和写入DB数据,单批insertSize条
			for(int i=0;i<=size;i+=insertSize){					
					if(i+insertSize>size){       
						insertSize=size-i; //作用为insertSize最后没有100条数据则剩余几条listTemp中就装几条
			        }
					listTemp =  list.subList(i,i+insertSize);
					if(listTemp==null||listTemp.isEmpty()){
						break;
					}
					for(JSONObject msg:listTemp){
						try {
							dataFormat(statement,msg);	
						} catch (Exception e) {
							logger.error("批量插入单条出错抛弃,数据:{},异常:{}",msg.toString(),e);
							continue;
						}
						//完成一条语句的赋值
						statement.addBatch();
					}
					//执行批量操作
					statement.executeBatch();
					//提交事务
					conn.commit();
			}
		} catch (SQLException e) {
			throw e;
		}finally{
			dbp.close(null, statement, conn);
		}
	}
	private  void dataFormat(PreparedStatement  statement,JSONObject json) throws SQLException{
		for (Entry<String, Integer> entry : fieldMap.entrySet()) {
			Integer  index = entry.getValue();
			String key = entry.getKey();
			statement.setObject(index,json.get(key));
		}
	}
}
