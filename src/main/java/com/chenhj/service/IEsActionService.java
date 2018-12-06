/**
 * 
 */
package com.chenhj.service;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: IEsActionService.java
* @Description: ES操作类
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月5日 下午5:37:47 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月5日     chenhj          v1.0.0               修改原因
*/
public interface IEsActionService {
		public List<JSONObject> executeQuery(String srcollId,String query) throws Exception ;
		public String getSrcollId();
		public void clearSrcoll(String srcollId) throws IOException ;
}
