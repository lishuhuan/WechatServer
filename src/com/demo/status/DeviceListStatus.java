package com.demo.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.demo.accessToken.GetAccessToken;
import com.demo.device.FutureMap;
import com.demo.device.SyncFuture;
import com.demo.util.Ehcache;
import com.demo.util.HttpClient;

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
		String token =GetAccessToken.getAccessToken();
		JSONArray jsonArray=new JSONArray();
		for(String device:deviceList){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("device_type", "gh_3f4fcd63df5d");
			jsonObject.put("device_id", device);
			/*
			 * Map<String, String> map=new HashMap<>(); map.put("power_switch",
			 * "true");
			 */
			jsonObject.put("service", "");
			jsonObject.put("user", openid);
			JSONObject response = HttpClient.doPost(
					"https://api.weixin.qq.com/hardware/mydevice/platform/get_device_status?access_token=" + token,
					jsonObject);
			JSONObject deviceStatus=new JSONObject();
			if (response.get("error_code").equals(0)) {
				/*result = getJsonResult(response.getString("msg_id") + "_Get");
				JSONObject service=result.getJSONObject("services");
				JSONObject operation_status=service.getJSONObject("operation_status");
				String status=operation_status.getString("status");*/
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
	
	public JSONObject getJsonResult(String key) {
		SyncFuture<Object> future = new SyncFuture<>();
		FutureMap.addFuture(key, future);
		try {
			JSONObject getobj = (JSONObject) future.get(6, TimeUnit.SECONDS);
			FutureMap.removeFutureMap(key);
			return getobj;
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

	}
}
