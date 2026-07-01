
app = angular.module('modified_operationalFactory', [])

app.factory('modified_operationalFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAllById = function(dcu_id){
		        return $http.get(serviceBase + '/modified_io/modified_io_list/' +  dcu_id);
		    }
			
		    obj.getAll = function(){
		        return $http.get(serviceBase + '/modified_io/modified_io_list/');
		    }
		    
		    obj.getAllDcuNames = function(){
		        return $http.get(serviceBase + '/dcu/dcu_name_list');
		    }
		    
		    obj.getAllExport = function(qs_params){
		        return $http.get(serviceBase + '/io/export_operationalhour' +  qs_params)
		   
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
		    }
		  
		    return obj;   
		    
		    }]);