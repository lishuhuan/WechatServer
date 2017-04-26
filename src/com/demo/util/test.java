package com.demo.util;

import com.demo.accessToken.GetAccessToken;
import com.demo.service.impl.OneNetServiceImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class test {

	public static void main(String[] args) {
		String value="883B010001870734303933353635861C793D44485246524C304A65594E356F5A304F68637477353654546B3D850C43383933343644413433373370056C";
		String deviceId="4093565";
		int deviceIdLen=Integer.valueOf(value.substring(12, 14), 16)*2;
		int apiLen=Integer.valueOf(value.substring(16+deviceIdLen, 18+deviceIdLen), 16)*2;
		String sn=value.substring(22+deviceIdLen+apiLen);
		System.out.println(sn);
		wechatRegister(deviceId,sn);
		
	}
	
	private static void wechatRegister(String deviceId, String sn) {
		// TODO Auto-generated method stub
		String token=GetAccessToken.getAccessToken();
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("device_num", "1");
		jsonObject.put("op_type", "0");
		jsonObject.put("product_id", "22040");
		JSONArray jsonArray=new JSONArray();
		JSONObject object=new JSONObject();
		object.put("id", deviceId);
		object.put("mac", sn);
		object.put("auth_key", "");
		object.put("connect_protocol", "4");
		object.put("close_strategy", "1");
		object.put("conn_strategy", "1");
		object.put("crypt_method", "0");
		object.put("auth_ver", "0");
		object.put("manu_mac_pos", "-1");
		object.put("ser_mac_pos", "-2");
		object.put("ble_simple_protocol", "0");
		jsonArray.add(object);
		jsonObject.put("device_list", jsonArray);
		
		JSONObject json=HttpClient.doPost("https://api.weixin.qq.com/device/authorize_device?access_token="+token, jsonObject);
		System.out.println(json);
		
	}
}
