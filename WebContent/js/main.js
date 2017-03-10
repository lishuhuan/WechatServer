

define('main', ['./router', './model'], function(Router, Model) {
	Router.init();
	var $$ = Dom7;
	var f7 = new Framework7();
	var mainView = f7.addView('.view-main', {
		dynamicNavbar: true
	});

	return {
		Model: Model,
		$$: $$,
		touch: touch,
		f7: f7,
		mainView: mainView,
		router: Router
	};
});