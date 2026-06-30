app = angular.module('filterFactory', [])

app.factory('filterFactory', [
		'$http',
		function($http) {

			var serviceBase = '/CCMS'
				 var obj = {};

			obj.getByMandal = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_mandal/' , qs_params);
			}
			
			obj.getByGp = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_gp/' , qs_params);
			}
			
			obj.getByVillage = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_vilage/' , qs_params);
			}
			
			obj.add = function(qs_params) {
				return $http.post(serviceBase+ '/filter/create', qs_params);
			}

			return obj;

		} ]);