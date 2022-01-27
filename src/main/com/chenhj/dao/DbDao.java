package com.chenhj.dao;



import java.sql.SQLException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;


/**
 * 
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: DBDao.java
* @Description: 数据库接口
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年8月7日 上午8:55:59 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月7日     chenhj          v1.0.0               修改原因
 */
public interface DbDao {

	public void insert(List<JSONObject> list) throws SQLException;

}
