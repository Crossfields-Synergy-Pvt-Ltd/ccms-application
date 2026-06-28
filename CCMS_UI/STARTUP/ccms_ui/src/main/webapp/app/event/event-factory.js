
app = angular.module('eventFactory', [])

app.factory('eventFactory', ['$http', function($http) {

	 var serviceBase = '/CCMS'
		    var obj = {};
		    obj.getAll = function(){
		        return $http.get(serviceBase + '/events/event_list');
		    }
			
		    obj.getAllDcuNames = function(qs_params){
		        return $http.get(serviceBase + '/dcu/dcu_name_list'+qs_params);
		    }
		    
		    obj.getByID = function(qs_params){
		        return $http.get(serviceBase + '/events/events_between_date/'+ qs_params);
		    }
		    
		    obj.getByMondal = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_mandal?distrtict=' + qs_params);
			}
			
			obj.getByGp = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_gp?mandal=' + qs_params);
			}
			
			obj.getByVillage = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_vilage?gp=' + qs_params);
			}
		    
			
			  obj.exportEventData = function(qs_params){
			        return $http.get(serviceBase + '/events/export_events/'+ qs_params )
			        
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