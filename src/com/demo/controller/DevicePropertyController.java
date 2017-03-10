package com.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.model.DeviceProperty;
import com.demo.service.DevicePropertyService;
import com.demo.util.JsonUtil;

@Controller
@RequestMapping(value = "/property")
public class DevicePropertyController {
	
	@Autowired
	private DevicePropertyService devicePropertyService;
	
	@RequestMapping(value = "/setDeviceProperty")
	public @ResponseBody Boolean setDeviceProperty(@RequestBody String json){
		String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
		String openId=(String) JsonUtil.jsonTranslate(json,"openId");
		String name=(String) JsonUtil.jsonTranslate(json,"name");
		String time=(String) JsonUtil.jsonTranslate(json,"time");
		String repetition=(String) JsonUtil.jsonTranslate(json,"repetition");
		String waterTime=(String) JsonUtil.jsonTranslate(json,"waterTime");
		boolean state=devicePropertyService.insertDeviceProperty(openId, deviceId, name, time,repetition,waterTime);
		return state;
	}
	
	@RequestMapping(value = "/getDeviceProperty")
	public @ResponseBody List<DeviceProperty> getDeviceProperty(@RequestBody String json){
		String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
		List<DeviceProperty> list=devicePropertyService.getDeviceProperty(deviceId);
		return list;
	}
	
	@RequestMapping(value = "/editDeviceProperty")
	public @ResponseBody Boolean editDeviceProperty(@RequestBody String json){
		String id=(String) JsonUtil.jsonTranslate(json,"id");
		String time=(String) JsonUtil.jsonTranslate(json,"time");
		String repetition=(String) JsonUtil.jsonTranslate(json,"repetition");
		boolean state=devicePropertyService.editDeviceProperty(id, time,repetition);
		return state;
	}
	
	@RequestMapping(value = "/deleteDeviceProperty")
	public @ResponseBody Boolean deleteDeviceProperty(@RequestBody String json){
		String id=(String) JsonUtil.jsonTranslate(json,"id");
		boolean state=devicePropertyService.deleteDeviceProperty(id);
		return state;
	}

}
