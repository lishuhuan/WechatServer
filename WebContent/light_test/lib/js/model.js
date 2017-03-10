define(function() {


	// var _code = getCode();
	// var _openId = undefined;
	// var _openId = "oqL-ewa5iNHK6wR93qNnnILkDl4g";
	// var _openId = "oqL-ewXqKopyC1d18BTaRZTrl5vY";
	//获取code
	function getCode() {
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

	//获取openid
	function getOpenId() {
		if (_openId != undefined) {
			return _openId;
		} else {
			_openId = ajaxSync('/WechatServer/user/getOpenIdByCode', {
				code: {
					code: _code.code
				}
			}, 'POST');
			return _openId;
		}
	}

	//微信sdk配置
	function wxConfig(url, cb) {
		ajax('/WechatServer/user/getJsapiTicket', {
			url: url
		}, 'POST', function(data) {
			cb(data);
		});
	}

	function share(deviceId) {
		wx.onMenuShareTimeline({
			title: '我的植物灯',
			link: 'http://www.shuaikang.iego.cn/share?' + deviceId,
			imgUrl: 'http://121.40.65.146/flower_photo/huapen.png',
			success: function() {
				console.log("timeline success");
			},
			cancel: function() {
				console.log("timeline cancel");
			}
		});
		wx.onMenuShareAppMessage({
			title: '我的植物灯',
			desc: '这是我的植物灯，你也一起来吗？',
			link: 'http://www.shuaikang.iego.cn/share?' + deviceId,
			imgUrl: 'http://121.40.65.146/flower_photo/huapen.png',
			type: 'link',
			success: function() {
				console.log("appmessage success");
			},
			cancel: function() {
				console.log("appmessage cancel");
			}
		});
	}

	//控制设备
	function controlDevice(openid, deviceid, body, cb) {
		ajax('/WechatServer/device/setDevice', {
			openId: openid,
			deviceId: deviceid,
			action: body
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//查询设备状态
	function deviceQuery(openid, deviceid, cb) {
		ajax('/WechatServer/device/getDeviceQuery', {
			openId: openid,
			deviceId: deviceid
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取该用户下的设备列表
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

	//主人处理授权	1同意 2拒绝
	function handleDeviceApply(openId, deviceId, result, cb) {
		ajax('/WechatServer/relation/handleDeviceApply', {
			openId: openId,
			deviceId: deviceId,
			result: result
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//主人获取请求授权列表
	function getApplyControlByDevice(deviceId, cb) {
		ajax('/WechatServer/relation/getApplyControlByDevice', {
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//根据用户获取好友授权申请列表
	function getApplyControlByUser(openId, cb) {
		ajax('/WechatServer/relation/getApplyControlByUser', {
			openId: openId
		}, 'POST', function(data){
			cb(data);
		});
	}

	//申请授权
	function applyAuth(openId, deviceId, cb) {
		ajax('/WechatServer/relation/applyAuth', {
			openId: openId,
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取好友列表
	function getFriendList(openId, deviceId, cb) {
		ajax('/WechatServer/user/getFriendList', {
			openId: openId,
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取用户设置好的浇水时间段
	function getDeviceProperty(deviceid, cb) {
		ajax('/WechatServer/property/getDeviceProperty', {
			deviceId: deviceid
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取温湿度
	function getTempAndHumidity(deviceId, cb) {
		ajax('/WechatServer/device/getTempAndHumidity', {
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//设置浇花时间段
	function setDeviceProperty(deviceId, openId, name, time, repetition, cb) {
		ajax('/WechatServer/property/setDeviceProperty', {
			deviceId: deviceId,
			openId: openId,
			name: name,
			time: time,
			repetition: repetition
		}, 'POST', function(data) {
			cb(data);
		})
	}

	//编辑已经设置好的浇水时间
	function editDeviceProperty(id, time, repetition, cb) {
		ajax('/WechatServer/property/editDeviceProperty', {
			id: id,
			time: time,
			repetition: repetition
		}, 'POST', function(data) {
			cb(data);
		})
	}

	//获取好友设备
	function getDeviceListByFriends(openId, cb) {
		ajax('/WechatServer/relation/getDeviceListByFriends', {
			openId: openId
		}, 'POST', function(data) {
			cb(data);
		})
	}

	//更新设备名字
	function updateDeviceName(deviceId, name, cb) {
		ajax('/WechatServer/device/updateDeviceName', {
			deviceId: deviceId,
			name: name
		}, 'POST', function(data) {
			cb(data);
		})
	}

	//解绑设备
	function unbind(deviceId, openId, cb) {
		ajax('/WechatServer/relation/unbind', {
			"deviceId": deviceId,
			"openId": openId
		}, 'POST', function(data) {
			cb(data);
		})
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
				console.log(xmlhttp.responseText);
				cb(JSON.parse(xmlhttp.responseText));
			}
		}
		console.log("body:------:" + JSON.stringify(body));
		xmlhttp.send(JSON.stringify(body));
	}

	function ajaxSync(url, body, method) {
		var xmlhttp;
		if (window.XMLHttpRequest) {
			xmlhttp = new XMLHttpRequest();
		} else {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		};
		xmlhttp.open(method, "http://www.shuaikang.iego.cn" + url, false);
		if (method == "POST") {
			xmlhttp.setRequestHeader("Content-type", "application/json");
		}
		xmlhttp.send(JSON.stringify(body));
		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				var data = JSON.parse(xmlhttp.responseText);
				return data.openid;
			} else {
				return undefined;
			}
		}
	}

	return {
		getOpenId: getOpenId,
		wxConfig: wxConfig,
		controlDevice: controlDevice,
		deviceQuery: deviceQuery,
		getDevice: getDevice,
		getDeviceStatus: getDeviceStatus,
		handleDeviceApply: handleDeviceApply,
		getApplyControlByDevice: getApplyControlByDevice,
		getApplyControlByUser: getApplyControlByUser,
		applyAuth: applyAuth,
		getFriendList: getFriendList,
		getDeviceProperty: getDeviceProperty,
		getTempAndHumidity: getTempAndHumidity,
		setDeviceProperty: setDeviceProperty,
		editDeviceProperty: editDeviceProperty,
		getDeviceListByFriends: getDeviceListByFriends,
		updateDeviceName: updateDeviceName,
		unbind: unbind
	}
});