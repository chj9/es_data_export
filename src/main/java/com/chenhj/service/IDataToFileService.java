/**
 * 
 */
package com.chenhj.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: IDataToFileService.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年11月17日 下午5:11:43 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年11月17日     chenhj          v1.0.0               修改原因
*/
public interface IDataToFileService {
		
	public void write2File(List<JSONObject> list) throws Exception;
}
