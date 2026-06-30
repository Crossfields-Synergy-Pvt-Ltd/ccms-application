app = angular.module('dashboardFactory', [])

app.factory('dashboardFactory', [ '$http', 'inform', function($http, inform) {

	var serviceBase = '/CCMS';
	var obj = {};
	obj.getAll = function() {
		return $http.get(serviceBase + '/dld_server/list');
	}

	obj.getByID = function(customerID) {
		return $http.get(serviceBase + '/dld_server/list/' + customerID);
	}

	 obj.getByMandal = function(qs_params) {
			return $http.get(serviceBase+ '/filter/get_mandal?district=' + qs_params);
		}
		
		obj.getByGp = function(qs_params) {
			return $http.get(serviceBase+ '/filter/get_gp?mandal=' + qs_params);
		}
		
		obj.getAllCount = function(qs_params){
	        return $http.get(serviceBase + '/dashboard/count'+ qs_params);
	    }
		obj.getAllMapDashboardData = function(qs_params){
	        return $http.get(serviceBase + '/dashboard/map_data'+ qs_params);
	    } 
		
	obj.getAllDcuNames = function(qs_params){
		  return $http.get(serviceBase + '/dcu/dcu_name_list' + qs_params);
	}
		    
		
	return obj;
} ]);