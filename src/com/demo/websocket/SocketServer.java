package com.demo.websocket;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Controller;

import com.demo.model.ResponseData;
import com.demo.util.Ehcache;
import com.demo.util.GetToken;
import com.demo.util.HttpClient;
import com.demo.util.JsonUtil;

import net.sf.json.JSONObject;

@ServerEndpoint("/websocket2")
@Controller
public class SocketServer {

	// 闈欐�佸彉閲忥紝鐢ㄦ潵璁板綍褰撳墠鍦ㄧ嚎杩炴帴鏁般�傚簲璇ユ妸瀹冭璁℃垚绾跨▼瀹夊叏鐨勩��
	private static int onlineCount = 0;

	// concurrent鍖呯殑绾跨▼瀹夊叏Set锛岀敤鏉ュ瓨鏀炬瘡涓鎴风瀵瑰簲鐨凪yWebSocket瀵硅薄銆傝嫢瑕佸疄鐜版湇鍔＄涓庡崟涓�瀹㈡埛绔�氫俊鐨勮瘽锛屽彲浠ヤ娇鐢∕ap鏉ュ瓨鏀撅紝鍏朵腑Key鍙互涓虹敤鎴锋爣璇�
	private static CopyOnWriteArraySet<SocketServer> webSocketSet = new CopyOnWriteArraySet<SocketServer>();

	// 涓庢煇涓鎴风鐨勮繛鎺ヤ細璇濓紝闇�瑕侀�氳繃瀹冩潵缁欏鎴风鍙戦�佹暟鎹�
	private Session session;

	/**
	 * 杩炴帴寤虹珛鎴愬姛璋冪敤鐨勬柟娉�
	 * 
	 * @param session
	 *            鍙�夌殑鍙傛暟銆俿ession涓轰笌鏌愪釜瀹㈡埛绔殑杩炴帴浼氳瘽锛岄渶瑕侀�氳繃瀹冩潵缁欏鎴风鍙戦�佹暟鎹�
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		webSocketSet.add(this); // 鍔犲叆set涓�
		addOnlineCount(); // 鍦ㄧ嚎鏁板姞1
		System.out.println("鏈夋柊杩炴帴鍔犲叆锛佸綋鍓嶅湪绾夸汉鏁颁负" + getOnlineCount());
	}

	/**
	 * 杩炴帴鍏抽棴璋冪敤鐨勬柟娉�
	 */
	@OnClose
	public void onClose() {
		webSocketSet.remove(this); // 浠巗et涓垹闄�
		subOnlineCount(); // 鍦ㄧ嚎鏁板噺1
		System.out.println("鏈変竴杩炴帴鍏抽棴锛佸綋鍓嶅湪绾夸汉鏁颁负" + getOnlineCount());
	}

	/**
	 * 鏀跺埌瀹㈡埛绔秷鎭悗璋冪敤鐨勬柟娉�
	 * 
	 * @param message
	 *            瀹㈡埛绔彂閫佽繃鏉ョ殑娑堟伅
	 * @param session
	 *            鍙�夌殑鍙傛暟
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	@OnMessage
	public void onMessage(String json, Session session)
			throws InterruptedException, ExecutionException, TimeoutException, IOException {
		String deviceId = (String) JsonUtil.jsonTranslate(json, "deviceId");
		String userid = (String) JsonUtil.jsonTranslate(json, "openId");
		String data = (String) JsonUtil.jsonTranslate(json, "action");
		System.out.println("data" + data);
		if (null == deviceId || null == userid || null == data) {
			System.out.println("Lock params!");
			this.session.getBasicRemote().sendText("Lock params!");
			return;
		}
		data = paramConsume(data);
		if (null == data) {
			this.session.getBasicRemote().sendText("param false!");
			return;
		}

		String token = GetToken.getAccessToken();
		JSONObject jsonObject = paramToJson(token, deviceId, userid, data);
		JSONObject response = HttpClient.doPost(
				"https://api.weixin.qq.com/hardware/mydevice/platform/ctrl_device?access_token=" + token, jsonObject); // 鍙戦�佹帶鍒惰姹�
		if (response.get("error_code").equals(0)) {
			String msgId = response.getString("msg_id") + "_Set";
			paramResponse(msgId);

		} else {
			this.session.getBasicRemote().sendText((String) JSONObject.toBean(response));
			return;
		}

	}

	public void paramResponse(final String msgId) throws InterruptedException, ExecutionException, TimeoutException {
		Callable<JSONObject> callable = new Callable<JSONObject>() {
			public JSONObject call() throws Exception {
				JSONObject result = new JSONObject();
				while (true) {
					Ehcache ehCache = new Ehcache();
					ResponseData getobj = (ResponseData) ehCache.getCacheElement(msgId);
					if (null != getobj) {
						result = JSONObject.fromObject(getobj);
						// insertDeviceStatus(deviceId, userid, resultData);
						ehCache.deleteCache(msgId);
						break;
					}
				}
				return result;

			}
		};

		FutureTask<JSONObject> future = new FutureTask<JSONObject>(callable);
		new Thread(future).start();
		JSONObject result = future.get(10, TimeUnit.SECONDS);
		try {
			this.session.getBasicRemote().sendText((String) JSONObject.toBean(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String paramConsume(String data) {
		if (data.contains("watering")) {
			int second = Integer.parseInt(data.substring(data.indexOf("-") + 1));
			String to = String.format("%4s", Integer.toHexString(second)).replace(' ', '0');
			data = "0101" + to;
			return data;
		} else if (data.contains("led_pwm")) {
			int second = Integer.parseInt(data.substring(data.indexOf("-") + 1));
			String to = String.format("%2s", Integer.toHexString(second)).replace(' ', '0');
			data = "02" + to;
			return data;
		} else {
			return null;
		}
	}

	public JSONObject paramToJson(String token, String deviceId, String userid, String data) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("device_type", "gh_3f4fcd63df5d");
		jsonObject.put("device_id", deviceId);
		/*
		 * Map<String, String> map=new HashMap<>(); map.put("power_switch",
		 * "true");
		 */
		jsonObject.put("service", "");
		jsonObject.put("user", userid);

		jsonObject.put("data", data);
		return jsonObject;
	}

	/**
	 * 鍙戠敓閿欒鏃惰皟鐢�
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("鍙戠敓閿欒");
		error.printStackTrace();
	}

	/**
	 * 杩欎釜鏂规硶涓庝笂闈㈠嚑涓柟娉曚笉涓�鏍枫�傛病鏈夌敤娉ㄨВ锛屾槸鏍规嵁鑷繁闇�瑕佹坊鍔犵殑鏂规硶銆�
	 * 
	 * @param message
	 * @throws IOException
	 */
	/*
	 * public void sendMessage(String message) throws IOException{
	 * this.session.getBasicRemote().sendText(message);
	 * //this.session.getAsyncRemote().sendText(message); }
	 */

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		SocketServer.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		SocketServer.onlineCount--;
	}

}
