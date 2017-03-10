define(["main"], function(Main) {

	function beforeEnter(query) {
		var mySwiper = Main.f7.swiper('.setting-swiper-container', {
			allowClick: false,
			shortSwipes: false,
			longSwipes: false,
			onSlideChangeStart: function(a) {
				vm.$set('setting_header.water.background', a.activeIndex == '0' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.water.color', a.activeIndex == '0' ? 'white' : 'black');
				vm.$set('setting_header.light.background', a.activeIndex == '1' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.light.color', a.activeIndex == '1' ? 'white' : 'black');
			}
		});
		mySwiper.lockSwipes();
		mySwiper.detachEvents();
		Main.Model.getDeviceProperty(query.deviceId, function(data) {
			for (var i in data) {
				var date = new Date(data[i].timePoint);
				data[i]["time"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
			}
			vm.$set('waterTimeList', data);
		});
		var vm = new Vue({
			el: '#setting',
			data: {
				device: query == "" ? undefined : query,
				display: {
					addTime: false,
					repeatTime: false,
					customTime: false,
					repeatPrev: "button1",
					repeatContent: "自定义"
				},
				setting_header: {
					water: {
						background: "#7AC8D5",
						color: "white"
					},
					light: {
						background: "white",
						color: "balck"
					}
				},
				waterTimeList: [],
				repeatTime: {
					button1: {
						color: "blue",
						img: "ui/device_setting/g.png",
						value: "仅一次"
					},
					button2: {
						color: "black",
						img: "ui/device_setting/g_n.png",
						value: "周一至周五"
					},
					button3: {
						color: "black",
						img: "ui/device_setting/g_n.png",
						value: "每天"
					},
					button4: {
						color: "black",
						img: "ui/device_setting/g_n.png",
						value: "法定工作日"
					},
					button5: {
						color: "black",
						img: "ui/device_setting/g_n.png",
						value: "自定义"
					}
				},
				selectCustomTime: {
					one: {
						color: "black",
						backgroundColor: "white",
						borderColor: "gray"
					},
					two: {
						color: "black",
						backgroundColor: "white",
						borderColor: "gray"
					},
					three: {
						color: "black",
						backgroundColor: "white",
						borderColor: "gray"
					},
					four: {
						color: "black",
						backgroundColor: "white",
						borderColor: "gray"
					},
					five: {
						color: "black",
						backgroundColor: "white",
						borderColor: "gray"
					},
					six: {
						color: "black",
						backgroundColor: "white",
						borderColor: "gray"
					},
					seven: {
						color: "black",
						backgroundColor: "white",
						borderColor: "gray"
					}
				},
				edit: undefined,
				selectTime: "",
				time1: "",
				time2: "",
				time3: "",
				time4: ""
			},
			methods: {
				changeMode: function(key) {
					mySwiper.unlockSwipes();
					mySwiper.slideTo(Number(key), 400, true);
					mySwiper.lockSwipes();
				},
				waterTimeEdit: function(item) {
					this.edit = item.id;
					this.time1 = item.time.substr(0, 1);
					this.time2 = item.time.substr(1, 1);
					this.time3 = item.time.substr(3, 1);
					this.time4 = item.time.substr(4, 1);
					var array = ["one", "two", "three", "four", "five", "six", "seven"];
					for (var i = 0; i < 7; i++) {
						var str = Number(item.repetition.substr(i, 1)) - 1;
						if (str >= 0) {
							this.selectCustomTime[array[str]].color = "white";
							this.selectCustomTime[array[str]].backgroundColor = "#3F9BFC";
							this.selectCustomTime[array[str]].borderColor = "#3F9BFC";
						} else {
							break;
						}
					}
					setTimeout(function() {
						vm.addWaterTime("add");
					}, 100);

				},
				addWaterTime: function(key) {
					if (key == "add") {
						if (!this.display.addTime) {
							this.display.addTime = true;
							setTimeout(function() {
								Main.$$('.addtime').transition(300);
								Main.$$('.addtime').transform('translate3d(0, -400px, 0)');
							}, 50);
						}
					} else if (key == "close") {
						if (this.display.addTime) {
							this.edit = undefined;
							Main.$$('.addtime').transition(300);
							Main.$$('.addtime').transform('translate3d(0, 400px, 0)');
							setTimeout(function() {
								vm.$set('display.addTime', false);
							}, 300);
						}
					} else if (key == "ok") {
						if (this.display.addTime) {
							if (this.time1 == "" || this.time2 == "" || this.time3 == "" || this.time4 == "") {
								Main.f7.alert("请输入时间");
							} else if (this.selectTime == "") {
								Main.f7.alert("请选择浇水的天数");
							} else {
								Main.$$('.addtime').transition(300);
								Main.$$('.addtime').transform('translate3d(0, 400px, 0)');
								setTimeout(function() {
									vm.$set('display.addTime', false);
								}, 300);
								if (this.edit != undefined) {
									Main.Model.editDeviceProperty(this.edit, this.time1 + this.time2 + ":" + this.time3 + this.time4 + ":00", this.selectTime, function(data) {
										if (data) {
											vm.time1 = "";
											vm.time2 = "";
											vm.time3 = "";
											vm.time4 = "";
											vm.selectTime = "";
											vm.edit = undefined;
											Main.Model.getDeviceProperty(query.deviceId, function(data) {
												for (var i in data) {
													var date = new Date(data[i].timePoint);
													data[i]["time"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
												}
												vm.$set('waterTimeList', data);
											});
										}
									});
								} else {
									Main.Model.setDeviceProperty(query.deviceId, Main.Model.getOpenId, "watering", this.time1 + this.time2 + ":" + this.time3 + this.time4 + ":00", this.selectTime, function(data) {
										if (data) {
											vm.time1 = "";
											vm.time2 = "";
											vm.time3 = "";
											vm.time4 = "";
											vm.selectTime = "";
											Main.Model.getDeviceProperty(query.deviceId, function(data) {
												for (var i in data) {
													var date = new Date(data[i].timePoint);
													data[i]["time"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
												}
												vm.$set('waterTimeList', data);
											});
										}
									});
								}
							}
						}
					}
				},
				selectRepeatTime: function(key) {
					if (key == "open") {
						this.display.repeatTime = true;
					} else if (key == "close") {
						this.display.repeatTime = false;
					}
				},
				repeatAlertClick: function(key) {
					this.repeatTime[this.display.repeatPrev].color = "black";
					this.repeatTime[this.display.repeatPrev].img = "ui/device_setting/g_n.png";
					this.repeatTime[key].color = "blue";
					this.repeatTime[key].img = "ui/device_setting/g.png";
					this.display.repeatContent = this.repeatTime[key].value;
					this.display.repeatPrev = key;
					if (key != "button5") {
						this.display.repeatTime = false;
					} else {
						this.display.repeatTime = false;
						this.display.customTime = true;
					}
				},
				dayTime: function(key) {
					if (this.selectCustomTime[key].color == "black") {
						this.selectCustomTime[key].color = "white";
						this.selectCustomTime[key].backgroundColor = "#3F9BFC";
						this.selectCustomTime[key].borderColor = "#3F9BFC";
					} else {
						this.selectCustomTime[key].color = "black";
						this.selectCustomTime[key].backgroundColor = "white";
						this.selectCustomTime[key].borderColor = "gray";
					}
				},
				customTimeClick: function(key) {
					if (key == "no") {
						this.display.customTime = false;
						var array = ["one", "two", "three", "four", "five", "six", "seven"];
						for (var i = 0; i < array.length; i++) {
							if (this.selectCustomTime[array[i]].color == "white") {
								this.selectCustomTime[array[i]].color = "black";
								this.selectCustomTime[array[i]].backgroundColor = "white";
								this.selectCustomTime[array[i]].borderColor = "gray";
							}
						}
					} else if (key == "yes") {
						this.display.customTime = false;
						var array = ["one", "two", "three", "four", "five", "six", "seven"];
						for (var i = 0; i < array.length; i++) {
							if (this.selectCustomTime[array[i]].color == "white") {
								this.selectTime = this.selectTime + (i + 1);
							}
						}
					}
				}
			},
			watch: {
				'time1': function(val, oldVal) {
					if (val.length >= 1) {
						this.time1 = this.time1.substr(0, 1);
						Main.$$('#input1').val(this.time1);
						// Main.$$('#input1').blur();
						Main.$$('#input2').focus();
					}
				},
				'time2': function(val, oldVal) {
					if (val.length >= 1) {
						this.time2 = this.time2.substr(0, 1);
						Main.$$('#input2').val(this.time2);
						// Main.$$('#input2').blur();
						Main.$$('#input3').focus();
					}
				},
				'time3': function(val, oldVal) {
					if (val.length >= 1) {
						this.time3 = this.time3.substr(0, 1);
						Main.$$('#input3').val(this.time3);
						// Main.$$('#input3').blur();
						Main.$$('#input4').focus();
					}
				},
				'time4': function(val, oldVal) {
					if (val.length >= 1) {
						this.time4 = this.time4.substr(0, 1);
						Main.$$('#input4').val(this.time4);
						Main.$$('#input4').blur();
					}
				}
			}
		});
		Main.touch.on('.swiper-slide', 'swiperight', function(ev) {
			Main.mainView.router.back();
		});
	};

	return {
		beforeEnter: beforeEnter
	};
});