
app = angular.module('meterFactory', [])

app.factory('meterFactory', ['$http', function($http) {

	 var serviceBase = '/CCMS'
		    var obj = {};
		  /*  obj.getAllMeterByDeviceID = function(id){
		        return $http.get(serviceBase + '/meter_conf/meter_list_by_id');
		    }*/
		    
		    obj.add = function (obj) {
		    return $http.post(serviceBase + '/meter_conf/create_meter', obj).then(function (results) {
		        return results;
		    });
		    }
		    
		    
		    obj.getAllMeterByDeviceID = function (gateway_identifier) {
			    return $http.post(serviceBase + '/meter_conf/meter_list_by_id/' + gateway_identifier).then(function (results) {
			        return results;
			    });
			    }
		    
			obj.delete = function (id) {
			    return $http.delete(serviceBase + '/meter_conf/delete_meter/' + id).then(function (status) {
			        return status.data;
			    });
			}
			
		    return obj;   
		    
		    }]);