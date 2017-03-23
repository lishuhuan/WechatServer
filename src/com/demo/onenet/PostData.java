package com.demo.onenet;

import net.sf.json.JSONObject;

public class PostData {
	
	public static String Post(String deviceId,String data){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("cmd", data);
		String result = HttpClientUtil.onenetPost("http://api.heclouds.com/cmds?device_id="+deviceId, jsonObject.toString(), "api-key",
				"y=DHRFRL0JeYN5oZ0Ohctw56TTk=");
		return result;
	}

}
