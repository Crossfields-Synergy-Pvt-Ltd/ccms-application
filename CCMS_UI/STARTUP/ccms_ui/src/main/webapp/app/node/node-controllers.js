
var nodeCntl = angular.module('nodeControllers', []);

nodeCntl.controller('nodeListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, nodeFactory) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  nodeFactory.getAllNode().then(function(data){
//	        $scope.listData = data.data;
	        
	        $scope.todos = data.data;
		       
			  
			  $scope.list = [];
			  $scope.itemsPerPage = 8;
			  $scope.currentPage = 1;
			  
			  $scope.figureOutTodosToDisplay = function() {
			    var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
			    var end = begin + $scope.itemsPerPage;
			    $scope.list = $scope.todos.slice(begin, end);
			  };
			  
			  
			  $scope.figureOutTodosToDisplay();

			  $scope.pageChanged = function() {
			    $scope.figureOutTodosToDisplay();
			  };
	   
	        
	    });
	      
	  $scope.add = function () {
		  $state.go('user.node_add', {user : $scope.dcu})
	  };
		    
	  $scope.delete = function(id){ 
		  userFactory.delete(id);
	  }
	  
	  $scope.deleteconf = function (id) {	
	         var modalInstance = $modal.open({
	             templateUrl: 'app/common/delete.html',
	             controller: 'nodeDeleteController',
	             resolve: {
	                 id: function () {
	                     return id;
	                 }
	             }
	         });
	     }
	  
	  $scope.update = function (obj) {
     	 $scope.node = obj;
     	 $state.go('user.node_edit', {node : $scope.node});
      }
});


nodeCntl.controller('nodeAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, nodeFactory) 	{
		 
				$scope.ok = function () {	
				$scope.node;
				nodeFactory.add($scope.node);
				$state.reload();
				$state.go('user.node');
		};

				$scope.cancel = function () {
				$state.go('user.node');
		};
		
})
      
nodeCntl.controller('nodeUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,nodeFactory) 	{

	       $scope.node = $stateParams.node;
	  
				
				$scope.update=function(){
				$scope.node;
				nodeFactory.add($scope.node);
				$state.reload();
				$state.go('user.node');
		};
		
				$scope.close = function () {
				$state.go('user.node');
	  }
})


nodeCntl.controller('nodeDeleteController', function ($scope, $state, $modalInstance, id, nodeFactory) {

				$scope.ok = function () {
				nodeFactory.delete(id);
				$modalInstance.close($scope.node);
				$state.reload();
	  };

	  			$scope.cancel = function () {
	  			$modalInstance.dismiss('cancel');
	  };
	});