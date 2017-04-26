package com.demo.service;

import java.util.List;

import com.demo.model.Device;
import com.demo.model.User;

import net.sf.json.JSONObject;

public interface UserBindingService {
	
	List<User> bindingService(/*String openId,String deviceType,String deviceId,String createTime,String event*/);
	
	List<User> getFriendList(String id,String deviceId);
	
	Boolean addUserPoint(String openId);
	
	void addUserPoint(String openId,int point);
	
	public User getUserById(String openId);
	
	List<Device> getDeviceByUser(String userId);
	
	void updateUser(JSONObject jsonObject);

	Device getDeviceByVirtualId(String string);

}
