package com.demo.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.DevicePropertyDao;
import com.demo.dao.RelationDao;
import com.demo.model.DeviceProperty;
import com.demo.onenet.PostData;
import com.demo.service.DevicePropertyService;

@Service("DevicePropertyService")
public class DevicePropertyServiceImpl implements DevicePropertyService {

	@Autowired
	private DevicePropertyDao devicePropertyDao;
	
	@Autowired
	private RelationDao relationDao;

	@Override
	public Boolean insertDeviceProperty(String openId, String deviceId, String name, String time, String repetition,
			String waterTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		DeviceProperty deviceProperty = new DeviceProperty();
		String id = UUID.randomUUID().toString().replace("-", "");
		deviceProperty.setId(id);
		deviceProperty.setDeviceId(deviceId);
		deviceProperty.setPropertyName(name);
		deviceProperty.setCreateBy(openId);
		deviceProperty.setCreateAt(new Date());
		deviceProperty.setRepetition(repetition);
		if (waterTime != null && !"".equals(waterTime)) {
			deviceProperty.setActionCount(Integer.parseInt(waterTime));
		}
		try {
			deviceProperty.setTimePoint(sdf.parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Integer> tags=devicePropertyDao.getTagByOrder(deviceId);
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
		deviceProperty.setTag(tag);
		devicePropertyDao.insertDeviceProperty(deviceProperty);
		/*SimpleDateFormat second = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat stand = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SlotSetByProperty.setPropertyToSlot(deviceProperty, new Date().getTime(), second, day, stand, new Date());*/
		sendWaterPlanToDevice(openId, deviceId, time, repetition, Integer.parseInt(waterTime),tag);
		return true;
	}

	private void sendWaterPlanToDevice(String openId, String deviceId, String time, String repeat,
			int waterTime, int tag) {
		// TODO Auto-generated method stub
		String days = "7F";
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
		int delay = waterTime;
		String data="A107"
				+String.format("%2s", Integer.toHexString(tag)).replace(' ', '0')
				+days
				+ String.format("%2s", Integer.toHexString(hour)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(minute)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(second)).replace(' ', '0')
				+ String.format("%4s", Integer.toHexString(delay)).replace(' ', '0');
		PostData.Post(deviceId, data);
	}

	@Override
	public List<DeviceProperty> getDeviceProperty(String deviceId) {
		// TODO Auto-generated method stub
		return devicePropertyDao.getDeviceProperty(deviceId);
	}

	@Override
	public Boolean editDeviceProperty(String id, String time, String repetition) {
		// TODO Auto-generated method stub
		DeviceProperty deviceProperty = devicePropertyDao.getdevicePropertyById(id);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		deviceProperty.setRepetition(repetition);
		try {
			deviceProperty.setTimePoint(sdf.parse(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		devicePropertyDao.updateDeviceProperty(deviceProperty);
		String openId=relationDao.getOpenIdByDeviceHost(deviceProperty.getDeviceId());
		sendWaterPlanToDevice(openId, deviceProperty.getDeviceId(), time, deviceProperty.getRepetition(), deviceProperty.getActionCount(), deviceProperty.getTag());
		/*Map<String, Integer> notifyMap = PropertyLocalion.notifyLocal;
		if (notifyMap != null && notifyMap.size() > 0) {
			if (notifyMap.containsKey(id)) {
				int notifyloc = notifyMap.get(id);
				List<PropertyUtil> list = SlotMap.map.get(notifyloc);
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getProperty().getId().equals(id)) {
						list.remove(i);
						SlotMap.map.put(notifyloc, list);
						PropertyLocalion.notifyLocal.remove(id);
						break;
					}
				}

			}
		}

		Map<String, Integer> deadlineMap = PropertyLocalion.deadlineLocal;
		if (deadlineMap != null && deadlineMap.size() > 0) {
			if (deadlineMap.containsKey(id)) {
				int deadlineLocal = deadlineMap.get(id);
				List<PropertyUtil> list = SlotMap.map.get(deadlineLocal);
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getProperty().getId().equals(id)) {
						list.remove(i);
						SlotMap.map.put(deadlineLocal, list);
						PropertyLocalion.deadlineLocal.remove(id);
						break;
					}
				}

			}
		}
		SimpleDateFormat second = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat stand = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SlotSetByProperty.setPropertyToSlot(deviceProperty, new Date().getTime(), second, day, stand, new Date());*/
		return true;
	}

	@Override
	public List<DeviceProperty> getAllProperties() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int xingqi = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (xingqi == 0) {
			xingqi = 7;
		}
		return devicePropertyDao.getAllProperties(String.valueOf(xingqi));
	}

	@Override
	public Boolean deleteDeviceProperty(String id) {
		// TODO Auto-generated method stub
		DeviceProperty deviceProperty=devicePropertyDao.getdevicePropertyById(id);
		String data="A401"+String.format("%2s", Integer.toHexString(deviceProperty.getTag())).replace(' ', '0');
		PostData.Post(deviceProperty.getDeviceId(), data);
		devicePropertyDao.deleteDeviceProperty(id);
		return true;
	}

	@Override
	public List<DeviceProperty> getPropertyByTime(int week) {
		// TODO Auto-generated method stub
		return devicePropertyDao.getPropertyByTime(week);
	}
}
