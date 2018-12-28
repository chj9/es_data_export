/**
 * 
 */
package com.chenhj.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.constant.Constant;
import com.chenhj.init.Rest;
import com.chenhj.service.IEsActionService;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: EsActionServiceImpl.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月5日 下午5:43:04 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月5日     chenhj          v1.0.0               修改原因
*/
public class EsActionServiceImpl implements IEsActionService{
	
	private static final Logger logger = LoggerFactory.getLogger(EsActionServiceImpl.class);
	 private RestClient client;
	 private String index;
	 private String type;
	 private String srcollId;
	 private String endPoint1;
	 private String endPoint2 ;
	 private JSONObject params;
	public EsActionServiceImpl() throws Exception {
		 index = Config.ES_CONFIG.getIndex();
		 type = Config.ES_CONFIG.getDocument_type();
		 this.endPoint1 = index+"/_search?scroll=1h";
		 if(StringUtils.isNotBlank(type)){
			 this.endPoint1 = index+"/"+type+"/_search?scroll=1h";

		 }
		 this.endPoint2 = "/_search/scroll"; 
		 client = Rest.Client.getRestClient();
	}
	@Override
	public List<JSONObject> executeQuery(String srcollId,String query) throws Exception {
		 Request request = null;
		 InputStream in = null;
		 String	content ="";
		 List<JSONObject> list =null;
		try {
			if(StringUtils.isNotBlank(srcollId)){
				params = new JSONObject();
				request = new Request(Constant.POST, endPoint2);
				params.put("scroll_id", srcollId);
				params.put("scroll", "1h");
				request.setJsonEntity(params.toJSONString());
			}else{
				request = new Request(Constant.GET, endPoint1);
				request.setJsonEntity(query);
				logger.debug("Query:"+query);
			}
		    //ES 6.4版本之前的实现
		    //Response  response =client.performRequest(Constant.GET,endPoint,Collections.<String, String> emptyMap(),entity);
			//ES6.4以后的实现
			 Response  response =client.performRequest(request);
			 int code = response.getStatusLine().getStatusCode();
		 //如果请求成功
		if(code==HttpStatus.SC_OK){
			in = response.getEntity().getContent();
			content = IOUtils.toString(in,Constant.ENCODE_UTF8);
			if(StringUtils.isNotEmpty(content)){
				try {
					//服务端返回的数据
					JSONObject resultData = JSON.parseObject(content);
					JSONObject hits = resultData.getJSONObject("hits");
					JSONArray hitsData = hits.getJSONArray("hits");
					int size = hitsData.size();
					if(size>0){
						list = new ArrayList<>();
						for(int i=0;i<size;i++){
							JSONObject data = JSON.parseObject(hitsData.getJSONObject(i).getString("_source"));
							list.add(data);
						}
						this.srcollId = resultData.getString("_scroll_id");
					}
				} catch (JSONException e) {
					 //数据格式出错的话直接跳出本次
					 logger.error("数据格式错误:{}",content);
				}
			}
		}else{
			 logger.error("服务端响应异常,状态码:{},内容:{}",code,content);
			 TimeUnit.SECONDS.sleep(120);
			 client = Rest.Client.getRestClient();
		}
		} catch (Exception e) {
			throw e;
		}finally {
			if(in!=null){
				in.close();
			}
		}
		return list;
	}

	@Override
	public String getSrcollId() {
		return srcollId;
	}
	@Override
	public void clearSrcoll(String srcollId) throws IOException {
		if(StringUtils.isNotBlank(srcollId)){
			String endPoint = "/_search/scroll/"+srcollId.trim();
			Request request = new Request(Constant.DELETE, endPoint);
			client.performRequest(request);
		}
	}

}
