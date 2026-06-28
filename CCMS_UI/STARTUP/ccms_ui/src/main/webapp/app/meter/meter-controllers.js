
var meterCntl = angular.module('meterControllers', []);

meterCntl.controller('meterListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, meterFactory) {

	 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	console.log("SERIAL NUMBER : " + $scope.gateway_serial_number)
	
	 
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  meterFactory.getAllMeterByDeviceID($scope.gateway_serial_number ).then(function(data){
		  console.log($scope.gateway_serial_number)
	        $scope.listData = data.data;
	    });
	
	
	  $scope.add_meter = function () {
		  $state.go('dashboard.meter_add', {gateway_serial_number:$scope.gateway_serial_number})
	  };
	  
	  $scope.dcu_list = function () {
		  $state.go('dashboard.dcu')
	  };
	 
	  $scope.meter_calibration = function () {
		  $state.go('dashboard.meter_add')
	  };
	  
	  $scope.associate_tag = function () {
		  $state.go('dashboard.meter_add')
	  };
	  
	  
	/*	    
	  $scope.delete = function(id){ 
		  meterFactory.delete(id);
	  }*/
	  
	  $scope.delete_meter = function (id) {	
	         var modalInstance = $modal.open({
	             templateUrl: 'app/common/delete.html',
	             controller: 'meterDeleteController',
	             resolve: {
	                 id: function () {
	                     return id;
	                 }
	             }
	         });
	     }
	  
	  $scope.update_meter = function (obj) {
     	 $scope.meter = obj;
     	 console.log($scope.meter);
     	 $state.go('dashboard.meter_edit', {meter : $scope.meter});
      }
});


meterCntl.controller('meterAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, meterFactory) 	{
 
	$scope.gateway_serial_number = $stateParams.gateway_serial_number;
	
	$scope.subtype = [ {
		state : "Single Phase-LG",
		code : "Single Phase-LG"
	},
	
	{
		state : "Three Phase-LG",
		code : "Three Phase-LG"
	},
	{
		state : "Single Phase-LG with Single Op_Code",
		code : "Single Phase-LG with Single Op_Code"
	},
	{
		state : "Three Phase-L&amp;T",
		code : "Three Phase-L&amp;T"
	},
	{
		state : "Maxwell three phase",
		code : "Maxwell three phase"
	},
	{
		state : "Three Phase-Adept (7)",
		code : "Three Phase-Adept (7)"
	},
	
	{
		state : "Microstar",
		code : "Microstar"
	},
	{
		state : "Abacus",
		code : "Abacus"
	},
	{
		state : " Water meter",
		code : " Water meter"
	},
	{
		state : "Schneider meter",
		code : "Schneider meter"
	},
	{
		state : "Three Phase-Adept (12)",
		code : "Three Phase-Adept (12)"
	},
	
	{
		state : "L&amp;T Nova (Three Phase)",
		code : "L&amp;T Nova (Three Phase)"
	},
	{
		state : "Water meter 2",
		code : "Water meter 2"
	},
	{
		state : " Adept water meter",
		code : " Adept water meter"
	},
	{
		state : "Abacus JP CRC NON-STD",
		code : "Abacus JP CRC NON-STD"
	},
	{
		state : "	Generic Modbus",
		code : "	Generic Modbus"
	},
	
	{
		state : "Abacus JP CRC STD",
		code : "Abacus JP CRC STD"
	},
	{
		state : " Schneider EM6400 meter",
		code : " Schneider EM6400 meter"
	},
	{
		state : " Naina",
		code : " Naina"
	},
	{
		state : "UTL Single Phase",
		code : "UTL Single Phase"
	},
	
	{
		state : " Pulse energy meter",
		code : " Pulse energy meter	"
	},
	{
		state : "  IAR energy meter",
		code : "  IAR energy meter"
	},
	
	];
	 
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
				$scope.meter;
				$scope.meter.gateway_serial_number = $scope.gateway_serial_number;
				console.log($scope.meter)
				
				meterFactory.add($scope.meter);
				$state.reload();
				$state.go('dashboard.meter');
		};

				$scope.cancel = function () {
				$state.go('dashboard.meter');
		};
});
      
meterCntl.controller('meterUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,meterFactory) 	{
	
	$scope.subtype = [ {
		state : "Single Phase-LG",
		code : "Single Phase-LG"
	},
	
	{
		state : "Three Phase-LG",
		code : "Three Phase-LG"
	},
	{
		state : "Single Phase-LG with Single Op_Code",
		code : "Single Phase-LG with Single Op_Code"
	},
	{
		state : "Three Phase-L&amp;T",
		code : "Three Phase-L&amp;T"
	},
	{
		state : "Maxwell three phase",
		code : "Maxwell three phase"
	},
	{
		state : "Three Phase-Adept (7)",
		code : "Three Phase-Adept (7)"
	},
	
	{
		state : "Microstar",
		code : "Microstar"
	},
	{
		state : "Abacus",
		code : "Abacus"
	},
	{
		state : " Water meter",
		code : " Water meter"
	},
	{
		state : "Schneider meter",
		code : "Schneider meter"
	},
	{
		state : "Three Phase-Adept (12)",
		code : "Three Phase-Adept (12)"
	},
	
	{
		state : "L&amp;T Nova (Three Phase)",
		code : "L&amp;T Nova (Three Phase)"
	},
	{
		state : "Water meter 2",
		code : "Water meter 2"
	},
	{
		state : " Adept water meter",
		code : " Adept water meter"
	},
	{
		state : "Abacus JP CRC NON-STD",
		code : "Abacus JP CRC NON-STD"
	},
	{
		state : "	Generic Modbus",
		code : "	Generic Modbus"
	},
	
	{
		state : "Abacus JP CRC STD",
		code : "Abacus JP CRC STD"
	},
	{
		state : " Schneider EM6400 meter",
		code : " Schneider EM6400 meter"
	},
	{
		state : " Naina",
		code : " Naina"
	},
	{
		state : "UTL Single Phase",
		code : "UTL Single Phase"
	},
	
	{
		state : " Pulse energy meter",
		code : " Pulse energy meter	"
	},
	{
		state : "  IAR energy meter",
		code : "  IAR energy meter"
	},
	
	];
	
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
	 // $scope.meter = $stateParams.meter;
	//  console.log($scope.gateway_serial_number)
	  
				$scope.meter = $stateParams.meter;
				console.log($scope.meter)
	  $scope.gateway_serial_number = $scope.meter.gateway_serial_number;
		$scope.ok=function(){
				$scope.meter;
				console.log($scope.meter)
				meterFactory.add($scope.meter);
				$state.reload();
				$state.go('dashboard.meter');
		};
		
				$scope.cancel = function () {
				$state.go('dashboard.meter');
	  }
})


meterCntl.controller('meterDeleteController', function ($scope, $state, $modalInstance, id, meterFactory) {

				$scope.ok = function () {
				meterFactory.delete(id);
				$modalInstance.close($scope.meter);
				$state.reload();
	  };

	  			$scope.cancel = function () {
	  			$modalInstance.dismiss('cancel');
	  };
	});