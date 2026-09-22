var loginApp = angular.module('loginFactory', []);

loginApp.factory('loginFactory', ['$http', function($http) {
    return {
        login_user: function(credentials) {
            return $http.post('/superadmin/user/login', credentials);
        }
    };
}]);

loginApp.factory('authService', ['$http', '$rootScope', function($http, $rootScope) {
    var TOKEN_KEY = 'ccms_auth';
    var PRIVILEGE_KEY = 'ccms_privilege';

    function clearStorage(storage) {
        storage.removeItem(TOKEN_KEY);
        storage.removeItem(PRIVILEGE_KEY);
    }

    return {
        setSession: function(user, remember) {
            var storage = remember ? localStorage : sessionStorage;
            clearStorage(localStorage);
            clearStorage(sessionStorage);
            $http.defaults.headers.common.Authorization = user.authToken;
            storage.setItem(TOKEN_KEY, user.authToken);
            storage.setItem(PRIVILEGE_KEY, JSON.stringify(user));
            $rootScope.privilege = user;
        },
        clear: function() {
            clearStorage(localStorage);
            clearStorage(sessionStorage);
            delete $http.defaults.headers.common.Authorization;
            $rootScope.privilege = null;
        },
        restore: function() {
            var token = localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY);
            var rawPrivilege = localStorage.getItem(PRIVILEGE_KEY) || sessionStorage.getItem(PRIVILEGE_KEY);
            if (!token || !rawPrivilege) {
                return false;
            }
            try {
                $rootScope.privilege = JSON.parse(rawPrivilege);
                $http.defaults.headers.common.Authorization = token;
                return true;
            } catch (e) {
                this.clear();
                return false;
            }
        },
        isAuthenticated: function() {
            return !!(localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY));
        }
    };
}]);
