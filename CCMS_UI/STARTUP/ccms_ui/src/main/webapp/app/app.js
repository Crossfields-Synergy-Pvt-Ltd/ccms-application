
var app = angular.module('demoapp', [ 'ngResource', 'ui.router', 'inform','ngSanitize','ui.select',
                              		'loginControllers', 'loginFactory', 'dashboardControllers',
                            		'ui.bootstrap', 'dashboardFactory',
                            		'dcuFactory', 'dcuControllers',  'userControllers',
                            		'userFactory', 'angucomplete', 'highcharts-ng','mapControllers',
                            		'mapFactory', 'ngMaterial','ui.slider',
                            		 'ui.bootstrap', 'ngMessages','daterangepicker',
                            		 'meterControllers', 'meterFactory', 'historyControllers', 'filterControllers', 'filterFactory',
                            		'historyFactory', 'highcharts-ng', 
                            		'switchpointControllers', 'switchpointFactory', 'ngMaterial','modified_operationalControllers','modified_operationalFactory',
                            		 'schedulesControllers', 'schedulesFactory','operationalControllers','operationalFactory',
                            		'eventControllers', 'eventFactory', 'defaultControllers',
                            		'defaultFactory', 'monitorandcontrolControllers',
                            		'monitorandcontrolFactory','nodeControllers','nodeFactory'
		
])

app.run(function($rootScope, $http) {
    var storedPrivilege = localStorage.getItem('ccms_privilege');
    if (storedPrivilege) {
        $rootScope.privilege = JSON.parse(storedPrivilege);
    }
    var storedAuth = localStorage.getItem('ccms_auth');
    if (storedAuth) {
        $http.defaults.headers.common['Authorization'] = storedAuth;
    }
});

app.constant('config', {
    appName: 'CCMS - VNETSOFT HUBLI',
    appVersion: 1.0,
    serviceBase: '',
    publicLoginUrl: '',
    districts: [ {
   		state : "--Select--",
   		code : "--Select--"
   	},
   	{
   		state : "5 Districts",
   		code : "5_districts"
   	},
   	{
   		state : "YSR Kadapa",
   		code : "YSR Kadapa"
   	},
   	{
   		state : "Srikakulam",
   		code : "Srikakulam"
   	},
   	{
   		state : "Prakasam",
   		code : "Prakasam"
   	},
   	{
   		state : "Kurnool",
   		code : "Kurnool"
   	},
	{
   		state : "West Godavari",
   		code : "West Godavari"
   	},
	{
   		state : "Other",
   		code : "Other"
   	},
	{
   		state : "ALL",
   		code : "ALL"
   	}
   	]
    	
});

