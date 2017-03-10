package com.demo.service;

import com.demo.model.ResponseData;

import net.sf.json.JSONObject;

public interface MessageAnalysisService {
	
	ResponseData getData(JSONObject jsonObject);
	
	void deviceBind(JSONObject jsonObject);
	
	void getMessageByNotify(JSONObject jsonObject);

}
