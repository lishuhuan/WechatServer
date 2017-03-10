define(["main"], function(Main) {

	var mySearchbar, mySwiper;

	function beforeEnter() {
		mySearchbar = Main.f7.searchbar('#searchTest', {
			searchList: '.list-block-search',
			searchIn: '.title',
			onSearch: function(s) {},
			onEnable: function(s) {
				vue.display.devicesearch = false;
			},
			onDisable: function(s) {
				vue.display.devicesearch = true;
			},
			onClear: function(s) {}
		});
		mySwiper = Main.f7.swiper('.device-swiper-container', {
			onSlideChangeStart: function(a) {
				if (a.activeIndex == '1') {
					Main.Model.getApplyControlByUser(Main.Model.getOpenId(), function(data) {
						vue.$set("userList", data);
					});
				}
				vue.$set('device_header.device.background', a.activeIndex == '0' ? '#7AC8D5' : 'white');
				vue.$set('device_header.device.color', a.activeIndex == '0' ? 'white' : 'black');
				vue.$set('device_header.friend.background', a.activeIndex == '1' ? '#7AC8D5' : 'white');
				vue.$set('device_header.friend.color', a.activeIndex == '1' ? 'white' : 'black');
			}
		});
		mySwiper.lockSwipes();
		mySwiper.detachEvents();
		settimeout();
	};

	function settimeout() {
		if (vue.ready) {
			init();
		} else {
			setTimeout(settimeout, 1000);
		}
	}

	function init() {
		Main.$$('.miniPermissions').on('click', function(data) {
			Main.f7.confirm('要同意授权吗？', function() {
				Main.Model.handleDeviceApply(Main.Model.getOpenId(), vue.select.permissions.item.deviceId, function(data) {
					if (data) {
						Main.Model.getApplyControlByUser(Main.Model.getOpenId(), function(data) {
							vm.$set("userList", data);
						});
					}
				});
			});
		});
		Main.$$('.apply').on('click', function(data) {
			Main.Model.applyAuth(Main.Model.getOpenId(), vue.select.apply.item.deviceId, function(data) {
				if (data) {
					Main.f7.alert("已经发送申请");
				} else {
					Main.f7.alert("发送申请失败");
				}
			});
		});
		Main.$$('.changeModel').on('click', function(data) {
			mySwiper.unlockSwipes();
			mySwiper.slideTo(Number(vue.select.changeModel.key), 400, true);
			mySwiper.lockSwipes();
		});
		
		Main.touch.on('.device_item', 'hold', function(e) {
			stopBubble(e);
			Main.f7.confirm('要解绑设备吗？', function() {
				Main.Model.unbind(vue.select.device.item.deviceId, Main.Model.getOpenId(), function(data) {
					if (data.base_resp.errcode == 0) {
						if (vue.select.device.bool) {
							Main.Model.getDevice(Main.Model.getOpenId(), function(device) {
								var array = [];
								for (var i in device) {
									if (device[i].isHost == "0") {
										array.push(device[i]);
									}
								}
								vue.$set('device_arr', array);
								setTimeout(function() {
									init();
								}, 1000);
							});
						} else {
							Main.Model.getDeviceListByFriends(Main.Model.getOpenId(), function(device) {
								vue.$set('device_arr_friend', device);
								setTimeout(function() {
									init();
								}, 1000);
							});
						}
					} else {
						Main.f7.alert("解绑失败，错误原因： " + data.base_resp.errmsg);
					}
				})
			});
			
		});
		Main.touch.on('.device_item', 'tap', function(e) {
			if (vue.select.device.item) {
				Main.mainView.router.load({
					url: "templates/device/device.html",
					query: vue.select.device.item
				});
			}
		});
		Main.$$('.share').on('click', function(data) {
			vue.display.customTime = true;
			var time = String(new Date().getTime());
			// vue.display.qrUrl = "http://www.shuaikang.iego.cn/WechatServer/user/qrcodeForShare/" + vue.select.share.item.deviceId + "/" + time;
			Main.Model.qrcodeForShareApi(time, vue.select.share.item.deviceId, function(data) {
				if (data.result_code == 0) {
					document.getElementById("qrCode").innerHTML = "";
					jQuery('#qrCode').qrcode(data.data);
					Main.share(vue.select.share.item.deviceId, time);
				}
			});
		});
		Main.$$('.edit').on('click', function(data) {
			Main.f7.prompt('请输入需要修改的名字', function(value) {
				Main.Model.setDeviceNickName(vue.select.edit.item.deviceId, value, Main.Model.getOpenId(), function(data) {
					if (data) {
						if (vue.select.edit.bool) {
							Vue.set(vue.device_arr[vue.select.edit.index], "nickName", value);
						} else {
							Vue.set(vue.device_arr_friend[vue.select.edit.index], "nickName", value);
						}
					}
				});
			});
		});
		Main.$$('.selectRepeatTime').on('click', function(data) {
			vue.display.customTime = false;
			vue.display.qrUrl = "";
		});
	};

	function stopBubble(e) {
		if (e && e.stopPropagation) {
			e.stopPropagation();
		} else {
			window.event.cancelBubble = true;
		}
	};

	function animationEnter(query) {

	};

	return {
		beforeEnter: beforeEnter,
		animationEnter: animationEnter
	};
});