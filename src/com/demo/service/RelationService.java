package com.demo.service;

import java.util.List;

import com.demo.model.ApplyControl;
import com.demo.model.Device;
import com.demo.model.Relation;

import net.sf.json.JSONObject;

public interface RelationService {

	JSONObject unbind(String deviceId, String userid);

	Boolean applyAuth(String deviceId, String userid);
	
	List<ApplyControl> getApplyControlByDevice(String deviceId);
	
	List<ApplyControl> getApplyControlByUser(String openId);
	
	Boolean handleDeviceApply(String deviceId, String userid, String result);
	
	Boolean updateDeviceNickName(String deviceId,String openid,String nickName);
	
	int indexUserAuth(String deviceId, String userid);
	
	List<Device> getDeviceListByFriends(String openid);
	
	List<Relation> getRelationByDevice(String deviceId);
	
	String getRelationByHost(String deviceId);

}
