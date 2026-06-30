
app = angular.module('mapFactory', [])

app.factory('mapViewFactory', ['$http', 'inform', function($http, inform) {

	
	 var serviceBase = '/CCMS'
		    var obj = {};
		  
	  var obj = {};
	    
	 
	  	obj.getAll = function(){
	        return $http.get(serviceBase + '/om/get_essl_list');
	    };
	    
	    obj.getByMandal = function(qs_params) {
			return $http.get(serviceBase+ '/filter/get_mandal?district=' + qs_params);
		}
		
		obj.getByGp = function(qs_params) {
			return $http.get(serviceBase+ '/filter/get_gp?mandal=' + qs_params);
		}
		
		obj.getByVillage = function(qs_params) {
			return $http.get(serviceBase+ '/filter/get_vilage?gp=' + qs_params);
		}
		
		/*obj.getAllMapData = function(qs_params){
	        return $http.get(serviceBase + '/dcu/handshake_list'+ qs_params);
	    }*/
		
		obj.getAllMapDashboardData = function(qs_params){
	        return $http.get(serviceBase + '/dashboard/map_data'+ qs_params);
	    } 
		
	    obj.getAllCount = function(qs_params){
	        return $http.get(serviceBase + '/dashboard/count'+ qs_params);
	    }
	    
	    return obj;   
	    
}]);