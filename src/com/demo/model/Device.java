package com.demo.model;

import java.util.Date;

public class Device {
	
	private String id;
	
	private String deviceId;
	
	private String deviceType;
	
	private String name;
	
	private Date createTime;
	
	private String photo;
	
	private String nickName;
	
	private String temp;
	
	private String humidity;
	
	private String isHost;
	
	private String lightIntensity;
	
	private String colorTem;
	
	private int sunModel;
	
	public int getSunModel() {
		return sunModel;
	}
	
	public void setSunModel(int sunModel) {
		this.sunModel = sunModel;
	}
	
	public String getLightIntensity() {
		return lightIntensity;
	}

	public void setLightIntensity(String lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	public String getColorTem() {
		return colorTem;
	}

	public void setColorTem(String colorTem) {
		this.colorTem = colorTem;
	}

	public String getIsHost() {
		return isHost;
	}
	
	public void setIsHost(String isHost) {
		this.isHost = isHost;
	}
	
	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getPhoto() {
		return photo;
	}
	
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
