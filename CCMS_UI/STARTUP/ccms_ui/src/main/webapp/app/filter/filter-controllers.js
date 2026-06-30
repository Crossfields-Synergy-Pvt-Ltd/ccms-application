
var filterCntl = angular.module('filterControllers', []);

filterCntl.controller('filterListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, filterFactory, config) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
 $scope.selectedDistrict = '';
 $scope.districts = config.districts; 
	/*  
 $scope.districts = [ {
		state : "--Select--",
		code : "--Select--"
	},
	{
		state : "Srikakulam-11",
		code : "Srikakulam-11"
	},
	
	{
		state : "Visakhapatnam-13",
		code : "Visakhapatnam-13"
	},
	
	{
		state : "Prakasam-18",
		code : "Prakasam-18"
	},
	
	{
		state : "Nellore-19",
		code : "Nellore-19"
	},
	
	{
		state : "Kadapa-20",
		code : "Kadapa-20"
	},
	
	{
		state : "Kurnool-21",
		code : "Kurnool-21"
	},
	{
		state : "Guntur-17",
		code : "Guntur-17"
	},
	{
		state : "West Godavari-15",
		code : "West Godavari-15"
	}];
	 */
	  
	 
	  $scope.getMandalOnSelect = function() {
			filterFactory.getByMandal($scope.selectedDistrict).then(function(data) {
				$scope.mandal_list = data.data;
			});
		}

		$scope.getGponSelect = function() {
			filterFactory.getByGp($scope.selectedMandal).then(function(data) {
				$scope.gp_list = data.data;
			});
		}
		
		$scope.getVillagreonSelect = function() {
			filterFactory.getByGp($scope.select_gp).then(function(data) {
				$scope.village_list = data.data;
			});
		}

		$scope.filter = function () {
		$state.go('dashboard.filter_add');
}
		
})
      
filterCntl.controller('filterAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,filterFactory, config) 	{
	 $scope.districts = config.districts; 
	/*$scope.districts = [ {
		state : "--Select--",
		code : "--Select--"
	},
	{
		state : "Srikakulam-11",
		code : "Srikakulam-11"
	},
	
	{
		state : "Visakhapatnam-13",
		code : "Visakhapatnam-13"
	},
	
	{
		state : "Prakasam-18",
		code : "Prakasam-18"
	},
	
	{
		state : "Nellore-19",
		code : "Nellore-19"
	},
	
	{
		state : "Kadapa-20",
		code : "Kadapa-20"
	},
	
	{
		state : "Kurnool-21",
		code : "Kurnool-21"
	},
	{
   		state : "Guntur-17",
   		code : "Guntur-17"
   	},
	{
   		state : "West Godavari-15",
   		code : "West Godavari-15"
   	}];
	 */
	   
				
				$scope.ok=function(){
					console.log($scope.filter);
				filterFactory.add($scope.filter);
				$state.reload();
				/*$state.go('dashboard.filter')*/
				alert("Successfully Added");
		};
		
				$scope.close = function () {
				$state.go('dashboard.filter');
	  }
})


