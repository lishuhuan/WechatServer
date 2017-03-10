package com.demo.model;

import java.util.Date;

public class Relation {
	
	private int id;
	
	private String openId;
	
	private String deviceId;
	
	private int host;
	
	private Date createTime;
	
	private int rwFlag;
	
	private String deviceNickName;
	
	public String getDeviceNickName() {
		return deviceNickName;
	}
	
	public void setDeviceNickName(String deviceNickName) {
		this.deviceNickName = deviceNickName;
	}
	
	public void setRwFlag(int rwFlag) {
		this.rwFlag = rwFlag;
	}
	
	public int getRwFlag() {
		return rwFlag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getHost() {
		return host;
	}

	public void setHost(int host) {
		this.host = host;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	

}
