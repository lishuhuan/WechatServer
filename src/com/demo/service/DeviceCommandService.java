package com.demo.service;

import com.demo.model.Device;

import net.sf.json.JSONObject;

public interface DeviceCommandService {
	
	JSONObject getDeviceQuery(String token,String deviceId,String userid);
	
	JSONObject setDevice(String token,String deviceId,String userid,String data,String jsonInfo,String tag);
	
	Boolean updateDeviceName(String deviceId,String name);
	
	Device getDeviceById(String deviceId);

}
