package com.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.accessToken.GetAccessToken;
import com.demo.dao.DeviceDao;
import com.demo.dao.DevicePropertyDao;
import com.demo.dao.DeviceStatusDao;
import com.demo.dao.DeviceVersionDao;
import com.demo.dao.LedPropertyDao;
import com.demo.dao.RelationDao;
import com.demo.device.FutureMap;
import com.demo.device.GetOrderByDevice;
import com.demo.device.SendOrderToDevice;
import com.demo.device.SyncFuture;
import com.demo.model.Device;
import com.demo.model.DeviceProperty;
import com.demo.model.DeviceStatus;
import com.demo.model.LedProperty;
import com.demo.model.Relation;
import com.demo.onenet.PostData;
import com.demo.redis.RedisAPI;
import com.demo.redis.RedisOperation;
import com.demo.service.OneNetService;
import com.demo.util.DataProtocol;
import com.demo.util.HttpClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("OneNetServiceImpl")
public class OneNetServiceImpl implements OneNetService {


	@Autowired
	private DeviceVersionDao deviceVersionDao;

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private RelationDao relationDao;

	@Autowired
	private DeviceStatusDao deviceStatusDao;

	@Autowired
	private DevicePropertyDao devicePropertyDao;

	@Autowired
	private LedPropertyDao ledPropertyDao;
	
	@Override
	public void processJson(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		if(jsonObject.containsKey("msg")){
			JSONObject msg=jsonObject.getJSONObject("msg");
			if(msg.containsKey("value")){
				String value=msg.getString("value");
				String deviceId=msg.getString("dev_id");
				if(value.startsWith("01") || value.startsWith("02")){
					processCommand(value,deviceId);
				}
				if (value.startsWith("03")) {
					GetOrderByDevice.getDeviceTempAndHumidity(value, deviceId);
					String response = "030100";
					PostData.Post(deviceId, response);
				}
				if (value.startsWith("04")) {
					Device device = deviceDao.getDeviceById(deviceId);
					String openId=relationDao.getOpenIdByDeviceHost(deviceId);
					GetOrderByDevice.deviceAlarm(openId, device.getName());
					String response = "040100";
					PostData.Post(deviceId, response);
				}
				if (value.startsWith("90")) {
					List<DeviceProperty> deviceProperties = devicePropertyDao.getDeviceProperty(deviceId);
					List<LedProperty> ledProperties = ledPropertyDao.getLedProperty(deviceId);
					Device device = deviceDao.getDeviceById(deviceId);
					GetOrderByDevice.statusSync(deviceProperties, ledProperties, device,deviceId);
				}
				if (value.startsWith("88")) {
					boolean deviceLogin=deviceLoginProcess(value,deviceId);
					
					String response = "880100";
					if (!deviceLogin) {
						response = "880101";
						// SendOrderToDevice.sendUpgradeToDevice(device_id,openId);
					}
					PostData.Post(deviceId, response);
				}
				if (value.startsWith("92")) {
					String version = deviceVersionDao.getNewestVersion();
					SendOrderToDevice.sendUpgradeToDevice(deviceId, version);
				}
				if (value.startsWith("05")) {
					String power = value.substring(2);
					int p = Integer.parseInt(power, 16);
					String operation = "led_pwm-" + String.valueOf(p);
					insertDeviceStatus(deviceId, "local", operation);
					List<Relation> list = relationDao.getRelationByDevice(deviceId);
					String openid = relationDao.getOpenIdByDeviceHost(deviceId);
					RedisOperation.setControlRecordToRedisHostAndFriend(deviceId, "led", list, openid);
					RedisAPI.rpush("ledenergy" + deviceId, p + "#" + String.valueOf(new Date().getTime()));
					String response = "050100";
					PostData.Post(deviceId, response);
				}
				if (value.startsWith("06")) {
					String rule = value.substring(2, 4);
					if (rule.equals("01")) {
						String time = value.substring(4);
						int p = Integer.parseInt(time, 16);
						String operation = "watering-" + String.valueOf(p);
						insertDeviceStatus(deviceId, "local", operation);
						List<Relation> list = relationDao.getRelationByDevice(deviceId);
						String openid = relationDao.getOpenIdByDeviceHost(deviceId);
						RedisOperation.setControlRecordToRedisHostAndFriend(deviceId, "water", list, openid);
					}
					String response = "060100";
					PostData.Post(deviceId, response);
				}

				if (value.startsWith("91")) {
					String response = timeCalibration();
					PostData.Post(deviceId, response);
				}
				if (value.startsWith("07")) {
					String tag = value.substring(4);
					if (tag.equals("00")) {
						Relation relation = relationDao.getRelationByDeviceHost(deviceId);
						DataProtocol.sendTextToWechat(relation.getOpenId(), "您的设备" + relation.getDeviceNickName() + "光照计划已经关闭",
								"ledClose");
					}
					String response = "070100";
					PostData.Post(deviceId, response);
				}
				
				
			}
		}
	}
	
