package com.demo.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.dao.RelationDao;
import com.demo.model.DeviceStatus;
import com.demo.redis.RedisAPI;
import com.demo.service.DeviceStatusService;
import com.demo.util.JsonUtil;
import com.github.pagehelper.Page;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@Controller
@RequestMapping(value = "/status")
public class DeviceStatusController {
	
	@Autowired
	private DeviceStatusService deviceStatusService;
	
	@Autowired
	private RelationDao relationDao;
	
	
	@RequestMapping(value = "/getDeviceStatus")
	public @ResponseBody JSONObject getDeviceStatus(@RequestBody String json)
			throws ParseException {
		String deviceId = JSONObject.fromObject(json).getString("deviceId");
		String type = JSONObject.fromObject(json).getString("type");
		String openId = JSONObject.fromObject(json).getString("openId");
		String startPage = JSONObject.fromObject(json).getString("startPage");
		if(startPage==null){
			startPage="1";
		}
		Page<DeviceStatus> page = deviceStatusService.getDeviceStatus(deviceId,type,openId,startPage);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("count", page.getTotal());
		jsonObject.put("status", page.getResult());
		
		RedisAPI.hdel(openId+"unread", deviceId+"myunread");
		//String hoString=relationDao.getOpenIdByDeviceHost(deviceId);
		if("1".equals(type)){
			RedisAPI.hdel(openId+"unread", deviceId+"friendunread");
		}
		return jsonObject;
	}
	
	@RequestMapping(value = "/getRecentOperationRecord")
	public @ResponseBody List<DeviceStatus> getRecentOperationRecord(@RequestBody String json){
		String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
		List<DeviceStatus> list=deviceStatusService.getRecentOperation(deviceId);
		return list;
	}
	
	@RequestMapping(value = "/operationUnread")
	public @ResponseBody JSONObject operationUnread(@RequestBody String json){
		String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
		String openId = JSONObject.fromObject(json).getString("openId");
		String friend=RedisAPI.hget(openId+"unread", deviceId+"friendunread");
		String my=RedisAPI.hget(openId+"unread", deviceId+"myunread");
		
		JSONObject jsonObject=new JSONObject();
		if(friend==null){
			jsonObject.put("friends", 0);
		}
		else{
			jsonObject.put("friends", friend);
		}
		if(my==null){
			jsonObject.put("my", 0);
		}
		else{
			jsonObject.put("my", my);
		}
		return jsonObject;
	}
	

}
