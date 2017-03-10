package com.demo.util;

import net.sf.json.JSONObject;

public class ErrorResponse {
	
	public static JSONObject ResponseResult(int code,String value){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("error_code", code);
		jsonObject.put("error_msg", value);
		return jsonObject;
	}

}
