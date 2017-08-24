package com.demo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

@SuppressWarnings("deprecation")
public class HttpClient {

	public static String httpGet(String url) {
		HttpMethod method = new GetMethod(url);
		org.apache.commons.httpclient.HttpClient httpclient = new org.apache.commons.httpclient.HttpClient();

		try {
			httpclient.executeMethod(method);
			String result = new String(method.getResponseBody(), "utf-8");
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static String httpPost(String url, Map<String, String> params) {
		try {
			HttpPost httpPost = new HttpPost(url);
			@SuppressWarnings({ "resource" })
			DefaultHttpClient client = new DefaultHttpClient();
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
				valuePairs.add(nameValuePair);
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, "GBK");
			httpPost.setEntity(formEntity);
			HttpResponse resp = client.execute(httpPost);

			HttpEntity entity = resp.getEntity();
			String respContent = EntityUtils.toString(entity, "GBK").trim();
			httpPost.abort();
			client.getConnectionManager().shutdown();

			return respContent;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject doPost(String url, JSONObject json) {
		@SuppressWarnings("resource")
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		try {
			StringEntity s = new StringEntity(json.toString(), "UTF-8");
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");// ����json������Ҫ����contentType
			post.setEntity(s);
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(entity);// ����json��ʽ��
				response = JSONObject.fromObject(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return response;
	}

	public static String onenetPost(String url, String params, String header, String value) {
		// （1）构造HttpClient的实例
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
		// （2）创建POST方法的实例
		PostMethod postMethod = new PostMethod(url);
		String result = null;
		// （3）设置http request头
		postMethod.addRequestHeader(header, value);
		// 使用系统提供的默认的恢复策略
		postMethod.setRequestBody(params);
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed:" + postMethod.getStatusLine());
			}
			byte[] responseBody = postMethod.getResponseBody();
			result = new String(responseBody);
			// （7） 处理内容
		} catch (HttpException e)

		{
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 释放连接
			postMethod.releaseConnection();
		}
		return result;
	}

	public static String onenetGet(String url, String header, String value) {
		// （1）构造HttpClient的实例
		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
		// （2）创建POST方法的实例
		GetMethod getMethod = new GetMethod(url);
		String result = null;
		// （3）设置http request头
		getMethod.addRequestHeader(header, value);
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed:" + getMethod.getStatusLine());
			}
			byte[] responseBody = getMethod.getResponseBody();
			result = new String(responseBody);
			// （7） 处理内容
		} catch (HttpException e)

		{
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return result;
	}

	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("cmd", "");
		String result = onenetPost("http://api.heclouds.com/cmds?device_id=4093565", jsonObject.toString(), "api-key",
				"86Mh=64iCtTJdXRx=sUdT=G75qw=");
		System.out.println(result);
	}

}
