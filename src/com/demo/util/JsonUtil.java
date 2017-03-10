package com.demo.util;

import net.sf.json.JSONObject;

public class JsonUtil {
	
	public static Object jsonTranslate(String json,String key){
		if(null!=json){
			JSONObject jsonObject=JSONObject.fromObject(json);
			Object value=jsonObject.get(key);
			return value;
		}
		else{
			return null;
		}
	}

}
