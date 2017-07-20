package com.demo.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.accessToken.GetAccessToken;
import com.demo.dao.ApplyControlDao;
import com.demo.dao.DeviceDao;
import com.demo.dao.RelationDao;
import com.demo.model.ApplyControl;
import com.demo.model.Device;
import com.demo.model.Relation;
import com.demo.redis.RedisAPI;
import com.demo.service.RelationService;
import com.demo.util.DataProtocol;
import com.demo.util.GetToken;
import com.demo.util.HttpClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("RelationService")
public class RelationServiceImpl implements RelationService {

	private static Logger logger = Logger.getLogger(RelationServiceImpl.class);
	
	@Autowired
	private RelationDao relationDao;
	
	@Autowired
	private ApplyControlDao applyControlDao;
	
	@Autowired
	private DeviceDao deviceDao;

	@Override
	public JSONObject unbind(String deviceId, String userid) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_id", deviceId);
		jsonObject.put("openid", userid);
		String token = GetToken.getAccessToken();
		Relation relation = relationDao.getRelationByUserAndDevice(deviceId, userid);
		if (relation.getHost() == 0) {
			List<Relation> list = relationDao.getRelationByDevice(deviceId);
			JSONObject response = new JSONObject();
			for (Relation eachone : list) {
				String openid = eachone.getOpenId();
				JSONObject param = new JSONObject();
				param.put("device_id", deviceId);
				param.put("openid", openid);
				response = HttpClient.doPost("https://api.weixin.qq.com/device/compel_unbind?access_token=" + token, param);
				deleteRelation(openid, deviceId);
			}
			return response;
		} else {
			JSONObject response = HttpClient.doPost("https://api.weixin.qq.com/device/compel_unbind?access_token=" + token,
					jsonObject);
			deleteRelation(userid, deviceId);
			return response;
		}
		/*
		 * if(relation.getHost()==1 || relation.getHost()==2){ //delete Relation
		 * search=new Relation(); search.setDeviceId(deviceId);
		 * search.setOpenId(userid); relationDao.deleteRelation(search); } else{
		 * relationDao.deleteByDevice(deviceId); }
		 */
	}
	
	public void deleteRelation(String openid,String deviceid){
		Relation relation=new Relation();
		relation.setDeviceId(deviceid);
		relation.setOpenId(openid);
		relationDao.deleteRelation(relation);
	}

	@Override
	public Boolean applyAuth(String deviceId, String userid) {
		ApplyControl control = new ApplyControl();
		control.setApplyPersonId(userid);
		control.setDeviceId(deviceId);
		control.setTime(new Date());
		applyControlDao.insertApplyControl(control);
		Relation relation=relationDao.getRelationByDeviceHost(deviceId);
		sendNotifyToUser(relation.getOpenId(),relation.getDeviceNickName());
		return true;
	}
	
	public void sendNotifyToUser(String openId,String name){
		JSONObject content=new JSONObject();
		content.put("title", "您的好友请求控制您的设备:"+name);
		content.put("desription", "您的好友请求控制您的设备:"+name);
		content.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2c97180bd57e660f&redirect_uri=http%3a%2f%2fwww.lvjianit.com%2fWechatServer%2f&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
		content.put("picurl", "http://121.40.65.146/flower_photo/huapen.png");
		JSONArray jsonArray=new JSONArray();
		jsonArray.add(content);
		JSONObject articles=new JSONObject();
		articles.put("articles", jsonArray);
		String token=GetAccessToken.getAccessToken();
		DataProtocol.sendNewsToWechat(openId, articles, token, "control requst");
	}
	
	@Override
	public List<ApplyControl> getApplyControlByDevice(String deviceId){
		return applyControlDao.getApplyControlByDevice(deviceId);
	}
	
	@Override
	public Boolean handleDeviceApply(String deviceId, String userid, String result){
		relationDao.updateRelation(deviceId, userid,result);
		applyControlDao.updateApplyControl(deviceId, userid, result);
		return true;
	}
	
	@Override
	public int indexUserAuth(String deviceId, String userid){
		Relation relation=relationDao.getRelationByUserAndDevice(deviceId, userid);
		return relation.getHost();
	}

	@Override
	public List<Device> getDeviceListByFriends(String openid) {
		// TODO Auto-generated method stub
		List<Device> list=deviceDao.getDeviceListByFriends(openid);
		for(Device device:list){
			String deviceId=device.getDeviceId();
			String temp=RedisAPI.lindex(deviceId+"_tem",0);
			String hum=RedisAPI.lindex(deviceId+"_hum",0);
			device.setTemp(temp);
			device.setHumidity(hum);
		}
		return list;
	}

	@Override
	public List<ApplyControl> getApplyControlByUser(String openId) {
		// TODO Auto-generated method stub
		return applyControlDao.getApplyControlByUser(openId);
	}

	@Override
	public Boolean updateDeviceNickName(String deviceId, String openid, String nickName) {
		// TODO Auto-generated method stub
		relationDao.updateDeviceNickName(deviceId, openid, nickName);
		return true;
	}

	@Override
	public List<Relation> getRelationByDevice(String deviceId) {
		// TODO Auto-generated method stub
		return relationDao.getRelationByDevice(deviceId);
	}

	@Override
	public String getRelationByHost(String deviceId) {
		// TODO Auto-generated method stub
		return relationDao.getOpenIdByDeviceHost(deviceId);
	}
}
