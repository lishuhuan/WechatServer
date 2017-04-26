
var api = {
	//获取code
	getCode: function() {
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
	},
	//获取openid
	getOpenId: function() {
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
	},
	//微信sdk配置
	wxConfig: function(url, cb) {
		ajax('/WechatServer/user/getJsapiTicket', {
			url: url
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//控制设备
	controlDevice: function(openid, deviceid, body, cb) {
		if (deviceid == '10000'){
			cb({asyErrorCode: 0});
			return;
		}
		ajax('/WechatServer/device/setDevice', {
			openId: openid,
			deviceId: deviceid,
			action: body
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//查询设备状态
	deviceQuery: function(openid, deviceid, cb) {
		ajax('/WechatServer/device/getDeviceQuery', {
			openId: openid,
			deviceId: deviceid
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取该用户下的设备列表
	getDevice: function(openid, cb) {
		ajax('/WechatServer/user/getDeviceByUser', {
			openId: openid
		}, 'POST', function(data) {
			cb(data);
		});
	},
	getDeviceStatus: function(deviceid, cb) {
		ajax('/WechatServer/user/getDeviceStatus', {
			deviceId: deviceid
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//主人处理授权	1同意 2拒绝
	handleDeviceApply: function(openId, deviceId, result, cb) {
		ajax('/WechatServer/relation/handleDeviceApply', {
			openId: openId,
			deviceId: deviceId,
			result: result
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//主人获取请求授权列表
	getApplyControlByDevice: function(deviceId, cb) {
		ajax('/WechatServer/relation/getApplyControlByDevice', {
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//根据用户获取好友授权申请列表
	getApplyControlByUser: function(openId, cb) {
		ajax('/WechatServer/relation/getApplyControlByUser', {
			openId: openId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//申请授权
	applyAuth: function(openId, deviceId, cb) {
		ajax('/WechatServer/relation/applyAuth', {
			openId: openId,
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取好友列表
	getFriendList: function(openId, deviceId, cb) {
		ajax('/WechatServer/user/getFriendList', {
			openId: openId,
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取用户设置好的浇水时间段
	getDeviceProperty: function(deviceid, cb) {
		ajax('/WechatServer/property/getDeviceProperty', {
			deviceId: deviceid
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取温湿度
	getTempAndHumidity: function(deviceId, cb) {
		ajax('/WechatServer/device/getTempAndHumidity', {
			deviceId: deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//设置浇花时间段
	setDeviceProperty: function(deviceId, openId, name, time, repetition, waterTime, cb) {
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
	},
	//编辑已经设置好的浇水时间
	editDeviceProperty: function(id, time, repetition, waterTime, cb) {
		ajax('/WechatServer/property/editDeviceProperty', {
			id: id,
			time: time,
			repetition: repetition,
			waterTime: waterTime
		}, 'POST', function(data) {
			cb(data);
		})
	},
	//获取好友设备
	getDeviceListByFriends: function(openId, cb) {
		ajax('/WechatServer/relation/getDeviceListByFriends', {
			openId: openId
		}, 'POST', function(data) {
			cb(data);
		})
	},
	//更新设备名字
	updateDeviceName: function(deviceId, name, cb) {
		ajax('/WechatServer/device/updateDeviceName', {
			deviceId: deviceId,
			name: name
		}, 'POST', function(data) {
			cb(data);
		})
	},
	setDeviceNickName: function(deviceId, name, openId, cb) {
		ajax('/WechatServer/relation/setDeviceNickName', {
			"deviceId": deviceId,
			"nickName": name,
			"openId": openId
		}, 'POST', function(data) {
			cb(data);
		})
	},
	//解绑设备
	unbind: function(deviceId, openId, cb) {
		ajax('/WechatServer/relation/unbind', {
			"deviceId": deviceId,
			"openId": openId
		}, 'POST', function(data) {
			cb(data);
		})
	},
	//获取当前设备浇花、施肥等状态
	getDeviceStatus_status: function(deviceId, type, openId, startPage, cb) {
		ajax('/WechatServer/status/getDeviceStatus', {
			"deviceId": deviceId,
			"type": type,
			"openId": openId,
			"startPage": startPage
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取设备在线情况
	getDeviceListStatus: function(openId, cb) {
		ajax('/WechatServer/user/getDeviceListStatus', {
			"openId": openId,
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取操作记录未读数量
	operationUnread: function(openId, deviceId, cb) {
		ajax('/WechatServer/status/operationUnread', {
			"openId": openId,
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取最近的操作记录
	getRecentOperationRecord: function(deviceId, cb) {
		ajax('/WechatServer/status/getRecentOperationRecord', {
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//删除浇水
	deleteDeviceProperty: function(id, cb) {
		ajax('/WechatServer/property/deleteDeviceProperty', {
			"id": id
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取阳光能量值
	getSunPower: function(id, cb) {
		ajax('/WechatServer/user/getSunPower', {
			"deviceId": id
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//采集阳光
	collectPower: function(id, openid, cb) {
		ajax('/WechatServer/user/collectPower', {
			"deviceId": id,
			"openId": openid
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//获取分享给别的用户的二维码
	qrcodeForShareApi: function(timestamp, deviceId, cb) {
		ajax('/WechatServer/user/qrcodeForShare', {
			"timestamp": timestamp,
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//查询是否首次使用，根据返回的数据是否为空来进行判断，为空则表示首次登陆
	indexUser: function(openId, cb) {
		ajax('/WechatServer/user/indexUser', {
			"openId": openId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//设置灯光
	sleepAndGetupLed: function(deviceId, openId, model, duration, time, type, repeat, cb) {
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
	},
	//获取设置的灯光
	getLedProperty: function(deviceId, cb) {
		ajax('/WechatServer/led/getLedProperty', {
			"deviceId": deviceId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//删除设置的灯光
	deleteLedProperty: function(propertyId, cb) {
		ajax('/WechatServer/led/deleteLedProperty', {
			"propertyId": propertyId
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//设置植物光照模式
	sunPowerPlan: function(deviceId, isOpen, cb) {
		ajax('/WechatServer/led/sunPowerPlan', {
			"deviceId": deviceId,
			"isOpen": isOpen
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//更新光照强度或者色温
	updatePowerAndCt: function(deviceId, type, power, cb) {
		ajax('/WechatServer/led/updatePowerAndCt', {
			"deviceId": deviceId,
			"type": type,
			"power": power
		}, 'POST', function(data) {
			cb(data);
		});
	},
	//修改灯光定时计划
	updateLedPlan: function(planId, model, duration, time, type, repeat, cb) {
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
}


function ajax(url, body, method, cb) {
	var xmlhttp;
	if (window.XMLHttpRequest) {
		xmlhttp = new XMLHttpRequest();
	} else {
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	};
	
	// xmlhttp.open(method, "http://121.40.65.146" + url, true);
	xmlhttp.open(method, "http://sk.shuaikang.iego.cn" + url, true);
	// xmlhttp.open(method, "http://www.shuaikang.iego.cn" + url, true);
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
	// xmlhttp.open(method, "http://121.40.65.146" + url, false);
	xmlhttp.open(method, "http://sk.shuaikang.iego.cn" + url, false);
	// xmlhttp.open(method, "http://www.shuaikang.iego.cn" + url, false);
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

var _code = api.getCode();
var _openId = api.getOpenId();
// var _openId = "oql-ewdiG_jz5V6e-OWwe7nNwl78"; //mine
// var _openId = "oqL-ewWCbv2lOiHTOAxNDioQ4NMw";//wang
