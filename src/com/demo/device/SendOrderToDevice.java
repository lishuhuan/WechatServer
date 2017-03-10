package com.demo.device;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.log4j.Logger;

import com.demo.accessToken.GetAccessToken;
import com.demo.util.DataProtocol;
import com.demo.util.HttpClient;

import net.sf.json.JSONObject;

public class SendOrderToDevice {

	private static final String FILE_ROOT = "/home/cloud/TftpServer/";
	
	private static final String IP = "121.40.65.146";
	
	private static final int port = 6969;

	private static final Logger logger = Logger.getLogger(SendOrderToDevice.class);

	public static void sendUpgradeToDevice(String deviceId, String userid, String version) {
		String ipAndPort = ipConvertToString(IP, port);
		String path = FILE_ROOT + "upgrade_" + version + ".zip";
		File file = new File(path);
		long size = file.length();
		String md5=getFileMD5(file);
		String data = "921B00" + ipAndPort
				+String.format("%8s", Long.toHexString(size)).replace(' ', '0')+md5;
		DataProtocol.sendDataToDevice(deviceId, userid, data, "Upgrade");
	}

	public static String ipConvertToString(String ip, int port) {
		long ip4 = ipToLong(ip);
		String ipAdd = String.format("%8s", Long.toHexString(ip4)).replace(' ', '0');
		String p = portToString(port);
		return ipAdd + p;
	}

	public static long ipToLong(String strIp) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0]) + (ip[1] << 8) + (ip[2] << 16) + (ip[3] << 24);
	}

	public static String portToString(int port) {
		return String.format("%4s", Integer.toHexString(port)).replace(' ', '0');
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
