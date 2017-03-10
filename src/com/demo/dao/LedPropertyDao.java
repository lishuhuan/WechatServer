package com.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.demo.model.LedProperty;

public interface LedPropertyDao {

	void setSleepAndGetupLed(LedProperty property);
	
	List<LedProperty> getLedProperty(@Param("deviceId") String deviceId);

	void deleteLedProperty(@Param("id") String id);

	LedProperty getPropertyById(@Param("id") String id);

	void updateLedProperty(LedProperty property);

	List<Integer> getTagByOrder(@Param("deviceId") String deviceId);
}
