app = angular.module('loginFactory', [])

app.factory('loginFactory', [
		'$http',
		function($http, $rootScope) {

			var serviceBase = ''
				
			var obj = {};
			
			obj.login_user = function(credentials) {
				return $http.post(serviceBase + '/superadmin/user/login', credentials);

			}

			return obj;

		} ]);
