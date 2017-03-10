package com.demo.device;

import java.util.HashMap;
import java.util.Map;

public class FutureMap {

	private static Map<String, SyncFuture<Object>> futureMap = new HashMap<>();

	public static void addFuture(String id, SyncFuture<Object> future) {
		futureMap.put(id, future);
	}
	
	public static SyncFuture<Object> getFutureMap(String id){
        return futureMap.get(id);
    }
	
	public static void removeFutureMap(String id){
		futureMap.remove(id);
    }
	
	public static Map<String,  SyncFuture<Object>> getAllMap(){
		return futureMap;
	}
}
