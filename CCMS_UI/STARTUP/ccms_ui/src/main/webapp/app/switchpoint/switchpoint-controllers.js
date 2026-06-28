var switchpointCntl = angular.module('switchpointControllers', []);

switchpointCntl.controller('switchpointListControllers', function($scope,
		$state, $stateParams, $modal, $location, $http, $rootScope,
		switchpointFactory) {

	$scope.selected_dcu = {};

	switchpointFactory.getAllDcuNames().then(function(data) {
		$scope.dcu_data = data.data;
	});

	$scope.view = function() {
		if (!$scope.selected_dcu || !$scope.selected_dcu.name) return;
		console.log($scope.selected_dcu.name.gateway_identifier)
		switchpointFactory.getByID($scope.selected_dcu.name.gateway_identifier).then(function(data) {
					console.log($scope.selected_dcu.name.gateway_identifier)
					$scope.obj = data.data;
					$scope.gateway_serial_number = $scope.selected_dcu.name.gateway_identifier;
					console.log($scope.obj)
				});
	};

	$scope.modifydcu = function(gateway_serial_number) {
		$scope.gateway_serial_number = gateway_serial_number;
		$state.go('dashboard.dcu_edit',{gateway_serial_number : $scope.gateway_serial_number
		})
	}
});
