package com.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.demo.model.User;

public interface UserBindingDao {
     public List<User> getUserList();
     
     public List<User> getFriendList(@Param("id") String id,@Param("deviceId") String deviceId);
     
     public void addPoint(User user);
     
     public User getUserById(@Param("openId") String openId);
     
     void insertUser(User user);
     
     void updateUser(User user);
     
}
