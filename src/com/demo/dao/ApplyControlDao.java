package com.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.demo.model.ApplyControl;

public interface ApplyControlDao {

	void insertApplyControl(ApplyControl applyControl);
	
	List<ApplyControl> getApplyControlByDevice(@Param("deviceId") String deviceId);
	
	void updateApplyControl(@Param("deviceId") String deviceId,@Param("applyPersonId") String openId,@Param("result") String result);
	
	List<ApplyControl> getApplyControlByUser(@Param("openId") String openId);
}
