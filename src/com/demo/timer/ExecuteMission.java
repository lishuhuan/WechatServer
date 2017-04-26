package com.demo.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.demo.accessToken.GetAccessToken;
import com.demo.model.Relation;
import com.demo.onenet.PostData;
import com.demo.redis.RedisAPI;
import com.demo.redis.RedisOperation;
import com.demo.service.impl.DeviceCommandServiceImpl;
import com.demo.util.DataProtocol;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExecuteMission extends Thread {
	
	private static Logger logger = Logger.getLogger(ExecuteMission.class);
	
	private List<PropertyUtil> list;
	
	public  ExecuteMission(List<PropertyUtil> list) {
		// TODO Auto-generated constructor stub
		this.list=list;
	}
	
	@Override
	public void run() {
		String token=GetAccessToken.getAccessToken();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat tt = new SimpleDateFormat("HH:mm:ss");
		for(PropertyUtil util:list){
			if(util.getType()==0){
				//notify
				List<Relation> list=WheelTime.getRelationList(util.getProperty().getDeviceId());
				for(Relation relation:list){
					sendNotifyToUser(relation.getOpenId(), relation.getDeviceNickName(), token);
				}
			}
			else {
				//watering
				String deviceId=util.getProperty().getDeviceId();
				String newOper = RedisAPI.lindex(deviceId + "water", 0);
				String point = format.format(new Date()) + " " + tt.format(util.getProperty().getTimePoint());
				if (newOper != null) {
					try {
						long stand = sdf.parse(point).getTime();
						long oper = sdf.parse(newOper).getTime();
						long diff = Math.abs(stand - oper);
						long cha=diff/1000/60;
						if(cha>60){
							//excute watering
							excuteWatering(deviceId);
							logger.info("water:"+deviceId);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void sendNotifyToUser(String openId,String name,String token){
		JSONObject content=new JSONObject();
		content.put("title", "可以给您的设备："+name+"浇水啦！");
		content.put("desription", "可以给您的设备："+name+"浇水啦！");
		content.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2c97180bd57e660f&redirect_uri=http%3a%2f%2fwww.shuaikang.iego.cn%2fWechatServer&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
		content.put("picurl", "http://121.40.65.146/flower_photo/notify.png");
		JSONArray jsonArray=new JSONArray();
		jsonArray.add(content);
		JSONObject articles=new JSONObject();
		articles.put("articles", jsonArray);
		DataProtocol.sendNewsToWechat(openId, articles, token, "water notify");
	}
	
	public void excuteWatering(String deviceId){
		String userid=WheelTime.getRelation(deviceId);
		JSONObject response=PostData.Post(deviceId, "01010005");
		int resultCode=response.getInt("errno");
		if (resultCode==0) {
			String result = DeviceCommandServiceImpl.getJsonResult(deviceId + "_command");
			if ("00".equals(result)) {		
				List<Relation> list=WheelTime.waterLog(deviceId, "10000", "watering-5");
				RedisOperation.setControlRecordToRedis(deviceId, "water",list);
				logger.info("water:"+userid);
			}
		}
	}
	

}
