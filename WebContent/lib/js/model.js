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
		}, 'POST', function(data) {
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
	function setDeviceProperty(deviceId, openId, name, time, repetition, waterTime, cb) {
		ajax('/WechatServer/property/setDeviceProperty', {
			deviceId: deviceId,
			openId: openId,
			name: name,
			time: time,
			waterTime: waterTime,
			repetition: repetition
		}, 'POST', function(data) {
			cb(data);
		})
	}

	//编辑已经设置好的浇水时间
	function editDeviceProperty(id, time, repetition, waterTime, cb) {
		ajax('/WechatServer/property/editDeviceProperty', {
			id: id,
			time: time,
			repetition: repetition,
			waterTime: waterTime
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

	function setDeviceNickName(deviceId, name, openId, cb) {
		ajax('/WechatServer/relation/setDeviceNickName', {
			"deviceId": deviceId,
			"nickName": name,
			"openId": openId
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

	//获取当前设备浇花、施肥等状态
	function getDeviceStatus_status(deviceId, type, openId, startPage, cb) {
		ajax('/WechatServer/status/getDeviceStatus', {
			"deviceId": deviceId,
			"type": type,
			"openId": openId,
			"startPage": startPage
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取设备在线情况
	function getDeviceListStatus(openId, cb) {
		ajax('/WechatServer/user/getDeviceListStatus', {
			"openId": openId,
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取操作记录未读数量
	function operationUnread(openId, deviceId, cb) {
		ajax('/WechatServer/status/operationUnread', {
			"openId": openId,
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取最近的操作记录
	function getRecentOperationRecord(deviceId, cb) {
		ajax('/WechatServer/status/getRecentOperationRecord', {
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//删除浇水
	function deleteDeviceProperty(id, cb) {
		ajax('/WechatServer/property/deleteDeviceProperty', {
			"id": id
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取阳光能量值
	function getSunPower(id, cb) {
		ajax('/WechatServer/user/getSunPower', {
			"deviceId": id
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//采集阳光
	function collectPower(id, openid, cb) {
		ajax('/WechatServer/user/collectPower', {
			"deviceId": id,
			"openId": openid
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取分享给别的用户的二维码
	function qrcodeForShareApi(timestamp, deviceId, cb) {
		ajax('/WechatServer/user/qrcodeForShare', {
			"timestamp": timestamp,
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//查询是否首次使用，根据返回的数据是否为空来进行判断，为空则表示首次登陆
	function indexUser(openId, cb) {
		ajax('/WechatServer/user/indexUser', {
			"openId": openId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//设置灯光
	function sleepAndGetupLed(deviceId, openId, model, duration, time, type, repeat, cb) {
		ajax('/WechatServer/led/sleepAndGetupLed', {
			"deviceId": deviceId,
			"openId": openId,
			"model": model,
			"duration": duration,
			"time": time,
			"type": type,
			"repeat": repeat,
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//获取设置的灯光
	function getLedProperty(deviceId, cb) {
		ajax('/WechatServer/led/getLedProperty', {
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//删除设置的灯光
	function deleteLedProperty(propertyId, cb) {
		ajax('/WechatServer/led/deleteLedProperty', {
			"propertyId": propertyId
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//设置植物光照模式
	function sunPowerPlan(deviceId, isOpen, cb) {
		ajax('/WechatServer/led/sunPowerPlan', {
			"deviceId": deviceId,
			"isOpen": isOpen
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//更新光照强度或者色温
	function updatePowerAndCt(deviceId, type, power, cb) {
		ajax('/WechatServer/led/updatePowerAndCt', {
			"deviceId": deviceId,
			"type": type,
			"power": power
		}, 'POST', function(data) {
			cb(data);
		});
	}

	//修改灯光定时计划
	function updateLedPlan(planId, model, duration, time, type, repeat, cb) {
		ajax('/WechatServer/led/updateLedPlan', {
			"planId": planId,
			"model": model,
			"duration": duration,
			"time": time,
			"type": type,
			"repeat": repeat
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
		setDeviceNickName: setDeviceNickName,
		unbind: unbind,
		getDeviceStatus_status: getDeviceStatus_status,
		getDeviceListStatus: getDeviceListStatus,
		operationUnread: operationUnread,
		getRecentOperationRecord: getRecentOperationRecord,
		deleteDeviceProperty: deleteDeviceProperty,
		qrcodeForShareApi: qrcodeForShareApi,
		getSunPower: getSunPower,
		collectPower: collectPower,
		indexUser: indexUser,
		sleepAndGetupLed: sleepAndGetupLed,
		getLedProperty: getLedProperty,
		deleteLedProperty: deleteLedProperty,
		sunPowerPlan: sunPowerPlan,
		updatePowerAndCt: updatePowerAndCt,
		updateLedPlan: updateLedPlan
	}
});