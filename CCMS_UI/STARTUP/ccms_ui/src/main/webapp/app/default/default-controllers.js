	
var defaultCntl = angular.module('defaultControllers', []);

defaultCntl.controller('defaultListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,defaultFactory) {

	
	
	 
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  defaultFactory.getByID().then(function(data){
	       
		  $scope.dcu = data.data;
	    });
	  
	  
	  		$scope.apply = function (dcu) {
	  			$scope.config = dcu;
	  			console.log($scope.config)
			defaultFactory.add($scope.config);
			$state.reload();
	};
	
	
	
});