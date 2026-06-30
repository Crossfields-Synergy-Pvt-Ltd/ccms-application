
var userCntl = angular.module('userControllers', []);

userCntl.controller('userListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, userFactory, config) {

	
	
	 
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  userFactory.getAll().then(function(data){
	        $scope.listData = data.data;
	    });
	
	
	
	  
	  $scope.add = function () {
		  $state.go('dashboard.user_add', {user : $scope.user})
	  };

		    
	  $scope.delete = function(id){ 
		  userFactory.delete(id);
	  }
	  
	  $scope.deleteconf = function (id) {	
	         var modalInstance = $modal.open({
	             templateUrl: 'app/common/delete.html',
	             controller: 'userDeleteController',
	             resolve: {
	                 id: function () {
	                     return id;
	                 }
	             }
	         });
	     }
	  
	  $scope.update = function (obj) {
     	 $scope.user = obj;
     	 console.log($scope.user);
     	 $state.go('dashboard.user_edit', {user : $scope.user});
      }
});


userCntl.controller('userAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, userFactory, config) 	{
	
	$scope.selectedDistrict = '';
	 $scope.districts = config.districts; 
   	  $scope.getMandalOnSelect = function(district) {
   		userFactory.getByMandal($scope.user.district).then(function(data) {
   				$scope.mandal_list = data.data;
   			});
   		}

   		$scope.getGpOnSelect = function(mandal) {
   			userFactory.getByGp($scope.user.mandal).then(function(data) {
   				$scope.gp_list = data.data;
   				
   			});
   		}
   		
   		$scope.getVillageOnSelect = function(gp) {
   			userFactory.getByVillage($scope.user.gp).then(function(data) {
   			$scope.village_list = data.data;
   			});
   		}
	
				$scope.ok = function () {	
				$scope.user;
				console.log($scope.user)
				userFactory.add($scope.user);
				
				$state.reload();
				$state.go('dashboard.user');
		};

				$scope.cancel = function () {
				$state.go('dashboard.user');
		};
})
      
userCntl.controller('userUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, userFactory, config) 	{
	$scope.user = $stateParams.user;
	$scope.selectedDistrict = '';
	 $scope.districts = config.districts; 
  
  	  
	
	userFactory.getByMandal($scope.user.district).then(function(data) {
			$scope.mandal_list = data.data;
		});
	
		userFactory.getByGp($scope.user.mandal).then(function(data) {
			$scope.gp_list = data.data;
			
		});
	
			
	
    $scope.getMandalOnSelect = function(district) {
    	
   		userFactory.getByMandal($scope.user.district).then(function(data) {
   				$scope.mandal_list = data.data;
   				console.log($scope.mandal_list)
   			});
   		}

   		$scope.getGpOnSelect = function(mandal) {
   			userFactory.getByGp($scope.user.mandal).then(function(data) {
   				$scope.gp_list = data.data;
   				
   			});
   		}
   		
   		$scope.getVillageOnSelect = function(gp) {
   			userFactory.getByVillage($scope.user.gp).then(function(data) {
   			$scope.village_list = data.data;
   			});
   		}
	  
				$scope.update=function(){
				$scope.user;
				console.log($scope.user)
				userFactory.add($scope.user);
				$state.reload();
				$state.go('dashboard.user');
		};
		
				$scope.close = function () {
				$state.go('dashboard.user');
	  }
})

userCntl.controller('userDeleteController', function ($scope, $state, $modalInstance, id, userFactory) {

				$scope.ok = function () {
				userFactory.delete(id);
				$modalInstance.close($scope.user);
				$state.reload();
	  };

	  			$scope.cancel = function () {
	  			$modalInstance.dismiss('cancel');
	  };
	});