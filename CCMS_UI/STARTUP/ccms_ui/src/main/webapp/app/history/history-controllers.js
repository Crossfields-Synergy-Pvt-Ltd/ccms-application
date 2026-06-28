	
var historyCntl = angular.module('historyControllers', []);

historyCntl.controller('historyListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, historyFactory, config) {
	 
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	  $scope.selected_dcu = {};
	  var dist = ($rootScope.previlage && $rootScope.previlage.dist) ? $rootScope.previlage.dist : 'ALL';
	  var mondal = ($rootScope.previlage && $rootScope.previlage.mondal) ? $rootScope.previlage.mondal : 'ALL';
	  var gp = ($rootScope.previlage && $rootScope.previlage.gp) ? $rootScope.previlage.gp : 'ALL';
	  $scope.qs_params = '?distrtict='+dist+ '&mandal='+mondal+'&gp='+gp;

	  $scope.dcu_data = [];
	  historyFactory.getAllDcuNames($scope.qs_params).then(function(data){
	    $scope.dcu_data = data.data;
	  });

	  $scope.filter = function() {
		var dist = $scope.selecte_distict || 'ALL';
		var mondal = $scope.select_mondal || 'ALL';
		var gp = $scope.select_gp || 'ALL';
		var qs = '?distrtict='+dist+ '&mandal='+mondal+'&gp='+gp;
		historyFactory.getAllDcuNames(qs).then(function(data) {
			$scope.dcu_data = data.data;
		});
	  };

	  $scope.datePicker = { date: {startDate: new Date(), endDate: new Date()} };
	    $scope.opts = {
	        locale: {
	            applyClass: 'btn-green',
	            applyLabel: "Apply",
	            fromLabel: "From",
	            startDate: "04/22/2013",
	    		endDate: "04/28/2020",
	            format: "YYYY-MM-DD",
	            toLabel: "To",
	            cancelLabel: 'Cancel',
	            customRangeLabel: 'Custom range'
	        },
	        ranges: {
	        	'Today': [moment(), moment()],
	        	'Yesterday': [moment().subtract(1, 'days'), moment()],
	            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
	            'This Month': [moment().startOf('month'), moment().endOf('month')],
	            'Last Month': [moment().subtract(29, 'days'), moment()]
	        }
	    };
	  
		        $scope.showdate = function(gateway_identifier) {
			     console.log($scope.selected_dcu.name.gateway_identifier)
			     
			     console.log($scope.datePicker.date.startDate);
		    	 console.log($scope.datePicker.date.endDate);
		    	 
		    	 $scope.start_date=$scope.datePicker.date.startDate
		    	 $scope.end_date=$scope.datePicker.date.endDate
				
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
		    	 
				$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier + '&start_date=' + start_date + '&end_date='+end_Date;
				console.log($scope.qs_params)
				
				historyFactory.getByID($scope.qs_params).then(function(data) {
				 	 $scope.todos = data.data;
					$scope.list = [];
					$scope.itemsPerPage = 25;
					$scope.currentPage = 1;

					$scope.figureOutTodosToDisplay = function() {
						var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
						var end = begin
								+ $scope.itemsPerPage;
						$scope.list = $scope.todos.slice(
								begin, end);
					};

					$scope.figureOutTodosToDisplay();

					$scope.pageChanged = function() {
						$scope.figureOutTodosToDisplay();
					};
					
				});
			
		}
		        
		        $scope.export_history = function(gateway_identifier) {
				  
		        	console.log($scope.datePicker.date.startDate);
			    	 console.log($scope.datePicker.date.endDate);
			    	 
			    	 $scope.start_date=$scope.datePicker.date.startDate
			    	 $scope.end_date=$scope.datePicker.date.endDate
					
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
					
					historyFactory.getByIDhistory($scope.qs_params).then(function(data) {
					 			$scope.list = data.data;
					});
				
			}
		        
		        $scope.selecte_distict = '';
		        $scope.districts = config.districts;

		       	  
		       	 
		       	  $scope.getMandalOnSelect = function(selecte_distict) {
		       		historyFactory.getByMondal($scope.selecte_distict).then(function(data) {
		       				$scope.mandal_list = data.data;
		       			});
		       		}

		       		$scope.getGpOnSelect = function(select_mondal) {
		       			historyFactory.getByGp($scope.select_mondal).then(function(data) {
		       				$scope.gp_list = data.data;
		       				
		       			});
		       		}
		       		
		       		$scope.getVillageOnSelect = function(select_gp) {
		       			historyFactory.getByVillage($scope.select_gp).then(function(data) {
		       			$scope.village_list = data.data;
		       			});
		       		}

		  });