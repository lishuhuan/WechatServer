package com.demo.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.DeviceDao;
import com.demo.dao.UserBindingDao;
import com.demo.model.Device;
import com.demo.model.User;
import com.demo.redis.RedisAPI;
import com.demo.service.UserBindingService;

import net.sf.json.JSONObject;

@Service
public class UserBindingServiceImpl implements UserBindingService {
	
	@Autowired
	//@Qualifier("userBindingDao")
	private UserBindingDao userBindingDao;
	
	@Autowired
	private DeviceDao deviceDao;
	
	@Override
	public List<User> bindingService(/*String openId,String deviceType,String deviceId,String createTime,String event*/){
		return userBindingDao.getUserList();
		
	}
	
	@Override
	public List<User> getFriendList(String id,String deviceId){
		return userBindingDao.getFriendList(id,deviceId);
	}
	
	@Override
	public Boolean addUserPoint(String openId){
		User user=userBindingDao.getUserById(openId);
		user.setMemberPoint(5);
		userBindingDao.addPoint(user);
		return true;
	}
	
	@Override
	public void addUserPoint(String openId,int point){
		User user=userBindingDao.getUserById(openId);
		user.setMemberPoint(point);
		userBindingDao.addPoint(user);
	}
	
	@Override
	public User getUserById(String openId){
		return userBindingDao.getUserById(openId);
	}
	
	@Override
	public List<Device> getDeviceByUser(String userId){
		List<Device> list=deviceDao.getDeviceByUser(userId);
		for(Device device:list){
			String deviceId=device.getDeviceId();
			String temp=RedisAPI.lindex(deviceId+"_tem",0);
			String hum=RedisAPI.lindex(deviceId+"_hum",0);
			device.setTemp(temp);
			device.setHumidity(hum);
		}
		return list;
	}

	@Override
	public void updateUser(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		String openId=jsonObject.getString("openid");
		User user=getUserById(openId);
		if(user!=null){
			long defference=new Date().getTime()-user.getCreateTime().getTime();
			int def=(int)(defference/(60*60*1000));
			if(def>24){
				user.setCreateTime(new Date());
				user.setNickName(jsonObject.getString("nickname"));
				user.setHeadimgurl(jsonObject.getString("headimgurl"));
				userBindingDao.updateUser(user);
			}
		}
		
	}
	

}
