package com.sun.rooster.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;


/**
 * HTTP/HTTPS请求工具类
 *
 * @author sunshibo
 */
public class HttpRequest {

	public static final ContentType DEFAULT_CONTENT_TYPE = ContentType.parse("text/plain; charset=UTF-8");

	/** 连接超时时间； 单位：毫秒 */
	private int socketTimeout = 10000;
	/** 传输超时时间； 单位：毫秒 */
	private int connectTimeout = 30000;
	/** 最大连接池 */
	private int maxConnTotal = 0;
	/** 每个域名最大连接数 */
	private int maxConnPerRoute = 0;
	/** 保持长连接 */
	private boolean soKeepAlive = false;
	/** 使用NoDelay策略 */
	private boolean tcpNoDelay = true;

	/** 是否HTTPS请求 */
	private boolean isHttps = false;
	/** HTTPS私钥 */
	private String primaryKey;
	/** HTTPS私钥密码 */
	private String primaryKeyPassword;
	/** HTTPS支持的协议 */
	private String[] supportedProtocols = new String[] { "TLSv1" };
	/** HTTPS支持的加密套件 */
	private String[] supportedCipherSuites;

	/** 请求器的配置 */
	private RequestConfig requestConfig;
	/** HTTP请求器 */
	private CloseableHttpClient httpClient;

