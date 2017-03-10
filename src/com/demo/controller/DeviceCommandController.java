package com.demo.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.accessToken.GetAccessToken;
import com.demo.model.Device;
import com.demo.redis.RedisAPI;
import com.demo.service.DeviceCommandService;
import com.demo.service.LedPropertyService;
import com.demo.util.ErrorResponse;
import com.demo.util.JsonUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/device")
public class DeviceCommandController {

	private static final Logger logger = Logger.getLogger(DeviceCommandController.class);

	@Autowired
	private DeviceCommandService deviceCommandService;
	
	@Autowired
	private LedPropertyService ledPropertyService;

	@RequestMapping(value = "/getDeviceQuery")
	public @ResponseBody JSONObject getDeviceQuery(@RequestBody String json, HttpServletRequest request)
			throws ParseException {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String userid = (String) JsonUtil.jsonTranslate(json, "openId");
		if (null == deviceId || null == userid) {
			return ErrorResponse.ResponseResult(1, "Lock params!");
		}
		String token =GetAccessToken.getAccessToken();
		/*if(ehCache.getCacheElement("access_token")==null){
			token=deviceCommandService.getAccessToken(request);
		}
		else{
			token=(String) ehCache.getCacheElement("access_token");
		}*/
		JSONObject jsonObject = deviceCommandService.getDeviceQuery(token, deviceId, userid);
		return jsonObject;

	}

	@RequestMapping(value = "/setDevice")
	@ResponseBody
	public JSONObject setDevice(@RequestBody String json, HttpServletRequest request) throws ParseException {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String userid = (String) JsonUtil.jsonTranslate(json, "openId");
		String data = (String) JsonUtil.jsonTranslate(json, "action");
		String tag=null;
		if (null == deviceId || null == userid || null == data) {
			System.out.println("Lock params!");
			return ErrorResponse.ResponseResult(1, "Lock params!");
		}
		if (data.contains("watering")) {
			//int second = Integer.parseInt(data.substring(data.indexOf("-") + 1));
			//String to = String.format("%4s", Integer.toHexString(second)).replace(' ', '0');
			data = "010301";
			tag="water";
		}
		if (data.contains("led_pwm")) {
			int second = Integer.parseInt(data.substring(data.indexOf("-") + 1));
			String to = String.format("%2s", Integer.toHexString(second)).replace(' ', '0');
			Device device=deviceCommandService.getDeviceById(deviceId);
			int ct=Integer.parseInt(device.getColorTem());
			data = "0203" + to+String.format("%4s", Integer.toHexString(ct)).replace(' ', '0');
			tag="led";
			ledPropertyService.updatePower(deviceId,"0",String.valueOf(second));
			
		}
		//Ehcache ehCache = new Ehcache();
		String token = GetAccessToken.getAccessToken();
		/*if(ehCache.getCacheElement("access_token")==null){
			token=deviceCommandService.getAccessToken(request);
		}
		else{
			token=(String) ehCache.getCacheElement("access_token");
		}*/
		logger.info("data:"+data);
		System.out.println("data"+data);

		JSONObject jsonObject = deviceCommandService.setDevice(token, deviceId, userid, data,(String) JsonUtil.jsonTranslate(json, "action"),tag);
		return jsonObject;
	}
	
	@RequestMapping(value = "/getTempAndHumidity")
	@ResponseBody
	public JSONObject getTempAndHumidity(@RequestBody String json, HttpServletRequest request) throws ParseException {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String temp=RedisAPI.lindex(deviceId+"_tem",0);
		String hum=RedisAPI.lindex(deviceId+"_hum",0);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("temperature", temp);
		jsonObject.put("humidity", hum);
		return jsonObject;
			
	}
	
	@RequestMapping(value = "/updateDeviceName")
	@ResponseBody
	public Boolean updateDeviceName(@RequestBody String json, HttpServletRequest request){
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String name = (String) JsonUtil.jsonTranslate(json, "name");
		boolean state=deviceCommandService.updateDeviceName(deviceId,name);
		return state;
	}
	

}
