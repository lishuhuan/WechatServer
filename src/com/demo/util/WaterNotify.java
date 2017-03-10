package com.demo.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.demo.model.Relation;

import net.sf.json.JSONObject;

public class WaterNotify {

	private static Logger logger = Logger.getLogger(WaterNotify.class);
	
	public static void notify(List<Relation> list,String token,String username){
		for(Relation relation:list){
			sendNotifyToUser(relation.getOpenId(), relation.getDeviceNickName(),username, token);
		}
	}
	
	public static void sendNotifyToUser(String openId,String deviceName,String username,String token){
		DataProtocol.sendTextToWechat(openId, username+"给您的设备："+deviceName+"浇水啦", "water remind");
	}
}
