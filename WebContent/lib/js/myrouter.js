define(function() {
	function init() {
		var $$ = Dom7;
		$$(document).on('pageBeforeInit', function(e) {
			var page = e.detail.page;
			pageBeforeInit(page.name, page.query);
		});
		$$(document).on('pageBeforeAnimation', function(e) {
			var page = e.detail.page;
			pageBeforeAnimation(page.name, page.query);
		});
		$$(document).on('pageBack', function(e) {
			var page = e.detail.page;
			pageBack(page.name, page.query);
		});
	};

	function pageBeforeInit(moduleName, query) {
		if (moduleName == "device") {
			require(['../../templates/device/' + moduleName], function(module) {
				module.beforeEnter(query);
			});
		} else if (moduleName == "friend") {
			require(['../../templates/friend/' + moduleName], function(module) {
				module.beforeEnter(query);
			});
		} else if (moduleName == "setting") {
			require(['../../templates/setting/' + moduleName], function(module) {
				module.beforeEnter(query);
			});
		} else if (moduleName == "infoBook") {
			require(['../../templates/infoBook/' + moduleName], function(module) {
				module.beforeEnter(query);
			});
		} else {
			require(['../../templates/' + moduleName], function(module) {
				module.beforeEnter(query);
			});
		}
	};

	function pageBeforeAnimation(moduleName, query) {
		if (moduleName == "home") {
			require(['../../templates/' + moduleName], function(module) {
				module.animationEnter(query);
			});
		}
	};

	function pageBack(moduleName, query) {

	};

	return {
		init: init
	};
});