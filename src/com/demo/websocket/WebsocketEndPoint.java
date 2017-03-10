package com.demo.websocket;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.demo.model.ResponseData;
import com.demo.service.DeviceStatusService;
import com.demo.util.Ehcache;
import com.demo.util.GetToken;
import com.demo.util.HttpClient;
import com.demo.util.JsonUtil;

import net.sf.json.JSONObject;

public class WebsocketEndPoint extends TextWebSocketHandler {  
	@Autowired
	private DeviceStatusService deviceStatusService;
      
    @Override  
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {  
        String json=message.getPayload();
        String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String userid = (String) JsonUtil.jsonTranslate(json, "openId");
		String data = (String) JsonUtil.jsonTranslate(json, "action");
		System.out.println("data" + data);
		if (null == deviceId || null == userid || null == data) {
			System.out.println("Lock params!");
			 session.sendMessage(new TextMessage("Lock params!"));
			return;
		}
		data = paramConsume(data);
		if (null == data) {
			 session.sendMessage(new TextMessage("param false!"));
			return;
		}

		String token = GetToken.getAccessToken();
		JSONObject jsonObject = paramToJson(token, deviceId, userid, data);
		JSONObject response = HttpClient.doPost(
				"https://api.weixin.qq.com/hardware/mydevice/platform/ctrl_device?access_token=" + token, jsonObject); // 鍙戦�佹帶鍒惰姹�
		if (response.get("error_code").equals(0)) {
			String msgId = response.getString("msg_id") + "_Set";
			paramResponse(deviceId,userid,msgId,session,data);

		} else {
			TextMessage reTextMessage=new TextMessage(response.toString());
			 session.sendMessage(reTextMessage);
			return;
		}
        
          
    }  
    
    
    public void paramResponse(final String deviceId,final String userid,final String msgId,WebSocketSession session,final String data) throws InterruptedException, ExecutionException, TimeoutException {
		Callable<JSONObject> callable = new Callable<JSONObject>() {
			public JSONObject call() throws Exception {
				JSONObject result = new JSONObject();
				while (true) {
					Ehcache ehCache = new Ehcache();
					ResponseData getobj = (ResponseData) ehCache.getCacheElement(msgId);
					if (null != getobj) {
						result = JSONObject.fromObject(getobj);
						deviceStatusService.setDeviceStatus(deviceId, userid, data);
						ehCache.deleteCache(msgId);
						break;
					}
				}
				return result;

			}
		};

		FutureTask<JSONObject> future = new FutureTask<JSONObject>(callable);
		new Thread(future).start();
		JSONObject result = future.get(10, TimeUnit.SECONDS);
		try {
			 session.sendMessage(new TextMessage((String) JSONObject.toBean(result)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String paramConsume(String data) {
		if (data.contains("watering")) {
			int second = Integer.parseInt(data.substring(data.indexOf("-") + 1));
			String to = String.format("%4s", Integer.toHexString(second)).replace(' ', '0');
			data = "0101" + to;
			return data;
		} else if (data.contains("led_pwm")) {
			int second = Integer.parseInt(data.substring(data.indexOf("-") + 1));
			String to = String.format("%2s", Integer.toHexString(second)).replace(' ', '0');
			data = "02" + to;
			return data;
		} else {
			return null;
		}
	}

	public JSONObject paramToJson(String token, String deviceId, String userid, String data) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_type", "gh_3f4fcd63df5d");
		jsonObject.put("device_id", deviceId);
		/*
		 * Map<String, String> map=new HashMap<>(); map.put("power_switch",
		 * "true");
		 */
		jsonObject.put("service", "");
		jsonObject.put("user", userid);

		jsonObject.put("data", data);
		return jsonObject;
	}
}  