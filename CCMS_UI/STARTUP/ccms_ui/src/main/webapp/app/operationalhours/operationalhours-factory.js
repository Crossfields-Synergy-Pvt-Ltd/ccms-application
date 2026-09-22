
app = angular.module('operationalFactory', [])

app.factory('operationalFactory', ['$http', function($http) {

	 var serviceBase = ''
		    var obj = {};
		    obj.getAll = function(dcu_id){
		        return $http.get(serviceBase + '/io/get_io_details/' +  dcu_id);
		    }
			
			    obj.getAllDcuNames = function(qs_params){
			        return $http.get(serviceBase + '/dcu/dcu_name_list' + (qs_params || ''));
		    }
		    
		    obj.getAllOperationalHourByDate = function(qs_params){
		        return $http.get(serviceBase + '/io/io_data_between_date' +  qs_params)
		    }
		    
		    obj.getByMandal = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_mandal?district=' + qs_params);
			}
			
			obj.getByGp = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_gp?mandal=' + qs_params);
			}

			obj.getByVillage = function(qs_params) {
				return $http.get(serviceBase+ '/filter/get_village?gp=' + qs_params);
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
