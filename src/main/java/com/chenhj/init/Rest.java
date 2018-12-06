/**
 * 
 */
package com.chenhj.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**   
* Copyright: Copyright (c) 2018 Montnets
* 
* @ClassName: EsConnect.java
* @Description: 初始化ES连接
*
* @version: v1.0.0
* @author: chenhj
* @date: 2018年12月4日 下午4:59:02 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年12月4日     chenhj          v1.0.0               修改原因
*/
public enum Rest {
	Client;
	private static final Logger logger = LoggerFactory.getLogger(Rest.class);
	private static final String SCHEME = "http";
	private static RestClient restClient;
	private static HttpHost[] httpHosts;
	private static String username;
	private static String password;
	private Integer timeout=30000;
	private RestClientBuilder builder;
    private static final int CONNECT_TIME_OUT = 1000;  
    private static final int SOCKET_TIME_OUT = 30000;  
    private static final int CONNECTION_REQUEST_TIME_OUT = 500;  
    private static final int MAX_CONNECT_NUM = 100;  
    private static final int MAX_CONNECT_PER_ROUTE = 100;  
	/**
	 * 设置集群IP,逗号想个,ip1:端口,ip2:端口
	 * @param httpHosts
	 */
	public Rest setHttpHosts(String[] ips) {
		Objects.requireNonNull(ips,"IP不能为空...");
		int ip_num = ips.length;
		httpHosts = new HttpHost[ip_num];
		for(int i=0;i<ip_num;i++){
			String ip = ips[i];
			String ipvalue[] = ip.split("\\:");
			Integer port = Integer.valueOf(ipvalue[1]);
			HttpHost httpHost = new HttpHost(ipvalue[0],port, SCHEME);
			httpHosts[i]=httpHost;
		}
		return this;
	}
	public Rest setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public  Rest validation(String username1,String password1){
		username = username1;
		password = password1;
		return this;
	}
	public void build(){
        
         builder = RestClient.builder(httpHosts);
         if(StringUtils.isNotBlank(username)&&StringUtils.isNotBlank(password)){
        	 final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
             credentialsProvider.setCredentials(AuthScope.ANY,
                     new UsernamePasswordCredentials(username, password));
             builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                 @Override
                 public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                     return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                 }
             });
         }
         //设置节点选择器
         builder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);
         builder.setFailureListener(new RestClient.FailureListener() {
        	    @Override
        	    public void onFailure(Node node) {
        	    	logger.warn("IP:{} 连接失败...", node.getHost());
        	    }
         });
         builder.setMaxRetryTimeoutMillis(timeout);
         restClient = builder.build();
      
	}
	  /** 
     *     主要关于异步httpclient的连接延时配置 
     */  
    public  void setConnectTimeOutConfig(){  
        builder.setRequestConfigCallback(requestConfigBuilder -> {  
            requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);  
            requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);  
            requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);  
            return requestConfigBuilder;  
        });  
    }  
    public  void setMutiConnectConfig(){  
        builder.setHttpClientConfigCallback(httpClientBuilder -> {  
            httpClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM);  
            httpClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);  
            return httpClientBuilder;  
        });  
    }
	public RestClient getRestClient(){
		if(restClient==null){
			build();
		}
		return restClient;
	}
	public void close() throws IOException{
		if(restClient!=null){
			restClient.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		RestClient client = Rest.Client.getRestClient();
		
		Response  response =client.performRequest(null);
        InputStream in=response.getEntity().getContent();
        IOUtils.toString(in,"UTF-8");
        //释放该连接
        in.close();
        
	}
}
