package com.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.demo.model.Device;

public interface DeviceDao {
	
	Device getDeviceById(@Param("deviceId") String deviceId);

	void insertDevice(Device device);
	
	List<Device> getDeviceByUser(@Param("openId") String openId);
	
	//User indexUser(@Param("openId") String openId);
	
	void updateDeviceName(@Param("deviceId") String deviceId,@Param("name") String name);
	
	List<Device> getDeviceListByFriends(@Param("openId") String openId);

	void updateDeviceSunModel(@Param("deviceId") String deviceId, @Param("sunModel") String sunModel);

	void updatePowerAndCt(@Param("deviceId") String deviceId, @Param("type") String type, @Param("power") String power);
}
