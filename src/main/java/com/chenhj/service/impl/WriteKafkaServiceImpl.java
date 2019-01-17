/**
 * 
 */
package com.chenhj.service.impl;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenhj.config.Config;
import com.chenhj.constant.Constant;
import com.chenhj.service.IWriteKafkaService;
import com.chenhj.util.MyTool;
import com.chenhj.util.kafka.KafkaUtil;
/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: WriteKafkaServiceImpl.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年11月17日 下午5:13:01 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年11月17日     chenhj          v1.0.0               修改原因
*/
public class WriteKafkaServiceImpl implements IWriteKafkaService{
	private int send_size;
	private String topic;
	private int delay;
	private JSONObject add_value_JSON;
	private Map<String,String> replace_key_Map;

	public WriteKafkaServiceImpl() {
		this.send_size = Config.Kafka_CONFIG.getSend_size();
		this.topic = Config.Kafka_CONFIG.getTopic();
		this.delay = Config.Kafka_CONFIG.getDelay();
		this.add_value_JSON = Config.Kafka_CONFIG.getAdd_value_JSON();
		this.replace_key_Map = Config.Kafka_CONFIG.getReplace_key_Map();
	}

	@Override
	public  void write2Kafka(List<JSONObject> list) throws Exception {
		try {
			//是否需要处理数据
			if(add_value_JSON!=null){
				addValue(list);
			}
			if(replace_key_Map!=null){
				replaceKey(list);
			}
			int size = list.size();
			/*******************数据分批次写入kafka*********************/
		    int toIndex=send_size;
		    long startTime = System.currentTimeMillis();
		    List<JSONObject> newList = null;
			//分批数据,单批100条
		    for(int i=0;i<=size;i+=send_size){					
				if(i+send_size>size){       
		           toIndex=size-i; //作用为toIndex最后没有100条数据则剩余几条newList中就装几条
		        }
				newList =  list.subList(i,i+toIndex);
				String msg = JSON.toJSONString(newList);
				KafkaUtil.sendMessage(msg,topic);
				//查看是否启用延迟写入,启用的话在这里造成阻塞
				if(delay>0){
					long endTime = System.currentTimeMillis();
					long differ = endTime-startTime;
					//查看是否达到延迟时间,未到达则延迟
					if(differ<delay*1000){
						Thread.sleep(delay*1000-differ);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	private void  addValue(List<JSONObject> list){

		for(String str:add_value_JSON.keySet()){
			String value = add_value_JSON.get(str)+"";
			if(value.startsWith("#{now")){
				String ss[] = MyTool.getConfigParent(value).split(Constant.COMMA_SIGN);
				if(ss.length==1){
					add_value_JSON.put(str,System.currentTimeMillis()/1000);
				}else if(ss.length==2){
					add_value_JSON.put(str, MyTool.getNowTime(ss[1]));
				}
			}
		}
		for(JSONObject json:list){
				json.putAll(add_value_JSON);
		}
	}
	private void replaceKey(List<JSONObject> list){
		for(JSONObject json:list){
			 for (Map.Entry<String, String> m : replace_key_Map.entrySet()) {
				 String oldKey =  m.getKey();
				 String newKey =  m.getValue();
				 Object value = json.get(oldKey);
				 //不为null说明有值
				 if(value!=null){
					 json.remove(oldKey);
					 json.put(newKey, value);
				 }
			}
		}
	}

}
