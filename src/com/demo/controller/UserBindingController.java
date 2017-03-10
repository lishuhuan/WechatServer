package com.demo.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.demo.accessToken.GetAccessToken;
import com.demo.accessToken.ResponseCode;
import com.demo.accessToken.Sign;
import com.demo.dao.DeviceDao;
import com.demo.model.Device;
import com.demo.model.Relation;
import com.demo.model.User;
import com.demo.redis.RedisAPI;
import com.demo.service.RelationService;
import com.demo.service.UserBindingService;
import com.demo.status.DeviceListStatus;
import com.demo.util.Ehcache;
import com.demo.util.HttpClient;
import com.demo.util.JsonUtil;
import com.thoughtworks.xstream.core.util.Base64Encoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/user")
public class UserBindingController {

	private static Logger logger=Logger.getLogger(UserBindingController.class);
	@Autowired
	private UserBindingService userBindingService;
	
	@Autowired
	private RelationService relationService;
	
	
	@Autowired
	private DeviceDao deviceDao;

	@RequestMapping(value = "/test")
	public ModelAndView test(HttpServletRequest request, HttpServletResponse response) {
		//String aString = request.getParameter("test");
		List<User> list = userBindingService.bindingService();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/main");
		mav.addObject("list", list);
		return mav;
	}


	@RequestMapping(value = "/getFriendList")
	public @ResponseBody List<User> getFriendList(@RequestBody String json)
			throws ParseException {
		String openId = JSONObject.fromObject(json).getString("openId");
		String deviceId = JSONObject.fromObject(json).getString("deviceId");
		List<User> list = userBindingService.getFriendList(openId,deviceId);
		return list;
	}

	@RequestMapping(value = "/addMemberPoint")
	public @ResponseBody String addMemberPoint(@RequestBody String json) {
		String openId = JSONObject.fromObject(json).getString("openId");
		boolean state = userBindingService.addUserPoint(openId);
		if (state) {
			return "success";
		} else {
			return "error";
		}
	}

	@RequestMapping(value = "/getDeviceByUser")
	public @ResponseBody List<Device> getDeviceByUser(@RequestBody String json)
			throws ParseException {
		String openId = JSONObject.fromObject(json).getString("openId");
		List<Device> list = userBindingService.getDeviceByUser(openId);
		List<String> deviceList=new ArrayList<>();
		if(list.size()>0){
			for(Device device:list){
				deviceList.add(device.getDeviceId());
			}
			DeviceListStatus deviceListStatus=new DeviceListStatus(deviceList, openId);
			deviceListStatus.start();
		}
		else {
			Device device=deviceDao.getDeviceById("10000");
			list.add(device);
		}
		return list;
	}
	
	@RequestMapping(value = "/getDeviceListStatus")
	public @ResponseBody JSONArray getDeviceListStatus(@RequestBody String json) throws InterruptedException{
		String openId = JSONObject.fromObject(json).getString("openId");
		Ehcache ehcache=new Ehcache();
		JSONArray jsonArray=new JSONArray();
		int i=0;
		while(true){
			jsonArray= (JSONArray) ehcache.getCacheElement(openId);
			if(jsonArray!=null || i==3){
				break;
			}
			i++;
			Thread.sleep(1000);
		}
		//ehcache.deleteCache(openId);
		return jsonArray;
	}

	@RequestMapping(value = "/qrcodeForShare")
	public @ResponseBody JSONObject qrcodeForShare(@RequestBody String json)
			throws ParseException, IOException {
		String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
		String timestamp=(String) JsonUtil.jsonTranslate(json,"timestamp");
		JSONObject responseCode=new JSONObject();
		long stamp=Long.parseLong(timestamp);
		long current=new Date().getTime();
		long cha=(current-stamp)/1000/60;
		if(cha>10){
			responseCode.put("result_code", 1);
			responseCode.put("data", "time out");
		}
		String qrcode=RedisAPI.stringGet(timestamp);
		if(qrcode!=null){
			responseCode.put("result_code", 2);
			responseCode.put("data", "it has been used");
		}
		String token = GetAccessToken.getAccessToken();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_num", "1");
		jsonObject.put("device_id_list", deviceId);
		JSONObject result = HttpClient.doPost("https://api.weixin.qq.com/device/create_qrcode?access_token=" + token,
				jsonObject);
		if (result.get("errmsg").equals("ok")) {
			JSONArray array = result.getJSONArray("code_list");
			// String[] code_list=(String[]) result.get("code_list");
			if (array.size() > 0) {
				JSONObject code = JSONObject.fromObject(array.get(0));
				String ticket = code.getString("ticket").replace("\\", "");
				String base64=new Base64Encoder().encode(String.valueOf(stamp).getBytes());
				ticket=ticket+"#"+base64;
				responseCode.put("result_code", 0);
				responseCode.put("data", ticket);

			}
		}
		return responseCode;
	}
	
