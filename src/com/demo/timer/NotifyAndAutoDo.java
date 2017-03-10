package com.demo.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class NotifyAndAutoDo implements Runnable{
	
	private static final Logger logger = Logger.getLogger(NotifyAndAutoDo.class);
	
	@Override
	public void run() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int startupTime = cal.get(Calendar.DAY_OF_WEEK)-1;
		 WheelTime.initMap();
		 int tag=0;
		while(true){
			Calendar calnew = Calendar.getInstance();
			calnew.setTime(new Date());
			int time = calnew.get(Calendar.DAY_OF_WEEK)-1;
			if(time!=startupTime){
				startupTime=time;
				WheelTime.initMap();
				tag=0;
			}
			Map<Integer, List<PropertyUtil>> map=SlotMap.map;
			logger.info(tag);
			if(map!=null){
				if(map.containsKey(tag)){
					List<PropertyUtil> list=map.get(tag);
					ExecuteMission mission=new ExecuteMission(list);
					mission.start();
				}
			}
			
			try {
				Thread.sleep(60*1000*5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tag++;
		}
	}
	
}
