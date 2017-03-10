package com.demo.service;

import java.util.List;

import com.demo.model.DeviceStatus;
import com.demo.model.Relation;
import com.github.pagehelper.Page;

public interface DeviceStatusService {
	
	Page<DeviceStatus> getDeviceStatus(String deviceId,String type,String openId,String startPage);
	
	void setDeviceStatus(String deviceId,String userid,String data);
	
	List<DeviceStatus> getRecentOperation(String deviceId);
	
	List<Relation> waterLog(String deviceId, String userId, String data);

}
