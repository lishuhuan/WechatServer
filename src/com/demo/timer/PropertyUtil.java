package com.demo.timer;

import com.demo.model.DeviceProperty;

public class PropertyUtil {
	
	private DeviceProperty property;
	
	private int type;   //0 notyfi  1 deadline

	public DeviceProperty getProperty() {
		return property;
	}

	public void setProperty(DeviceProperty property) {
		this.property = property;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}