app.config(function($locationProvider, $stateProvider, $urlRouterProvider) {

	
     
	$urlRouterProvider.otherwise("/public_monitor");

	$stateProvider.state('dashboard', {
		url : '',
		abstract : true,
		templateUrl : 'app/navi/leftnavi.html',
		controller: function($scope, $rootScope) {
			$scope.privilege = $rootScope.privilege;
		},
		ncyBreadcrumb : {
			skip : true
		},
	})
	
	.state('map', {
		url : '/public_monitor',
		templateUrl : 'app/map/map-view.html',
		/*controller : 'mapViewControllers',*/
	})
	
	.state('login', {
		templateUrl : 'app/login/login.html',
		url : '/login'
	})
	
	.state('dashboard.home', {
		url : '/home',
		templateUrl : 'app/dashboard/home.html',
		controller : 'AppCtrl',
		ncyBreadcrumb : {
			label : 'HOME'
		},
	})
	
	
	.state("dashboard.dashboard", {
		parent : 'dashboard',
		url : '/dashboard',
		templateUrl : 'app/dashboard/dashboard-view.html?v=2',
		controller : 'dashboardControllers',
		ncyBreadcrumb : {
			label : 'DASHBOARD',
			parent : 'dashboard'
		}
	})
	
	.state("dashboard.user", {
		parent : 'dashboard',
		url : '/user',
		templateUrl : 'app/user/user-list.html?v=2',
		controller : 'userListControllers',
		ncyBreadcrumb : {
			label : 'USER',
			parent : 'dashboard'
		}
	})

	.state("dashboard.user_add", {
		parent : 'dashboard',
		url : '/user_add',
		templateUrl : 'app/user/user-add.html?v=2',
		controller : 'userAddControllers',
		ncyBreadcrumb : {
			label : 'USER',
			parent : 'dashboard'
		}
	})

	.state("dashboard.user_edit", {
		parent : 'dashboard',
		url : '/user_edit',
		params : {user : null},
		templateUrl : 'app/user/user-update.html?v=2',
		controller : 'userUpdateControllers',
		ncyBreadcrumb : {
			label : 'USER',
			parent : 'dashboard'
		}
	})
	
		.state("dashboard.dcu", {
		parent : 'dashboard',
		url : '/dcu',
		templateUrl : 'app/dcu/dcu-list.html?v=2',
		controller : 'dcuListControllers',
		ncyBreadcrumb : {
			label : 'DCU',
			parent : 'dashboard'
		}
	})

	.state("dashboard.dcu_add", {
		parent : 'dashboard',
		url : '/dcu_add',
		templateUrl : 'app/dcu/dcu-add.html?v=2',
		controller : 'dcuAddControllers',
		ncyBreadcrumb : {
			label : 'DCU',
			parent : 'dashboard'
		}
	})

	.state("dashboard.dcu_edit", {
		parent : 'dashboard',
		url : '/dcu_edit',
		params : {dcu : null,gateway_serial_number:null},
		templateUrl : 'app/dcu/dcu-update.html?v=2',
		controller : 'dcuUpdateControllers',
		ncyBreadcrumb : {
			label : 'DCU',
			parent : 'dashboard'
		}
	})
	
	.state("dashboard.dcu_modify", {
		parent : 'dashboard',
		url : '/dcu_modify',
		params : {gateway_serial_number : null},
		templateUrl : 'app/dcu/dcu-modify.html?v=2',
		controller : 'dcuModifyControllers',
		ncyBreadcrumb : {
			label : 'DCU',
			parent : 'dashboard'
		}
	})

	.state("dashboard.node", {
		parent : 'dashboard',
		url : '/node',
		templateUrl : 'app/node/node-list.html?v=2',
		controller : 'nodeListControllers',
		ncyBreadcrumb : {
			label : 'NODE',
			parent : 'dashboard'
		}
	})

	
	.state("dashboard.node_add", {
		parent : 'dashboard',
		url : '/node_add',
		templateUrl : 'app/node/node-add.html?v=2',
		controller : 'nodeAddControllers',
		ncyBreadcrumb : {
			label : 'NODE',
			parent : 'dashboard'
		}
	})

	.state("dashboard.node_edit", {
		parent : 'dashboard',
		url : '/node_edit',
		params : {node : null},
		templateUrl : 'app/node/node-update.html?v=2',
		controller : 'nodeUpdateControllers',
		ncyBreadcrumb : {
			label : 'NODE',
			parent : 'dashboard'
		}
	})
	
	.state("dashboard.monitorandcontrol", {
		parent : 'dashboard',
		url : '/monitorandcontrol',
		templateUrl : 'app/monitorandcontrol/monitorandcontrol-list.html?v=2',
		controller : 'monitorandcontrolListControllers',
		ncyBreadcrumb : {
			label : 'MONITORANDCONTROL',
			parent : 'dashboard'

		}
	})
	
	.state("dashboard.schedules", {
		parent : 'dashboard',
		url : '/schedules',
		templateUrl : 'app/schedules/schedules-list.html?v=2',
		controller : 'schedulesListControllers',
		ncyBreadcrumb : {
			label : 'SCHEDULES',
			parent : 'dashboard'
		}
	})

	.state("dashboard.schedules_add", {
		parent : 'dashboard',
		url : '/schedules_add',
		templateUrl : 'app/schedules/schedules-add.html?v=2',
		controller : 'schedulesAddControllers',
		ncyBreadcrumb : {
			label : 'SCHEDULES',
			parent : 'dashboard'
		}
	})

	.state("dashboard.schedules_edit", {
		parent : 'dashboard',
		url : '/schedules_edit',
		params : {schedule : null},
		templateUrl : 'app/schedules/schedules-update.html?v=2',
		controller : 'schedulesUpdateControllers',
		ncyBreadcrumb : {
			label : 'SCHEDULES',
			parent : 'dashboard'
		}
	})

	.state("dashboard.filter", {
		parent : 'dashboard',
		url : '/filter',
		templateUrl : 'app/filter/filte-list.html?v=2',
		controller : 'filterListControllers',
		ncyBreadcrumb : {
			label : 'FILTER',
			parent : 'dashboard'
		}
	})

	.state("dashboard.filter_add", {
		parent : 'dashboard',
		url : '/filter_add',
		templateUrl : 'app/filter/filter-add.html?v=2',
		controller : 'filterAddControllers',
		ncyBreadcrumb : {
			label : 'FILTER',
			parent : 'dashboard'
		}
	})

	.state("dashboard.filter_edit", {
		parent : 'dashboard',
		url : '/filter_edit',
		params : {
			dcu : null
		},
		templateUrl : 'app/filter/filter-update.html?v=2',
		controller : 'filterUpdateControllers',
		ncyBreadcrumb : {
			label : 'FILTER',
			parent : 'dashboard'
		}
	})

	
	.state("dashboard.event", {
		parent : 'dashboard',
		url : '/event',
		params :{gateway_serial_number :null},  
		templateUrl : 'app/event/event-list.html?v=2',
		controller : 'eventListControllers',
		ncyBreadcrumb : {
			label : 'EVENT',
			parent : 'dashboard'
		}
	})
	
	.state("dashboard.history", {
		parent : 'dashboard',
		url : '/history',
		templateUrl : 'app/history/history-list.html?v=2',
		controller : 'historyListControllers',
		ncyBreadcrumb : {
			label : 'history',
			parent : 'dashboard'
		}
	})

	.state("dashboard.default", {
		parent : 'dashboard',
		url : '/default',
		templateUrl : 'app/default/default-add.html?v=2',
		controller : 'defaultListControllers',
		ncyBreadcrumb : {
			label : 'DEFAULT',
			parent : 'dashboard'
		}
	})

	.state("dashboard.switchpoint", {
		parent : 'dashboard',
		url : '/switchpoint',
		params :{gateway_serial_number :null},  
		templateUrl : 'app/switchpoint/switchpoint-list.html?v=2',
		controller : 'switchpointListControllers',
		ncyBreadcrumb : {
			label : 'SWITCHPOINT',
			parent : 'dashboard'
		}
	})

	.state("dashboard.meter", {
		parent : 'dashboard',
		url : '/meter',
		params : {gateway_serial_number : null},
		templateUrl : 'app/meter/meter-list.html?v=2',
		controller : 'meterListControllers',
		ncyBreadcrumb : {
			label : 'METER',
			parent : 'dashboard'
		}
	})

	.state("dashboard.meter_add", {
		parent : 'dashboard',
		url : '/meter_add',
		params : {
			gateway_serial_number : null
		},
		templateUrl : 'app/meter/meter-add.html?v=2',
		controller : 'meterAddControllers',
		ncyBreadcrumb : {
			label : 'METER',
			parent : 'dashboard'
		}
	})

	.state("dashboard.meter_edit", {
		parent : 'dashboard',
		url : '/meter_edit',
		params : {
			meter : null
		},
		templateUrl : 'app/meter/meter-add.html?v=2',
		controller : 'meterUpdateControllers',
		ncyBreadcrumb : {
			label : 'METER',
			parent : 'dashboard'
		}
	})
	
	.state("dashboard.operationalhour", {
		parent : 'dashboard',
		url : '/operationalhour',
		templateUrl : 'app/operationalhours/operationalhours-list.html?v=2',
		controller : 'operationalListControllers',
		ncyBreadcrumb : {
			label : 'OPERATIONALHOUR',
			parent : 'dashboard'
		}
	})

	.state("dashboard.modified_operationalhour", {
		parent : 'dashboard',
		url : '/modified_operationalhour',
		templateUrl : 'app/modified_operationalhours/modified_operationalhours-list.html?v=2',
		controller : 'modified_operationalListControllers',
		ncyBreadcrumb : {
			label : 'MODIFIED_OPERATIONALHOUR',
			parent : 'dashboard'
		}
	})
		
	});







