package com.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.demo.model.DeviceStatus;

public interface DeviceStatusDao {
	
	public List<DeviceStatus> getDeviceStatus(@Param("deviceId") String deviceId,@Param("type") String type,@Param("openId") String openId);
	
	public void setDeviceStatus(DeviceStatus deviceStatus);
	
	public DeviceStatus getRecentOperation(@Param("deviceId") String deviceId,@Param("action") String action);

}
