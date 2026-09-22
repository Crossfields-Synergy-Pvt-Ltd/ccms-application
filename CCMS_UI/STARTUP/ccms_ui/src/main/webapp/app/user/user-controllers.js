
var userCntl = angular.module('userControllers', []);

userCntl.controller('userListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, userFactory, config) {

	
	
	 
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  $scope.listData     = [];
	  $scope.itemsPerPage = 10;
	  $scope.currentPage  = 1;
	  
	 
	  userFactory.getAll().then(function(data){
	        $scope.listData = data.data;
	    }, function() {
	        $scope.error = 'Unable to load users.';
	    });
	
	
	
	  
	  $scope.add = function () {
		  $state.go('dashboard.user_add', {user : $scope.user})
	  };

		    
	  $scope.delete = function(id){ 
		  return userFactory.delete(id);
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
	$scope.user = {};
	$scope.saving = false;
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
				$scope.saving = true;
				userFactory.add($scope.user).then(function() {
					$state.go('dashboard.user');
				}, function(error) {
					$scope.saving = false;
					$scope.error = (error.data && error.data.message) || 'Unable to create user.';
				});
		};

				$scope.cancel = function () {
				$state.go('dashboard.user');
		};
})
      
userCntl.controller('userUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, userFactory, config) 	{
	$scope.user = $stateParams.user;
	$scope.originalEmail = $scope.user && $scope.user.email;
	$scope.saving = false;
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
				$scope.saving = true;
				userFactory.update($scope.originalEmail, $scope.user).then(function() {
					$state.go('dashboard.user');
				}, function(error) {
					$scope.saving = false;
					$scope.error = (error.data && error.data.message) || 'Unable to update user.';
				});
		};
		
				$scope.close = function () {
				$state.go('dashboard.user');
	  }
})

userCntl.controller('userDeleteController', function ($scope, $state, $modalInstance, id, userFactory) {

				$scope.ok = function () {
				userFactory.delete(id).then(function() {
					$modalInstance.close($scope.user);
					$state.go('dashboard.user');
				}, function() {
					$scope.error = 'Unable to delete user.';
				});
	  };

	  			$scope.cancel = function () {
	  			$modalInstance.dismiss('cancel');
	  };
	});
