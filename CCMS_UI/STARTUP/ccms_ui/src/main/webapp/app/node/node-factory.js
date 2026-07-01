
app = angular.module('nodeFactory', [])

app.factory('nodeFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAllNode = function(){
		        return $http.get(serviceBase + '/node/list/');
		    }
		    
		    obj.getByID = function(customerID){
		        return $http.get(serviceBase + '/node/list/' + customerID);
		    }
		 
		    obj.add = function (obj) {
		    return $http.post(serviceBase + '/node/create', obj).then(function (results) {
		        return results;
		    });
		    }
		    
			obj.delete = function (nodeid) {
			    return $http.delete(serviceBase + '/node/delete/' + nodeid).then(function (status) {
			        return status.data;
			    });
			}
			
		    return obj;   
		    
		    }]);