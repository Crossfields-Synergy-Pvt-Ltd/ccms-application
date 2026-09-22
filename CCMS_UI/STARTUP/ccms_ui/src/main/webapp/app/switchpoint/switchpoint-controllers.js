var switchpointCntl = angular.module('switchpointControllers', []);

switchpointCntl.controller('switchpointListControllers', function($scope,
		$state, $stateParams, $rootScope,
		switchpointFactory) {

	$scope.selected_dcu = {};
	$scope.dcu_data = [];
	$scope.loading = false;
	$scope.error_message = null;
	var privileges = $rootScope.privilege || {};
	var dcuListParams = '?district=' + encodeURIComponent(privileges.district || 'ALL') +
		'&mandal=' + encodeURIComponent(privileges.mandal || 'ALL') +
		'&gp=' + encodeURIComponent(privileges.gp || 'ALL');

	function findDcuByIdentifier(identifier) {
		for (var i = 0; i < $scope.dcu_data.length; i++) {
			var dcu = $scope.dcu_data[i];
			if (dcu && String(dcu.gateway_identifier) === String(identifier)) {
				return dcu;
			}
		}
		return null;
	}

	switchpointFactory.getAllDcuNames(dcuListParams).then(function(data) {
		$scope.dcu_data = data.data;
		if ($stateParams.gateway_serial_number) {
			var matchingDcu = findDcuByIdentifier($stateParams.gateway_serial_number);
			if (matchingDcu) {
				$scope.selected_dcu = matchingDcu;
				$scope.view();
			} else {
				$scope.error_message = 'The requested DCU could not be found.';
			}
		}
	}).catch(function() {
		$scope.error_message = 'Unable to load the switch point list.';
	});

	$scope.view = function() {
		if (!$scope.selected_dcu || !$scope.selected_dcu.gateway_identifier) return;
		$scope.obj = null;
		$scope.loading = true;
		$scope.error_message = null;
		$scope.gateway_serial_number = $scope.selected_dcu.gateway_identifier;
		switchpointFactory.getByID($scope.gateway_serial_number).then(function(data) {
			$scope.obj = data.data;
		}).catch(function() {
			$scope.error_message = 'Unable to load switch point information.';
		}).finally(function() {
			$scope.loading = false;
		});
	};

	$scope.lighton = function() {
		if (!$scope.obj || !$scope.obj.dcu_details) return;
		var details = $scope.obj.dcu_details;
		var params = '?device_serial_number=' + encodeURIComponent(details.gateway_serial_number) +
			'&device_identifier=' + encodeURIComponent(details.serial_number);
		var request = details.light_status == 1 ?
			switchpointFactory.turnOffLights(params) : switchpointFactory.turnOnLights(params);
		request.then(function() {
			details.light_status = details.light_status == 1 ? 0 : 1;
		}).catch(function() {
			$scope.error_message = 'Unable to change the light status.';
		});
	};

	$scope.modifydcu = function(obj) {
		var details = obj && obj.dcu_details ? obj.dcu_details : obj;
		if (!details || !details.gateway_serial_number) return;
		$scope.gateway_serial_number = details.gateway_serial_number;
		$state.go('dashboard.dcu_edit',{gateway_serial_number : $scope.gateway_serial_number
		})
	};
});
