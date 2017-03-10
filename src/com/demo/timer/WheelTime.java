package com.demo.timer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.demo.model.DeviceProperty;
import com.demo.model.Relation;
import com.demo.service.DevicePropertyService;
import com.demo.service.DeviceStatusService;
import com.demo.service.RelationService;

@Component("wheelTime")
public class WheelTime implements ApplicationContextAware {
	private static ApplicationContext context;

	public static ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
	}

	public static void initMap() {
		SlotMap.map.clear();
		PropertyLocalion.notifyLocal.clear();
		PropertyLocalion.deadlineLocal.clear();
		System.out.println("init");
		DevicePropertyService Service = (DevicePropertyService) WheelTime.getContext().getBean("DevicePropertyService");
		List<DeviceProperty> list = Service.getAllProperties();
		Date now = new Date();
		SimpleDateFormat second = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat stand = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long current = now.getTime();
		for (DeviceProperty property : list) {
			SlotSetByProperty.setPropertyToSlot(property, current, second, day, stand, now);
		}
		Map<Integer, List<PropertyUtil>> map = SlotMap.map;
		System.out.println(map);

	}
	
	public static List<Relation> getRelationList(String deviceId){
		RelationService Service = (RelationService) WheelTime.getContext().getBean("RelationService");
		List<Relation> list=Service.getRelationByDevice(deviceId);
		return list;
	}
	
	public static List<Relation> waterLog(String deviceId, String userId, String data){
		DeviceStatusService Service = (DeviceStatusService) WheelTime.getContext().getBean("DeviceStatusService");
		List<Relation> list=Service.waterLog(deviceId, userId, data);
		return list;
		
	}
	
	public static String getRelation(String deviceId){
		RelationService Service = (RelationService) WheelTime.getContext().getBean("RelationService");
		return Service.getRelationByHost(deviceId);
	}

}
