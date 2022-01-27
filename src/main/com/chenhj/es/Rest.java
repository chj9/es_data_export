/**
 * 
 */
package com.chenhj.es;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chenhj.config.Config;
import com.chenhj.constant.Constant;

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
    private static final int MAX_CONNECT_NUM = Runtime.getRuntime().availableProcessors();  
    private static final int MAX_CONNECT_PER_ROUTE = Runtime.getRuntime().availableProcessors();  
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
	public void build() throws Exception{
         boolean needLogin = StringUtils.isNotBlank(username)&&StringUtils.isNotBlank(password);
         builder = RestClient.builder(httpHosts);
         if(needLogin){
        	 final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
             credentialsProvider.setCredentials(AuthScope.ANY,
                     new UsernamePasswordCredentials(username, password));
             builder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
                 @Override
                 public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                     //禁用认证缓存
                	 httpClientBuilder.disableAuthCaching(); 
                     return httpClientBuilder
                         .setDefaultCredentialsProvider(credentialsProvider);
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
         setConnectTimeOutConfig();
         setMutiConnectConfig();
         setEsSSL();
         restClient = builder.build();
         //验证帐号密码
         if(needLogin){
        	boolean flag = validation(); 
        	if(!flag){
        		throw new IllegalAccessException("帐号:"+username+",密码:"+password+">>>验证错误,请检查是否输入正确");
        	}
         }
      
	}
	  /** 
     *     主要关于异步httpclient的连接延时配置 
     */  
    private  void setConnectTimeOutConfig(){  
        builder.setRequestConfigCallback(requestConfigBuilder -> {  
            requestConfigBuilder.setConnectTimeout(CONNECT_TIME_OUT);  
            requestConfigBuilder.setSocketTimeout(SOCKET_TIME_OUT);  
            requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);  
            return requestConfigBuilder;  
        });  
    }  
    private  void setMutiConnectConfig(){  
        builder.setHttpClientConfigCallback(httpClientBuilder -> {  
            httpClientBuilder.setMaxConnTotal(MAX_CONNECT_NUM);  
            httpClientBuilder.setMaxConnPerRoute(MAX_CONNECT_PER_ROUTE);  
            return httpClientBuilder;  
        }); 
        /**
         * estClient支持设置连接池,默认调度线程是1个，连接线程跟处理器是一样的（取决于Runtime.getRuntime().availableProcessors()），自定义的方式代码如下
         */
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
           public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                 return httpClientBuilder.setDefaultIOReactorConfig(
                         IOReactorConfig.custom().setIoThreadCount(Runtime.getRuntime().availableProcessors()).build());
             }
         });
    }
    private void setEsSSL() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException{
    	String SSL_type  = Config.ES_CONFIG.getSsl_type();
    	String SSL_keyStorePath = Config.ES_CONFIG.getSsl_keystorepath();
    	String SSL_keyStorePass= Config.ES_CONFIG.getSsl_keystorepass();
    	if(StringUtils.isNotBlank(SSL_type)&&StringUtils.isNotBlank(SSL_keyStorePath)&&StringUtils.isNotBlank(SSL_keyStorePass)){
	    	KeyStore truststore = KeyStore.getInstance(SSL_type);
	    	Path path= Paths.get(SSL_keyStorePath);
	    	try (InputStream is = Files.newInputStream(path)) {
	    	    truststore.load(is, SSL_keyStorePass.toCharArray());
	    	}
	    	SSLContextBuilder sslBuilder = SSLContexts.custom()
	    	    .loadTrustMaterial(truststore, null);
	    	final SSLContext sslContext = sslBuilder.build();
	    	builder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
	            @Override
	            public HttpAsyncClientBuilder customizeHttpClient(
	                    HttpAsyncClientBuilder httpClientBuilder) {
	                return httpClientBuilder.setSSLContext(sslContext);
	            }
	        });
    	}
    }
	public RestClient getRestClient() throws Exception{
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
	/**
	 * 验证帐号密码是否正确
	 * @return
	 */
	private  boolean validation(){
		boolean flag = false;
		try {
			String endpoint ="_xpack/security/_authenticate";
			Request request = new Request(Constant.GET, endpoint);
			Response response =restClient.performRequest(request);
			int code = response.getStatusLine().getStatusCode();
			if(code==HttpStatus.SC_OK){
				flag = true;
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	public static void main(String[] args) throws Exception {
		RestClient client = Rest.Client.getRestClient();
		
		Response  response =client.performRequest(null);
        InputStream in=response.getEntity().getContent();
        IOUtils.toString(in,"UTF-8");
        //释放该连接
        in.close();
        
	}
}
