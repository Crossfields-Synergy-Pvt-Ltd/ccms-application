
var dcuCntl = angular.module('dcuControllers', []);

dcuCntl.controller('dcuListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory, inform) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	$scope.searchFish = $rootScope.searchFish || '';     // set the default search/filter term
	  var dist = ($rootScope.privilege && $rootScope.privilege.district) ? $rootScope.privilege.district : 'ALL';
	  var mandal = ($rootScope.privilege && $rootScope.privilege.mandal) ? $rootScope.privilege.mandal : 'ALL';
	  var gp = ($rootScope.privilege && $rootScope.privilege.gp) ? $rootScope.privilege.gp : 'ALL';
	  $scope.qs_params = '?district='+dist+ '&mandal='+mandal+'&gp='+gp;
		 
	
		  $rootScope.searchFish = $scope.searchFish;
		  console.log($scope.searchFish.length)
		  $scope.filter_qs_params = $scope.qs_params + '&name='+ $scope.searchFish;
			console.log($scope.qs_params)
	
			if($rootScope.searchFish.length < 3){
				  dcuFactory.getAllHandShake($scope.qs_params).then(function(data){
				        //$scope.list = data.data;
					  
				       $scope.todos = data.data;
						  $scope.list = [];
						  $scope.itemsPerPage = 15;
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
			} else {
			dcuFactory.getByID($scope.filter_qs_params).then(function(data){
	 	      //  $scope.obj = data.data;
	 	        
	 	       $scope.todos = data.data;
				  $scope.list = [];
				  $scope.itemsPerPage = 15;
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
			}
			

		 
	  $scope.search = function() {
		  $rootScope.searchFish = $scope.searchFish;
		  console.log($scope.searchFish.length)
		  $scope.filter_qs_params = $scope.qs_params + '&name='+ $scope.searchFish;
			console.log($scope.qs_params)
			dcuFactory.getByID($scope.filter_qs_params).then(function(data){
		 	      //  $scope.obj = data.data;
		 	        
		 	       $scope.todos = data.data;
					  $scope.list = [];
					  $scope.itemsPerPage = 15;
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
			
	  }
	
	
	  $scope.add = function () {
		  $state.go('dashboard.dcu_add')

	  };
	  
	  $scope.update_dcu = function (obj) {
		  $scope.dcu=obj
		  $state.go('dashboard.dcu_edit', {dcu:$scope.dcu})
	  };
	  
	  $scope.modifydcu = function (gateway_serial_number) {
		  $scope.gateway_serial_number=gateway_serial_number
		  $state.go('dashboard.dcu_modify',{gateway_serial_number:$scope.gateway_serial_number})
	  };
	  
	  $scope.meterlist = function (gateway_serial_number) {
		  $scope.gateway_serial_number=gateway_serial_number
		  console.log($scope.gateway_serial_number)
		  $state.go('dashboard.meter',{gateway_serial_number:$scope.gateway_serial_number})
	  };
	  
	  function notify(message, type) {
    if (inform && inform.add) {
      inform.add(message, {ttl: type === "danger" ? 5000 : 3000, type: type || "success"});
    }
  }

  function reportOperation(promise, successMessage, failureMessage) {
    promise.then(function(response) {
      var status = response && response.data ? response.data : response;
      if (status && status.code === 0) {
        notify(status.message || failureMessage, "danger");
      } else {
        notify((status && status.message) || successMessage, "success");
      }
    }).catch(function(error) {
      notify(failureMessage + ": " + ((error && error.data) || (error && error.statusText) || "request failed"), "danger");
    });
  }

  $scope.syncnodeconfig = function(gateway_serial_number) {
    reportOperation(dcuFactory.getSyncByID(gateway_serial_number), "Node configuration sync triggered", "Node configuration sync failed");
  };

  $scope.syncsheduleconfig = function(gateway_serial_number) {
    reportOperation(dcuFactory.getScheduleSyncByID("?id=" + gateway_serial_number), "Scheduler configuration sync triggered", "Scheduler configuration sync failed");
  };

	  $scope.syncAllNodeConfig = function() {
    if (!window.confirm("Sync node configuration to every DCU?")) { return; }
    reportOperation(dcuFactory.syncAllNodeConf(), "Node configuration sync triggered for all DCUs", "Bulk node configuration sync failed");
  };

  $scope.syncAllSchedulerConfig = function() {
    if (!window.confirm("Sync scheduler configuration to every DCU?")) { return; }
    reportOperation(dcuFactory.syncAllSchedulerConf(), "Scheduler configuration sync triggered for all DCUs", "Bulk scheduler configuration sync failed");
  };
	  $scope.delete = function(id) {
    $scope.deleteconf(id);
  }
	  
	  $scope.deleteconf = function (id) {	
	         var modalInstance = $modal.open({
	             templateUrl: 'app/common/delete.html',
	             controller: 'dcuDeleteController',
	             resolve: {
	                 id: function () {
	                     return id;
	                 }
	             }
	         });
	     }
	  
	  
	  
	  
	  $scope.sync_config  = function (gateway_serial_number) {
		    
			console.log(gateway_serial_number);
		      var tmp = gateway_serial_number;
		      var modalInstance = $modal.open({
		      templateUrl: 'app/dcu/conf-sync-details.html',
		       controller: 'viewModelCntroler_updated',
		      resolve: {
		    	  gateway_serial_number: function () {
		           return gateway_serial_number;
		     }
		 }
		});
		};
	 
});



dcuCntl.controller('viewModelCntroler_updated', ['$scope','$modalInstance', 'dcuFactory','gateway_serial_number',function ($scope,  $modalInstance,dcuFactory,  gateway_serial_number) {
	   
	$scope.gateway_serial_number = gateway_serial_number;

	console.log($scope.gateway_serial_number);
	 $scope.listData = [];
	dcuFactory.getAllConfSyncStatus($scope.gateway_serial_number).then(function(data) {
        $scope.listData = angular.isArray(data.data) ? data.data : [];
    }).catch(function() {
        $scope.listData = [];
    });
	
	
	    $scope.closemodule=function () {
	    	 $modalInstance.dismiss('cancel');
	    }
}]);

dcuCntl.controller('dcuAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory, inform, config) 	{
	
	 $scope.selected_scheduler = {};
	  dcuFactory.getAllScheduler("schedule_1").then(function(data){
	   $scope.scheduler_data = data.data;
	    });
	
	  /*$scope.selectedDistrict = '';
		$scope.districts = ["Srikakulam-11" ,"Visakhapatnam-13" ,"Prakasam-18" ,"Nellore-19" ,"Kadapa-20", "Kurnool-21"];
		 $scope.searchTerm;
	    $scope.clearSearchTerm = function() {
	      $scope.searchTerm = '';
	    };
	    $element.find('input').on('keydown', function(ev) {
	        ev.stopPropagation();
	    });*/
	  
	$scope.selectedDistrict = '';
	$scope.districts = config.districts; 
	 
   	  
   	 
    $scope.getMandalOnSelect = function(district) {
   		dcuFactory.getByMandal($scope.dcu.district).then(function(data) {
   				$scope.mandal_list = data.data;
   			});
   		}

   		$scope.getGpOnSelect = function(mandal) {
   			dcuFactory.getByGp($scope.dcu.mandal).then(function(data) {
   				$scope.gp_list = data.data;
   				
   			});
   		}
   		$scope.getVillageOnSelect = function(gp) {
   			dcuFactory.getByVillage($scope.dcu.gp).then(function(data) {
   			$scope.village_list = data.data;
   			});
   		}
	
		$scope.locatedcuonmap = function () {
			if (!window.google || !google.maps) {
				inform.add("Map is not available", {ttl: 5000, type: "danger"});
				return;
			}
			var lat = parseFloat($scope.dcu && $scope.dcu.lat) || 15.350328;
			var lng = parseFloat($scope.dcu && $scope.dcu.lang) || 75.110198;
			$scope.map_location_lat = lat;
			$scope.map_location_long = lng;
			var map = new google.maps.Map(document.getElementById("map_canvas"), {
				zoom: 16,
				center: new google.maps.LatLng(lat, lng),
				mapTypeId: google.maps.MapTypeId.ROADMAP
			});
			var marker = new google.maps.Marker({draggable: true, position: new google.maps.LatLng(lat, lng), map: map, title: "Your location"});
			google.maps.event.addListener(marker, 'dragend', function (event) {
				$scope.$apply(function () {
					$scope.dcu.lat = String(event.latLng.lat());
					$scope.dcu.lang = String(event.latLng.lng());
				});
			});
		};
		$scope.submitting = false;
		$scope.ok = function () {
			if ($scope.submitting) { return; }
			var dcu = angular.copy($scope.dcu || {});
			var serial = String(dcu.gateway_serial_number || dcu.serial_number || '').trim();
			if (!serial) {
				inform.add("Gateway serial number is required", {ttl: 5000, type: "danger"});
				return;
			}
			dcu.gateway_serial_number = serial;
			dcu.serial_number = dcu.serial_number || serial;
			$scope.submitting = true;
			dcuFactory.add(dcu).then(function (response) {
				var status = response && response.data ? response.data : response;
				if (!status || status.code !== 200) {
					inform.add((status && status.message) || "Unable to save DCU", {ttl: 5000, type: "danger"});
					return;
				}
				inform.add("DCU saved successfully", {ttl: 3000, type: "success"});
				$state.go('dashboard.dcu');
			}, function (error) {
				inform.add("Failed to save DCU: " + ((error && error.data) || (error && error.message) || (error && error.statusText) || "request failed"), {ttl: 5000, type: "danger"});
			}).finally(function () { $scope.submitting = false; });
		};

				$scope.cancel = function () {
				$state.go('dashboard.dcu');
		};
		
})
      
dcuCntl.controller('dcuUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,dcuFactory, inform, config) 	{
	
	$scope.dcu = $stateParams.dcu || $stateParams.gateway_serial_number;
	if (angular.isString($scope.dcu)) {
		$scope.dcu = {gateway_serial_number: $scope.dcu, serial_number: $scope.dcu};
	}
	if (!$scope.dcu) {
		$scope.dcu = {};
	}
	  dcuFactory.getAllScheduler().then(function(data){
	   $scope.scheduler_data = data.data;
	   $scope.locatedcuonmap();
	    });
	  
	
	   		dcuFactory.getByMandal($scope.dcu.district).then(function(data) {
	   				$scope.mandal_list = data.data;
	   			});
	   		
	   			dcuFactory.getByGp($scope.dcu.mandal).then(function(data) {
	   				$scope.gp_list = data.data;
	   				
	   			});
	   		
	$scope.selectedDistrict = '';
	 $scope.districts = config.districts; 
   	  
   	 
   	  $scope.getMandalOnSelect = function(district) {
   		dcuFactory.getByMandal($scope.dcu.district).then(function(data) {
   				$scope.mandal_list = data.data;
   			});
   		}

   		$scope.getGpOnSelect = function(mandal) {
   			dcuFactory.getByGp($scope.dcu.mandal).then(function(data) {
   				$scope.gp_list = data.data;
   				
   			});
   		}
	$scope.locatedcuonmap = function () {
		if (!window.google || !google.maps) {
			inform.add("Map is not available", {ttl: 5000, type: "danger"});
			return;
		}
		var lat = parseFloat($scope.dcu && $scope.dcu.lat) || 15.350328;
		var lng = parseFloat($scope.dcu && $scope.dcu.lang) || 75.110198;
		$scope.map_location_lat = lat;
		$scope.map_location_long = lng;
		var map = new google.maps.Map(document.getElementById("map_canvas"), {zoom: 16, center: new google.maps.LatLng(lat, lng), mapTypeId: google.maps.MapTypeId.ROADMAP});
		var marker = new google.maps.Marker({draggable: true, position: new google.maps.LatLng(lat, lng), map: map, title: "Your location"});
		google.maps.event.addListener(marker, 'dragend', function (event) {
			$scope.$apply(function () {
				$scope.dcu.lat = String(event.latLng.lat());
				$scope.dcu.lang = String(event.latLng.lng());
			});
		});
	};
 
	   
				$scope.submitting = false;
				$scope.update=function(){
					if ($scope.submitting || !$scope.dcu) { return; }
					var dcu = angular.copy($scope.dcu);
					dcu.gateway_serial_number = String(dcu.gateway_serial_number || dcu.serial_number || '').trim();
					if (!dcu.gateway_serial_number) {
						inform.add("Gateway serial number is required", {ttl: 5000, type: "danger"});
						return;
					}
					dcu.serial_number = dcu.serial_number || dcu.gateway_serial_number;
					$scope.submitting = true;
					dcuFactory.add(dcu).then(function (response) {
						var status = response && response.data ? response.data : response;
						if (!status || status.code !== 200) {
							inform.add((status && status.message) || "Unable to update DCU", {ttl: 5000, type: "danger"});
							return;
						}
						inform.add("DCU updated successfully", {ttl: 3000, type: "success"});
						$state.go('dashboard.dcu');
					}, function (error) {
						inform.add("Failed to update DCU: " + ((error && error.data) || (error && error.message) || (error && error.statusText) || "request failed"), {ttl: 5000, type: "danger"});
					}).finally(function () { $scope.submitting = false; });
				};
		
				$scope.close = function () {
				$state.go('dashboard.dcu');
	  }
})


dcuCntl.controller('dcuModifyControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory, inform) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	  dcuFactory.getByIDmodify(	$scope.gateway_serial_number).then(function(data){
	        $scope.dcu = data.data;
	        $scope.dcu.dcu_id = $scope.gateway_serial_number;
	    });
	  
	
	   $scope.apply=function(){
		$scope.dcu.dcu_id = $scope.gateway_serial_number;
		dcuFactory.modifysystemconfig($scope.dcu).then(function() {
			inform.add("DCU configuration saved successfully", {ttl: 3000, type: "success"});
			$state.go("dashboard.dcu");
		}).catch(function(error) {
			inform.add("Failed to save DCU configuration: " + ((error && error.data) || (error && error.statusText) || "request failed"), {ttl: 5000, type: "danger"});
		});
};

		$scope.close = function () {
		$state.go('dashboard.dcu');
}
	

		   $scope.loadDefualtconfigsettings = function(dcu_id){
			   $scope.dcu_id =  dcu_id;
			   dcuFactory.load_default_conf($scope.dcu_id).then(function() {
					inform.add("Default configuration loaded successfully", {ttl: 3000, type: "success"});
					$state.reload();
				}).catch(function(error) {
					inform.add("Failed to load default configuration: " + ((error && error.data) || (error && error.statusText) || "request failed"), {ttl: 5000, type: "danger"});
				});
	};

		
});



dcuCntl.controller('dcuDeleteController', function ($scope, $state, $modalInstance, id, dcuFactory, inform) {

				$scope.deleting = false;
				$scope.ok = function () {
					if ($scope.deleting) { return; }
					$scope.deleting = true;
					dcuFactory.delete(id).then(function (status) {
						if (!status || status.code === 0) {
							inform.add((status && status.message) || "Unable to delete DCU", {ttl: 5000, type: "danger"});
							return;
						}
						$modalInstance.close($scope.dcu);
						$state.reload();
					}, function (error) {
						inform.add("Failed to delete DCU: " + ((error && error.data) || (error && error.message) || (error && error.statusText) || "request failed"), {ttl: 5000, type: "danger"});
					}).finally(function () { $scope.deleting = false; });
	  };

	  			$scope.cancel = function () {
	  			$modalInstance.dismiss('cancel');
	  };
	});
