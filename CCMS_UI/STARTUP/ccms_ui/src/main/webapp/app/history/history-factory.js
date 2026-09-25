app = angular.module('historyFactory', [])

app.factory('historyFactory', ['$http', function($http) {
    var serviceBase = '';
    var obj = {};

    obj.getByID = function(qs_params) {
        return $http.get(serviceBase + '/meter/meter_data_between_date' + qs_params);
    };
    obj.getByMandal = function(qs_params) {
        return $http.get(serviceBase + '/filter/get_mandal?district=' + encodeURIComponent(qs_params));
    };
    obj.getByGp = function(qs_params) {
        return $http.get(serviceBase + '/filter/get_gp?mandal=' + encodeURIComponent(qs_params));
    };
    obj.getByVillage = function(qs_params) {
        return $http.get(serviceBase + '/filter/get_vilage?gp=' + encodeURIComponent(qs_params));
    };
    obj.getByIDhistory = function(qs_params) {
        return $http.get(serviceBase + '/meter/export_history' + qs_params).then(function(response) {
            var headers = response.headers();
            var filename = headers['x-filename'] || 'history.csv';
            var contentType = headers['content-type'] || 'text/csv';
            var linkElement = document.createElement('a');
            var blob = new Blob([response.data], {type: contentType});
            var url = window.URL.createObjectURL(blob);
            linkElement.setAttribute('href', url);
            linkElement.setAttribute('download', filename);
            linkElement.dispatchEvent(new MouseEvent('click', {view: window, bubbles: true, cancelable: false}));
            window.URL.revokeObjectURL(url);
            return response;
        });
    };
    return obj;
}]);