	@RequestMapping(value = "/bindDevice")
	public @ResponseBody JSONObject bindDevice(@RequestBody String json)
			throws ParseException, IOException {
		String deviceId=(String) JsonUtil.jsonTranslate(json,"deviceId");
		String openId=(String) JsonUtil.jsonTranslate(json,"openId");
		String timestamp=(String) JsonUtil.jsonTranslate(json,"timestamp");
		long stamp=Long.parseLong(timestamp);
		long current=new Date().getTime();
		long cha=(current-stamp)/1000/60;
		if(cha>10){
			return ResponseCode.response(1, "time out");
		}
		String qrcode=RedisAPI.stringGet(timestamp);
		if(qrcode!=null){
			return ResponseCode.response(2, "it has been used");
		}
		String token = GetAccessToken.getAccessToken();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_id", deviceId);
		jsonObject.put("openid", openId);
		JSONObject result = HttpClient.doPost("https://api.weixin.qq.com/device/compel_bind?access_token=" + token,
				jsonObject);
		JSONObject base_resp=result.getJSONObject("base_resp");
		if(base_resp.getString("errmsg").equals("ok")){
			return ResponseCode.response(0, "");
		}
		else{
			return ResponseCode.response(3, base_resp.getString("errmsg"));
		}
		
	}
	
	@RequestMapping(value = "/getOpenIdByCode")
	@ResponseBody
	public JSONObject getOpenIdByCode(@RequestBody String json) throws UnsupportedEncodingException{
		//System.out.println(json);
		JSONObject st=JSONObject.fromObject(json).getJSONObject("code");
		String code=st.getString("code");
		//String code=requestJson.getString("code");
		String result=HttpClient.httpGet("https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx2c97180bd57e660f&secret=aaf9082cef39b7ce507d67c1f4a3f065&code="+code+"&grant_type=authorization_code");
		logger.info(result);
		JSONObject jsonObject=JSONObject.fromObject(result);
		
		String token=jsonObject.getString("access_token");
		logger.info(token);
		String openId=jsonObject.getString("openid");
		if(token!=null && openId!=null){
			String info=HttpClient.httpGet("https://api.weixin.qq.com/sns/userinfo?access_token="+token+"&openid="+openId+"&lang=zh_CN");
			logger.info(info);
			System.out.println(info);
			JSONObject peopleInfo=JSONObject.fromObject(info);
			userBindingService.updateUser(peopleInfo);
			
		}
		return jsonObject;
	}
	
	@RequestMapping(value = "/indexUser")
	@ResponseBody
	public User indexUser(@RequestBody String json){
		String openId = (String) JsonUtil.jsonTranslate(json, "openId");
		User user=userBindingService.getUserById(openId);
		return user;
	}
	
	@RequestMapping(value = "/getJsapiTicket")
	@ResponseBody
	public JSONObject getJsapiTicket(@RequestBody String params){
		String url = JSONObject.fromObject(params).getString("url");
		JSONObject result=new JSONObject();
		String token=GetAccessToken.getAccessToken();
		String jsapi_ticket=HttpClient.httpGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+token+"&type=jsapi");
		JSONObject jsonObject=JSONObject.fromObject(jsapi_ticket);
		if(jsonObject.getString("errmsg").equals("ok")){
			String ticket=jsonObject.getString("ticket");
			Map<String, String> map=Sign.getSign(ticket, url);
			result.put("result_code", 0);
			result.put("data", map);
		}
		else{
			result.put("result_code", 1);
			result.put("data", "");
		}
		return result;
	}
	
	@RequestMapping(value = "/test123")
	@ResponseBody
	public void test(@RequestBody String params){
		try {
			Ehcache ehcache=new Ehcache();
			ehcache.addToCache("123", "123");
			System.out.println(Ehcache.getCacheElement("123"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = "/getSunPower")
	@ResponseBody
	public JSONObject getSunPower(@RequestBody String json){
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		List<String> list=RedisAPI.lrange("sun"+deviceId,0,-1);
		return ResponseCode.response(0, list);
		
		/*if(list==null){
			return 0;
		}
		else{
			int power=0;
			for(String a:list){
				power+=Integer.valueOf(a);
			}
			return power;
		}*/
	}
	
	
	@RequestMapping(value = "/collectPower")
	@ResponseBody
	public JSONObject collectPower(@RequestBody String json){
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String openId = (String) JsonUtil.jsonTranslate(json, "openId");
		String host=relationService.getRelationByHost(deviceId);
		List<String> list=RedisAPI.lrange("sun"+deviceId,0,-1);
		if(list!=null){
			if(host.equals(openId)){
				int power=0;
				for(String a:list){
					power+=Integer.valueOf(a);
				}
				userBindingService.addUserPoint(openId, power);
				RedisAPI.del("sun"+deviceId);
				RedisAPI.del("collect"+deviceId);
				return ResponseCode.response(0, power);
			}
			else{
				boolean state=RedisAPI.Sismember("collect"+deviceId, openId);
				if(list.size()>1 && !state){
					int power=Integer.parseInt(RedisAPI.lpop("sun"+deviceId));
					userBindingService.addUserPoint(openId, power);
					long len=RedisAPI.llen("sun"+deviceId);
					if(len>0){
						RedisAPI.sadd("collect"+deviceId, openId);
					}
					else{
						RedisAPI.del("collect"+deviceId);
					}
					return ResponseCode.response(0, power);
				}
				else if(state){
					return ResponseCode.response(1, "you have collected");
				}
				else{
					return ResponseCode.response(2, "power lack");
				}
			}
		}
		else{
			 return ResponseCode.response(2, "power lack");
		}
	}
	
	

}
