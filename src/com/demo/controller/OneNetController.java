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

import com.demo.service.OneNetService;
import com.demo.util.Util;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/onenet")
public class OneNetController {
	
	@Autowired
	private OneNetService oneNetService;
	
	private static String token="onenet1208";
	
	private static Logger logger=Logger.getLogger(OneNetController.class);
	
	@RequestMapping(value = { "/netserver" }, method = RequestMethod.GET)
	public void Get(HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {

		String msg = request.getParameter("msg");

		String nonce = request.getParameter("nonce");
	
		String signature = request.getParameter("signature");
	
		Util.BodyObj obj=new Util.BodyObj();
		obj.setMsg(msg);
		obj.setMsgSignature(signature);
		obj.setNonce(nonce);
		
	
		PrintWriter out = response.getWriter();

		if (Util.checkSignature(obj, token)) {
			out.print(msg);
		}
		out.close();
		out = null;
	}
	
	
	@RequestMapping(value = { "/netserver" }, method = RequestMethod.POST)
	public void Post(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		StringBuffer json = new StringBuffer();
		String line = null;
		BufferedReader reader =null;
		try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}		
			JSONObject jsonObject = JSONObject.fromObject(json.toString());
			logger.info(jsonObject);
			oneNetService.processJson(jsonObject);
		} catch (Exception e) {
			logger.info(e.toString());
		}
		finally {
			if(reader!=null){
				reader.close();
			}
		}
		
		
	}

}
