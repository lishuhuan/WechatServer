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

		String signature = request.getParameter("signature");

		String timestamp = request.getParameter("timestamp");

		String nonce = request.getParameter("nonce");

		String echostr = request.getParameter("echostr");
		PrintWriter out = response.getWriter();

		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		out.close();
		out = null;
	}

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
			if (jsonObject.get("msg_type").equals("bind") || jsonObject.get("msg_type").equals("unbind")) {
				messageAnalysisService.deviceBind(jsonObject);
			}
		}
		
	}

}
