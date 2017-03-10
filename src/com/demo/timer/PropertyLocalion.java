package com.demo.timer;

import java.util.HashMap;
import java.util.Map;

public class PropertyLocalion {

	public static Map<String, Integer> notifyLocal=new HashMap<>();
	
	public static Map<String, Integer> deadlineLocal=new HashMap<>();
	
	public static void insertNotifyLocal(String propertyId,int local){
		if(notifyLocal==null){
			notifyLocal=new HashMap<>();
		}
		notifyLocal.put(propertyId, Integer.valueOf(local));
	}
	
	public static void insertDeadlineLocal(String propertyId,int local){
		if(deadlineLocal==null){
			deadlineLocal=new HashMap<>();
		}
		deadlineLocal.put(propertyId, Integer.valueOf(local));
	}
}
