package com.chenhj.dao.impl;




import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.dao.ConnectionManager;
import com.chenhj.dao.DbDao;
import com.chenhj.util.SqlParser;
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
	private  ConnectionManager dbp =ConnectionManager.getInstance();
	private static String sql = "INSERT INTO %s (%s) VALUES (%s);" ;
	private static Integer insertSize = Config.JDBC_CONFIG.getJdbc_size();
	Map<String,Object> map;
	public DbDaoImpl() {
		map = SqlParser.parserInsert(Config.JDBC_CONFIG.getJdbc_template());
		sql =String.format(sql,SqlParser.getTableName(),StringUtils.join(SqlParser.getColumnList(), ","),StringUtils.join(SqlParser.getValueList(), ",")); 
	}
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
			
			if(size>insertSize){
				list.subList(0, insertSize-1);
			}
			for(JSONObject msg:list){
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
		} catch (SQLException e) {
			throw e;
		}finally{
			dbp.close(null, statement, conn);
		}
		
	}
	private  void dataFormat(PreparedStatement  statement,JSONObject json) throws SQLException{
		int i = 1;
		for (Entry<String, Object> entry : map.entrySet()) {
			Object obj = entry.getValue();
			if(obj instanceof String){
				String value = String.valueOf(obj);
				if(value.startsWith("##param")){
					String v = SqlParser.getConfigParent(value);
					statement.setObject(i, json.get(v));
					i++;
				}
			}
		}
	}

	public static void main(String[] args) {
		
	}

}
