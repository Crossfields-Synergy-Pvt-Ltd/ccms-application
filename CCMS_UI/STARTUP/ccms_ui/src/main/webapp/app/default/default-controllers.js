	
var defaultCntl = angular.module('defaultControllers', []);

defaultCntl.controller('defaultListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,defaultFactory, inform) {

	
	
	 
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  defaultFactory.getByID().then(function(data){
	       
		  $scope.dcu = data.data;
	    });
	  
	  
	  		$scope.apply = function (dcu) {
	  			$scope.config = dcu;
	  			console.log($scope.config)
			defaultFactory.add($scope.config).then(function() {
				inform.add("Default DCU configuration saved successfully", {ttl: 3000, type: "success"});
				$state.reload();
			}).catch(function(error) {
				inform.add("Failed to save default DCU configuration: " + ((error && error.data) || (error && error.statusText) || "request failed"), {ttl: 5000, type: "danger"});
			});
	};
	
	
	
});