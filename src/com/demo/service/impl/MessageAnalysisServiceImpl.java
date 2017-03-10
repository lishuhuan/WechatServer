package com.demo.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.accessToken.Const;
import com.demo.accessToken.GetAccessToken;
import com.demo.dao.DeviceDao;
import com.demo.dao.DevicePropertyDao;
import com.demo.dao.DeviceStatusDao;
import com.demo.dao.DeviceVersionDao;
import com.demo.dao.LedPropertyDao;
import com.demo.dao.RelationDao;
import com.demo.dao.UserBindingDao;
import com.demo.device.GetOrderByDevice;
import com.demo.device.SendOrderToDevice;
import com.demo.model.Device;
import com.demo.model.DeviceProperty;
import com.demo.model.DeviceStatus;
import com.demo.model.LedProperty;
import com.demo.model.Relation;
import com.demo.model.ResponseData;
import com.demo.model.User;
import com.demo.redis.RedisAPI;
import com.demo.redis.RedisOperation;
import com.demo.service.MessageAnalysisService;
import com.demo.util.DataProtocol;
import com.demo.util.GetToken;
import com.demo.util.HttpClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

@Service
public class MessageAnalysisServiceImpl implements MessageAnalysisService {

	private static Logger logger = Logger.getLogger(MessageAnalysisServiceImpl.class);

	@Autowired
	private UserBindingDao userBindingDao;

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
	public ResponseData getData(JSONObject jsonObject) {
		String asyErrorCode = jsonObject.getString("asy_error_code");
		String asyErrorMsg = jsonObject.getString("asy_error_msg");
		String msgId = jsonObject.getString("msg_id");
		String msgType = jsonObject.getString("msg_type");
		String services = "";
		if (jsonObject.containsKey("services")) {
			services = jsonObject.getString("services");
		}

		ResponseData responseData = new ResponseData();
		if (jsonObject.containsKey("data")) {
			responseData.setData(jsonObject.getString("data"));
		}
		responseData.setAsyErrorCode(asyErrorCode);
		responseData.setAsyErrorMsg(asyErrorMsg);
		responseData.setMsgId(msgId);
		responseData.setMsgType(msgType);
		responseData.setServices(services);
		return responseData;

	}

