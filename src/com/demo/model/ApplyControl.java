package com.demo.model;

import java.util.Date;

public class ApplyControl {
	
	private int id;

	private String deviceId;
	
	private String applyPersonId;
	
	private String result;
	
	private Date time;
	
	private String applyPersonName;
	
	private String deviceName;
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setApplyPersonName(String applyPersonName) {
		this.applyPersonName = applyPersonName;
	}
	
	public String getApplyPersonName() {
		return applyPersonName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getApplyPersonId() {
		return applyPersonId;
	}

	public void setApplyPersonId(String applyPersonId) {
		this.applyPersonId = applyPersonId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	

}
