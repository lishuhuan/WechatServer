package com.demo.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.accessToken.ResponseCode;
import com.demo.model.LedProperty;
import com.demo.service.LedPropertyService;
import com.demo.util.JsonUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/led")
public class LedPropertyController {

	private static Logger logger = Logger.getLogger(LedPropertyController.class);

	@Autowired
	private LedPropertyService ledPropertyService;

	@RequestMapping(value = "/sleepAndGetupLed")
	public @ResponseBody JSONObject setSleepAndGetupLed(@RequestBody String json) {
		String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
		String openId=(String) JsonUtil.jsonTranslate(json,"openId");
		String model = (String) JsonUtil.jsonTranslate(json, "model");
		String type = (String) JsonUtil.jsonTranslate(json, "type");
		String duration = (String) JsonUtil.jsonTranslate(json, "duration");
		String time = (String) JsonUtil.jsonTranslate(json, "time");
		String repeat = (String) JsonUtil.jsonTranslate(json, "repeat");
		try {
			boolean state=ledPropertyService.setSleepAndGetupLed(deviceId, model, duration, time,openId,type,repeat);
			return ResponseCode.response(0, state);
		} catch (Exception e) {
			logger.error("sleepAndGetupLed failure:" + e.getMessage());
			return ResponseCode.response(1, "failure");
		}
	}
	
	@RequestMapping(value = "/updateLedPlan")
	public @ResponseBody JSONObject updateLedPlan(@RequestBody String json) {
		String planId=(String) JsonUtil.jsonTranslate(json,"planId");
		String model = (String) JsonUtil.jsonTranslate(json, "model");
		String type = (String) JsonUtil.jsonTranslate(json, "type");
		String duration = (String) JsonUtil.jsonTranslate(json, "duration");
		String time = (String) JsonUtil.jsonTranslate(json, "time");
		String repeat = (String) JsonUtil.jsonTranslate(json, "repeat");
		try {
			boolean state=ledPropertyService.editSleepAndGetupLed(planId, model, duration, time,type,repeat);
			return ResponseCode.response(0, state);
		} catch (Exception e) {
			logger.error("sleepAndGetupLed failure:" + e.getMessage());
			return ResponseCode.response(1, "failure");
		}
	}
	
	
	
	@RequestMapping(value = "/getLedProperty")
	public @ResponseBody JSONObject getLedProperty(@RequestBody String json) {
		try {
			String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
			List<LedProperty> list=ledPropertyService.getLedProperty(deviceId);
			return ResponseCode.response(0, list);
		}
		catch (Exception e) {
			logger.error("getLedProperty failure:" + e.getMessage());
			return ResponseCode.response(1, "failure");
		}
	}
	
	@RequestMapping(value = "/sunPowerPlan")
	public @ResponseBody JSONObject sunPowerPlan(@RequestBody String json) {
		try {
			String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
			String isOpen=(String) JsonUtil.jsonTranslate(json,"isOpen");
			boolean state=ledPropertyService.setSunPowerPlan(deviceId,isOpen);
			if(state){
				return ResponseCode.response(0, state);
			}
			else{
				return ResponseCode.response(1, "failure");
			}
		}
		catch (Exception e) {
			logger.error("getLedProperty failure:" + e.getMessage());
			return ResponseCode.response(1, "failure");
		}
	}
	
	@RequestMapping(value = "/updatePowerAndCt")
	public @ResponseBody JSONObject updatePowerAndCt(@RequestBody String json) {
		try {
			String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
			String type=(String) JsonUtil.jsonTranslate(json,"type");
			String power=(String) JsonUtil.jsonTranslate(json,"power");
			boolean state=ledPropertyService.updatePowerAndCt(deviceId,type,power);
			if(state){
				return ResponseCode.response(0, state);
			}
			else{
				return ResponseCode.response(1, "failure");
			}
		}
		catch (Exception e) {
			logger.error("getLedProperty failure:" + e.getMessage());
			return ResponseCode.response(1, "failure");
		}
	}
	
	@RequestMapping(value = "/deleteLedProperty")
	public @ResponseBody JSONObject deleteLedProperty(@RequestBody String json) {
		try {
			String propertyId=(String) JsonUtil.jsonTranslate(json,"propertyId");
			ledPropertyService.deleteLedProperty(propertyId);
			return ResponseCode.response(0, null);
		}
		catch (Exception e) {
			logger.error("getLedProperty failure:" + e.getMessage());
			return ResponseCode.response(1, "failure");
		}
	}

}
