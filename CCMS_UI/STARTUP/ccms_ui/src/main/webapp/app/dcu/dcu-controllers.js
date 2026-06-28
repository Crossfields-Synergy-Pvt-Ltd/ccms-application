
var dcuCntl = angular.module('dcuControllers', []);

dcuCntl.controller('dcuListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	$scope.searchFish = $rootScope.searchFish || '';     // set the default search/filter term
	  var dist = ($rootScope.previlage && $rootScope.previlage.dist) ? $rootScope.previlage.dist : 'ALL';
	  var mondal = ($rootScope.previlage && $rootScope.previlage.mondal) ? $rootScope.previlage.mondal : 'ALL';
	  var gp = ($rootScope.previlage && $rootScope.previlage.gp) ? $rootScope.previlage.gp : 'ALL';
	  $scope.qs_params = '?distrtict='+dist+ '&mandal='+mondal+'&gp='+gp;
		 
	
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
	  
	  $scope.syncnodeconfig = function (gateway_serial_number) {
		  console.log($scope.gateway_serial_number)
		   dcuFactory.getSyncByID(gateway_serial_number).then(function(){
		   })
	 
	  };
	 
	  $scope.syncsheduleconfig = function (gateway_serial_number) {
		  console.log($scope.gateway_serial_number)
		  
		  var qs_params = '?id='+ gateway_serial_number ;
		  
		   dcuFactory.getScheduleSyncByID(qs_params).then(function(){
		   })
	 
	  };
	  $scope.delete = function(id){ 
		  dcuFactory.delete(id);
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
	 $scope.listData;
	dcuFactory.getAllConfSyncStatus($scope.gateway_serial_number).then(function(data){
        $scope.listData = data.data;
    });
	
	
	    $scope.closemodule=function () {
	    	 $modalInstance.dismiss('cancel');
	    }
}]);

dcuCntl.controller('dcuAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory, config) 	{
	
	 $scope.selected_scheduler = {};
	  dcuFactory.getAllScheduler("schedule_1").then(function(data){
	   $scope.scheduler_data = data.data;
	    });
	
	  /*$scope.selecte_distict = '';
		$scope.districts = ["Srikakulam-11" ,"Visakhapatnam-13" ,"Prakasam-18" ,"Nellore-19" ,"Kadapa-20", "Kurnool-21"];
		 $scope.searchTerm;
	    $scope.clearSearchTerm = function() {
	      $scope.searchTerm = '';
	    };
	    $element.find('input').on('keydown', function(ev) {
	        ev.stopPropagation();
	    });*/
	  
	$scope.selecte_distict = '';
	$scope.districts = config.districts; 
	 
   	  
   	 
    $scope.getMandalOnSelect = function(distict) {
   		dcuFactory.getByMondal($scope.dcu.distict).then(function(data) {
   				$scope.mandal_list = data.data;
   			});
   		}

   		$scope.getGpOnSelect = function(mondal) {
   			dcuFactory.getByGp($scope.dcu.mondal).then(function(data) {
   				$scope.gp_list = data.data;
   				
   			});
   		}
   		$scope.getVillageOnSelect = function(gp) {
   			dcuFactory.getByVillage($scope.dcu.gp).then(function(data) {
   			$scope.village_list = data.data;
   			});
   		}
	
   		$scope.locatedcuonmap = function () {
  		  var map;
  		    function initialize() {
  		    	
  		    	 $scope.map_location_lat	= document.getElementById("lat").value ;
  		    	 $scope.map_location_long	= document.getElementById("long").value ;
  				  
  		        var myLatlng = new google.maps.LatLng( $scope.map_location_lat,  $scope.map_location_long);
  		        var myOptions = {
  		                  zoom: 16,
  		                  center: myLatlng,
  		                  mapTypeId: google.maps.MapTypeId.ROADMAP
  		              };
  		        
  		        map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
  		        var marker = new google.maps.Marker({
  		            draggable: true,
  		            position: myLatlng,
  		            map: map,
  		            title: "Your location"
  		        });
  		        google.maps.event.addListener(marker, 'dragend', function (event) {
  		            document.getElementById("latlong").value = event.latLng.lat() + " , "+event.latLng.lng(); 
  		            //document.getElementById("long").value = event.latLng.lng();
  		            infoWindow.open(map, marker);
  		        });
  		    }
  		    google.maps.event.addDomListener(window, "load", initialize());
  		    
  	  };
				$scope.ok = function (schedules_name) {	
				$scope.schedules_name=schedules_name;
				alert($scope.schedules_name)
				$scope.dcu;
				dcuFactory.add($scope.dcu);
				$state.reload();
				$state.go('dashboard.dcu');
		};

				$scope.cancel = function () {
				$state.go('dashboard.dcu');
		};
		
})
      
