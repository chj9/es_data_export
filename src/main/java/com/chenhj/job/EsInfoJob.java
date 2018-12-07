/**
 * 
 */
package com.chenhj.job;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.constant.Constant;
import com.chenhj.init.Rest;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: EsInfoJob.java
* @Description: 用来查询ES的相关信息
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月5日 下午7:58:55 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月5日     chenhj          v1.0.0               修改原因
*/
public class EsInfoJob {
	
	RestClient client;
	public EsInfoJob() throws Exception{
		 client = Rest.Client.getRestClient();
	}
	/**
	 * 获取分片数
	 * @return
	 * @throws Exception 
	 */
	public int getIndexShards(String index) throws Exception{
		Objects.requireNonNull(index, "index can not null");
		String endpoint =index+"/_settings";
		int shards = 0;
		InputStream in =null;
		try {
			Request request = new Request(Constant.GET, endpoint);
			Response response = client.performRequest(request);
			int code = response.getStatusLine().getStatusCode();
			if(code==HttpStatus.SC_OK){
				 in = response.getEntity().getContent();
				String content = IOUtils.toString(in,Constant.ENCODE_UTF8);
				JSONObject json = JSON.parseObject(content);
				JSONObject indexs = json.getJSONObject(index).getJSONObject("settings").getJSONObject("index");
				shards = indexs.getIntValue("number_of_shards");
			}else if(code==HttpStatus.SC_NOT_FOUND){
				throw new Exception(index+" not found");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(in!=null){
				in.close();
			}
		}
		return shards;
	}
}
