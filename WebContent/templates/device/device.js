define(["main"], function(Main) {

	function beforeEnter(query) {
		var now = (new Date()).getHours();
		var vm = new Vue({
			el: '#device',
			data: {
				theme: {
					bg: now < 18 ? "./ui/home/bj.png" : "./ui/home/w_bj.png",
					sun: now < 18 ? "ui/home/yg.png" : "ui/home/w_yg.png",
					sh: now < 18 ? "ui/home/sh.png" : "ui/home/w_sh.png",
					gz: now < 18 ? "ui/home/gz.png" : "ui/home/w_gz.png",
					zz: now < 18 ? "ui/home/zz.png" : "ui/home/w_zz.png",
					light: now < 18 ? "ui/home/gd.png" : "ui/home/w_gd.png",
					light_openImg: now < 18 ? "ui/home/g.png" : "ui/home/g.png"
				},
				sun: {
					number: 0,
					data: []
				},
				isHost: query.isHost,
				temperature: "",
				humidity: "",
				currentDevice: query == "" ? undefined : query,
				light_open: false,
				lightChange_open: false,
				input_light: 0,
				light: {
					img: "ui/home/gd.png",
					openImg: "ui/home/g.png"
				},
				count: 0,
				userInfo: {
					headImg: "",
					nickName: "",
					sun: 0,
					widthStyle: "width: calc(0% - 2px);"
				}
			},
			methods: {
				moveLight: function() {
					if (!this.light_open) {
						Main.f7.alert("请先开灯");
					} else {
						if (this.lightChange_open) {
							this.lightChange_open = false;
							Main.$$('.changeLight').transition(200);
							Main.$$('.changeLight').transform('translate3d(0px, 0, 0)');
						} else {
							this.lightChange_open = true;
							Main.$$('.changeLight').transition(200);
							Main.$$('.changeLight').transform('translate3d(200px, 0, 0)');
						}
					}
				},
				toolBarClick: function(key) {
					if (key == "2") {
						Main.mainView.router.back();
					} else {
						var url = "";
						if (key == "0") {
							url = "templates/setting/setting.html";
						} else if (key == "1") {
							this.count = 0;
							url = "templates/infoBook/infoBook.html";
						} else if (key == "2") {

						} else if (key == "3") {
							url = "templates/friend/friend.html";
						};
						Main.mainView.router.load({
							url: url,
							query: vm.currentDevice
						});
					}
				},
				open: function(sync) {
					var body;
					if (this.light_open) {
						body = 0;
						this.light_open = false;
						this.theme.light = now < 18 ? "ui/home/gd.png" : "ui/home/w_gd.png";
						this.theme.light_openImg = "ui/home/g.png";
					} else {
						body = this.input_light;
						this.light_open = true;
						this.theme.light = now < 18 ? "ui/home/kd.png" : "ui/home/w_kd.png";
						this.theme.light_openImg = "ui/home/k.png";
					}
					if (sync == undefined || sync == false) {
						if (!this.light_open) {
							if (this.lightChange_open) {
								this.lightChange_open = false;
								Main.$$('.changeLight').transition(200);
								Main.$$('.changeLight').transform('translate3d(0px, 0, 0)');
							}
						}
						Main.Model.controlDevice(Main.Model.getOpenId(), this.currentDevice.deviceId, 'led_pwm-' + body, function(data) {
							if (data.asyErrorCode == 0) {

							} else if (data.asyErrorCode == 1) {
								Main.f7.alert("没有控制权限");
								vm.open(true);
							} else {
								Main.f7.alert("设备离线");
								vm.open(true);
							}
						});
					}
				},
				collectSun: function() {
					Main.Model.collectPower(query.deviceId, Main.Model.getOpenId(), function(data) {
						if (data.result_code == 0) {
							Main.$$('.sunImg').toggleClass("end");
							setTimeout(function() {
								vm.$set("sun.number", 0);
							}, 2000);
							Main.Model.indexUser(Main.Model.getOpenId(), function(data) {
								if (data) {
									vm.$set("userInfo.headImg", data.headimgurl ? data.headimgurl : "");
									vm.$set("userInfo.nickName", data.nickName ? data.nickName : "");
									vm.$set("userInfo.sun", data.memberPoint ? data.memberPoint : 0);
									if (data.memberPoint > 100) {
										vm.$set("userInfo.widthStyle", "width: calc(100% - 2px);");
									} else {
										vm.$set("userInfo.widthStyle", data.memberPoint ? "width: calc(" + data.memberPoint + "% - 2px);" : "width: calc(0% - 2px);");
									}
								}
							});
						}
					});
				}
			}
		});
		Main.Model.indexUser(Main.Model.getOpenId(), function(data) {
			if (data) {
				vm.$set("userInfo.headImg", data.headimgurl ? data.headimgurl : "");
				vm.$set("userInfo.nickName", data.nickName ? data.nickName : "");
				vm.$set("userInfo.sun", data.memberPoint ? data.memberPoint : 0);
				if (data.memberPoint > 100) {
					vm.$set("userInfo.widthStyle", "width: calc(100% - 2px);");
				} else {
					vm.$set("userInfo.widthStyle", data.memberPoint ? "width: calc(" + data.memberPoint + "% - 2px);" : "width: calc(0% - 2px);");
				}

			}
			Main.Model.getTempAndHumidity(query.deviceId, function(data) {
				vm.$set('humidity', data.humidity);
				vm.$set('temperature', data.temperature);
				Main.Model.getRecentOperationRecord(query.deviceId, function(data) {
					var open = (data[1].action).substr(8, data[1].action.length - 7);
					if (open == '0') {
						vm.$set('input_light', open);
					} else {
						vm.$set('input_light', open);
						vm.light_open = false;
						vm.open(true);
					}
					Main.Model.operationUnread(Main.Model.getOpenId(), query.deviceId, function(data) {
						var number = Number(data.my) + Number(data.friends);
						vm.$set('count', number > 99 ? "99+" : number);
						Main.Model.getSunPower(query.deviceId, function(data) {
							vm.$set("sun.number", data.result_code == 0 ? data.data.length : 0);
							vm.$set("sun.data", data.result_code == 0 ? data.data : []);
						});
					});
				});
			});
		});

		Main.touch.on('#input_light', 'dragstart', function(e) {
			console.log('input 拖动开始');
		});
		Main.touch.on('#input_light', 'dragend', function(e) {
			Main.Model.controlDevice(Main.Model.getOpenId(), vm.currentDevice.deviceId, 'led_pwm-' + vm.input_light, function(data) {
				if (data.asyErrorCode == 0) {
					vm.light_open = vm.input_light != 0 ? true : false;
				} else if (data.asyErrorCode == 1) {
					Main.f7.alert("没有控制权限");
				} else {
					Main.f7.alert("设备离线");
				}
			});
		});

		var screenWidth = Main.$$('.page').width();
		var screenHeight = Main.$$('.page').height();
		var left_0 = screenWidth - 0.5 * Main.$$('.kettle').width();
		var top_0 = screenHeight - 0.5 * Main.$$('.kettle').height() - 0.2 * screenHeight;
		var height = 0.2 * screenHeight + Main.$$('.flower').height() - 0.5 * Main.$$('.kettle').height()
		var point = {
			left_top: {
				x: -left_0,
				y: -top_0 + height
			},
			right_bottom: {
				x: -left_0 + Main.$$('.flower').width(),
				y: -top_0 + height + Main.$$('.flower').height()
			},
			width: Main.$$('.flower').width(),
			height: Main.$$('.flower').height(),
			ok: {
				x: {
					min: -left_0 + Main.$$('.flower').width() * (6 / 17.67),
					max: -left_0 + Main.$$('.flower').width()
				},
				y: {
					min: -height + Main.$$('.flower').height() * (9.1 / 25.15),
					max: -height + Main.$$('.flower').height() * (15 / 25.15)
				}
			}
		};
		console.log(point);
		Main.touch.on('#kettle', 'touchstart', function(ev) {
			ev.preventDefault();
		});
		Main.touch.on("#kettle", "drag", function(ev) {
			console.log(ev.x);
			console.log(ev.y);
			this.style.webkitTransform = "translate3d(" + ev.x + "px," + ev.y + "px,0)";
			this.style.webkitTransitionDuration = "0.25s";
			this.style.webkitTransitionTimingFunction = 'linear';
		});
		Main.touch.on('#kettle', 'dragend', function(ev) {
			var x = ev.x - 0.5 * Main.$$('.kettle').width();
			var y = ev.y;
			console.log("x: " + x + "--- y: " + y);
			if (y >= point.ok.y.min && y <= point.ok.y.max && x >= point.ok.x.min && x <= point.ok.x.max) {
				this.style.webkitTransform = "translate3d(" + ev.x + "px," + ev.y + "px,0) rotate(-45deg)";
				this.style.webkitTransformOrigin = "5% 10%"
				this.style.webkitTransitionDuration = "0.15s";
				this.style.webkitTransitionTimingFunction = 'linear';
				setTimeout(function() {
					var kettle = document.getElementById("kettle");
					kettle.style.webkitTransform = "translate3d(0px,0px,0) rotate(0deg)";
					kettle.style.webkitTransformOrigin = "5% 10%"
					kettle.style.webkitTransitionDuration = "0.5s";
				}, 3000);
				Main.Model.controlDevice(Main.Model.getOpenId(), vm.currentDevice.deviceId, "watering-5", function(data) {
					if (data.asyErrorCode == 0) {
						Main.f7.alert("浇水成功");
					} else if (data.asyErrorCode == 1) {
						Main.f7.alert("没有控制权限");
					} else if (data.asyErrorCode == 2) {
						Main.f7.alert("不在浇水时间段或者有人浇过水");
					} else {
						Main.f7.alert("设备离线");
					}
				});
			} else {
				this.style.webkitTransform = "translate3d(0px,0px,0) rotate(0deg)";
			}
		});
	}

	return {
		beforeEnter: beforeEnter
	};
});