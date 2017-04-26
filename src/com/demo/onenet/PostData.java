package com.demo.onenet;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class PostData {
	
	private static Logger logger = Logger.getLogger(PostData.class);
	
	public static JSONObject Post(String deviceId,String data){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("cmd", data);
		logger.info(jsonObject);
		String result = HttpClientUtil.onenetPost("http://api.heclouds.com/cmds?device_id="+deviceId, jsonObject.toString(), "api-key",
				"y=DHRFRL0JeYN5oZ0Ohctw56TTk=");
		logger.info(result);
		return JSONObject.fromObject(result);
	}
	
	public static void main(String[] args) {
		JSONObject result=Post("4093576", "880101");
		System.out.println(result);
	}

}
