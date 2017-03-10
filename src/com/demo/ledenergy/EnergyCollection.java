package com.demo.ledenergy;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.demo.accessToken.GetAccessToken;
import com.demo.redis.RedisAPI;
import com.demo.util.HttpClient;

import net.sf.json.JSONObject;

public class EnergyCollection {

	public void collect() {
		System.out.println(new Date());
		Set<String> set=RedisAPI.keyspattern("ledenergy*");
		if(set!=null){
			for(String key:set){
				parseList(key);
			}
		}
	}
	
	public void parseList(String key){
		List<String> list=RedisAPI.lrange(key, 0, -1);
		long power=0;
		int size=list.size();
		String deviceid=key.substring(9);
		for(int i=0;i<size;i++){
			if(i>0){
				String big=list.get(i);
				String small=list.get(i-1);			
				//int bigpower=Integer.valueOf(big.substring(0,big.indexOf("#")));
				long bigtime=Long.valueOf(big.substring(big.indexOf("#")+1));
				
				int smallpower=Integer.valueOf(small.substring(0,small.indexOf("#")));
				long smalltime=Long.valueOf(small.substring(small.indexOf("#")+1));
				if(smallpower==0){
					continue;
				}
				long time=(bigtime-smalltime)/1000/60;
				power+=time*(smallpower+100)/100;
			}
		}
		
		if(size>0){
			String pString=list.get(size-1);
			long bigtime=new Date().getTime();
			
			int smallpower=Integer.valueOf(pString.substring(0,pString.indexOf("#")));
			long smalltime=Long.valueOf(pString.substring(pString.indexOf("#")+1));
			if(smallpower!=0){
				long time=(bigtime-smalltime)/1000/60;
				power+=time*(smallpower+100)/100;
			}
			
			calculation(deviceid, power);
			
			RedisAPI.ltrim(key, size, -1);
			if(getResponseCode(deviceid)){
				RedisAPI.lpush(key, smallpower+"#"+String.valueOf(new Date().getTime()));
			}
		}
	}
	
	public static void calculation(String deviceId,long power){
		List<String> list=RedisAPI.lrange("sun"+deviceId,0,-1);
		if(list!=null){
			for(String a:list){
				power+=Integer.valueOf(a);
			}
			RedisAPI.del("sun"+deviceId);
		}
		int hostPower=(int)power/5;
		if(hostPower==0){
			RedisAPI.lpush("sun"+deviceId, String.valueOf(power));
		}
		else {
			RedisAPI.lpush("sun"+deviceId, String.valueOf(hostPower));
			double pleft=power-power/5;
			double a1=pleft/10;
			double d=(pleft-5*a1)/10;
			RedisAPI.addPower("sun"+deviceId, a1, d);
		}
	}
	
	public static Boolean getResponseCode(String device){
		String token =GetAccessToken.getAccessToken();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_type", "gh_3f4fcd63df5d");
		jsonObject.put("device_id", device);
		jsonObject.put("service", "");
		jsonObject.put("user", "oqL-ewTqgIu7Rw3UWu10F75V6si8");
		JSONObject response = HttpClient.doPost(
				"https://api.weixin.qq.com/hardware/mydevice/platform/get_device_status?access_token=" + token,
				jsonObject);
		if (response.get("error_code").equals(0)) {
			return true;
		}
		else{
			return false;
		}
	}
}
