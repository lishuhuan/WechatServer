package com.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.demo.device.FutureMap;
import com.demo.device.SyncFuture;
import com.demo.model.ResponseData;
import com.demo.service.MessageAnalysisService;
import com.demo.util.SignUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/message")
public class MessageAnalysisController {
	
	private static final Logger logger = Logger.getLogger(MessageAnalysisController.class);
	@Autowired
	private MessageAnalysisService messageAnalysisService;

	@RequestMapping(value = { "/wechat" }, method = RequestMethod.GET)
	public void wechatGet(HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {
		// ΢�ż���ǩ��
		String signature = request.getParameter("signature");
		// ʱ���
		String timestamp = request.getParameter("timestamp");
		// �����
		String nonce = request.getParameter("nonce");
		// ����ַ���
		String echostr = request.getParameter("echostr");
		PrintWriter out = response.getWriter();
		// ͨ������signature���������У�飬��У��ɹ���ԭ������echostr����ʾ����ɹ����������ʧ��
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		out.close();
		out = null;
	}

	// @Cacheable(value="baseCache")
	@RequestMapping(value = { "/wechat" }, method = RequestMethod.POST)
	public void wechatPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		StringBuffer json = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		JSONObject jsonObject = JSONObject.fromObject(json.toString());
		logger.info(jsonObject);
		if (jsonObject.containsKey("msg_type")) {
			// ���뻺��
			if (jsonObject.get("msg_type").equals("get_resp")) {
				SyncFuture<Object> future=FutureMap.getFutureMap(jsonObject.getString("msg_id")+"_Get");
				if(future!=null){
					future.setResponse(jsonObject);
				}
			}
			if (jsonObject.get("msg_type").equals("set_resp")) {
				ResponseData result = messageAnalysisService.getData(jsonObject);
				System.out.println(result.getAsyErrorMsg()+result.getAsyErrorMsg());
				
				SyncFuture<Object> future=FutureMap.getFutureMap(result.getMsgId()+"_Set");
				if(future!=null){
					future.setResponse(result);
				}
			}
			if (jsonObject.get("msg_type").equals("bind") || jsonObject.get("msg_type").equals("unbind")) {
				messageAnalysisService.deviceBind(jsonObject);
			}
			if(jsonObject.get("msg_type").equals("notify")){
				messageAnalysisService.getMessageByNotify(jsonObject);
			}
		} else {
		}
		
	}

}
