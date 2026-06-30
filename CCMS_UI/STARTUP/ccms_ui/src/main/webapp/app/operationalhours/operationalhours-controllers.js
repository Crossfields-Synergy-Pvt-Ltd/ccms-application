	
var operationalCntl = angular.module('operationalControllers', []);

operationalCntl.controller('operationalListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,operationalFactory, config) {

	
	
	 
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  operationalFactory.getAll('1905HY3P3C000904').then(function(data){
	        $scope.listData = data.data;
	    });
	  
	  
	  $scope.selected_dcu = {};

	  $scope.filter = function() {
		var dist = $scope.selectedDistrict || 'ALL';
		var mandal = $scope.selectedMandal || 'ALL';
		var gp = $scope.select_gp || 'ALL';
		var qs = '?district='+dist+ '&mandal='+mandal+'&gp='+gp;
		operationalFactory.getAllDcuNames(qs).then(function(data) {
			$scope.dcu_data = data.data;
		});
	  };

	  operationalFactory.getAllDcuNames().then(function(data){
	        $scope.dcu_data = data.data;
	    });
	  
	  $scope.datePicker = { date: {startDate: new Date(), endDate: new Date()} };
	  
	    $scope.opts = {
	      locale: {
	        applyClass: 'btn-green',
	        applyLabel: "Apply",
	        fromLabel: "From",
	        format: "YYYY-MM-DD",
	        toLabel: "To",
	        cancelLabel: 'Cancel',
	        customRangeLabel: 'Custom range' },

	      ranges: {
	         'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
	        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
	        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
	        'This Month': [moment().startOf('month'), moment().endOf('month')],
	        'Last Month': [moment().subtract(29, 'days'), moment()] } };


	   
	    $scope.showdate = function(gateway_identifier) {
		     console.log($scope.selected_dcu.name.gateway_identifier)
		     console.log($scope.datePicker.date.startDate);
	    	 console.log($scope.datePicker.date.endDate);
	    	 
	    	 var start_date = $scope.datePicker.date.startDate
	    	  var dayWrapper = moment(start_date); 
	    	  var dayString = dayWrapper.format("DD/MM/YYYY"); 
	    	  var start_date = dayString
	    	  console.log(start_date)
	    	  
	    	   var end_Date = $scope.datePicker.date.endDate
	    	  var dayWrapper = moment(end_Date); 
	    	  var dayString = dayWrapper.format("DD/MM/YYYY"); 
	    	  var end_Date = dayString
	    	  console.log(end_Date)
	
			$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + '&start_date=' + start_date + '&end_date='+ end_Date;
			console.log($scope.qs_params)
			
			operationalFactory.getAllOperationalHourByDate($scope.qs_params).then(function(data) {
//			 			$scope.list = data.data;
			 			$scope.todos = data.data;
			 			
			 			$scope.list = [];
						  $scope.itemsPerPage = 25;
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
	    
	    $scope.export_operationalhour = function(gateway_identifier) {
		     console.log($scope.selected_dcu.name.gateway_identifier)
		     console.log($scope.datePicker.date.startDate);
	    	 console.log($scope.datePicker.date.endDate);
	    	 
	    	 var start_date = $scope.datePicker.date.startDate
	    	  var dayWrapper = moment(start_date); 
	    	  var dayString = dayWrapper.format("DD/MM/YYYY"); 
	    	  var start_date = dayString
	    	  console.log(start_date)
	    	  
	    	   var end_Date = $scope.datePicker.date.endDate
	    	  var dayWrapper = moment(end_Date); 
	    	  var dayString = dayWrapper.format("DD/MM/YYYY"); 
	    	  var end_Date = dayString
	    	  console.log(end_Date)
	

			
			$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + '&start_date=' +start_date + '&end_date='+ end_Date;
			console.log($scope.qs_params)
			
			operationalFactory.getAllExport($scope.qs_params).then(function(data) {
			 			$scope.list = data.data;
			});
		
	}

		$scope.selectedDistrict = '';
		$scope.districts = config.districts;

		$scope.getMandalOnSelect = function(selectedDistrict) {
			operationalFactory.getByMandal($scope.selectedDistrict).then(
					function(data) {
						$scope.mandal_list = data.data;
					});
		}

		$scope.getGpOnSelect = function(selectedMandal) {
			operationalFactory.getByGp($scope.selectedMandal).then(
					function(data) {
						$scope.gp_list = data.data;

					});
		}

		$scope.getVillageOnSelect = function(select_gp) {
			operationalFactory.getByVillage($scope.select_gp).then(
					function(data) {
						$scope.village_list = data.data;
					});
		}

	    
	  });

	 



