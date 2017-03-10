package com.demo.service;

import java.util.List;

import com.demo.model.LedProperty;

public interface LedPropertyService {

	
	public Boolean setSleepAndGetupLed(String deviceId,String model,String duration,String time, String openId, String type, String repeat);
	
	public List<LedProperty> getLedProperty(String deviceId);

	public void deleteLedProperty(String propertyId);

	public boolean setSunPowerPlan(String deviceId, String isOpen);

	public boolean updatePowerAndCt(String deviceId, String type, String power);
	
	public boolean updatePower(String deviceId, String type, String power);

	public boolean editSleepAndGetupLed(String planId, String model, String duration, String time, String type,
			String repeat);
}
