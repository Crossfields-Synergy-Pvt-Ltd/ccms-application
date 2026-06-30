
var app = angular.module('loginControllers', []);
var user_privilege;

app.controller('loginControllers', function($scope, $state, $stateParams,
		$rootScope, $modal, $location, $http, inform, loginFactory, Base64) {
	
	
	$scope.user_privilege;
	$scope.login = function() {
		//console.log($scope.user);
		try {

			 
			
				var qs = '?name=' + $scope.user.name + '&password='
						+ $scope.user.password;
				console.log(qs)
				loginFactory.login_user(qs).then(function(data) {
					$scope.user_privilege = data.data;
					//console.log($scope.user_privilege);
					$rootScope.privilege = $scope.user_privilege;
					//console.log($rootScope.privilege);
					
					if($scope.user_privilege.status == '100'){
						 $scope.authdata = Base64.encode($scope.user_privilege.email + '|' + $scope.user_privilege.district + '|' + $scope.user_privilege.mandal + '|' + $scope.user_privilege.gp
						 );
						 
						 $http.defaults.headers.common['Authorization'] =  $scope.authdata;
						 localStorage.setItem('ccms_privilege', JSON.stringify($scope.user_privilege));
						 localStorage.setItem('ccms_auth', $scope.authdata);
						inform.add('WEL COME TO CROSS FIELD.', {
							ttl : 5000,
							type : 'info'
						});
						
					
						$state.go('dashboard.dashboard');	
						 
					}
					else {
						inform.add('Invalid User Name or Password.', {
							ttl : 2000,
							type : 'warning'
						});
						$state.go('/login');
						
					}
					
				});

				
			

		} catch (e) {
			inform.add('Invalid User Name or Password.', {
				ttl : 2000,
				type : 'warning'
			});
		}
	};

});