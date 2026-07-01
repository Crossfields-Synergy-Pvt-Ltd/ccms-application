
app = angular.module('schedulesFactory', [])

app.factory('schedulesFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAll = function(){
		        return $http.get(serviceBase + '/scheduler/list');
		    }
		    obj.getByID = function(customerID){
		        return $http.get(serviceBase + '/scheduler/list' + customerID);
		    }
		 
		    obj.add = function (obj) {
		    return $http.post(serviceBase + '/scheduler/create', obj).then(function (results) {
		        return results;
		    });
		    }
		    
			obj.delete = function (scheduleId) {
			    return $http.delete(serviceBase + '/scheduler/delete/' + scheduleId).then(function (status) {
			        return status.data;
			    });
			}
			
		    return obj;   
		    
		    }]);