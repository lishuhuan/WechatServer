package com.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.demo.model.Relation;

public interface RelationDao {
	
	List<Relation> getRelationByDevice(@Param("deviceId") String deviceId);
	
	List<Relation> getRelationByDeviceNotHost(@Param("deviceId") String deviceId);
	
	void insertRelation(Relation relation);
	
	void deleteRelation(Relation relation);
	
	void updateDeviceNickName(@Param("deviceId") String deviceId,@Param("openId") String openid,@Param("deviceNickName") String nickName);
	
	Relation getRelationByUserAndDevice(@Param("deviceId") String deviceId,@Param("openId") String openId);

/*	void deleteByDevice(@Param("deviceId") String deviceId);*/
	
	void updateRelation(@Param("deviceId") String deviceId,@Param("openId") String openId,@Param("host") String host);
	
	String getOpenIdByDeviceHost(@Param("deviceId") String deviceId);
	
	Relation getRelationByDeviceHost(@Param("deviceId") String deviceId);
	
	List<Relation> getFriendRelationByDevice(@Param("deviceId") String deviceId,@Param("openId") String openId);
}
