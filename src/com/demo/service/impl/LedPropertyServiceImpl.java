package com.demo.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.DeviceDao;
import com.demo.dao.LedPropertyDao;
import com.demo.dao.RelationDao;
import com.demo.model.Device;
import com.demo.model.LedProperty;
import com.demo.service.LedPropertyService;
import com.demo.util.DataProtocol;

@Service("LedPropertyService")
public class LedPropertyServiceImpl implements LedPropertyService {

	private static Logger logger = Logger.getLogger(LedPropertyServiceImpl.class);

	@Autowired
	private LedPropertyDao ledPropertyDao;

	@Autowired
	private DeviceDao deviceDao;
	
	@Autowired
	private RelationDao relationDao;

	@Override
	public Boolean setSleepAndGetupLed(String deviceId, String model, String duration, String time, String openId,
			String type, String repeat) {
		if (repeat == null || "".equals(repeat)) {
			Calendar a = Calendar.getInstance();
			int week = a.get(Calendar.DAY_OF_WEEK) - 1;
			if (week == 0) {
				week = 7;
			}
			repeat = String.valueOf(week);
		}
		LedProperty property = new LedProperty();
		String id = UUID.randomUUID().toString().replace("-", "");
		property.setId(id);
		property.setDeviceId(deviceId);
		if (model != null && !"".equals(model)) {
			property.setModel(Integer.parseInt(model));
		}
		if (type != null && !"".equals(type)) {
			property.setType(Integer.parseInt(type));
		}
		property.setRepeat_date(repeat);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		property.setDuration(duration);
		try {
			property.setTime(sdf.parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat stand = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		property.setCreatedAt(stand.format(new Date()));
		property.setCreatedBy(openId);
		
		List<Integer> tags=ledPropertyDao.getTagByOrder(deviceId);
		int size=0;
		int tag=1;
		if(tags!=null && tags.size()>0){
			size=tags.size()+1;
		}
		for(int i=0;i<size;i++){
			if(!tags.contains(tag)){
				break;
			}
			tag++;
		}
		property.setTag(tag);
		ledPropertyDao.setSleepAndGetupLed(property);

		sendPlanToDevice(deviceId, model, duration, time, openId, type, repeat,tag);
		// TODO Auto-generated method stub
		return true;
	}

	private void sendPlanToDevice(String deviceId, String model, String duration, String time, String openId,
			String type, String repeat, int tag) {
		// TODO Auto-generated method stub
		String data = ledPlan(model, duration, time, type, repeat, deviceId,tag);
		DataProtocol.sendDataToDevice(deviceId, openId, data, "ledPlan");
	}

	private String ledPlan(String model, String duration, String time, String type, String repeat, String deviceId, int tag) {
		// TODO Auto-generated method stub
		String m = "01";
		Device device = deviceDao.getDeviceById(deviceId);
		//int intensity = Integer.parseInt(device.getLightIntensity());
		//int ct = Integer.parseInt(device.getColorTem());
		String startIntensity = String.format("%2s", Integer.toHexString(30)).replace(' ', '0');
		String endIntensity =  String.format("%2s", Integer.toHexString(30)).replace(' ', '0');
		String ctval =String.format("%4s", Integer.toHexString(3500)).replace(' ', '0');
		String adjust = "01";
		String days = "7F";
		if (model.equals("0")) {
			m = "02";
			endIntensity = "00";
			if("0".equals(type)){
				startIntensity = String.format("%2s", Integer.toHexString(15)).replace(' ', '0');
				endIntensity =  String.format("%2s", Integer.toHexString(15)).replace(' ', '0');
			}
			adjust = "02";
			/*if (type.equals("1")) {
				endIntensity = "00";
				adjust = "02";
			}*/
		} else {
			m = "03";
			startIntensity = "00";
			adjust = "03";
			/*if (type.equals("1")) {
				startIntensity = "00";
				adjust = "03";
			}*/
		}

		if (!repeat.equals("1234567")) {
			String num = repeat.substring(repeat.length() - 1);
			int len = Integer.parseInt(num);
			String dd = "";
			for (int i = 0; i < len; i++) {
				if (repeat.contains(String.valueOf(i + 1))) {
					dd += "1";
				} else {
					dd += "0";
				}
			}
			days = String.format("%2s", Integer.toHexString(Integer.parseInt(dd, 2))).replace(' ', '0');
		}
		int hour = Integer.parseInt(time.substring(0, 2));
		int minute = Integer.parseInt(time.substring(3, 5));
		int second = Integer.parseInt(time.substring(6));
		int delay = Integer.parseInt(duration);
		String data = "A20E" 
				+String.format("%2s", Integer.toHexString(tag)).replace(' ', '0')
				+m + startIntensity + ctval + endIntensity + ctval + adjust + days
				+ String.format("%2s", Integer.toHexString(hour)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(minute)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(second)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(delay)).replace(' ', '0');
		return data;
	}

	@Override
	public List<LedProperty> getLedProperty(String deviceId) {
		// TODO Auto-generated method stub
		return ledPropertyDao.getLedProperty(deviceId);
	}

	@Override
	public void deleteLedProperty(String propertyId) {
		// TODO Auto-generated method stub
		sendPlanDelete(propertyId);
		ledPropertyDao.deleteLedProperty(propertyId);
		
	}

	private void sendPlanDelete(String propertyId) {
		// TODO Auto-generated method stub
		LedProperty property=ledPropertyDao.getPropertyById(propertyId);
		String data="A501"+String.format("%2s", Integer.toHexString(property.getTag())).replace(' ', '0');
		String openId=relationDao.getOpenIdByDeviceHost(property.getDeviceId());
		DataProtocol.sendDataToDevice(property.getDeviceId(), openId, data, "sunPlanDelete");
	}

	@Override
	public boolean setSunPowerPlan(String deviceId, String isOpen) {
		// TODO Auto-generated method stub
		deviceDao.updateDeviceSunModel(deviceId, isOpen);
		sendSunModelToDevice(deviceId, isOpen);
		return true;
	}

	private void sendSunModelToDevice(String deviceId, String isOpen) {
		// TODO Auto-generated method stub
		String tag="00";
		if (isOpen.equals("0")) {
			tag="01";
		}
		String data="A301"+tag;
		String openId=relationDao.getOpenIdByDeviceHost(deviceId);
		DataProtocol.sendDataToDevice(deviceId, openId, data, "sunModel");
	}

	@Override
	public boolean updatePowerAndCt(String deviceId, String type, String power) {
		// TODO Auto-generated method stub
		Device device=deviceDao.getDeviceById(deviceId);
		String openId=relationDao.getOpenIdByDeviceHost(deviceId);
		String data="";
		if(type.equals("1")){
			int intensity=Integer.parseInt(device.getLightIntensity());
			int ct=Integer.parseInt(power);
			data="0203"+String.format("%2s", Integer.toHexString(intensity)).replace(' ', '0')
					+String.format("%4s", Integer.toHexString(ct)).replace(' ', '0');
		}
		else {
			int intensity=Integer.parseInt(power);
			int ct=Integer.parseInt(device.getColorTem());
			data="0203"+String.format("%2s", Integer.toHexString(intensity)).replace(' ', '0')
					+String.format("%4s", Integer.toHexString(ct)).replace(' ', '0');
		}
		DataProtocol.sendDataToDevice(deviceId, openId, data, "ledChange");
		deviceDao.updatePowerAndCt(deviceId, type, power);
		return true;
	}
	
	@Override
	public boolean updatePower(String deviceId, String type, String power) {
		// TODO Auto-generated method stub
		deviceDao.updatePowerAndCt(deviceId, type, power);
		return true;
	}

	@Override
	public boolean editSleepAndGetupLed(String planId, String model, String duration, String time, String type,
			String repeat) {
		// TODO Auto-generated method stub
		LedProperty property = ledPropertyDao.getPropertyById(planId);
		if (model != null && !"".equals(model)) {
			property.setModel(Integer.parseInt(model));
		}
		if (type != null && !"".equals(type)) {
			property.setType(Integer.parseInt(type));
		}
		property.setRepeat_date(repeat);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		property.setDuration(duration);
		try {
			property.setTime(sdf.parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ledPropertyDao.updateLedProperty(property);
		String openId=relationDao.getOpenIdByDeviceHost(property.getDeviceId());
		sendPlanToDevice(property.getDeviceId(), model, duration, time, openId, type, repeat, property.getTag());
		return true;
	}

}
