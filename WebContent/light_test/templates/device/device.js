define(["main"], function(Main) {

	function beforeEnter(query) {
		var vm = new Vue({
			el: '#device',
			data: {
				temperature: "",
				humidity: "",
				currentDevice: query == "" ? undefined : query,
				light_open: false,
				lightChange_open: false,
				input_light: 3,
				light: {
					img: "ui/home/gd.png",
					openImg: "ui/home/g.png"
				}
			},
			methods: {
				moveLight: function() {
					if (this.lightChange_open) {
						this.lightChange_open = false;
						Main.$$('.changeLight').transition(200);
						Main.$$('.changeLight').transform('translate3d(0px, 0, 0)');
					} else {
						this.lightChange_open = true;
						Main.$$('.changeLight').transition(200);
						Main.$$('.changeLight').transform('translate3d(200px, 0, 0)');
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
						this.light.img = "ui/home/gd.png";
						this.light.openImg = "ui/home/g.png";
					} else {
						body = this.input_light;
						this.light_open = true;
						this.light.img = "ui/home/kd.png";
						this.light.openImg = "ui/home/k.png";
					}
					if (sync == undefined || sync == false) {
						Main.Model.controlDevice(Main.Model.getOpenId(), this.currentDevice.deviceId, 'led_pwm-' + body, function(data) {
							if (data.asyErrorCode == 0) {

							} else {
								Main.f7.alert("出现了异常,异常信息:" + data.asyErrorMsg);
								vm.open(true);
							}
						});
					}
				}
			}
		});
		Main.Model.getTempAndHumidity(query.deviceId, function(data) {
			vm.$set('humidity', data.humidity);
			vm.$set('temperature', data.temperature);
		});
		Main.touch.on('#input_light', 'dragstart', function(e) {
			console.log('input 拖动开始');
		});
		Main.touch.on('#input_light', 'dragend', function(e) {
			Main.Model.controlDevice(Main.Model.getOpenId(), vm.currentDevice.deviceId, 'led_pwm-' + vm.input_light, function(data) {
				if (data.asyErrorCode == 0) {
					vm.light_open = vm.input_light == 0 ? true : false;
					vm.open(true);
				} else {
					Main.f7.alert("出现了异常,异常信息:" + data.asyErrorMsg);
				}
			});
		});

		var screenWidth = Main.$$('.page').width();
		var screenHeight = Main.$$('.page').height();
		// Main.$$('.flower').css('height', Main.$$('.flower').width() * (25.15 / 17.67));
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
					} else {
						Main.f7.alert("出现了异常,异常信息:" + data.asyErrorMsg);
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