package com.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.DeviceStatusDao;
import com.demo.dao.RelationDao;
import com.demo.model.DeviceStatus;
import com.demo.model.Relation;
import com.demo.service.DeviceStatusService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service("DeviceStatusService")
public class DeviceStatusServiceImpl implements DeviceStatusService {
	
	@Autowired
	private DeviceStatusDao deviceStatusDao;
	
	@Autowired
	private RelationDao relationDao;
	
	@Override
	public Page<DeviceStatus> getDeviceStatus(String deviceId,String type,String openId,String startPage){
		PageHelper.startPage(Integer.parseInt(startPage), 15);
		Page<DeviceStatus> page=(Page<DeviceStatus>) deviceStatusDao.getDeviceStatus(deviceId,type,openId);
		
		return page;
	}
	
	@Override
	public void setDeviceStatus(String deviceId,String userid,String data){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DeviceStatus deviceStatus = new DeviceStatus();
		deviceStatus.setDeviceId(deviceId);
		deviceStatus.setActionByUser(userid);
		deviceStatus.setAction(data);
		deviceStatus.setTime(sdf.format(new Date()));
		deviceStatusDao.setDeviceStatus(deviceStatus); 
	}

	@Override
	public List<DeviceStatus> getRecentOperation(String deviceId) {
		DeviceStatus water=deviceStatusDao.getRecentOperation(deviceId, "water");
		DeviceStatus led=deviceStatusDao.getRecentOperation(deviceId, "led");
		List<DeviceStatus> list=new ArrayList<>();
		list.add(water);
		list.add(led);
		return list;
	}

	@Override
	public List<Relation> waterLog(String deviceId, String userId, String data) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DeviceStatus deviceStatus = new DeviceStatus();
		deviceStatus.setDeviceId(deviceId);
		deviceStatus.setActionByUser(userId);
		deviceStatus.setAction(data);
		deviceStatus.setTime(sdf.format(new Date()));
		deviceStatusDao.setDeviceStatus(deviceStatus);
		List<Relation> list=relationDao.getRelationByDevice(deviceId);
		return list;
		
	}

}
