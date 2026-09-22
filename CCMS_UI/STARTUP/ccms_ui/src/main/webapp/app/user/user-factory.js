
app = angular.module('userFactory', [])

app.factory('userFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAll = function(){
		        return $http.get(serviceBase + '/superadmin/user/list');
		    }
		    obj.add = function (obj) {
		        return $http.post(serviceBase + '/superadmin/user/create', obj);
		    }

		    obj.update = function (email, obj) {
		        return $http.put(serviceBase + '/superadmin/user/update/' + encodeURIComponent(email), obj);
		    }
		    
			obj.delete = function (id) {
			    return $http.delete(serviceBase + '/superadmin/user/delete/' + encodeURIComponent(id)).then(function (status) {
			        return status.data;
			    });
			}
			
			 obj.getByMandal = function(qs_params) {
					return $http.get(serviceBase+ '/filter/get_mandal?district=' + encodeURIComponent(qs_params));
				}
				
				obj.getByGp = function(qs_params) {
					return $http.get(serviceBase+ '/filter/get_gp?mandal=' + encodeURIComponent(qs_params));
				}
				
				obj.getByVillage = function(qs_params) {
					return $http.get(serviceBase+ '/filter/get_vilage?gp=' + encodeURIComponent(qs_params));
				}
			    
			
		    return obj;   
		    
		    }]);
