package com.demo.util;

import org.apache.log4j.Logger;

import com.demo.accessToken.GetAccessToken;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DataProtocol {

	private static Logger logger = Logger.getLogger(DataProtocol.class);
	
	public static void sendDataToDevice(String device_id,String openId,String data,String log){
		JSONObject param = new JSONObject();
		param.put("device_type", "gh_3f4fcd63df5d");
		param.put("device_id", device_id);
		param.put("service", "");
		param.put("user", openId);
		param.put("data", data);
		String token = GetAccessToken.getAccessToken();
		JSONObject result = HttpClient.doPost(
				"https://api.weixin.qq.com/hardware/mydevice/platform/ctrl_device?access_token=" + token, param);
		logger.info(log+":"+result);
	}
	
	public static void sendTextToWechat(String openId,String content,String log){
		JSONObject param=new JSONObject();
		param.put("touser", openId);
		param.put("msgtype", "text");
		JSONObject text=new JSONObject();
		text.put("content", content);
		param.put("text", text);
		String token = GetAccessToken.getAccessToken();
		JSONObject response = HttpClient.doPost(
				"https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token, param);
		logger.info(log+":"+response);
	}
	
	public static void sendNewsToWechat(String openId,JSONObject articles,String token,String log){
		JSONObject send=new JSONObject();
		send.put("touser", openId);
		send.put("msgtype", "news");
		send.put("news", articles);
		JSONObject response = HttpClient.doPost(
				"https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token, send);
		logger.info(log+":"+response);
	}
	
	
}