app.controller('AppCtrl', function($scope) {
	$scope.demo = {

	};

	$scope.currentNavItem = 'page1';

});

app.controller('timeCtrl', function($scope) {
	 $scope.date = new Date();
});

app.directive('uiSrefActiveIf',
		[
				'$state',
				function($state) {
					return {
						restrict : "A",
						controller : [
								'$scope',
								'$element',
								'$attrs',
								function($scope, $element, $attrs) {
									var state = $attrs.uiSrefActiveIf;

									function update() {
										if ($state.includes(state)
												|| $state.is(state)) {
											$element.addClass("active");
										} else {
											$element.removeClass("active");
										}
									}

									$scope.$on('$stateChangeSuccess', update);
									update();
								} ]
					};
				} ]);

app.config(function($httpProvider) {
	$httpProvider.interceptors.push(function($q, $rootScope) {
		return {
			'request' : function(config) {
				$rootScope.$broadcast('loading-started');
				return config || $q.when(config);
			},
			'response' : function(response) {
				$rootScope.$broadcast('loading-complete');
				return response || $q.when(response);
			},
			'responseError' : function(rejection) {
				$rootScope.$broadcast('loading-complete');
				return $q.reject(rejection);
			}
		};
	});
});

app.factory('loadingCounts', function() {
	return {
		enable_count : 0,
		disable_count : 0
	}
});



