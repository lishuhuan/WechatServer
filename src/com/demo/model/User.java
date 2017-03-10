package com.demo.model;

import java.util.Date;

public class User {
	
	private String id;
	
	private String openId;
	
	private String nickName;
	
	private Date createTime;
	
	private int memberPoint;
	
	private String headimgurl;
	
	public String isControl;
	
	public String getIsControl() {
		return isControl;
	}
	
	public void setIsControl(String isControl) {
		this.isControl = isControl;
	}
	
	public String getHeadimgurl() {
		return headimgurl;
	}
	
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	
	public void setMemberPoint(int memberPoint) {
		this.memberPoint = memberPoint;
	}
	
	public int getMemberPoint() {
		return memberPoint;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
