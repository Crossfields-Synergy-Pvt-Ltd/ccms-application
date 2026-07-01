
app = angular.module('userFactory', [])

app.factory('userFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAll = function(){
		        return $http.get(serviceBase + '/superadmin/user/list');
		    }
		    obj.getByID = function(customerID){
		        return $http.get(serviceBase + '/superadmin/user/list/' + customerID);
		    }
		 
		    obj.add = function (obj) {
		    return $http.post(serviceBase + '/superadmin/user/create', obj).then(function (results) {
		        return results;
		    });
		    }
		    
			obj.delete = function (id) {
			    return $http.delete(serviceBase + '/superadmin/user/delete/' + id).then(function (status) {
			        return status.data;
			    });
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
			    
			
		    return obj;   
		    
		    }]);