
var monitorandcontrolCntl = angular.module('monitorandcontrolControllers', []);

monitorandcontrolCntl.controller('monitorandcontrolListControllers',function($scope, $state,$stateParams, $modal,$location,$http,$rootScope, monitorandcontrolFactory, config) {
	  
	  $scope.sortType     = 'id';
	  $scope.sortReverse  = false;
	  $scope.searchFish   = '';
	  $scope.show_more = 'true';
	  
	  // Pagination
	  $scope.currentPage = 0;
	  $scope.pageSize = 50;
	  $scope.totalRecords = 0;
	  $scope.handshake_Data = [];
	  $scope.filteredData = [];
	  $scope.loading = false;
	  
	  // Checkbox filters
	  $scope.mcbtrip_check = true;
	  $scope.contactorfailure_check = true;
	  $scope.mainssupplyoff_check = true;
	  $scope.dooropen_check = true;
	  $scope.spdfailure_check = true;
	  $scope.nooutput_check = true;
	  $scope.manualmode_check = true;
	  $scope.no_check = true;
	  $scope.off_check = true;
	  $scope.goodgprsconnectivity_check = true;
	  $scope.poorgprsconnectivity_check = true;
	  
	  $scope.selectedDistrict = ($rootScope.privilege && $rootScope.privilege.district) ? $rootScope.privilege.district : 'ALL';
	  $scope.selectedMandal = ($rootScope.privilege && $rootScope.privilege.mandal) ? $rootScope.privilege.mandal : 'ALL';
	  $scope.select_gp = ($rootScope.privilege && $rootScope.privilege.gp) ? $rootScope.privilege.gp : 'ALL';
	  $scope.districts = config.districts;
	  
	  $scope.datePicker = { date: { startDate: null, endDate: null } };
	  $scope.opts = {
		  locale: { applyClass: 'btn-green', applyLabel: "Apply", fromLabel: "From", format: "YYYY-MM-DD", toLabel: "To", cancelLabel: 'Cancel', customRangeLabel: 'Custom range' },
		  ranges: {
			  'Today': [moment(), moment()],
			  'Yesterday': [moment().subtract(1, 'days'), moment()],
			  'Last 7 Days': [moment().subtract(6, 'days'), moment()],
			  'This Month': [moment().startOf('month'), moment().endOf('month')],
			  'Last Month': [moment().subtract(29, 'days'), moment()]
		  }
	  };
	  
	  $scope.qs_params = '?district=' + $scope.selectedDistrict + '&mandal=' + $scope.selectedMandal + '&gp=' + $scope.select_gp;
	  
	  $scope.loadPage = function(page) {
		  if ($scope.loading) return;
		  $scope.loading = true;
		  $scope.currentPage = page;
		  
		  monitorandcontrolFactory.getAllHandShake($scope.qs_params, page, $scope.pageSize).then(function(data){
		        var newData = data.data;
		        if (page === 0) {
			        $scope.handshake_Data = newData;
		        } else {
			        $scope.handshake_Data = $scope.handshake_Data.concat(newData);
		        }
		        $scope.applyFilters();
		        $scope.loading = false;
		    }, function(error) {
			    $scope.loading = false;
		    });
	  };
	  
  $scope.applyFilters = function() {
	  var checks = [
		  $scope.mcbtrip_check, $scope.contactorfailure_check, $scope.mainssupplyoff_check,
		  $scope.dooropen_check, $scope.spdfailure_check, $scope.nooutput_check,
		  $scope.manualmode_check, $scope.no_check, $scope.off_check,
		  $scope.goodgprsconnectivity_check, $scope.poorgprsconnectivity_check
	  ];
	  
	  if (!$scope.handshake_Data) {
		  $scope.filteredData = [];
		  return;
	  }
	  
	  var searchText = ($scope.searchFish || '').toLowerCase();
	  
	  // If all checkboxes are checked and no search text, show all
	  var allChecked = checks.every(function(c) { return c === true; });
	  if (allChecked && !searchText) {
		  $scope.filteredData = $scope.handshake_Data;
		  return;
	  }
	  
	  $scope.filteredData = [];
	  for (var i = 0; i < $scope.handshake_Data.length; i++) {
		  var item = $scope.handshake_Data[i];
		  
		  // Text search
		  if (searchText) {
			  var textMatch = false;
			  if (item.device_name && item.device_name.toLowerCase().indexOf(searchText) !== -1) textMatch = true;
			  if (item.id && item.id.toLowerCase().indexOf(searchText) !== -1) textMatch = true;
			  if (item.dcu_details && item.dcu_details.name && item.dcu_details.name.toLowerCase().indexOf(searchText) !== -1) textMatch = true;
			  if (!textMatch) continue;
		  }
		  
		  var add_obj = false;
		  
		  if(checks[0] && item.dcu_details.mcb_trip == 1) add_obj = true;
		  if(checks[1] && item.dcu_details.cnt_status == 1) add_obj = true;
		  if(checks[2] && item.dcu_details.main_supply_status == 1) add_obj = true;
		  if(checks[3] && item.dcu_details.door_status == 1) add_obj = true;
		  if(checks[4] && item.dcu_details.spd_status == 1) add_obj = true;
		  if(checks[5] && item.dcu_details.main_supply_status == 1) add_obj = true;
		  if(checks[6] && item.dcu_details.manual_mode_status == 1) add_obj = true;
		  if(checks[7] && item.dcu_details.light_status == 1) add_obj = true;
		  if(checks[8] && item.dcu_details.light_status == 0) add_obj = true;
		  if(checks[9] && item.dcu_details.csq > 15) add_obj = true;
		  if(checks[10] && item.dcu_details.csq < 15) add_obj = true;
		  
		  if(add_obj) {
			  $scope.filteredData.push(item);
		  }
	  }
  };
	  
	  // Watch checkbox changes
	  var checkboxes = ['mcbtrip_check', 'contactorfailure_check', 'mainssupplyoff_check',
		  'dooropen_check', 'spdfailure_check', 'nooutput_check', 'manualmode_check',
		  'no_check', 'off_check', 'goodgprsconnectivity_check', 'poorgprsconnectivity_check'];
	  
	  checkboxes.forEach(function(name) {
		  $scope.$watch(name, function() {
			  $scope.applyFilters();
		  });
	  });
	  
	  // Watch text search
	  $scope.$watch('searchFish', function() {
		  $scope.applyFilters();
	  });
	  
	  monitorandcontrolFactory.getAllCount($scope.qs_params).then(function(data){
	        $scope.count_stats = data.data;
	        $scope.totalRecords = data.data.total_devices || 0;
	  });
	  
	  $scope.loadPage(0);
	  
	  $scope.search = function() {
		  var dateParams = '';
		  if ($scope.datePicker && $scope.datePicker.date && $scope.datePicker.date.startDate && $scope.datePicker.date.endDate) {
			  var startDate = moment($scope.datePicker.date.startDate).format('YYYY-MM-DD');
			  var endDate = moment($scope.datePicker.date.endDate).format('YYYY-MM-DD');
			  dateParams = '&start_date=' + startDate + '&end_date=' + endDate;
		  }
		  $scope.qs_params = '?district=' + $scope.selectedDistrict + '&mandal=' + $scope.selectedMandal + '&gp=' + $scope.select_gp + dateParams;
		  monitorandcontrolFactory.getAllCount($scope.qs_params).then(function(data){
		        $scope.count_stats = data.data;
		        $scope.totalRecords = data.data.total_devices || 0;
		  });
		  $scope.loadPage(0);
	  };
	  
	  $scope.nextPage = function() {
		  $scope.loadPage($scope.currentPage + 1);
	  };
	  
	  $scope.prevPage = function() {
		  if ($scope.currentPage > 0) {
			  $scope.loadPage($scope.currentPage - 1);
		  }
	  };
	  
	  $scope.hasMoreData = function() {
		  return $scope.handshake_Data.length > 0 && $scope.handshake_Data.length >= ($scope.currentPage + 1) * $scope.pageSize;
	  };
	  
	  $scope.handle_showmore_event = function () {
		  $scope.show_more = false;
	  }
	  
	  $scope.selected_dcu = {};
	  monitorandcontrolFactory.getAllDcuNames().then(function(data){
	        $scope.dcu_data = data.data;
	    });
	  
	  $scope.filter = function () {
		  console.log($scope.selected_dcu.name.name)
	  };
	  
	  monitorandcontrolFactory.getByID().then(function(data){
		  $scope.meter_data = data.data;
	  });
	  
	 $scope.gotoswitchpoint = function (gateway_serial_number) {
		  $scope.gateway_serial_number=gateway_serial_number
		  $state.go('dashboard.switchpoint',{gateway_serial_number:$scope.gateway_serial_number})
	  };
	
	  $scope.turn_on_light = function (obj) {
		  $scope.obj = obj;
		$scope.qs_params = '?device_serial_number='+$scope.obj.dcu_details.gateway_serial_number + '&device_identifier='+$scope.obj.dcu_details.serial_number;
	     if($scope.obj.dcu_details.light_status == 1) {
	   
			monitorandcontrolFactory.turnOffLights($scope.qs_params).then(function(data){
		      });
	     } else {
	    
	    	 monitorandcontrolFactory.turnOnLights($scope.qs_params).then(function(data){
		      });
	     }
	     
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
     	 $state.go('dashboard.dcu_edit', {dcu : $scope.dcu});
     }
	  
	  $scope.getMandalOnSelect = function(selectedDistrict) {
			monitorandcontrolFactory.getByMandal($scope.selectedDistrict).then(function(data) {
				$scope.mandal_list = data.data;
			});
		}

		$scope.getGpOnSelect = function(selectedMandal) {
				monitorandcontrolFactory.getByGp($scope.selectedMandal).then(function(data) {
				$scope.gp_list = data.data;
				
			});
		}
		
		$scope.getMandalOnSelect($scope.selectedDistrict);
		$scope.getGpOnSelect($scope.selectedMandal);
	
})

monitorandcontrolCntl.filter('myFormat', function() {
	return function(items) {
		return items;
	};
});

