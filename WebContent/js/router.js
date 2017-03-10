
define(function() {
    function init() {
    	var $$ = Dom7;
		$$(document).on('pageBeforeInit', function (e) {
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
		require(['./' + moduleName], function(module) {
			module.beforeEnter(query);
		});
	};
	function pageBeforeAnimation(moduleName, query) {
		
	};
	function pageBack(moduleName, query) {
		
	};

	return {
        init: init
    };
});