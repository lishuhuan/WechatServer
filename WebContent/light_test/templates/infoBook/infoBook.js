define(["main"], function(Main) {

	function beforeEnter() {
		var mySwiper = Main.f7.swiper('.setting-swiper-container', {
			allowClick: false,
			shortSwipes: false,
			longSwipes: false,
			onSlideChangeStart: function(a) {
				vm.$set('setting_header.friend.background', a.activeIndex == '0' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.friend.color', a.activeIndex == '0' ? 'white' : 'black');
				vm.$set('setting_header.mine.background', a.activeIndex == '1' ? '#7AC8D5' : 'white');
				vm.$set('setting_header.mine.color', a.activeIndex == '1' ? 'white' : 'black');
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
	};

	return {
		beforeEnter: beforeEnter
	};
});