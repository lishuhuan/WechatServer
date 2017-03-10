define(["main"], function(Main) {

	Main.touch.on("#homePage", "swipeleft", function(ev) {
		Main.f7.openPanel('right');
	});

	var target = document.getElementById("target");
	var flower = document.getElementsByClassName("flower")[0];
	var bookmark = document.getElementsByClassName("bookmark")[0];
	Main.touch.on('#kettle', 'touchstart', function(ev) {
		ev.preventDefault();
	});
	Main.touch.on("#kettle", "drag", function(ev) {
		this.style.webkitTransform = "translate3d(" + ev.x + "px," + ev.y + "px,0)";
		this.style.webkitTransitionDuration = "0.15s";
		this.style.webkitTransitionTimingFunction = 'linear';
	});
	Main.touch.on('#kettle', 'dragend', function(ev) {
		var centerX = this.offsetLeft + 25;
		var centerY = this.offsetTop + 25 + bookmark.offsetTop;
		if (centerX + ev.x > flower.offsetLeft && centerX + ev.x < flower.offsetLeft + flower.offsetWidth && centerY + ev.y > flower.offsetTop && centerY + ev.y < flower.offsetTop + flower.offsetHeight) {
			this.style.webkitTransform = "translate3d(" + ev.x + "px," + ev.y + "px,0) rotate(-45deg)";
			this.style.webkitTransitionDuration = "0.15s";
			this.style.webkitTransitionTimingFunction = 'linear';
			setTimeout(function() {
				var kettle = document.getElementById("kettle");
				kettle.style.webkitTransform = "translate3d(0px,0px,0) rotate(0deg)";
				kettle.style.webkitTransitionDuration = "0.5s";
			}, 3000);
			Main.Model.controlDevice(openid, device_arr[0].deviceId,
				"watering-5",
				function(data) {
					if (data.asyErrorCode == "0") {
						alert("浇水成功");
					}else{
						alert(data.asyErrorMsg);
					}
				});
		} else {
			this.style.webkitTransform = "translate3d(0px,0px,0) rotate(0deg)";
		}
	});

	var current = 0;
	var offset = 0;
	Main.touch.on('#dian', 'touchstart', function(ev) {
		ev.preventDefault();
	});
	Main.touch.on('#dian', 'drag', function(ev) {
		offset = current + ev.y;
		if (document.body.offsetHeight > 500) {
			if (offset >= 0 && offset <= 180) {
				this.style.webkitTransform = "translate3d(0px," + offset + "px,0)";
				this.style.webkitTransitionDuration = "0.05s";
				this.style.webkitTransitionTimingFunction = 'linear';
			}
		} else {
			if (offset >= 0 && offset <= 80) {
				this.style.webkitTransform = "translate3d(0px," + offset + "px,0)";
				this.style.webkitTransitionDuration = "0.05s";
				this.style.webkitTransitionTimingFunction = 'linear';
			}
		}

	});
	Main.touch.on('#dian', 'dragend', function(ev) {
		current = current + ev.y;
		var sun;
		if (document.body.offsetHeight > 500) {
			sun = parseInt(current / 18);
		} else {
			sun = parseInt(current / 8);
		}
		Main.Model.controlDevice(openid, device_arr[0].deviceId,
			"led_pwm-" + sun*10,
			function(data) {
				if (data.asyErrorCode == "0") {
					if (sun > 0) {
						document.getElementsByClassName('class_light')[0].src = "img/light_open.png";
						document.getElementsByClassName('class_plant1')[0].src = "img/plant_open.png";
						document.getElementsByClassName('class_plant2')[0].src = "img/plant2_open.png";
					} else {
						document.getElementsByClassName('class_light')[0].src = "img/light_close.png";
						document.getElementsByClassName('class_plant1')[0].src = "img/plant_close.png";
						document.getElementsByClassName('class_plant2')[0].src = "img/plant2_close.png";
					}
				} else {
					alert(data.asyErrorMsg);
				}
			});
	});

	Main.touch.on('#light_setting', 'tap', function(ev) {
		if (document.getElementsByClassName('slide')[0].style.display == "none") {
			document.getElementsByClassName('slide')[0].className=document.getElementsByClassName('slide')[0].className + " show"; 
			document.getElementsByClassName('slide')[0].style.display = "block";
			
		}else{
			document.getElementsByClassName('slide')[0].className=document.getElementsByClassName('slide')[0].className + " close"; 
			setTimeout(function(){
				document.getElementsByClassName('slide')[0].style.display = "none";
			}, 1000);
		}
	});

	var vm = new Vue({
		el: '#homePage',
		data: {
			open: false
		}
	})

	var openid;
	var device_arr;

	function beforeEnter() {
		var code = Main.Model.getRequest();
		Main.Model.getOpenId(code, function(data) {
			openid = data;
			Main.Model.getDevice(data, function(device) {
				device_arr = device;
			});
		});
	};

	return {
		beforeEnter: beforeEnter
	};
});