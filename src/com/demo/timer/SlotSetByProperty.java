package com.demo.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.demo.model.DeviceProperty;

public class SlotSetByProperty {
	
	private static Logger logger = Logger.getLogger(SlotSetByProperty.class);
	
	public static void setPropertyToSlot(DeviceProperty property,long current,SimpleDateFormat second,SimpleDateFormat day,SimpleDateFormat stand,Date now){
		if(property.getTimePoint()!=null){
			String point=second.format(property.getTimePoint());
			String time=day.format(now)+" "+point;
			long startPointer=0;
			long endPoint=0;
			try {
				long pro=stand.parse(time).getTime();
				if(pro>current){
					startPointer=(pro-current)/1000/60/5-11;  //间隔5分钟为以单位
					endPoint=(pro-current)/1000/60/5+12;        
				}
	
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(startPointer>0){
				int start=(int)startPointer;
				PropertyUtil notify=new PropertyUtil();
				notify.setProperty(property);
				notify.setType(0);	
				SlotMap.insertMap(start, notify);
				PropertyLocalion.insertNotifyLocal(property.getId(), start);
				logger.info("notify:"+property.getDeviceId()+" "+start);
			}
			if(endPoint>0){
				int end=(int)endPoint;
				PropertyUtil deadline=new PropertyUtil();
				deadline.setProperty(property);
				deadline.setType(1);
				SlotMap.insertMap(end, deadline);
				PropertyLocalion.insertDeadlineLocal(property.getId(), end);
				logger.info("autowater:"+property.getDeviceId()+" "+end);
			}
		}
	}

}
