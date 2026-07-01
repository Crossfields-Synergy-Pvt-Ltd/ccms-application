
app = angular.module('historyFactory', [])

app.factory('historyFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
			
		    obj.getAll = function(){
		        return $http.get(serviceBase + '/meter/meter_data_list/');
		    }
		    
		    obj.getByID = function(qs_params){
		        return $http.get(serviceBase + '/meter/meter_data_between_date/'+ qs_params);
		    }
		    
		    
		    obj.getAllDcuNames = function(qs_params){
		        return $http.get(serviceBase + '/dcu/dcu_name_list' + qs_params);
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
		    
		    obj.getByIDhistory = function(qs_params){
		        return $http.get(serviceBase + '/meter/export_history/'+ qs_params )
		        
		        .success(function (data, status, headers) {
		            headers = headers();
		     
		            var filename = headers['x-filename'];
		            var contentType = headers['content-type'];
		     
		            var linkElement = document.createElement('a');
		            try {
		                var blob = new Blob([data], { type: contentType });
		                var url = window.URL.createObjectURL(blob);
		     
		                linkElement.setAttribute('href', url);
		                linkElement.setAttribute("download", filename);
		     
		                var clickEvent = new MouseEvent("click", {
		                    "view": window,
		                    "bubbles": true,
		                    "cancelable": false
		                });
		                linkElement.dispatchEvent(clickEvent);
		            } catch (ex) {
		                console.log(ex);
		            }
		        }).error(function (data) {
		            console.log(data);
		        });
		    };	
		    
		    return obj;   
		    
		    }]);