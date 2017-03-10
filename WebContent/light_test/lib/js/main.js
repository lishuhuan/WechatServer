define('main', ['./router', './model', 'http://res.wx.qq.com/open/js/jweixin-1.0.0.js'], function(Router, Model, wx) {
	try {
		Router.init();
	} catch (e) {
		window.location.reload(true);
		return;
	}
	var $$ = Dom7;
	var f7 = new Framework7({
		modalTitle: "植物灯",
		modalButtonOk: "确定",
		modalButtonCancel: "取消",
		swipeBackPage: false,
	});
	var mainView = f7.addView('.view-main', {
		dynamicNavbar: true,
		domCache: true
	});
	mainView.hideNavbar();

	try {
		var url = window.location.href;
		Model.wxConfig(url, function(data) {
			if (data.result_code == 0) {
				wx.config({
					debug: false,
					appId: data.data.appid,
					timestamp: data.data.timestamp,
					nonceStr: data.data.nonceStr,
					signature: data.data.signature,
					jsApiList: ["onMenuShareTimeline", "onMenuShareAppMessage"]
				});
			}
		});
	} catch (e) {
		window.location.reload(true);
		return;
	}

	function share(deviceId) {
		wx.onMenuShareTimeline({
			title: '我的植物灯',
			link: 'http://www.shuaikang.iego.cn/share?id=' + deviceId,
			imgUrl: 'http://121.40.65.146/flower_photo/huapen.png',
			success: function() {
				console.log("timeline success");
			},
			cancel: function() {
				console.log("timeline cancel");
			}
		});
		wx.onMenuShareAppMessage({
			title: '我的植物灯',
			desc: '这是我的植物灯，你也一起来吗？',
			link: 'http://www.shuaikang.iego.cn/share?id=' + deviceId,
			imgUrl: 'http://121.40.65.146/flower_photo/huapen.png',
			type: 'link',
			success: function() {
				console.log("appmessage success");
			},
			cancel: function() {
				console.log("appmessage cancel");
			}
		});
	}

	return {
		share: share,
		wx: wx,
		Model: Model,
		$$: $$,
		touch: touch,
		f7: f7,
		mainView: mainView,
		router: Router
	};
});