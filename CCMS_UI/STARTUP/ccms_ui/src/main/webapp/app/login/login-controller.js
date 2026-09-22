var app = angular.module('loginControllers', []);

app.controller('loginControllers', function($scope, $state, $stateParams,
        inform, loginFactory, authService) {
    $scope.user = {};
    $scope.isSubmitting = false;

    $scope.login = function(form) {
        if ($scope.isSubmitting) {
            return;
        }
        if (!form || form.$invalid || !$scope.user.name || !$scope.user.password) {
            inform.add('User Name and Password are required.', {ttl: 2000, type: 'warning'});
            return;
        }
        $scope.isSubmitting = true;
        authService.clear();
        loginFactory.login_user({
            name: $scope.user.name.trim(),
            password: $scope.user.password
        }).then(function(response) {
            var user = response.data;
            if (user.status === '100' && user.authToken) {
                authService.setSession(user, !!$scope.user.rememberMe);
                inform.add('WEL COME TO CROSS FIELD.', {ttl: 5000, type: 'info'});
                $state.go($stateParams.returnTo || 'dashboard.dashboard');
            } else {
                inform.add('Invalid User Name or Password.', {ttl: 2000, type: 'warning'});
            }
            $scope.isSubmitting = false;
        }, function(error) {
            $scope.isSubmitting = false;
            inform.add(error.status >= 500 ? 'Unable to contact the login service.' : 'Invalid User Name or Password.', {
                ttl: 3000,
                type: error.status >= 500 ? 'danger' : 'warning'
            });
        });
    };
});
