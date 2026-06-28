// Stub modules for third-party dependencies not loaded via npm/bower
angular.module('inform', [])
  .factory('inform', function() {
    return {
      add: function(message, options) {
        return { message: message, options: options };
      },
      success: function(message) {
        return { message: message, type: 'success' };
      },
      error: function(message) {
        return { message: message, type: 'error' };
      },
      warning: function(message) {
        return { message: message, type: 'warning' };
      },
      info: function(message) {
        return { message: message, type: 'info' };
      }
    };
  });
angular.module('ui.select', []);
angular.module('angucomplete', []);
angular.module('highcharts-ng', []);
angular.module('ngMaterial', []);
angular.module('ui.slider', []);
angular.module('daterangepicker', []);
angular.module('ngMessages', []);
