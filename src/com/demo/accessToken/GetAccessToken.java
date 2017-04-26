package com.demo.accessToken;

import com.demo.util.HttpClient;

import net.sf.json.JSONObject;

public class GetAccessToken {
	
	public static String getAccessToken() {
		String result = HttpClient.httpGet(
				"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2c97180bd57e660f&secret=aaf9082cef39b7ce507d67c1f4a3f065");
		JSONObject jsonObject = JSONObject.fromObject(result);
		String token = jsonObject.getString("access_token");
		if (null != token && !"".equals(token)) {
			/*Ehcache ehcache = new Ehcache();
			try {
				ehcache.addToCache("access_token", token);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			return token;
		} else
			return "error";
	}
	
	public static void main(String[] args) {
		System.out.println(getAccessToken());
	}

}
