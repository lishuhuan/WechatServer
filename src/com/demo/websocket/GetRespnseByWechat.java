package com.demo.websocket;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.json.JSONObject;

public class GetRespnseByWechat extends Thread {

	CountDownLatch latch;
	String msgId;
	public GetRespnseByWechat(String msgId,CountDownLatch latch) {
		// TODO Auto-generated constructor stub
		this.latch=latch;
		this.msgId=msgId;
	}
	@Override
	public void run(){
		FutureTask<JSONObject> task=new FutureTask<JSONObject>(new Callable<JSONObject>() {
			public JSONObject call(){
				return null;
				
			}
		});
		
		try {
			JSONObject result=task.get(10, TimeUnit.SECONDS);
			latch.countDown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
