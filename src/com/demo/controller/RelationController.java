package com.demo.controller;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.accessToken.ResponseCode;
import com.demo.model.ApplyControl;
import com.demo.model.Device;
import com.demo.service.RelationService;
import com.demo.util.JsonUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/relation")
public class RelationController {

	@Autowired
	private RelationService relationService;

	@RequestMapping(value = "/unbind")
	@ResponseBody
	public JSONObject unbind(@RequestBody String json, HttpServletRequest request) throws ParseException {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		if ("10000".equals(deviceId)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("errcode", 0);
			jsonObject.put("errmsg", "ok");
			JSONObject re = new JSONObject();
			re.put("base_resp", jsonObject);
			return re;
		}
		String userid = (String) JsonUtil.jsonTranslate(json, "openId");
		JSONObject jsonObject = relationService.unbind(deviceId, userid);
		return jsonObject;
	}

	@RequestMapping(value = "/applyAuth")
	@ResponseBody
	public Boolean applyAuth(@RequestBody String json, HttpServletRequest request) {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		if ("10000".equals(deviceId)) {
			return true;
		}

		String userid = (String) JsonUtil.jsonTranslate(json, "openId");
		boolean state = relationService.applyAuth(deviceId, userid);
		return state;
	}

	@RequestMapping(value = "/getApplyControlByDevice")
	@ResponseBody
	public List<ApplyControl> getApplyControlByDevice(@RequestBody String json, HttpServletRequest request) {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		List<ApplyControl> list = relationService.getApplyControlByDevice(deviceId);
		return list;
	}

	@RequestMapping(value = "/getApplyControlByUser")
	@ResponseBody
	public List<ApplyControl> getApplyControlByUser(@RequestBody String json, HttpServletRequest request) {
		String openId = (String) JsonUtil.jsonTranslate(json, "openId");
		List<ApplyControl> list = relationService.getApplyControlByDevice(openId);
		return list;
	}

	@RequestMapping(value = "/handleDeviceApply")
	@ResponseBody
	public boolean handleDeviceApply(@RequestBody String json, HttpServletRequest request) {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String openid = (String) JsonUtil.jsonTranslate(json, "openId");
		String result = (String) JsonUtil.jsonTranslate(json, "result");
		boolean state = relationService.handleDeviceApply(deviceId, openid, result);
		return state;
	}

	@RequestMapping(value = "/indexUserAuth")
	@ResponseBody
	public int indexUserAuth(@RequestBody String json, HttpServletRequest request) {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String openid = (String) JsonUtil.jsonTranslate(json, "openId");
		int code = relationService.indexUserAuth(deviceId, openid);
		return code;
	}

	@RequestMapping(value = "/getDeviceListByFriends")
	@ResponseBody
	public List<Device> getDeviceListByFriends(@RequestBody String json, HttpServletRequest request) {
		String openid = (String) JsonUtil.jsonTranslate(json, "openId");
		List<Device> list = relationService.getDeviceListByFriends(openid);
		return list;

	}

	@RequestMapping(value = "/setDeviceNickName")
	@ResponseBody
	public boolean setDeviceNickName(@RequestBody String json, HttpServletRequest request) {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		if ("10000".equals(deviceId)) {
			return true;
		}
		String openid = (String) JsonUtil.jsonTranslate(json, "openId");
		String nickName = (String) JsonUtil.jsonTranslate(json, "nickName");
		boolean state = relationService.updateDeviceNickName(deviceId, openid, nickName);
		return state;
	}
}