dcuCntl.controller('dcuUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,dcuFactory, config) 	{
	
	$scope.dcu = $stateParams.gateway_serial_number;
	console.log($scope.dcu)
	if ($scope.dcu  == null){
	$scope.dcu = $stateParams.dcu;
	console.log($scope.dcu)
	}
	  dcuFactory.getAllScheduler().then(function(data){
	   $scope.scheduler_data = data.data;
	   $scope.locatedcuonmap();
	    });
	  
	
	   		dcuFactory.getByMondal($scope.dcu.distict).then(function(data) {
	   				$scope.mandal_list = data.data;
	   			});
	   		
	   			dcuFactory.getByGp($scope.dcu.mondal).then(function(data) {
	   				$scope.gp_list = data.data;
	   				
	   			});
	   		
	$scope.selecte_distict = '';
	 $scope.districts = config.districts; 
   	  
   	 
   	  $scope.getMandalOnSelect = function(distict) {
   		dcuFactory.getByMondal($scope.dcu.distict).then(function(data) {
   				$scope.mandal_list = data.data;
   			});
   		}

   		$scope.getGpOnSelect = function(mondal) {
   			dcuFactory.getByGp($scope.dcu.mondal).then(function(data) {
   				$scope.gp_list = data.data;
   				
   			});
   		}
   		$scope.selected_scheduler = {};
  	  dcuFactory.getAllScheduler().then(function(data){
  	   $scope.scheduler_data = data.data;
  	    });
  	  
	$scope.locatedcuonmap = function () {
		  var map;
		    function initialize() {
		    	console.log($scope.dcu.latitude)
		    	console.log($scope.dcu.longitude)
		    	 $scope.map_location_lat	= document.getElementById("lat").value ;
		    	 $scope.map_location_long	= document.getElementById("long").value ;
				  
		        var myLatlng = new google.maps.LatLng( $scope.map_location_lat, $scope.map_location_long);
		        var myOptions = {
		                  zoom: 16,
		                  center: myLatlng,
		                  mapTypeId: google.maps.MapTypeId.ROADMAP
		              };
		        
		        map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
		        var marker = new google.maps.Marker({
		            draggable: true,
		            position: myLatlng,
		            map: map,
		            title: "Your location"
		        });
		        google.maps.event.addListener(marker, 'dragend', function (event) {
		            document.getElementById("latlong").value = event.latLng.lat() + " , "+event.latLng.lng(); 
		            //document.getElementById("long").value = event.latLng.lng();
		            infoWindow.open(map, marker);
		        });
		    }
		    google.maps.event.addDomListener(window, "load", initialize());
		    
	  };
 
	   
	  if($scope.dcu == null){
	         $scope.dcu = $stateParams.gateway_serial_number;
	         
}    
				
				$scope.update=function(){
				$scope.dcu;
				dcuFactory.add($scope.dcu);
				$state.reload();
				$state.go('dashboard.dcu');
		};
		
				$scope.close = function () {
				$state.go('dashboard.dcu');
	  }
})


dcuCntl.controller('dcuModifyControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	  dcuFactory.getByIDmodify(	$scope.gateway_serial_number).then(function(data){
	        $scope.dcu = data.data;
	        $scope.dcu.dcu_id = $scope.gateway_serial_number;
	    });
	  
	
	   $scope.apply=function(){
		$scope.dcu.dcu_id =  $scope.gateway_serial_number;
		$scope = dcuFactory.modifysystemconfig($scope.dcu);
		$state.reload();
		$state.go('dashboard.dcu');
};

		$scope.close = function () {
		$state.go('dashboard.dcu');
}
	

		   $scope.loadDefualtconfigsettings = function(dcu_id){
			   $scope.dcu_id =  dcu_id;
			   dcuFactory.load_default_conf($scope.dcu_id);
			   	$state.reload();
	};

		
});



dcuCntl.controller('dcuDeleteController', function ($scope, $state, $modalInstance, id, dcuFactory) {

				$scope.ok = function () {
				dcuFactory.delete(id);
				$modalInstance.close($scope.dcu);
				$state.reload();
	  };

	  			$scope.cancel = function () {
	  			$modalInstance.dismiss('cancel');
	  };
	});