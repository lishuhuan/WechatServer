define(["main"], function(Main) {

	function beforeEnter(query) {
		var mySwiper = Main.f7.swiper('.setting-swiper-container', {
			allowClick: false,
			shortSwipes: false,
			longSwipes: false,
			onSlideChangeStart: function(a) {
				vm.$set('setting_header.friend.background', a.activeIndex == '0' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.friend.color', a.activeIndex == '0' ? 'white' : 'black');
				vm.$set('setting_header.mine.background', a.activeIndex == '1' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.mine.color', a.activeIndex == '1' ? 'white' : 'black');
				if (vm.info.isMine) {
					vm.info.isMine = false;
					Main.Model.getDeviceStatus_status(query.deviceId, '1', Main.Model.getOpenId(), Number(vm.info.friendListNum), function(data) {
						for (var i in data.status) {
							if (data.status[i].action.indexOf("led_pwm") != -1) {
								data.status[i]["actionTitle"] = "开了灯";
							} else {
								data.status[i]["actionTitle"] = "洒了水";
							}
						}
						vm.$set('friendList', data.status);
						vm.info.allFriendList = data.count;
						vm.info.curFriendList = data.status.length;
						if (vm.info.curFriendList < vm.info.allFriendList) {
							Main.f7.attachInfiniteScroll(Main.$$('.infinite-scroll1'));
						}
					});
				}
			}
		});
		mySwiper.lockSwipes();
		mySwiper.detachEvents();
		var vm = new Vue({
			el: '#infoBook',
			data: {
				setting_header: {
					friend: {
						background: "#7AC8D5",
						color: "white"
					},
					mine: {
						background: "white",
						color: "balck"
					}
				},
				myList: [],
				friendList: [],
				info: {
					allMyList: 0,
					curMyList: 0,
					allFriendList: 0,
					curFriendList: 0,
					isMine: true,
					myListNum: 1,
					friendListNum: 1
				}
			},
			methods: {
				changeMode: function(key) {
					mySwiper.unlockSwipes();
					mySwiper.slideTo(Number(key), 400, true);
					mySwiper.lockSwipes();
				},
			}
		});
		Main.touch.on('.swiper-slide', 'swiperight', function(ev) {
			Main.mainView.router.back();
		});
		Main.Model.getDeviceStatus_status(query.deviceId, '0', Main.Model.getOpenId(), Number(vm.info.myListNum), function(data) {
			for (var i in data.status) {
				if (data.status[i].action.indexOf("led_pwm") != -1) {
					data.status[i]["actionTitle"] = "开了灯";
				} else {
					data.status[i]["actionTitle"] = "洒了水";
				}
				var str = data.status[i].time.replace(" ", "T");
				var time = new Date(Date.parse(str));
				var month = (time.getUTCMonth() + 1) < 10 ? "0" + (time.getUTCMonth() + 1) : (time.getUTCMonth() + 1);
				var day = time.getUTCDate() < 10 ? "0" + time.getUTCDate() : time.getUTCDate();
				data.status[i]["month"] = month;
				data.status[i]["day"] = day
			}
			vm.$set('myList', data.status);
			vm.info.allMyList = data.count;
			vm.info.curMyList = data.status.length;
			if (vm.info.curMyList < vm.info.allMyList) {
				Main.f7.attachInfiniteScroll(Main.$$('.infinite-scroll'));
			}
		});

		var loading = false;
		Main.f7.detachInfiniteScroll(Main.$$('.infinite-scroll'));
		Main.$$('.infinite-scroll-preloader').remove();
		Main.$$('.infinite-scroll').on('infinite', function() {
			if (loading) return;
			loading = true;
			if (vm.info.curMyList >= vm.info.allMyList) {
				Main.f7.detachInfiniteScroll(Main.$$('.infinite-scroll'));
				// Main.$$('.infinite-scroll-preloader').remove();
				return;
			};
			vm.info.myListNum = Number(vm.info.myListNum) + 1;
			Main.Model.getDeviceStatus_status(query.deviceId, '0', Main.Model.getOpenId(), Number(vm.info.myListNum), function(data) {
				loading = false;
				for (var i in data.status) {
					if (data.status[i].action.indexOf("led_pwm") != -1) {
						data.status[i]["actionTitle"] = "开了灯";
					} else {
						data.status[i]["actionTitle"] = "洒了水";
					}
					var str = data.status[i].time.replace(" ", "T");
					var time = new Date(Date.parse(str));
					var month = (time.getUTCMonth() + 1) < 10 ? "0" + (time.getUTCMonth() + 1) : (time.getUTCMonth() + 1);
					var day = time.getUTCDate() < 10 ? "0" + time.getUTCDate() : time.getUTCDate();
					data.status[i]["month"] = month;
					data.status[i]["day"] = day
				}
				vm.myList = vm.myList.concat(data.status);
				vm.$set('myList', vm.myList);
				vm.info.curMyList = vm.info.curMyList + data.status.length;
			});
		});
		var loading1 = false;
		Main.f7.detachInfiniteScroll(Main.$$('.infinite-scroll1'));
		Main.$$('.infinite-scroll-preloader1').remove();
		Main.$$('.infinite-scroll1').on('infinite', function() {
			if (loading1) return;
			loading1 = true;
			if (vm.info.curFriendList >= vm.info.allFriendList) {
				Main.f7.detachInfiniteScroll(Main.$$('.infinite-scroll1'));
				// Main.$$('.infinite-scroll-preloader1').remove();
				return;
			};
			vm.info.friendListNum = Number(vm.info.friendListNum) + 1;
			Main.Model.getDeviceStatus_status(query.deviceId, '1', Main.Model.getOpenId(), Number(vm.info.friendListNum), function(data) {
				loading1 = false;
				for (var i in data.status) {
					if (data.status[i].action.indexOf("led_pwm") != -1) {
						data.status[i]["actionTitle"] = "开了灯";
					} else {
						data.status[i]["actionTitle"] = "洒了水";
					}
				}
				vm.friendList = vm.friendList.concat(data.status);
				vm.$set('friendList', vm.friendList);
				vm.info.curFriendList = vm.info.curFriendList + data.status.length;
			});
		});
	};

	return {
		beforeEnter: beforeEnter
	};
});