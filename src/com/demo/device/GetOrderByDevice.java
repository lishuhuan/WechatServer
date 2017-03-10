package com.demo.device;

import java.text.SimpleDateFormat;
import java.util.List;

import com.demo.model.Device;
import com.demo.model.DeviceProperty;
import com.demo.model.LedProperty;
import com.demo.redis.RedisAPI;
import com.demo.util.DataProtocol;

public class GetOrderByDevice {
	

	public static void getDeviceTempAndHumidity(String data,String device_id){
		if(data.length()>=10){
			int temperature=Integer.valueOf(data.substring(2, 4),16);
			int tem_point=Integer.valueOf(data.substring(4, 6),16);
			int humidity=Integer.valueOf(data.substring(6, 8),16);
			int hum_point=Integer.valueOf(data.substring(8),16);
			RedisAPI.lpush(device_id+"_tem", temperature+"."+tem_point);
			RedisAPI.lpush(device_id+"_hum",  humidity+"."+hum_point);
			RedisAPI.ltrim(device_id, 0, 19);
		}
	}
	
	public static void deviceAlarm(String openId,String name){
		DataProtocol.sendTextToWechat(openId, "您的宝贝设备:"+name+"正处于缺水状态", "deviceAlarm");
	}
	
	public static void statusSync(List<DeviceProperty> deviceProperties,List<LedProperty> ledProperties,String openId, Device device){
		int waterLen=0;
		int ledLen=0;
		if(deviceProperties!=null){
			waterLen=deviceProperties.size();
		}
		if(ledProperties!=null){
			ledLen=ledProperties.size();
		}
		int len=ledLen*16+waterLen*9+1;
		String ledData="";
		for(LedProperty proerty:ledProperties){
			ledData+=ledPlan(proerty,device);
		}
		String waterData="";
		for(DeviceProperty property:deviceProperties){
			waterData+=waterPlan(property);
		}
		String lenStr=String.format("%2s", Integer.toHexString(len)).replace(' ', '0');
		String response="90"+ lenStr.toUpperCase()
				+"00"+ledData+waterData;
		DataProtocol.sendDataToDevice(device.getDeviceId(), openId, response, "statusSync");
		
	}
	
	private static String waterPlan(DeviceProperty property) {
		// TODO Auto-generated method stub
		String days = "7F";
		String repeat=property.getRepetition();
		if (!repeat.equals("1234567")) {
			String num = repeat.substring(repeat.length() - 1);
			int len = Integer.parseInt(num);
			String dd = "";
			for (int i = 0; i < len; i++) {
				if (repeat.contains(String.valueOf(i + 1))) {
					dd += "1";
				} else {
					dd += "0";
				}
			}
			days = String.format("%2s", Integer.toHexString(Integer.parseInt(dd, 2))).replace(' ', '0');
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time=sdf.format(property.getTimePoint());
		int hour = Integer.parseInt(time.substring(0, 2));
		int minute = Integer.parseInt(time.substring(3, 5));
		int second = Integer.parseInt(time.substring(6));
		int delay = property.getActionCount();
		String data="A107"
				+String.format("%2s", Integer.toHexString(property.getTag())).replace(' ', '0')
				+days
				+ String.format("%2s", Integer.toHexString(hour)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(minute)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(second)).replace(' ', '0')
				+ String.format("%4s", Integer.toHexString(delay)).replace(' ', '0');
		return data;
	}
	
	
	private static String ledPlan(LedProperty property, Device device) {
		// TODO Auto-generated method stub
		String m = "01";
		//int intensity = Integer.parseInt(device.getLightIntensity());
		//int ct = Integer.parseInt(device.getColorTem());
		String startIntensity = String.format("%2s", Integer.toHexString(30)).replace(' ', '0');
		String endIntensity =  String.format("%2s", Integer.toHexString(30)).replace(' ', '0');
		//String ctval = String.format("%4s", Integer.toHexString(ct)).replace(' ', '0');
		String ctval=String.format("%4s", Integer.toHexString(3500)).replace(' ', '0');
		String adjust = "01";
		String days = "7F";
		if (property.getModel()==0) {
			m = "02";
			endIntensity = "00";
			if(property.getType()==0){
				startIntensity = String.format("%2s", Integer.toHexString(15)).replace(' ', '0');
				endIntensity =  String.format("%2s", Integer.toHexString(15)).replace(' ', '0');
			}
			adjust = "02";
			/*if (property.getType()==1) {
				endIntensity = "00";
				adjust = "02";
			}*/
		} else {
			m = "03";
			startIntensity = "00";
			adjust = "03";
			/*if (property.getType()==1) {
				startIntensity = "00";
				adjust = "03";
			}*/
		}
		String repeat=property.getRepeat_date();
		if (!repeat.equals("1234567")) {
			String num = repeat.substring(repeat.length() - 1);
			int len = Integer.parseInt(num);
			String dd = "";
			for (int i = 0; i < len; i++) {
				if (repeat.contains(String.valueOf(i + 1))) {
					dd += "1";
				} else {
					dd += "0";
				}
			}
			days = String.format("%2s", Integer.toHexString(Integer.parseInt(dd, 2))).replace(' ', '0');
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time=sdf.format(property.getTime());
		int hour = Integer.parseInt(time.substring(0, 2));
		int minute = Integer.parseInt(time.substring(3, 5));
		int second = Integer.parseInt(time.substring(6));
		int delay = Integer.parseInt(property.getDuration());
		String data = "A20E"
				+String.format("%2s", Integer.toHexString(property.getTag())).replace(' ', '0')
				+ m + startIntensity + ctval + endIntensity + ctval + adjust + days
				+ String.format("%2s", Integer.toHexString(hour)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(minute)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(second)).replace(' ', '0')
				+ String.format("%2s", Integer.toHexString(delay)).replace(' ', '0');
		return data;
	}
}
