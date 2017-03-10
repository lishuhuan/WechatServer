package com.demo.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotMap {
	
	public static Map<Integer, List<PropertyUtil>> map=new HashMap<>();
	
	public static void insertMap(int key,PropertyUtil propertyUtil){
		if(map==null){
			map=new HashMap<>();
		}
		List<PropertyUtil> list=map.get(Integer.valueOf(key));
		if(list==null){
			list=new ArrayList<>();
		}
		list.add(propertyUtil);
		map.put(Integer.valueOf(key), list);
	}
	
	public static List<PropertyUtil> searchMap(int key){
		return map.get(Integer.valueOf(key));
	}

}
