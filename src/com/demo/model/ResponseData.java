package com.demo.model;

import java.io.Serializable;

public class ResponseData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3227858550961672134L;

	public String asyErrorCode;
	
	public String asyErrorMsg;
	
	public String msgId;
	
	public String msgType;
	
	public String services;
	
	public String data;
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}

	public String getAsyErrorCode() {
		return asyErrorCode;
	}

	public void setAsyErrorCode(String asyErrorCode) {
		this.asyErrorCode = asyErrorCode;
	}

	public String getAsyErrorMsg() {
		return asyErrorMsg;
	}

	public void setAsyErrorMsg(String asyErrorMsg) {
		this.asyErrorMsg = asyErrorMsg;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}
	
	
	
	

}
