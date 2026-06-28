
var dcuCntl = angular.module('dcuControllers', []);

dcuCntl.controller('dcuListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  dcuFactory.getAllHandShake().then(function(data){
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
		  $state.go('user.dcu_add', {user : $scope.dcu})
	  };
	  
	  $scope.modifydcu = function (gateway_serial_number) {
		  $scope.gateway_serial_number=gateway_serial_number
		  console.log(gateway_serial_number)
		  $state.go('user.dcu_modify',{gateway_serial_number:$scope.gateway_serial_number})
	  };
	  
	  $scope.meterlist = function (gateway_serial_number) {
		  $scope.gateway_serial_number=gateway_serial_number
		  $state.go('user.meter',{gateway_serial_number:$scope.gateway_serial_number})
	  };
	  
	  

		    
	  $scope.delete = function(id){ 
		  userFactory.delete(id);
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
	  
	  $scope.update = function (obj) {
     	 $scope.dcu = obj;
     	 console.log($scope.dcu);
     	 $state.go('user.dcu_edit', {dcu : $scope.dcu});
      }
});


dcuCntl.controller('dcuAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory) 	{
	$scope.locatedcuonmap = function () {
			  var map;
			    function initialize() {
			        var myLatlng = new google.maps.LatLng(15.350328, 75.110198);
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
			    
			   // $scope.map_location	= document.getElementById("latlong").value ;
		  };
		  
		 
				$scope.ok = function () {	
				$scope.dcu;
				console.log($scope.dcu)
				
				dcuFactory.add($scope.dcu);
				$state.reload();
				$state.go('user.dcu');
		};

				$scope.cancel = function () {
				$state.go('user.dcu');
		};
		
})
      
dcuCntl.controller('dcuUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,dcuFactory) 	{
	
	$scope.locatedcuonmap = function () {
	       
	  	
		  
		  var map;
		    function initialize() {
		        var myLatlng = new google.maps.LatLng(15.350328, 75.110198);
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
		    
		   // $scope.map_location	= document.getElementById("latlong").value ;
	  };
 
	       $scope.dcu = $stateParams.dcu;
           console.log($scope.dcu)
	  if($scope.dcu == null){
	         $scope.dcu = $stateParams.gateway_serial_number;
	         console.log($scope.dcu)
	         dcuFactory.getByID('1905HY3P3C000904').then(function(data){
	 	        $scope.obj = data.data;
	 	    });
}    
				
				$scope.update=function(){
				$scope.dcu;
				console.log($scope.dcu)
				dcuFactory.add($scope.dcu);
				$state.reload();
				$state.go('user.dcu');
		};
		
				$scope.close = function () {
				$state.go('user.dcu');
	  }
})


dcuCntl.controller('dcuModifyControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, dcuFactory) {
	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	console.log($scope.gateway_serial_number)
	   
	  dcuFactory.getByIDmodify(	$scope.gateway_serial_number).then(function(data){
	        $scope.dcu = data.data;
	        $scope.dcu.dcu_id = $scope.gateway_serial_number;
	        console.log($scope.dcu )
	        console.log($scope.gateway_serial_number )
	    });
	  
	
	   $scope.apply=function(){
		   $scope.dcu.dcu_id =  $scope.gateway_serial_number;
		   console.log($scope.dcu )
		$scope = dcuFactory.modifysystemconfig($scope.dcu);
		$state.reload();
		$state.go('user.dcu');
};

		$scope.close = function () {
		$state.go('user.dcu');
}
	

		   $scope.loadDefualtconfigsettings = function(dcu_id){
			   console.log(dcu_id)
			   $scope.dcu_id =  dcu_id;
			   console.log($scope.dcu_id)
			   alert(' UPDATED 111: ' + $scope.dcu_id)
			   alert(dcu_id)
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