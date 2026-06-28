
var app = angular.module('loginControllers', []);
var user_previlage;

app.controller('loginControllers', function($scope, $state, $stateParams,
		$rootScope, $modal, $location, $http, inform, loginFactory, Base64) {
	
	
	$scope.user_previlage;
	$scope.login = function() {
		//console.log($scope.user);
		try {

			 
			
				var qs = '?name=' + $scope.user.name + '&password='
						+ $scope.user.password;
				console.log(qs)
				loginFactory.login_user(qs).then(function(data) {
					$scope.user_previlage = data.data;
					//console.log($scope.user_previlage);
					$rootScope.previlage = $scope.user_previlage;
					//console.log($rootScope.previlage);
					
					if($scope.user_previlage.status == '100'){
						 $scope.authdata = Base64.encode($scope.user_previlage.email + '|' + $scope.user_previlage.dist + '|' + $scope.user_previlage.mondal + '|' + $scope.user_previlage.gp
						 );
						 
						 $http.defaults.headers.common['Authorization'] =  $scope.authdata;
						 localStorage.setItem('ccms_previlage', JSON.stringify($scope.user_previlage));
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