	public void init() throws Exception {
		if (requestConfig == null) {
			requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
		}

		if (httpClient == null) {
			SSLConnectionSocketFactory sslConnectionSocketFactory = null;
			if (isHttps) {
				SSLContext sslContext = null;

				// HTTPS双向证书认证
				if (org.apache.commons.lang.StringUtils.isNotBlank(primaryKey)) {
					char[] keyPassword = primaryKeyPassword.toCharArray();

					InputStream primaryKeyInputStream = new ByteArrayInputStream(primaryKey.getBytes());
					KeyStore keyStore = KeyStore.getInstance("PKCS12");
					keyStore.load(primaryKeyInputStream, keyPassword);
					primaryKeyInputStream.close();

					sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, keyPassword).build();
				}
				// HTTPS单向证书认证
				else {
					sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

						@Override
						public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
							return true;
						}
					}).build();
				}

				sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, supportedProtocols, supportedCipherSuites, SSLConnectionSocketFactory.getDefaultHostnameVerifier()); // SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
			}

			HttpClientBuilder httpClientBuilder = HttpClients.custom();
			httpClientBuilder.setDefaultRequestConfig(requestConfig);
			httpClientBuilder.setMaxConnTotal(maxConnTotal);
			httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
			httpClientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(soKeepAlive).setTcpNoDelay(tcpNoDelay).build());
			if (sslConnectionSocketFactory != null) {
				httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
			}

			httpClient = httpClientBuilder.build();
		}
	}

	////////////////////////
	// GET METHOD
	///////////////////////

	public byte[] doGet(String url, Map<String, String> headers) throws HttpRequestException {
		HttpGet httpGet = new HttpGet(url);
		copyHeaders(httpGet, headers);

		CloseableHttpResponse httpResponse = execute(httpGet);

		try {
			return EntityUtils.toByteArray(httpResponse.getEntity());
		} catch (IOException e) {
			throw new HttpRequestException(HttpRequestException.HTTP_STATUS_ERRPR, e.getMessage(), e);
		}
	}

	public String doGetAsString(String url, Map<String, String> headers) throws HttpRequestException {
		String charset = getContentTypeCharset(headers);
		
		HttpGet httpGet = new HttpGet(url);
		copyHeaders(httpGet, headers);
		
		CloseableHttpResponse httpResponse = execute(httpGet);

		try {
			return EntityUtils.toString(httpResponse.getEntity(), charset);
		} catch (IOException e) {
			throw new HttpRequestException(HttpRequestException.HTTP_STATUS_ERRPR, e.getMessage(), e);
		}
	}

	////////////////////////
	// POST METHOD
	///////////////////////
	public byte[] doPost(String url, Map<String, Object> param) throws HttpRequestException {
		return this.doPost(url , param , null) ;
	}

	public byte[] doPost(String url, byte[] body, Map<String, String> headers) throws HttpRequestException {
		HttpPost httpPost = new HttpPost(url);
		copyHeaders(httpPost, headers);
		httpPost.setEntity(new ByteArrayEntity(body));

		CloseableHttpResponse httpResponse = execute(httpPost);

		try {
			return EntityUtils.toByteArray(httpResponse.getEntity());
		} catch (IOException e) {
			throw  new HttpRequestException(HttpRequestException.HTTP_STATUS_ERRPR, e.getMessage(), e);
		}
	}

	public String doPostAsString(String url, String body, Map<String, String> headers) throws HttpRequestException {
		String charset = getContentTypeCharset(headers);
		
		byte[] bodyBytes = null;
		try {
			bodyBytes = body.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		return doPostAsString(url, bodyBytes, headers);
	}

	public String doPostAsString(String url, byte[] body, Map<String, String> headers) throws HttpRequestException {
		String charset = getContentTypeCharset(headers);
		
		HttpPost httpPost = new HttpPost(url);
		copyHeaders(httpPost, headers);
		httpPost.setEntity(new ByteArrayEntity(body));

		CloseableHttpResponse httpResponse = execute(httpPost);

		try {
			return EntityUtils.toString(httpResponse.getEntity(), charset);
		} catch (IOException e) {
			throw new HttpRequestException(HttpRequestException.HTTP_STATUS_ERRPR, e.getMessage(), e);
		}
	}

	public byte[] doPost(String url, Map<String, Object> paramMap, Map<String, String> headers) throws HttpRequestException {
		String charset = getContentTypeCharset(headers);
		
		HttpPost httpPost = new HttpPost(url);
		copyHeaders(httpPost, headers);
		
		List<NameValuePair> paramList = toNameValuePairList(paramMap);
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, charset);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		CloseableHttpResponse httpResponse = execute(httpPost);

		try {
			return EntityUtils.toByteArray(httpResponse.getEntity());
		} catch (IOException e) {
			throw new HttpRequestException(HttpRequestException.HTTP_STATUS_ERRPR, e.getMessage(), e);
		}
	}

	public String doPostAsString(String url, Map<String, Object> paramMap, Map<String, String> headers) throws HttpRequestException {
		String charset = getContentTypeCharset(headers);
		
		HttpPost httpPost = new HttpPost(url);
		copyHeaders(httpPost, headers);
		
		List<NameValuePair> paramList = toNameValuePairList(paramMap);
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, charset);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

		CloseableHttpResponse httpResponse = execute(httpPost);

		try {
			return EntityUtils.toString(httpResponse.getEntity(), charset);
		} catch (IOException e) {
			throw new HttpRequestException(HttpRequestException.HTTP_STATUS_ERRPR, e.getMessage(), e);
		}
	}
	
	////////////////////////

	public CloseableHttpResponse execute(HttpRequestBase request) throws HttpRequestException {
		CloseableHttpResponse httpResonse = null;
		try {
			httpResonse = httpClient.execute(request);
		} catch (Exception e) {
			throw new HttpRequestException(HttpRequestException.HTTP_STATUS_ERRPR, e.getMessage(), e);
		}

		int statusCode = httpResonse.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new HttpRequestException(statusCode, "请求错误，http状态码不是200", null);
		}

		return httpResonse;
	}

	private void resetRequestConfig() {
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
	}

	private void copyHeaders(HttpRequestBase request, Map<String, String> headers) {
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				request.addHeader(header.getKey(), header.getValue());
			}
		}
	}
	
	private List<NameValuePair> toNameValuePairList(Map<String, Object> paramMap) {
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		if (paramMap != null) {
			for (Map.Entry<String, Object> param : paramMap.entrySet()) {
				String value = param != null ? param.toString() : ""; // TODO null to ""
				paramList.add(new BasicNameValuePair(param.getKey(), value));  
			}
		}
		
		return paramList;
	}
	
	private ContentType getContentType(Map<String, String> headers) {
		if (headers != null) {
			String contentTypeString = headers.get(HTTP.CONTENT_TYPE);
			if (StringUtils.isNotBlank(contentTypeString)) {
				return ContentType.parse(contentTypeString);
			}
		}
		
		return DEFAULT_CONTENT_TYPE;
	}
	
	private String getContentTypeCharset(Map<String, String> headers) {
		return getContentType(headers).getCharset().name();
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		resetRequestConfig();
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		resetRequestConfig();
	}

	public boolean isHttps() {
		return isHttps;
	}

	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
		this.isHttps = true;
	}

	public String getPrimaryKeyPassword() {
		return primaryKeyPassword;
	}

	public void setPrimaryKeyPassword(String primaryKeyPassword) {
		this.primaryKeyPassword = primaryKeyPassword;
	}

	public String[] getSupportedProtocols() {
		return supportedProtocols;
	}

	public void setSupportedProtocols(String[] supportedProtocols) {
		this.supportedProtocols = supportedProtocols;
	}

	public String[] getSupportedCipherSuites() {
		return supportedCipherSuites;
	}

	public void setSupportedCipherSuites(String[] supportedCipherSuites) {
		this.supportedCipherSuites = supportedCipherSuites;
	}

	public RequestConfig getRequestConfig() {
		return requestConfig;
	}

	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public static void main(String[] args) throws Exception {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttps(false);
		httpRequest.init();
		
//		String url = "https://api.mch.weixin.qq.com/pay/closeorder";
//		String body = "<xml><appid>wx2421b1c4370ec43b</appid><mch_id>10000100</mch_id><nonce_str>4ca93f17ddf3443ceabf72f26d64fe0e</nonce_str><out_trade_no>1415983244</out_trade_no><sign>59FF1DF214B2D279A0EA7077C54DD95D</sign></xml>";
//		String rsp = httpRequest.doPostAsString(url, body, null);
//		System.out.println(rsp);
		
//		String url = "https://api.mch.weixin.qq.com/pay/downloadbill";
//		String body = "<xml><appid>wx2421b1c4370ec43b</appid><bill_date>20141110</bill_date><bill_type>ALL</bill_type><mch_id>10000100</mch_id><nonce_str>21df7dc9cd8616b56919f20d9f679233</nonce_str><sign>332F17B766FC787203EBE9D6E40457A1</sign></xml>";

		String url = "http://baidu.com/";

		Map<String, Object> paramMap = null ;
		byte[] rsp = httpRequest.doPost(url, paramMap  , null);
		System.out.println(rsp.length);
		System.out.println(new String(rsp));
//		System.out.println(new String(CompressUtil.ungzip(rsp)));
	}
}