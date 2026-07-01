
app = angular.module('dcuFactory', [])

app.factory('dcuFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAll = function(){
		        return $http.get(serviceBase + '/dcu/list/');
		    }
		    
		    obj.getAllHandShake = function(qs_params){
		        return $http.get(serviceBase + '/dcu/handshake_list'+qs_params);
		    }
		    
		    obj.getByID = function(customerID){
		        return $http.get(serviceBase + '/dcu/handshake_info_by_id' + customerID);
		    }
		 
		    obj.add = function (obj) {
		    return $http.post(serviceBase + '/dcu/create', obj).then(function (results) {
		        return results;
		    });
		    }
		    
		    obj.getByIDmodify = function (gatway_id) {
			    return $http.post(serviceBase + '/dcu/sys_conf_id/' + gatway_id).then(function (results) {
			        return results;
			    });
			    }
		
		    obj.getSyncByID = function(gateway_serial_number){
		        return $http.get(serviceBase + '/device_conf/sync_node_conf/'+ gateway_serial_number);
		    }
		    
		    obj.getScheduleSyncByID = function(qs_params){
		        return $http.get(serviceBase + '/device_conf/sync_schduler_conf'+ qs_params);
		    }
		    
		    
		    obj.modifysystemconfig = function (obj) {
			    return $http.post(serviceBase + '/device_conf/sync_dcu_configuration', obj).then(function (results) {
			        return results;
			    });
			    }
		    
			obj.delete = function (id) {
			    return $http.delete(serviceBase + '/dcu/delet_dcu_id/' + id).then(function (status) {
			        return status.data;
			    });
			}
			
			 obj.load_default_conf = function(dcu_id){
			        return $http.get(serviceBase + '/conf/update_dcu_conf/'+ dcu_id );
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
			    
				 obj.getAllScheduler = function(){
				        return $http.get(serviceBase + '/scheduler/list');
				    }
			 
				 
				 
				  obj.getAllConfSyncStatus= function(qs_params){
				        return $http.get(serviceBase + '/cloudsms/conf_details_list?id='+ qs_params);
				    }
				  
		    return obj;   
		    
		    }]);