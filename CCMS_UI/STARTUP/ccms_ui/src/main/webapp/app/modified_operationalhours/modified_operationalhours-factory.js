
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
			        return $http.get(serviceBase + '/io/export_operationalhour' + qs_params, { responseType: 'arraybuffer' })
			            .then(function(response) {
			                var headers = response.headers();
			                var filename = headers['x-filename'] || 'operational-hours.csv';
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
