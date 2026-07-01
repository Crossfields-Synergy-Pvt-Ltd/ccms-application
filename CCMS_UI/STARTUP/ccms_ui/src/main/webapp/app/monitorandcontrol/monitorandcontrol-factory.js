
app = angular.module('monitorandcontrolFactory', [])

app.factory('monitorandcontrolFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    
			 obj.getAllCount = function(qs_params){
			        return $http.get(serviceBase + '/dashboard/count'+ qs_params);
			    }
	 
		    obj.getAllHandShake = function(qs_params, page, size){
		        return $http.post(serviceBase + '/dashboard/instant_data_filter'+ qs_params + '&page=' + page + '&size=' + size);
		    }
    
		    obj.getAllDcuNames = function(){
		        return $http.get(serviceBase + '/dcu/dcu_name_list');
		    }
		    
			obj.delete = function (id) {
			    return $http.delete(serviceBase + '/dcu/delete/' + id).then(function (status) {
			        return status.data;
			    });
			}
			
			 obj.getByID = function(gateway_serial_number){
			        return $http.get(serviceBase + '/dashboard/meter_data_by_id/' + gateway_serial_number);
			    }
			
			 obj.getByMandal = function(qs_params) {
					return $http.get(serviceBase+ '/filter/get_mandal?district=' + qs_params);
				}
				
				obj.getByGp = function(qs_params) {
					return $http.get(serviceBase+ '/filter/get_gp?mandal=' + qs_params);
				}
				
				obj.getByVillage = function(qs_params) {
					return $http.get(serviceBase+ '/filter/get_vilage?gp=' + qs_params);
				}
			 
				 obj.turnOnLights = function(qs_params){
				        return $http.get(serviceBase + '/device_conf/lights_on' + qs_params);
				    }
				 
				 obj.turnOffLights = function(qs_params){
				        return $http.get(serviceBase + '/device_conf/lights_off' + qs_params);
				    }
				
		    return obj;   
		    
		    }]);

