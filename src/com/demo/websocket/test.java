package com.demo.websocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import com.demo.accessToken.GetAccessToken;
import com.demo.util.HttpClient;

import net.sf.json.JSONObject;

public class test {
	
	 public static long ipToLong(String strIp){
         long[] ip = new long[4];
         //先找到IP地址字符串中.的位置
         int position1 = strIp.indexOf(".");
         int position2 = strIp.indexOf(".", position1 + 1);
         int position3 = strIp.indexOf(".", position2 + 1);
         //将每个.之间的字符串转换成整型
         ip[0] = Long.parseLong(strIp.substring(0, position1));
         ip[1] = Long.parseLong(strIp.substring(position1+1, position2));
         ip[2] = Long.parseLong(strIp.substring(position2+1, position3));
         ip[3] = Long.parseLong(strIp.substring(position3+1));
         return (ip[0]) + (ip[1] << 8) + (ip[2] << 16) + (ip[3]<<24);
     }
	 public static String portToString(int port){
			return String.format("%4s", Integer.toHexString(port)).replace(' ', '0');
		 }
	public static void main(String[] args) throws IOException  {
		String lenStr="1a";
		System.out.println(lenStr.toUpperCase());
	}
	
	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
}
