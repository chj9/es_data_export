package com.chenhj.dao;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.chenhj.config.Config;
import com.chenhj.util.DriverLoader;
/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: DruidManager.java
* @Description: 数据库连接池管理
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年10月13日 下午3:03:52 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年10月13日     chenhj          v1.0.0               修改原因
*/
public class ConnectionManager {
	private static Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
	/** 定义连接池的数据源 */
	private static DruidDataSource cpds =null;
	private static volatile ConnectionManager dbConnection;
	/**
	 * 在构造函数初始化的时候获取数据库连接
	 * @throws Exception 
	 */
	private ConnectionManager() throws Exception{
		/** 获取属性文件中的值 **/
		String jdbc_driver_library = Config.JDBC_CONFIG.getJdbc_driver_library();
		String driverName = Config.JDBC_CONFIG.getJdbc_driver_class();
		String url = Config.JDBC_CONFIG.getJdbc_connection_string();
		String username = Config.JDBC_CONFIG.getJdbc_user();
		String password = Config.JDBC_CONFIG.getJdbc_password();	
			/** 数据库连接池对象 **/
			cpds = new DruidDataSource();
			/** 设置数据库连接驱动 **/
			//cpds.setDriverClassName(driverName);
			cpds.setDriver(DriverLoader.getDriverLoaderByName(jdbc_driver_library, driverName));
			/** 设置数据库连接地址 **/
			cpds.setUrl(url);
			/** 设置数据库连接用户名 **/
			cpds.setUsername(username);
			/** 设置数据库连接密码 **/
			cpds.setPassword(password);
			/** 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 **/
			cpds.setTestWhileIdle(true);
			/*******最大连接池数量********/
			cpds.setMaxActive(30);
			/*******初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时********/
			cpds.setInitialSize(3);
			/** 超过30分钟开始关闭空闲连接  **/
			cpds.setRemoveAbandonedTimeout(1800);
			/** 最大等待时间为60S  单位是毫秒**/
			cpds.setMaxWait(60000);
			/**配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒**/
			cpds.setTimeBetweenEvictionRunsMillis(30000);
		}

	/**
	 * 获取数据库连接对象，单例
	 * 
	 * @return
	 * @throws Exception 
	 */
	public synchronized static ConnectionManager getInstance(){
		if (dbConnection == null) {
			synchronized (ConnectionManager.class) {
				if (dbConnection == null) {
					try {
						dbConnection = new ConnectionManager();
					} catch (Exception e) {
						LOG.error("获取DB连接失败...",e);
					}
				}
			}
		}
		return dbConnection;
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return 数据库连接
	 * @throws SQLException
	 */
	public  final  Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}
	/**
	 * 判断表是否存在
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
    public boolean validateTableNameExist(String tableName) throws SQLException {  
        Connection con= null;  
        ResultSet rs = null;  
       try {
    	   con = getConnection();
    	   rs = con.getMetaData().getTables(null, null, tableName, null); 
	       if (rs.next()) {  
	           return true;  
	       }else {  
	            return false;  
	       }  
	   	} catch (Exception e) {
			throw e;
		}finally {
			close(rs,null,con);
		}
    }  
	/**
	 * 验证数据库连接是否有效
	 * @return
	 * @throws SQLException
	 */
	public boolean isValid() throws SQLException{
		Connection con = cpds.getConnection();
		try {
			if(con!=null){
				return con.isValid(10);
			}
		} catch (Exception e) {
			throw e;
		}finally {
			if(con!=null){
				con.close();
			}
		}
		return false;
	}
	public void close(ResultSet rs, Statement stmt, Connection connection) throws SQLException {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			LOG.error("数据连接关闭失败！", e);
			throw e;
		}
	}
}
