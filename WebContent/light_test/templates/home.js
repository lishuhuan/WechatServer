define(["main"], function(Main) {

	function beforeEnter() {
		setTimeout(function() {
			Main.f7.accordionOpen(document.getElementById('device_item'));
		}, 300);
		setTimeout(function() {
			Main.$$('.launch').transition(500);
			Main.$$('.launch').transform('scale(0,0)');
		}, 100);
		var mySearchbar = Main.f7.searchbar('#searchTest', {
			searchList: '.list-block-search',
			searchIn: '.title',
			onSearch: function(s) {},
			onEnable: function(s) {
				vm.display.devicesearch = false;
			},
			onDisable: function(s) {
				vm.display.devicesearch = true;
			},
			onClear: function(s) {}
		});
		var mySwiper = Main.f7.swiper('.device-swiper-container', {
			onSlideChangeStart: function(a) {
				if (a.activeIndex == '1') {
					Main.Model.getApplyControlByUser(Main.Model.getOpenId(), function(data) {
						vm.$set("userList", data);
					});
				}
				vm.$set('device_header.device.background', a.activeIndex == '0' ? '#7AC8D5' : 'white');
				vm.$set('device_header.device.color', a.activeIndex == '0' ? 'white' : 'black');
				vm.$set('device_header.friend.background', a.activeIndex == '1' ? '#7AC8D5' : 'white');
				vm.$set('device_header.friend.color', a.activeIndex == '1' ? 'white' : 'black');
			}
		});
		mySwiper.lockSwipes();
		mySwiper.detachEvents();


		// 	getPermissions: function(item) {
		// 		Main.f7.confirm('要同意授权吗？', function() {
		// 			Main.Model.handleDeviceApply(Main.Model.getOpenId(), item.deviceId, function(data) {
		// 				if (data) {
		// 					Main.Model.getApplyControlByUser(Main.Model.getOpenId(), function(data) {
		// 						vm.$set("userList", data);
		// 					});
		// 				}
		// 			});
		// 		});
		// 	}
		Main.$$('.changeModel').on('click', function(data){
			mySwiper.unlockSwipes();
			mySwiper.slideTo(Number(vue.select.changeModel.key), 400, true);
			mySwiper.lockSwipes();
		});
		Main.$$('.delect').on('click', function(data) {
			Main.f7.confirm('要解绑设备吗？', function() {
				Main.Model.unbind(vue.select.device.item.deviceId, Main.Model.getOpenId(), function(data) {
					if (data.base_resp.errcode == 0) {
						if (vue.select.device.bool) {
							Main.Model.getDevice(Main.Model.getOpenId(), function(device) {
								vue.$set('device_arr', device);
							});
						} else {
							Main.Model.getDeviceListByFriends(Main.Model.getOpenId(), function(data) {
								vue.$set('device_arr_friend', device);
							});
						}
					} else {
						Main.f7.alert("解绑失败，错误原因： " + data.base_resp.errmsg);
					}
				})
			});
		});
		Main.$$('.device_item').on('click', function(data) {
			if (vue.select.device.item) {
				Main.mainView.router.load({
					url: "templates/device/device.html",
					query: vue.select.device.item
				});
			}
		});
		Main.$$('.share').on('click', function(data) {
			vue.display.customTime = true;
			vue.display.qrUrl = "http://www.shuaikang.iego.cn/WechatServer/user/qrcodeForShare/" + vue.select.share.item.deviceId;
			Main.share(vue.select.share.item.deviceId);
		});
		Main.$$('.edit').on('click', function(data) {
			Main.f7.prompt('请输入需要修改的名字', function(value) {
				Main.Model.updateDeviceName(vue.select.edit.item.deviceId, value, function(data) {
					if (data) {
						if (vue.select.edit.bool) {
							Main.Model.getDevice(Main.Model.getOpenId(), function(device) {
								vue.$set('device_arr', device);
							});
						} else {
							Main.Model.getDeviceListByFriends(Main.Model.getOpenId(), function(data) {
								vue.$set('device_arr_friend', device);
							});
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

	function animationEnter(query) {

	};

	return {
		beforeEnter: beforeEnter,
		animationEnter: animationEnter
	};
});