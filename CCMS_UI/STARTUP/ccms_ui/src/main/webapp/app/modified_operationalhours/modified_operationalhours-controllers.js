	
var modified_operationalCntl = angular.module('modified_operationalControllers', []);

modified_operationalCntl.controller('modified_operationalListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,modified_operationalFactory) {
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	  
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

	  
	  $scope.selected_dcu = {};
	  modified_operationalFactory.getAllDcuNames().then(function(data){
	        $scope.dcu_data = data.data;
	    });
	   
	    $scope.showdate = function(gateway_identifier) {
	    	 var start_date = $scope.datePicker.date.startDate
	    	  var dayWrapper = moment(start_date); 
	    	  var dayString = dayWrapper.format("YYYYMMDD"); 
	    	  var start_date = dayString
	    	  
	    	  
	    	   var end_Date = $scope.datePicker.date.endDate
	    	  var dayWrapper = moment(end_Date); 
	    	  var dayString = dayWrapper.format("YYYYMMDD"); 
	    	  var end_Date = dayString
	    	  
	
			$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + '&start_date=' + start_date + '&end_date='+ end_Date;
			
			
			modified_operationalFactory.getAllById($scope.qs_params).then(function(data) {
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
	    	 
	    	 var start_date = $scope.datePicker.date.startDate
	    	  var dayWrapper = moment(start_date); 
	    	  var dayString = dayWrapper.format("YYYYMMDD"); 
	    	  var start_date = dayString
	    	  
	    	  
	    	   var end_Date = $scope.datePicker.date.endDate
	    	  var dayWrapper = moment(end_Date); 
	    	  var dayString = dayWrapper.format("YYYYMMDD"); 
	    	  var end_Date = dayString
			
			$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + '&start_date=' + start_date + '&end_date='+ end_Date;
			
			modified_operationalFactory.getAllExport($scope.qs_params).then(function(data) {
			 			$scope.list = data.data;
			});
		
	}


	    
	  });

	 



