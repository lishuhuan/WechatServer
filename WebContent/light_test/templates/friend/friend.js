define(["main"], function(Main) {

	function beforeEnter(query) {
		Main.Model.getFriendList(Main.Model.getOpenId(), query.deviceId, function(data) {
			for (var i in data) {
				data[i].isControl = data[i].isControl == "1" ? true : false;
			}
			vm.$set("friendList", data);
		});
		var vm = new Vue({
			el: '#friend',
			data: {
				device: query == "" ? undefined : query,
				friendList: []
			},
			methods: {
				click: function(item, index) {
					Main.Model.handleDeviceApply(item.openId, query.deviceId, item.isControl ? "1" : "2", function(data) {
						if (!data) {
							friendList[index].isControl = !friendList[index].isControl;
							vm.$set("friendList", friendList);
						}
					});
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