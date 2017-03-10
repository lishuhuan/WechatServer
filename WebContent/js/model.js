define(function() {
	var localOpenId = "oqL-ewXqKopyC1d18BTaRZTrl5vY";

	function GetRequest() {
		var url = location.search; 
		var theRequest = new Object();
		if (url.indexOf("?") != -1) {
			var str = url.substr(1);
			strs = str.split("&");
			for (var i = 0; i < strs.length; i++) {
				theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
			}
		}
		return theRequest;
	};

	function getOpenId(_code, cb) {
		if (localOpenId != undefined) {
			cb(localOpenId);
		} else {
			ajax('/WechatServer/user/getOpenIdByCode', {
				code: _code
			}, 'POST', function(data) {
				cb(data.openid);
			});
		}
	}

	function controlDevice(openid, deviceid, body, cb) {
		ajax('/WechatServer/device/setDevice', {
			openId: openid,
			deviceId: deviceid,
			action: body
		}, 'POST', function(data) {
			cb(data);
		});
	}

	function deviceQuery(openid, deviceid, cb) {
		ajax('/WechatServer/device/getDeviceQuery', {
			openId: openid,
			deviceId: deviceid
		}, 'POST', function(data) {
			cb(data);
		});
	}

	function getDevice(openid, cb) {
		ajax('/WechatServer/user/getDeviceByUser', {
			openId: openid
		}, 'POST', function(data) {
			cb(data);
		});
	}

	function getDeviceStatus(deviceid, cb) {
		ajax('/WechatServer/user/getDeviceStatus', {
			deviceId: deviceid
		}, 'POST', function(data) {
			cb(data);
		});
	}

	function ajax(url, body, method, cb) {
		var xmlhttp;
		if (window.XMLHttpRequest) {
			xmlhttp = new XMLHttpRequest();
		} else {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		};
		xmlhttp.open(method, "http://www.shuaikang.iego.cn" + url, true);
		if (method == "POST") {
			xmlhttp.setRequestHeader("Content-type", "application/json");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				cb(JSON.parse(xmlhttp.responseText));
			}
		}
		xmlhttp.send(JSON.stringify(body));
	}

	return {
		getRequest: GetRequest,
		getOpenId: getOpenId,
		controlDevice: controlDevice,
		deviceQuery: deviceQuery,
		getDevice: getDevice,
		getDeviceStatus: getDeviceStatus
	}
});