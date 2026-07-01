
app = angular.module('defaultFactory', [])

app.factory('defaultFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    
		    obj.getByID = function(customerID){
		        return $http.post(serviceBase + '/conf/get_dcu_defult_conf' );
		    }
		    
		    obj.add = function(dcu_conf){
		        return $http.post(serviceBase + '/conf/add_defult_dcu_conf' , dcu_conf);
		    }
		    
		   
			
		    return obj;   
		    
		    }]);