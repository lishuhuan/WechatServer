package com.demo.accessToken;

import net.sf.json.JSONObject;

public class ResponseCode {

	
	public static JSONObject response(int code,Object object){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("result_code", code);
		jsonObject.put("data", object);
		return jsonObject;
	}
}
