package com.demo.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.dao.DeviceDao;
import com.demo.dao.DevicePropertyDao;
import com.demo.dao.DeviceStatusDao;
import com.demo.dao.RelationDao;
import com.demo.device.FutureMap;
import com.demo.device.SyncFuture;
import com.demo.model.Device;
import com.demo.model.DeviceProperty;
import com.demo.model.DeviceStatus;
import com.demo.model.Relation;
import com.demo.model.ResponseData;
import com.demo.redis.RedisAPI;
import com.demo.redis.RedisOperation;
import com.demo.service.DeviceCommandService;
import com.demo.util.HttpClient;

import net.sf.json.JSONObject;

@Service("DeviceCommandService")
public class DeviceCommandServiceImpl implements DeviceCommandService {

	private static Logger logger = Logger.getLogger(DeviceCommandServiceImpl.class);

	@Autowired
	private DeviceStatusDao deviceStatusDao;

	@Autowired
	private DevicePropertyDao devicePropertyDao;

	@Autowired
	private RelationDao relationDao;

	@Autowired
	private DeviceDao deviceDao;

	@Override
	public JSONObject getDeviceQuery(String token, String deviceId, String userid) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_type", "gh_3f4fcd63df5d");
		jsonObject.put("device_id", deviceId);
		/*
		 * Map<String, String> map=new HashMap<>(); map.put("power_switch",
		 * "true");
		 */
		jsonObject.put("service", "");
		jsonObject.put("user", userid);
		String data = new String("swift,watering,sunshine,feed");
		jsonObject.put("data", data);
		JSONObject result = new JSONObject();

		JSONObject response = HttpClient.doPost(
				"https://api.weixin.qq.com/hardware/mydevice/platform/get_device_status?access_token=" + token,
				jsonObject);
		if (response.get("error_code").equals(0)) {
			// 锟接伙拷锟斤拷锟叫讹拷取锟斤拷锟�
			result = getJsonResult(response.getString("msg_id") + "_Get");
		} else {
			result = response;
		}

		return result;

	}

	@Override
	@Transactional
	public JSONObject setDevice(String token, String deviceId, String userid, String data, String jsonInfo,
			String tag) {
/*		String pp=jsonInfo.substring(jsonInfo.indexOf("-")+1);
		addPower(pp,deviceId);*/
		// insertDeviceStatus(deviceId, userid, jsonInfo);
		JSONObject validate = null;
		try {
			validate = validateAuth(userid, deviceId, tag);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!validate.getString("asyErrorCode").equals("0")) {
			return validate;
		}
		
		//增加浇水时间
		String count=validate.getString("data");
		if(count!=null && data.startsWith("01")){
			int co=Integer.parseInt(count);
			data=data+String.format("%4s", Integer.toHexString(co)).replace(' ', '0');
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_type", "gh_3f4fcd63df5d");
		jsonObject.put("device_id", deviceId);
		jsonObject.put("service", "");
		jsonObject.put("user", userid);

		jsonObject.put("data", data);
		JSONObject result = new JSONObject();
		JSONObject response = HttpClient.doPost(
				"https://api.weixin.qq.com/hardware/mydevice/platform/ctrl_device?access_token=" + token, jsonObject);
		System.out.println(response);
		if (response.get("error_code").equals(0)) {
			result = getJsonResult(response.getString("msg_id") + "_Set");
			if (result.get("asyErrorCode").equals("0")) {
				logger.info(data);
				insertDeviceStatus(deviceId, userid, jsonInfo);	
				List<Relation> list=relationDao.getFriendRelationByDevice(deviceId,userid);
				RedisOperation.setControlRecordToRedis(deviceId, tag,list);
				if("led".equals(tag)){
					String power=jsonInfo.substring(jsonInfo.indexOf("-")+1);
					addPower(power,deviceId);
				}
			}
		} else {
			result = response;
		}

		return result;
	}

	private void addPower(String power, String deviceId) {
		// TODO Auto-generated method stub
		RedisAPI.rpush("ledenergy"+deviceId, power+"#"+String.valueOf(new Date().getTime()));
	}

	public JSONObject validateAuth(String openid, String deviceId, String tag) throws ParseException {
		JSONObject jsonObject = new JSONObject();
		int count=10;
		Relation relation = relationDao.getRelationByUserAndDevice(deviceId, openid);
		if (relation.getHost() == 1) {
			jsonObject.put("asyErrorCode", 1);
			jsonObject.put("data", "have no permission!");
			return jsonObject;

		}
		String type=null;
		if(tag.equals("water")){
			type="watering";
		}
		else {
			type="led_pwm";
		}
		List<DeviceProperty> properties = devicePropertyDao.getDevicePropertyByType(deviceId,type);
		if(properties.size()==0){
			jsonObject.put("asyErrorCode", "0");
			jsonObject.put("data", count);
			return jsonObject;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		long time = date.getTime();
		for (DeviceProperty deviceProperty : properties) {
			String repetition = deviceProperty.getRepetition();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int xingqi = cal.get(Calendar.DAY_OF_WEEK)-1;
			if (repetition.contains(String.valueOf(xingqi))) {

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat tt = new SimpleDateFormat("HH:mm:ss");
				String newOper = RedisAPI.lindex(deviceId + tag, 0);

				if (deviceProperty.getTimePoint() != null) {
					String point = format.format(new Date()) + " " + tt.format(deviceProperty.getTimePoint());
					long stand = sdf.parse(point).getTime();
					long cat = Math.abs(stand - time);
					long result = cat / 1000 / 60;
					
					if (result < 60) {
						if (newOper != null) {
							long oper = sdf.parse(newOper).getTime();
							long diff = Math.abs(stand - oper);
							long cha=diff/1000/60;
							if (cha > 60) {
								jsonObject.put("asyErrorCode", "0");
								jsonObject.put("data", deviceProperty.getActionCount());
								return jsonObject;
							}
						}
						else {
							jsonObject.put("asyErrorCode", "0");
							jsonObject.put("data", deviceProperty.getActionCount());
							return jsonObject;
						}
					}
				}
			}
		}
		
		jsonObject.put("asyErrorCode",2);
		jsonObject.put("data", "not in the specified time!");
		return jsonObject;
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

	public JSONObject getJsonResult(String key) {
		SyncFuture<Object> future = new SyncFuture<>();
		FutureMap.addFuture(key, future);
		try {
			ResponseData getobj = (ResponseData) future.get(6, TimeUnit.SECONDS);
			JSONObject jsonObject = JSONObject.fromObject(getobj);
			FutureMap.removeFutureMap(key);
			return jsonObject;
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

	}

	@Override
	public Boolean updateDeviceName(String deviceId, String name) {
		// TODO Auto-generated method stub
		deviceDao.updateDeviceName(deviceId, name);
		return true;
	}

	@Override
	public Device getDeviceById(String deviceId) {
		// TODO Auto-generated method stub
		return deviceDao.getDeviceById(deviceId);
	}

}
