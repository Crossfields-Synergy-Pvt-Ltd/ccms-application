
app = angular.module('switchpointFactory', [])

app.factory('switchpointFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    
		    obj.getByID = function(gateway_serial_number){
		        return $http.get(serviceBase + '/dashboard/instant_data_id/'+ gateway_serial_number);
		    }
		    
	    obj.getAllDcuNames = function(qsParams){
	        return $http.get(serviceBase + '/dcu/dcu_name_list' + (qsParams || ''));
	    }

	    obj.turnOnLights = function(params) {
	        return $http.get(serviceBase + '/device_conf/lights_on' + params);
	    };

	    obj.turnOffLights = function(params) {
	        return $http.get(serviceBase + '/device_conf/lights_off' + params);
	    };
	    return obj;
		    
		    }]);
