package com.demo.status;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.demo.device.FutureMap;
import com.demo.device.SyncFuture;
import com.demo.onenet.PostData;
import com.demo.util.Ehcache;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DeviceListStatus extends Thread {

	private  List<String> deviceList;
	private  String openid;
	
	 public DeviceListStatus(List<String> deviceList,String openid) {
		// TODO Auto-generated constructor stub
		 this.deviceList=deviceList;
		 this.openid=openid;
	}
	
	@Override
	public void run() {
		JSONArray jsonArray=new JSONArray();
		for(String device:deviceList){
			JSONObject response=PostData.Post(device, "");
			int resultCode=response.getInt("errno");
			JSONObject deviceStatus=new JSONObject();
			if (resultCode==0) {
				deviceStatus.put("status", 1);
			}
			else {
				deviceStatus.put("status", 0);
			}
			deviceStatus.put("deviceId", device);
			jsonArray.add(deviceStatus);
			
		}
		
		try {
			Ehcache ehcache=new Ehcache();
			Object object=ehcache.getCacheElement(openid);
			if(object!=null){
				ehcache.deleteCache(openid);
			}
			ehcache.addToCache(openid, jsonArray);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
