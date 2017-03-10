define(["main"], function(Main) {

	var vm;

	function beforeEnter(query) {
		console.log(query);
		var mySwiper = Main.f7.swiper('.setting-swiper-container', {
			allowClick: false,
			shortSwipes: false,
			longSwipes: false,
			onSlideChangeStart: function(a) {
				vm.$set('setting_header.water.background', a.activeIndex == '1' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.water.color', a.activeIndex == '1' ? 'white' : 'black');
				vm.$set('setting_header.light.background', a.activeIndex == '0' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.light.color', a.activeIndex == '0' ? 'white' : 'black');
			}
		});
		mySwiper.lockSwipes();
		mySwiper.detachEvents();
		Main.Model.getDeviceProperty(query.deviceId, function(data) {
			for (var i in data) {
				var date = new Date(data[i].timePoint);
				data[i]["time"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
				date.setMinutes(date.getMinutes() + 30);
				data[i]["time2"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
				data[i]["time3"] = "每" + time(data[i].repetition);
			}
			vm.$set('display.colorTem', query.colorTem);
			vm.$set('display.sunModel', query.sunModel == 1 ? false : true);
			vm.$set('waterTimeList', data);
			setTimeout(function() {
				initWatchListen();
			}, 100);
			loadLightData(query);
		});
		vm = new Vue({
			el: '#setting',
			data: {
				device: query == "" ? undefined : query,
				type: 1,
				colorEdit: false,
				display: {
					addTime: false,
					addColorTime: false,
					repeatTime: false,
					repeatTime2: false,
					typeTime: false,
					customTime: false,
					typePrev: "button1",
					repeatPrev: "button1",
					repeatContent: "自定义",
					repeatType: "伴你入睡",
					waterTime: 15,
					colorTime: 15,
					colorTem: 3000,
					sunModel: false
				},
				setting_header: {
					water: {
						background: "white",
						color: "black"
					},
					light: {
						background: "#7AC8D5",
						color: "white"
					}
				},
				waterTimeList: [],
				lightTimeList: [],
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
				typeTimeImg: {
					button1: {
						color: "blue",
						img: "ui/device_setting/g.png",
						value: "伴你入睡"
					},
					button2: {
						color: "black",
						img: "ui/device_setting/g_n.png",
						value: "伴你起床"
					}
				},
				edit: undefined,
				editColor: undefined,
				selectTime: "",
				time1: "",
				time2: "",
				time3: "",
				time4: "",
				time5: "",
				time6: "",
				time7: "",
				time8: "",
				select: {
					watch: {
						item: "",
						event: "",
						index: ""
					},
					color: {
						item: "",
						event: "",
						index: ""
					}
				}
			},
			methods: {
				changeMode: function(key) {
					mySwiper.unlockSwipes();
					mySwiper.slideTo(Number(key), 400, true);
					mySwiper.lockSwipes();
				},
				waterTimeEdit: function(item, event, index) {
					vm.select.watch.item = item;
					vm.select.watch.event = event;
					vm.select.watch.index = index;
				},
				addWaterTime: function(key) {
					if (key == "add") {
						if (!this.display.addTime) {
							this.display.addTime = true;
							this.display.waterTime = 15;
							setTimeout(function() {
								Main.$$('.addtime').transition(300);
								Main.$$('.addtime').transform('translate3d(0, -350px, 0)');
							}, 50);
						}
					} else if (key == "close") {
						if (this.display.addTime) {
							this.edit = undefined;
							Main.$$('.addtime').transition(300);
							Main.$$('.addtime').transform('translate3d(0, 350px, 0)');
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
								Main.$$('.addtime').transform('translate3d(0, 350px, 0)');
								setTimeout(function() {
									vm.$set('display.addTime', false);
								}, 300);
								if (this.edit != undefined) {
									Main.Model.editDeviceProperty(this.edit, this.time1 + this.time2 + ":" + this.time3 + this.time4 + ":00", this.selectTime, String(this.display.waterTime), function(data) {
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
												setTimeout(function() {
													initWatchListen();
												}, 100);
											});
										}
									});
								} else {
									Main.Model.setDeviceProperty(query.deviceId, Main.Model.getOpenId, "watering", this.time1 + this.time2 + ":" + this.time3 + this.time4 + ":00", this.selectTime, String(this.display.waterTime), function(data) {
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
												setTimeout(function() {
													initWatchListen();
												}, 100);
											});
										}
									});
								}
							}
						}
					}
				},
				selectRepeatTime: function(key, index) {
					this.type = index == "repeatTime" ? 1 : 2;
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
						this.selectTime = "1234567";
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
						this.selectTime = "";
						var array = ["one", "two", "three", "four", "five", "six", "seven"];
						for (var i = 0; i < array.length; i++) {
							if (this.selectCustomTime[array[i]].color == "white") {
								this.selectTime = this.selectTime + (i + 1);
							}
						}
						this.display.repeatContent = "每周" + time(this.selectTime);
					}
				},
				addColorTime: function(key) {
					if (key == "add") {
						if (!this.display.addColorTime) {
							this.display.addColorTime = true;
							setTimeout(function() {
								Main.$$('.addtime').transition(300);
								Main.$$('.addtime').transform('translate3d(0, -350px, 0)');
							}, 50);
						}
					} else if (key == "close") {
						if (this.display.addColorTime) {
							this.edit = undefined;
							Main.$$('.addtime').transition(300);
							Main.$$('.addtime').transform('translate3d(0, 350px, 0)');
							setTimeout(function() {
								vm.$set('display.addColorTime', false);
							}, 300);
						}
					} else if (key == "ok") {
						if (this.colorEdit) {
							if (this.display.addColorTime) {
								if (this.time5 == "" || this.time6 == "" || this.time7 == "" || this.time8 == "") {
									Main.f7.alert("请输入时间");
								} else if (this.selectTime == "") {
									Main.f7.alert("请选择重复的天数");
								} else if ((Number(this.time5 * 600) + Number(this.time6 * 60) + Number(this.time7 * 10) + Number(this.time8)) > 24 * 60) {
									Main.f7.alert("请填写正确的时间");
								} else {
									Main.$$('.addtime').transition(300);
									Main.$$('.addtime').transform('translate3d(0, 350px, 0)');
									setTimeout(function() {
										vm.$set('display.addColorTime', false);
									}, 300);
									Main.Model.updateLedPlan(vm.select.color.item.id, this.display.repeatType == "伴你入睡" ? "0" : "1", String(this.display.colorTime), this.time5 + this.time6 + ":" + this.time7 + this.time8 + ":00", "0", this.selectTime,
										function(data) {
											if (data) {
												vm.colorEdit = false;
												vm.time5 = "";
												vm.time6 = "";
												vm.time7 = "";
												vm.time8 = "";
												vm.selectTime = "";
												loadLightData(query);
											}
										});
								}
							}
						} else {
							if (this.display.addColorTime) {
								if (this.time5 == "" || this.time6 == "" || this.time7 == "" || this.time8 == "") {
									Main.f7.alert("请输入时间");
								} else if (this.selectTime == "") {
									Main.f7.alert("请选择重复的天数");
								} else if ((Number(this.time5 * 600) + Number(this.time6 * 60) + Number(this.time7 * 10) + Number(this.time8)) > 24 * 60) {
									Main.f7.alert("请填写正确的时间");
								} else {
									Main.$$('.addtime').transition(300);
									Main.$$('.addtime').transform('translate3d(0, 350px, 0)');
									setTimeout(function() {
										vm.$set('display.addColorTime', false);
									}, 300);
									Main.Model.sleepAndGetupLed(query.deviceId, Main.Model.getOpenId(), this.display.repeatType == "伴你入睡" ? "0" : "1", String(this.display.colorTime), this.time5 + this.time6 + ":" + this.time7 + this.time8 + ":00", "0", this.selectTime, function(data) {
										if (data) {
											vm.time5 = "";
											vm.time6 = "";
											vm.time7 = "";
											vm.time8 = "";
											vm.selectTime = "";
											loadLightData(query);
										}
									});
								}
							}
						}
					}
				},
				selectType: function(key) {
					if (key == "open") {
						this.display.typeTime = true;
					} else if (key == "close") {
						this.display.typeTime = false;
					}
				},
				typeAlertClick: function(key) {
					this.typeTimeImg[this.display.typePrev].color = "black";
					this.typeTimeImg[this.display.typePrev].img = "ui/device_setting/g_n.png";
					this.typeTimeImg[key].color = "blue";
					this.typeTimeImg[key].img = "ui/device_setting/g.png";
					this.display.typePrev = key;
					this.display.typeTime = false;
					this.display.repeatType = this.typeTimeImg[key].value;
				},
				colorTimeEdit: function(item, event, index) {
					vm.select.color.item = item;
					vm.select.color.event = event;
					vm.select.color.index = index;
					this.display.colorTime = item.duration;
					this.colorEdit = true;
				},
				selectSunModel: function() {
					Main.Model.sunPowerPlan(query.deviceId, this.display.sunModel ? "0" : "1", function(data) {
						if (data.result_code != 0) {
							Main.f7.alert("设置失败");
						}
					});
				}
			},
			watch: {
				'time1': function(val, oldVal) {
					if (val.length >= 1) {
						this.time1 = this.time1.substr(0, 1);
						Main.$$('#input1').val(this.time1);
						Main.$$('#input2').focus();
					}
				},
				'time2': function(val, oldVal) {
					if (val.length >= 1) {
						this.time2 = this.time2.substr(0, 1);
						Main.$$('#input2').val(this.time2);
						Main.$$('#input3').focus();
					}
				},
				'time3': function(val, oldVal) {
					if (val.length >= 1) {
						this.time3 = this.time3.substr(0, 1);
						Main.$$('#input3').val(this.time3);
						Main.$$('#input4').focus();
					}
				},
				'time4': function(val, oldVal) {
					if (val.length >= 1) {
						this.time4 = this.time4.substr(0, 1);
						Main.$$('#input4').val(this.time4);
						Main.$$('#input4').blur();
					}
				},
				'time5': function(val, oldVal) {
					if (val.length >= 1) {
						this.time5 = this.time5.substr(0, 1);
						Main.$$('#input5').val(this.time5);
						Main.$$('#input6').focus();
					}
				},
				'time6': function(val, oldVal) {
					if (val.length >= 1) {
						this.time6 = this.time6.substr(0, 1);
						Main.$$('#input6').val(this.time6);
						Main.$$('#input7').focus();
					}
				},
				'time7': function(val, oldVal) {
					if (val.length >= 1) {
						this.time7 = this.time7.substr(0, 1);
						Main.$$('#input7').val(this.time7);
						Main.$$('#input8').focus();
					}
				},
				'time8': function(val, oldVal) {
					if (val.length >= 1) {
						this.time8 = this.time8.substr(0, 1);
						Main.$$('#input8').val(this.time8);
						Main.$$('#input8').blur();
					}
				}
			}
		});
		Main.touch.on('.swiper-slide', 'swiperight', function(ev) {
			Main.mainView.router.back();
		});
		Main.touch.on('#colorTem', 'dragstart', function(e) {
			console.log('input 拖动开始');

			function stopBubble(e) {
				if (e && e.stopPropagation) {
					e.stopPropagation();
				} else {
					window.event.cancelBubble = true;
				}
				stopBubble(e);
			};
		});
		Main.touch.on('#colorTem', 'dragend', function(e) {
			Main.Model.updatePowerAndCt(vm.device.deviceId, '1', String(vm.display.colorTem), function(data) {
				if (data.result_code != 0) {
					Main.f7.alert("设置失败");
				}
			});
		});

	};

	function loadLightData(query) {
		Main.Model.getLedProperty(query.deviceId, function(data) {
			if (data.result_code == 0) {
				for (var i in data.data) {
					var date = new Date(data.data[i].time.time);
					data.data[i]["time"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
					date.setMinutes(date.getMinutes() + Number(data.data[i].duration));
					data.data[i]["time2"] = (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes());
					data.data[i]["time3"] = data.data[i]["model"] == "0" ? "入睡模式" : "起床模式";
					data.data[i]["time3"] += "        每周" + time(data.data[i].repeat_date);
				}
				vm.$set('lightTimeList', data.data);
				setTimeout(function() {
					initColorListen();
				}, 100);
			}
		});
	}

	function initWatchListen() {
		Main.touch.on('.watchItem', 'hold', function(e) {
			Main.f7.confirm('要删除这条设置吗？', function() {
				Main.Model.deleteDeviceProperty(vm.select.watch.item.id, function(data) {
					if (data) {
						vm.waterTimeList.splice(vm.select.watch.index, 1);
						vm.$set('waterTimeList', vm.waterTimeList);
						setTimeout(function() {
							initWatchListen();
						}, 100);
					}
				});
			});
			stopBubble(e);
		});
		Main.touch.on('.watchItem', 'tap', function(e) {
			vm.edit = vm.select.watch.item.id;
			vm.time1 = vm.select.watch.item.time.substr(0, 1);
			vm.time2 = vm.select.watch.item.time.substr(1, 1);
			vm.time3 = vm.select.watch.item.time.substr(3, 1);
			vm.time4 = vm.select.watch.item.time.substr(4, 1);
			vm.display.waterTime = Number(vm.select.watch.item.waterTime);
			var array = ["one", "two", "three", "four", "five", "six", "seven"];
			for (var i = 0; i < 7; i++) {
				var str = Number(vm.select.watch.item.repetition.substr(i, 1)) - 1;
				if (str >= 0) {
					vm.selectCustomTime[array[str]].color = "white";
					vm.selectCustomTime[array[str]].backgroundColor = "#3F9BFC";
					vm.selectCustomTime[array[str]].borderColor = "#3F9BFC";
				} else {
					break;
				}
			}
			setTimeout(function() {
				vm.addWaterTime("add");
			}, 100);
		});
	};

	function initColorListen() {
		Main.touch.on('.colorItem', 'hold', function(e) {
			Main.f7.confirm('要删除这条设置吗？', function() {
				Main.Model.deleteLedProperty(vm.select.color.item.id, function(data) {
					if (data.result_code == 0) {
						vm.lightTimeList.splice(vm.select.color.index, 1);
						vm.$set('lightTimeList', vm.lightTimeList);
						// setTimeout(function() {
						// 	initColorListen();
						// }, 100);
					} else {
						Main.f7.alert("删除失败");
					}
				});
			});
			stopBubble(e);
		});
		Main.touch.on('.colorItem', 'tap', function(e) {
			vm.editColor = vm.select.color.item.id;
			vm.time5 = vm.select.color.item.time.substr(0, 1);
			vm.time6 = vm.select.color.item.time.substr(1, 1);
			vm.time7 = vm.select.color.item.time.substr(3, 1);
			vm.time8 = vm.select.color.item.time.substr(4, 1);
			vm.display.waterTime = Number(vm.select.color.item.waterTime);
			var array = ["one", "two", "three", "four", "five", "six", "seven"];
			for (var i = 0; i < 7; i++) {
				var str = Number(vm.select.color.item.repeat_date.substr(i, 1)) - 1;
				if (str >= 0) {
					vm.selectCustomTime[array[str]].color = "white";
					vm.selectCustomTime[array[str]].backgroundColor = "#3F9BFC";
					vm.selectCustomTime[array[str]].borderColor = "#3F9BFC";
				} else {
					break;
				}
			}
			setTimeout(function() {
				vm.addColorTime("add");
			}, 100);
		});
	};

	function stopBubble(e) {
		if (e && e.stopPropagation) {
			e.stopPropagation();
		} else {
			window.event.cancelBubble = true;
		}
	};

	function time(body) {
		var str = "";
		for (var i = 0; i < body.length; i++) {
			var object = body.substr(i, 1);
			if (object == '1') {
				str = str + "一 ";
			} else if (object == '2') {
				str = str + "二 ";
			} else if (object == '3') {
				str = str + "三 ";
			} else if (object == '4') {
				str = str + "四 ";
			} else if (object == '5') {
				str = str + "五 ";
			} else if (object == '6') {
				str = str + "六 ";
			} else if (object == '7') {
				str = str + "日 ";
			}
		}
		return str;
	};

	return {
		beforeEnter: beforeEnter
	};
});