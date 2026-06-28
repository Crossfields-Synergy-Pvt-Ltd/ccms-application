app = angular.module('loginFactory', [])

app.factory('loginFactory', [
		'$http',
		function($http, $rootScope) {

			var serviceBase = '/CCMS'
				
			var obj = {};
			
			obj.login_user = function(qs) {
			
				return $http.get(serviceBase + '/superadmin/user/login' + qs)
						.success(function(data) {
							
							/*app.run(function($rootScope) {
								console.log(data);
								$rootScope.color = data;
							});*/
							return data;
						});

			}

			return obj;

		} ]);