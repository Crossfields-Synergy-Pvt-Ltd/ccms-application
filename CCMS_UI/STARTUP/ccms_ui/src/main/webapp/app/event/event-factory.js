app = angular.module('eventFactory', [])

app.factory('eventFactory', ['$http', function($http) {
    var serviceBase = '';
    var obj = {};

    obj.getByID = function(qs_params) {
        return $http.get(serviceBase + '/events/events_between_date' + qs_params);
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
    obj.exportEventData = function(qs_params) {
        return $http.get(serviceBase + '/events/export_events' + qs_params, {responseType: 'arraybuffer'})
            .then(function(response) {
                if (response.status === 204) return response;
                var headers = response.headers();
                var filename = headers['x-filename'] || 'events.csv';
                var contentType = headers['content-type'] || 'text/csv';
                var linkElement = document.createElement('a');
                var blob = new Blob([response.data], {type: contentType});
                var url = window.URL.createObjectURL(blob);
                linkElement.setAttribute('href', url); linkElement.setAttribute('download', filename);
                linkElement.dispatchEvent(new MouseEvent('click', {view: window, bubbles: true, cancelable: false}));
                window.URL.revokeObjectURL(url);
                return response;
            });
    };
    return obj;
}]);
