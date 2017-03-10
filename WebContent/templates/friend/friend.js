define(["main"], function(Main) {

	function beforeEnter(query) {
		Main.Model.getFriendList(Main.Model.getOpenId(), query.deviceId, function(data) {
			var people = new Array();
			for (var i in data) {
				if (data[i].isControl == "2") {
					data[i].isControl = true;
					people.push(data[i]);
				} else if(data[i].isControl == "1"){
					data[i].isControl = false;
					people.push(data[i]);
				} else if(data[i].isControl == "0") {
					data[i].isControl = query.isHost == 1? true:false;
					vm.$set("host", data[i]);
				}
			}
			vm.$set("friendList", people);
		});
		var vm = new Vue({
			el: '#friend',
			data: {
				device: query == "" ? undefined : query,
				friendList: [],
				host: {
					headimgurl: "",
					nickName: "",
					isControl: ""
				}
			},
			methods: {
				click: function(key, item, index, bool) {
					if (query.isHost != "0") {
						Main.f7.alert("您没有操作权限");
					} else {
						if (key == "host") {
							Main.Model.applyAuth(item.openId, query.deviceId, function(data) {
								if (data) {
									vm.host.isControl = !(vm.host.isControl);
									vm.$set("host", vm.host);
								}
							});
						}else if(key == "people") {
							Main.Model.handleDeviceApply(item.openId, query.deviceId, bool ? "2" : "1", function(data) {
								if (data) {
									vm.friendList[index].isControl = !(vm.friendList[index].isControl);
									vm.$set("friendList", friendList);
								}
							});
						}
					}
				}
			}
		});
		Main.touch.on('.page-content', 'swiperight', function(ev) {
			Main.mainView.router.back();
		});
	};

	return {
		beforeEnter: beforeEnter
	};
});