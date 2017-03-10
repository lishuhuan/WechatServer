package com.demo.model;

import java.util.Date;

public class LedProperty {
	
	private String id;
	
	private String deviceId;
	
	private int model;
	
	private int type;
	
	private int tag;
	
	private String duration;
	
	private Date time;
	
	private String repeat_date;
	
	private String createdBy;
	
	private String createdAt;
	
	private String updatedBy;
	
	private String updatedAt;
	
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public int getTag() {
		return tag;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setRepeat_date(String repeat_date) {
		this.repeat_date = repeat_date;
	}
	
	public String getRepeat_date() {
		return repeat_date;
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

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}


	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	
}
