package com.demo.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.demo.accessToken.GetAccessToken;
import com.demo.model.DeviceProperty;
import com.demo.model.Relation;
import com.demo.onenet.PostData;
import com.demo.redis.RedisAPI;
import com.demo.redis.RedisOperation;
import com.demo.service.DevicePropertyService;
import com.demo.service.impl.DeviceCommandServiceImpl;
import com.demo.util.DataProtocol;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component("WaterPlan")
public class WaterPlan implements ApplicationContextAware{

	private static Logger logger = Logger.getLogger(WaterPlan.class);
	
	private static ApplicationContext context;

	public static ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	public void start() throws ParseException{
		Calendar a=Calendar.getInstance();
		int week=a.get(Calendar.DAY_OF_WEEK)-1;
		if(week==0){
			week=7;
		}
		DevicePropertyService propertyService = (DevicePropertyService) WheelTime.getContext().getBean("DevicePropertyService");
		List<DeviceProperty> list=propertyService.getPropertyByTime(week);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat tt = new SimpleDateFormat("HH:mm:ss");
		
		String date=tt.format(new Date());
		String token=GetAccessToken.getAccessToken();
		for(DeviceProperty deviceProperty:list){
			if(deviceProperty.getTimePoint().getTime()>tt.parse(date).getTime()){
				List<Relation> relations=WheelTime.getRelationList(deviceProperty.getDeviceId());
				for(Relation relation:relations){
					sendNotifyToUser(relation.getOpenId(), relation.getDeviceNickName(), token);
				}
			}
			else{
				//watering
				String deviceId=deviceProperty.getDeviceId();
				String newOper = RedisAPI.lindex(deviceId + "water", 0);
				String point = format.format(new Date()) + " " + tt.format(deviceProperty.getTimePoint());
				if (newOper != null) {
					try {
						long stand = sdf.parse(point).getTime();
						long oper = sdf.parse(newOper).getTime();
						long diff = Math.abs(stand - oper);
						long cha=diff/1000/60;
						if(cha>60){
							//excute watering
							excuteWatering(token, deviceId);
							logger.info("water:"+deviceId);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					excuteWatering(token, deviceId);
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
	
	public void excuteWatering(String token,String deviceId){
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
