
app = angular.module('modified_operationalFactory', [])

app.factory('modified_operationalFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAllByDate = function(qs_params){
		        return $http.get(serviceBase + '/modified_io/modified_io_list' + qs_params);
		    }
		    
		    obj.getAllDcuNames = function(){
		        return $http.get(serviceBase + '/dcu/dcu_name_list');
		    }
		    
			    obj.exportLightStatus = function(qs_params){
			        return $http.get(serviceBase + '/modified_io/export' + qs_params, { responseType: 'arraybuffer' })
			            .then(function(response) {
			                var headers = response.headers();
			                var filename = headers['x-filename'] || 'light-status.csv';
			                var contentType = headers['content-type'] || 'text/csv';
			                var linkElement = document.createElement('a');
			                var blob = new Blob([response.data], { type: contentType });
			                var url = window.URL.createObjectURL(blob);
			                linkElement.setAttribute('href', url);
			                linkElement.setAttribute('download', filename);
			                linkElement.dispatchEvent(new MouseEvent('click', { view: window, bubbles: true, cancelable: false }));
			                window.URL.revokeObjectURL(url);
			                return response;
			            });
		    }
		  
		    return obj;   
		    
		    }]);