	private boolean deviceLoginProcess(String value,String deviceId) {
		// TODO Auto-generated method stub
		Device device=deviceDao.getDeviceById(deviceId);
		if(device==null){
			int deviceIdLen=Integer.valueOf(value.substring(12, 14), 16)*2;
			int apiLen=Integer.valueOf(value.substring(16+deviceIdLen, 18+deviceIdLen), 16)*2;
			String sn=value.substring(22+deviceIdLen+apiLen);
			wechatRegister(deviceId,hexString2String(sn));
		}
		
		String version = deviceVersionDao.getNewestVersion();
		int high = Integer.valueOf(value.substring(6, 8), 16);
		int low = Integer.valueOf(value.substring(8,10), 16);
		String deviceVersion = String.valueOf(high) + "." + String.valueOf(low);
		if(version.equals(deviceVersion)){
			return true;
		}
		else{
			return false;
		}
	}
	
	private static String hexString2String(String src) {  
        String temp = "";  
        for (int i = 0; i < src.length() / 2; i++) {  
            temp = temp  
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),  
                            16).byteValue();  
        }  
        return temp;  
    }  


	private void wechatRegister(String deviceId, String sn) {
		// TODO Auto-generated method stub
		String token=GetAccessToken.getAccessToken();
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("device_num", "1");
		jsonObject.put("op_type", "0");
		jsonObject.put("product_id", "22040");
		JSONArray jsonArray=new JSONArray();
		JSONObject object=new JSONObject();
		object.put("id", deviceId);
		object.put("mac", sn);
		object.put("auth_key", "");
		object.put("connect_protocol", "4");
		object.put("close_strategy", "1");
		object.put("conn_strategy", "1");
		object.put("crypt_method", "0");
		object.put("auth_ver", "0");
		object.put("manu_mac_pos", "-1");
		object.put("ser_mac_pos", "-2");
		object.put("ble_simple_protocol", "0");
		jsonArray.add(object);
		jsonObject.put("device_list", jsonArray);
		
		HttpClient.doPost("https://api.weixin.qq.com/device/authorize_device?access_token="+token, jsonObject);
		
		
	}

	private String timeCalibration() {
		Calendar a = Calendar.getInstance();
		long timestamp = new Date().getTime() / 1000;
		int year = Integer.parseInt(String.valueOf(a.get(Calendar.YEAR)).substring(2));
		int month = a.get(Calendar.MONTH) + 1;
		int day = a.get(Calendar.DATE);
		int week = a.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 0) {
			week = 7;
		}
		int hour = a.get(Calendar.HOUR_OF_DAY);
		int min = a.get(Calendar.MINUTE);
		int second = a.get(Calendar.SECOND);
		String weekday = "0" + week;
		String response = "910B" + Long.toHexString(timestamp)
				+ String.format("%2s", Integer.toHexString(year)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(month)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(day)).replace(' ', '0') + weekday
				+ String.format("%2s", Integer.toHexString(hour)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(min)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(second)).replace(' ', '0');
		return response;
	}
	
	public Boolean insertDeviceStatus(String deviceId, String userId, String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DeviceStatus deviceStatus = new DeviceStatus();
		deviceStatus.setDeviceId(deviceId);
		deviceStatus.setActionByUser(userId);
		deviceStatus.setAction(data);
		deviceStatus.setTime(sdf.format(new Date()));

		deviceStatusDao.setDeviceStatus(deviceStatus);
		return true;

	}

	private void processCommand(String value, String deviceId) {
		// TODO Auto-generated method stub
		String tag=value.substring(4);
		SyncFuture<Object> future=FutureMap.getFutureMap(deviceId+"_command");
		if(future!=null){
			future.setResponse(tag);
		}	
	}
}
