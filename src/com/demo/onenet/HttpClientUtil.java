package com.demo.onenet;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;

public class HttpClientUtil {
	
	private final static HttpConnectionManager httpConnectionManager;  
	  
    static {  
        httpConnectionManager = new MultiThreadedHttpConnectionManager();  
        HttpConnectionManagerParams params = httpConnectionManager.getParams();  
        params.setConnectionTimeout(10000);  
        params.setSoTimeout(10000);  
        params.setDefaultMaxConnectionsPerHost(20);  
        params.setMaxTotalConnections(500);  
    }  

	public static String onenetPost(String url, String params, String header, String value) {
		
		HttpClient httpClient=new HttpClient(httpConnectionManager);
		// （2）创建POST方法的实例
		PostMethod postMethod = new PostMethod(url);
		String result = null;
		// （3）设置http request头
		postMethod.addRequestHeader(header, value);
		// 使用系统提供的默认的恢复策
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
		HttpClient httpClient=new HttpClient(httpConnectionManager);
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
}