	@Override
	public void deviceBind(JSONObject jsonObject) {
		String device_id = (String) jsonObject.get("device_id");
		String device_type = (String) jsonObject.get("device_type");
		// String msg_id=(String) jsonObject.get("msg_id");
		String msg_type = (String) jsonObject.get("msg_type");
		// String create_time=(String) jsonObject.get("create_time");
		String open_id = (String) jsonObject.get("open_id");
		String qrcode = null;
		// String session_id=(String) jsonObject.get("session_id");
		// String content=(String) jsonObject.get("content");
		// String qrcode_suffix_data=(String)
		// jsonObject.get("qrcode_suffix_data");
		if (msg_type.equals("bind")) {
			if (jsonObject.containsKey("qrcode_suffix_data")) {
				String qrcode_suffix_data = jsonObject.getString("qrcode_suffix_data");
				if (!"".equals(qrcode_suffix_data)) {
					long nowtime = new Date().getTime();
					try {
						byte[] bytes = new BASE64Decoder().decodeBuffer(qrcode_suffix_data);
						String string = new String(bytes);
						long con = Long.parseLong(string);
						long cha = (nowtime - con) / 1000 / 60;
						if (cha > 10) {
							unbindAndnotify(device_id, open_id);
							return;
						}
						String code = RedisAPI.stringGet(string);
						if (code != null) {
							return;
						}
						qrcode = string;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			User user = userBindingDao.getUserById(open_id);
			if (null == user) {
				String id = UUID.randomUUID().toString().replace("-", "");
				User userNew = new User();
				userNew.setId(id);
				userNew.setOpenId(open_id);
				userNew.setCreateTime(new Date());
				userBindingDao.insertUser(userNew);
			}
			Device device = deviceDao.getDeviceById(device_id);
			String nickname = null;
			if (null == device) {
				Random random = new java.util.Random();
				int r = random.nextInt(20);
				String name = Const.name[r];
				nickname = name;
				String photo = "http://121.40.65.146/default_photo/" + (r + 1) + ".png";
				String id = UUID.randomUUID().toString().replace("-", "");
				Device nDevice = new Device();
				nDevice.setId(id);
				nDevice.setDeviceId(device_id);
				nDevice.setDeviceType(device_type);
				nDevice.setName(name);
				nDevice.setPhoto(photo);
				nDevice.setCreateTime(new Date());
				deviceDao.insertDevice(nDevice);
			} else {
				nickname = device.getName();
			}
			Relation relation = new Relation();
			relation.setDeviceId(device_id);
			relation.setOpenId(open_id);
			relation.setDeviceNickName(nickname);
			relation.setCreateTime(new Date());
			List<Relation> relations = relationDao.getRelationByDevice(device_id);
			if (relations.size() > 0) {
				relation.setHost(1);
				relation.setRwFlag(0);
			} else {
				relation.setHost(0);
				relation.setRwFlag(1);
			}
			relationDao.insertRelation(relation);
			sendPageToUser(open_id, nickname);
			if (qrcode != null) {
				RedisAPI.stringSet(qrcode);
			}
		}
		if (msg_type.equals("unbind")) {
			Relation relation = new Relation();
			relation.setDeviceId(device_id);
			relation.setOpenId(open_id);
			relationDao.deleteRelation(relation);
		}
	}

	public void unbindAndnotify(String deviceId, String openid) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_id", deviceId);
		jsonObject.put("openid", openid);
		String token = GetToken.getAccessToken();
		JSONObject response = HttpClient.doPost("https://api.weixin.qq.com/device/compel_unbind?access_token=" + token,
				jsonObject);
		logger.info(response);
		DataProtocol.sendTextToWechat(openid, "绑定设备失败，二维码已过期，请联系管理员。", "unbind");

	}

	public void sendPageToUser(String openId, String name) {
		String token = GetAccessToken.getAccessToken();
		JSONObject content = new JSONObject();
		content.put("title", "绑定设备:" + name);
		content.put("desription", "在这里，您可以绑定您的设备，随时随地照顾您的植物");
		content.put("url",
				"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2c97180bd57e660f&redirect_uri=http%3a%2f%2fwww.shuaikang.iego.cn%2fWechatServer&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
		content.put("picurl", "http://121.40.65.146/flower_photo/huapen.png");
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(content);
		JSONObject articles = new JSONObject();
		articles.put("articles", jsonArray);
		DataProtocol.sendNewsToWechat(openId, articles, token, "qrcode time-out");
	}

	@Override
	public void getMessageByNotify(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		String device_id = (String) jsonObject.get("device_id");
		String data = jsonObject.getString("data");
		String openId = relationDao.getOpenIdByDeviceHost(device_id);
		logger.info(data);
		if (data.startsWith("03")) {
			GetOrderByDevice.getDeviceTempAndHumidity(data, device_id);
			String response = "030100";
			DataProtocol.sendDataToDevice(device_id, openId, response, "deviceTempAndHumidity");
		}
		if (data.startsWith("04")) {
			Device device = deviceDao.getDeviceById(device_id);
			GetOrderByDevice.deviceAlarm(openId, device.getName());
			String response = "040100";
			DataProtocol.sendDataToDevice(device_id, openId, response, "deviceAlarm");
		}
		if (data.startsWith("90")) {
			List<DeviceProperty> deviceProperties = devicePropertyDao.getDeviceProperty(device_id);
			List<LedProperty> ledProperties = ledPropertyDao.getLedProperty(device_id);
			Device device = deviceDao.getDeviceById(device_id);
			GetOrderByDevice.statusSync(deviceProperties, ledProperties, openId, device);
		}
		if (data.startsWith("88")) {
			logger.info(data);
			String version = deviceVersionDao.getNewestVersion();
			int high = Integer.valueOf(data.substring(6, 8), 16);
			int low = Integer.valueOf(data.substring(8), 16);
			String device = String.valueOf(high) + "." + String.valueOf(low);
			String response = "880100";
			if (!device.equals(version)) {
				response = "880101";
				// SendOrderToDevice.sendUpgradeToDevice(device_id,openId);
			}
			DataProtocol.sendDataToDevice(device_id, openId, response, "UpgradeInfo");
		}
		if (data.startsWith("92")) {
			String version = deviceVersionDao.getNewestVersion();
			SendOrderToDevice.sendUpgradeToDevice(device_id, openId, version);
		}
		if (data.startsWith("05")) {
			String power = data.substring(2);
			int p = Integer.parseInt(power, 16);
			String operation = "led_pwm-" + String.valueOf(p);
			insertDeviceStatus(device_id, "local", operation);
			List<Relation> list = relationDao.getRelationByDevice(device_id);
			String openid = relationDao.getOpenIdByDeviceHost(device_id);
			RedisOperation.setControlRecordToRedisHostAndFriend(device_id, "led", list, openid);
			RedisAPI.rpush("ledenergy" + device_id, p + "#" + String.valueOf(new Date().getTime()));
			String response = "050100";
			DataProtocol.sendDataToDevice(device_id, openId, response, "deviceSendLed");
		}
		if (data.startsWith("06")) {
			String rule = data.substring(2, 4);
			if (rule.equals("01")) {
				String time = data.substring(4);
				int p = Integer.parseInt(time, 16);
				String operation = "watering-" + String.valueOf(p);
				insertDeviceStatus(device_id, "local", operation);
				List<Relation> list = relationDao.getRelationByDevice(device_id);
				String openid = relationDao.getOpenIdByDeviceHost(device_id);
				RedisOperation.setControlRecordToRedisHostAndFriend(device_id, "water", list, openid);
			}
			String response = "060100";
			DataProtocol.sendDataToDevice(device_id, openId, response, "deviceSendWater");
		}

		if (data.startsWith("91")) {
			String response = timeCalibration();
			DataProtocol.sendDataToDevice(device_id, openId, response, "timeCalibration");
		}
		if (data.startsWith("07")) {
			String tag = data.substring(4);
			if (tag.equals("00")) {
				Relation relation = relationDao.getRelationByDeviceHost(device_id);
				DataProtocol.sendTextToWechat(relation.getOpenId(), "您的设备" + relation.getDeviceNickName() + "光照计划已经关闭",
						"ledClose");
			}
			String response = "070100";
			DataProtocol.sendDataToDevice(device_id, openId, response, "deviceSendSunModel");
		}

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
}
