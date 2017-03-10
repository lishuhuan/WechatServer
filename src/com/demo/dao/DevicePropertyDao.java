package com.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.demo.model.DeviceProperty;

public interface DevicePropertyDao {
	
	void insertDeviceProperty(DeviceProperty deviceProperty);
	
	List<DeviceProperty> getDeviceProperty(@Param("deviceId") String deviceId);
	
	List<DeviceProperty> getDevicePropertyByType(@Param("deviceId") String deviceId,@Param("propertyName") String propertyName);
	
	DeviceProperty getdevicePropertyById(@Param("id") String id);
	
	Boolean updateDeviceProperty(DeviceProperty deviceProperty);
	
	List<DeviceProperty> getAllProperties(@Param("repetition") String repetition);
	
	void deleteDeviceProperty(@Param("id") String id);

	List<DeviceProperty> getPropertyByTime(@Param("week") int week);

	List<Integer> getTagByOrder(@Param("deviceId") String deviceId);

}
