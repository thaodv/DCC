package io.wexchain.passport.chain.observer.ext.integration.http;

import org.apache.commons.beanutils.BeanMap;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient客户端
 * @version 1.0
 */
public class HttpClient {

	private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

	public static final CloseableHttpClient httpClient;
	public static final String DEFAULT_CHARSET = "UTF-8";

	//静态单例对象
	private HttpClient() {};

	static {
		httpClient = SSLUtils.createSSLInsecureClient();
	}

	/**
	 * 发送POST请求
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 */
	public static String doPost(String url, Map<String, Object> params, Map<String, Object> headers) {
		BeanMap beanMap = new BeanMap(params);
		return doPost(url, beanMap, headers, DEFAULT_CHARSET);
	}
	/**
	 * 发送POST请求
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String doPostWithJson(String url, Object obj, Map<String, Object> headers) {

		BeanMap beanMap = new BeanMap(obj);

		return doPost(url, beanMap, headers, DEFAULT_CHARSET);
	}

	/**
	 * GET请求
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String doGet(final String url) throws Exception {
		return doGet(url,null);
	}


	/**
	 * GET请求
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String doGet(final String url, Map<String, Object> headers){
		if (httpClient == null) {
			return null;
		}
		// 创建请求方式和URL对象
		HttpGet httpGet = new HttpGet(url);
		// HTTP 响应对象
		CloseableHttpResponse response = null;
		//设置headers
		if (null != headers) {
			for (String key : headers.keySet()) {
				httpGet.setHeader(key, headers.get(key).toString());
			}
		}
		try {
			// 设置采集唯一参数serverId
			response = httpClient.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
			return null;
		}catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpGet.releaseConnection();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}




	/**
	 * 发送POST请求
	 * @param url
	 * @param params
	 * @param headers
	 * @param charset 字符集
	 * @return
	 */
	public static String doPost(String url, BeanMap params, Map<String, Object> headers, String charset) {

		if (httpClient == null) {
			return null;
		}

		// 创建请求方式和URL对象
		HttpPost httpPost = new HttpPost(url);
		//设置headers
		if (null != headers) {
			for (String key : headers.keySet()) {
				httpPost.setHeader(key, headers.get(key).toString());
			}
		}
		//HTTP 响应对象
		CloseableHttpResponse response = null;
		//设置采集唯一参数serverId
		try {
			List<NameValuePair> nvpParams = resolveParams(params);
			HttpEntity httpEntity = new UrlEncodedFormEntity(nvpParams, charset);
			httpPost.setEntity(httpEntity);
			response = httpClient.execute(httpPost);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				if (null == entity) {
					return null;
				}
				String respJson = EntityUtils.toString(entity, charset);
				return respJson;
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpPost.releaseConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 解析Map格式封装的POST请求参数
	 * @param params 存放请求参数Map对象
	 * @return
	 */
	private static List<NameValuePair> resolveParams(BeanMap params) {

		List<NameValuePair> nvpList = new ArrayList<>();
		for (Object key : params.keySet()) {
			NameValuePair nvp = new BasicNameValuePair(key.toString(), params.get(key).toString());
			nvpList.add(nvp);
		}
		return nvpList;
	}

}
