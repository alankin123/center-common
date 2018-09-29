package com.alankin.center.common.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alankin.center.common.exception.CenterException;

public class HttpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	public static final String UTF8 = "UTF-8";
	public static final String GBK = "GBK";
	public static final String GB2312 = "GB2312";

	private static HttpConnectionManager connectionManager = null;

	/** 单个HOST最大连接数 */
	private static int defaultMaxConnPerHost = 500;
	/** 连接池最大连接数 */
	private static int defaultMaxTotalConn = 500;
	/** 请求超时 */
	private static int defaultConnectionManagerTimeout = 600 * 1000;
	/** 请求超时 */
	private static int defaultConnectionTimeout = 600 * 1000;
	/** 响应超时 */
	private static int defaultReadTimeout = 600 * 1000;

	public static HttpConnectionManager getManager() {
		if (connectionManager == null) {
			connectionManager = new MultiThreadedHttpConnectionManager();
			connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
			connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);
			connectionManager.getParams().setConnectionTimeout(defaultConnectionTimeout);
			connectionManager.getParams().setSoTimeout(defaultReadTimeout);
			connectionManager.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			connectionManager.getParams().setTcpNoDelay(true);
		}
		return connectionManager;
	}

	public static String post(String url, Map<String, String> param, Map<String, String> headers) throws IOException, CenterException {
		return post(url, param, headers, UTF8);
	}

	public static String post(String url, Map<String, String> param) throws IOException, CenterException {
		return post(url, param, null, UTF8);
	}

	public static String post(String url, Map<String, String> param, String charset) throws IOException, CenterException {
		return post(url, param, null, charset);
	}

	public static String post(String url, String param, Map<String, String> headers) throws IOException, CenterException {
		return post(url, param, headers, UTF8);
	}

	public static String post(String url, String param, String charset) throws IOException, CenterException {
		return post(url, param, null, charset);
	}

	public static String post(String url, String param) throws IOException, CenterException {
		return post(url, param, null, UTF8);
	}

	public static String get(String url) throws IOException, CenterException {
		return get(url, null, UTF8);
	}

	public static String get(String url, Map<String, String> headers) throws IOException, CenterException {
		return get(url, headers, UTF8);
	}

	public static String get(String url, String charset) throws IOException, CenterException {
		return get(url, null, charset);
	}

	public static String get(String url, Map<String, String> header, String charset) throws IOException, CenterException {
		logger.debug("send_url : {}", url);

		String reciveStr = null;
		GetMethod getMethod = null;
		try {
			HttpClient httpClient = new HttpClient(getManager());
			getMethod = new GetMethod(url);
			getMethod.setRequestHeader("Connection", "close");
			getMethod.addRequestHeader("Content-Type", "text/xml");
			getMethod.getParams().setContentCharset(UTF8);
			httpClient.getParams().setConnectionManagerTimeout(defaultConnectionManagerTimeout);
			if (header != null) {
				for (Entry<String, String> e : header.entrySet()) {
					getMethod.setRequestHeader(e.getKey(), e.getValue());
				}
			}
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			// HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发 301或者302
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = getMethod.getResponseHeader("location");
				logger.debug("location : {}", locationHeader.getValue());
			}

			if (statusCode > 299) {
				throw new CenterException("HTTP_RES_ERROR");
			}

			// 打印服务器返回的状态
			logger.debug("return code : {}", statusCode);
			reciveStr = getMethod.getResponseBodyAsString();
			logger.debug("return data : {}", reciveStr);
			return reciveStr;
		} catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			// 释放连接
			if (getMethod != null) {
				//getMethod.releaseConnection();
			}
		}

	}

	public static String post(String url, Map<String, String> form, Map<String, String> header, String charset) throws CenterException, IOException {

		logger.debug("url     : {}", url);
		logger.debug("form    : {}", form.toString());

		// 使用POST方法
		PostMethod postMethod = null;
		try {
			HttpClient httpClient = new HttpClient(getManager());
			postMethod = new PostMethod(url);
			postMethod.setRequestHeader("Connection", "close");
			postMethod.getParams().setContentCharset(charset);

			// 将header的值放入postMethod中
			if (header != null) {
				for (Entry<String, String> e : header.entrySet()) {
					postMethod.setRequestHeader(e.getKey(), e.getValue());
				}
			}
			// 组装表单
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			Set<String> keySet = form.keySet();
			for (String key : keySet) {
				nvps.add(new NameValuePair(key, form.get(key)));
			}
			NameValuePair[] param = new NameValuePair[nvps.size()];
			nvps.toArray(param);
			postMethod.addParameters(param);

			// 执行postMethod
			int statusCode = httpClient.executeMethod(postMethod);

			if (statusCode > 299) {
				throw new CenterException("HTTP_RES_ERROR");
			}

			String reciveStr = postMethod.getResponseBodyAsString();

			logger.debug("receive : {}", reciveStr);
			return reciveStr;
		} catch (RuntimeException e) {
			logger.error("receive:error", e);
			throw e;
		} finally {
			// 释放连接
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
	}

	public static String post(String url, String data, Map<String, String> header, String charset) throws IOException, CenterException {

		logger.info("url     : " + url);
		logger.info("data    : " + data);

		// 使用POST方法
		String reciveStr = null;
		HttpClient httpClient = new HttpClient(getManager());
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Connection", "close");
		postMethod.getParams().setContentCharset(charset);

		try {
			postMethod.setRequestEntity(new StringRequestEntity(data, "application/json", UTF8));
			// 将表单的值放入postMethod中
			if (header != null) {
				for (Entry<String, String> e : header.entrySet()) {
					postMethod.setRequestHeader(e.getKey(), e.getValue());
				}
			}
			// 执行postMethod
			int statusCode = httpClient.executeMethod(postMethod);

			if (statusCode > 299) {
				logger.error("code    : " + statusCode);
				throw new CenterException("HTTP_RES_ERROR");
			}

			// 打印服务器返回的状态
			logger.debug("code    : " + statusCode);
			reciveStr = postMethod.getResponseBodyAsString();

			logger.info("receive :" + reciveStr);

			return reciveStr;
		} catch (RuntimeException e) {
			logger.error("receive:error", e);
			throw e;
		} finally {
			// 释放连接
			if (null != postMethod) {
				postMethod.releaseConnection();
			}
		}
	}

	/**
	 * HttpClient Post 请求
	 * @param url   url
	 * @param body  Json数据
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String url, String body) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-type","application/json; charset=utf-8");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setEntity(new StringEntity(body, Charset.forName("UTF-8")));
		String responseContent = null;
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
			logger.debug(String.valueOf(response.getStatusLine().getStatusCode()));
			logger.debug(responseContent);
			response.close();
		} catch (RuntimeException e) {
			logger.error("receive:error", e);
			throw e;
		} finally {
			// 释放连接
			httpClient.close();
		}
		return responseContent;
	}
}
