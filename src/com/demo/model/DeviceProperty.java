package com.demo.model;

import java.util.Date;

public class DeviceProperty {

	private String id;

	private String deviceId;
	
	private int tag;

	private String propertyName;
	
	private Date timePoint;

	private int actionCount;
	
	private String repetition;

	private String createBy;

	private Date createAt;
	
	public int getTag() {
		return tag;
	}
	
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public void setRepetition(String repetition) {
		this.repetition = repetition;
	}
	
	public String getRepetition() {
		return repetition;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	

	public void setTimePoint(Date timePoint) {
		this.timePoint = timePoint;
	}
	
	public Date getTimePoint() {
		return timePoint;
	}

	public int getActionCount() {
		return actionCount;
	}

	public void setActionCount(int actionCount) {
		this.actionCount = actionCount;
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


	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	

}
