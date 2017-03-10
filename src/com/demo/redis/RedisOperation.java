package com.demo.redis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.demo.model.Relation;

public class RedisOperation {

	public static void setControlRecordToRedis(String deviceId,String tag,List<Relation> list){
		String key=deviceId+tag;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String oldFirst=RedisAPI.lindex(key, 0);
		if(oldFirst!=null){
			if(!sdf.format(new Date()).contains(oldFirst.substring(0,10))){
				RedisAPI.del(key);
				RedisAPI.lpush(key, sdf.format(new Date()));
			}
			else {
				RedisAPI.lpush(key, sdf.format(new Date()));
			}
		}
		else {
			RedisAPI.lpush(key, sdf.format(new Date()));
		}
		RedisAPI.multiHincrby(list);
		
	}
	
	public static void setControlRecordToRedisHostAndFriend(String deviceId,String tag,List<Relation> list,String openid){
		String key=deviceId+tag;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String oldFirst=RedisAPI.lindex(key, 0);
		if(oldFirst!=null){
			if(!sdf.format(new Date()).contains(oldFirst.substring(0,10))){
				RedisAPI.del(key);
				RedisAPI.lpush(key, sdf.format(new Date()));
			}
			else {
				RedisAPI.lpush(key, sdf.format(new Date()));
			}
		}
		else {
			RedisAPI.lpush(key, sdf.format(new Date()));
		}
		RedisAPI.multiZincrbyMyAndFriend(list,openid);
		
	}
}
