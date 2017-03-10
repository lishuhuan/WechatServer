package com.demo.service;

import java.util.List;

import com.demo.model.DeviceProperty;

public interface DevicePropertyService {
	
	public Boolean insertDeviceProperty(String openId,String deviceId,String name,String time,String repetition,String waterTime);
	
	public Boolean editDeviceProperty(String id,String time,String repetition);

	public List<DeviceProperty> getDeviceProperty(String deviceId);
	
	public List<DeviceProperty> getAllProperties();
	
	public Boolean deleteDeviceProperty(String id);

	public List<DeviceProperty> getPropertyByTime(int week);
}