app.factory('Base64', function () {
    
    var keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

    return {
        encode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;

            do {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                    keyStr.charAt(enc1) +
                    keyStr.charAt(enc2) +
                    keyStr.charAt(enc3) +
                    keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";
            } while (i < input.length);

            return output;
        },

        decode: function (input) {
            var output = "";
            var chr1, chr2, chr3 = "";
            var enc1, enc2, enc3, enc4 = "";
            var i = 0;

            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            var base64test = /[^A-Za-z0-9\+\/\=]/g;
            if (base64test.exec(input)) {
                window.alert("There were invalid base64 characters in the input text.\n" +
                    "Valid base64 characters are A-Z, a-z, 0-9, '+', '/',and '='\n" +
                    "Expect errors in decoding.");
            }
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

            do {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }

                chr1 = chr2 = chr3 = "";
                enc1 = enc2 = enc3 = enc4 = "";

            } while (i < input.length);

            return output;
        }
    };

});

app
		.directive(
				"loadingIndicator",
				function(loadingCounts, $timeout) {
					return {
						restrict : "A",
						link : function(scope, element, attrs) {
							scope
									.$on(
											"loading-started",
											function(e) {
												loadingCounts.enable_count++;
												console
														.log("displaying indicator "
																+ loadingCounts.enable_count);
												// only show if longer than one
												// sencond
												$timeout(
														function() {
															if (loadingCounts.enable_count > loadingCounts.disable_count) {
																element
																		.css({
																			"display" : ""
																		});
															}
														}, 1000);
											});
							scope
									.$on(
											"loading-complete",
											function(e) {
												loadingCounts.disable_count++;
												console
														.log("hiding indicator "
																+ loadingCounts.disable_count);
												if (loadingCounts.enable_count == loadingCounts.disable_count) {
													element.css({
														"display" : "none"
													});
												}
											});
						}
					};
				});