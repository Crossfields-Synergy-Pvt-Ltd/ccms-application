var eventCntl = angular.module('eventControllers', []);

eventCntl .controller( 'eventListControllers',
				function($scope, $state, $stateParams, $modal, $location,$http, $rootScope, eventFactory, config) {

					$scope.sortType = 'id'; // set the default sort type
					$scope.sortReverse = false; // set the default sort order
					$scope.searchFish = ''; // set the default search/filter
					// term

					$scope.selected_dcu = {};
					 var dist = ($rootScope.previlage && $rootScope.previlage.dist) ? $rootScope.previlage.dist : 'ALL';
					 var mondal = ($rootScope.previlage && $rootScope.previlage.mondal) ? $rootScope.previlage.mondal : 'ALL';
					 var gp = ($rootScope.previlage && $rootScope.previlage.gp) ? $rootScope.previlage.gp : 'ALL';
					 $scope.qs_params = '?distrtict='+dist+ '&mandal='+mondal+'&gp='+gp;

					 $scope.dcu_data = [];
					 eventFactory.getAllDcuNames($scope.qs_params).then(function(data) {
					 	$scope.dcu_data = data.data;
					 });

					 $scope.filter = function() {
					 	var dist = $scope.selecte_distict || 'ALL';
						var mondal = $scope.select_mondal || 'ALL';
						var gp = $scope.select_gp || 'ALL';
						var qs = '?distrtict='+dist+ '&mandal='+mondal+'&gp='+gp;
						eventFactory.getAllDcuNames(qs).then(function(data) {
							$scope.dcu_data = data.data;
						});
					};

					$scope.datePicker = {
						date : { startDate : new Date(), endDate : new Date()
						}
					};

					$scope.opts = {
						locale : {
							applyClass : 'btn-green',
							applyLabel : "Apply",
							fromLabel : "From",
							format : "YYYY-MM-DD",
							toLabel : "To",
							cancelLabel : 'Cancel',
							customRangeLabel : 'Custom range'
						},
						ranges : {
							'Today' : [ moment().today, moment() ],
							'Yesterday' : [ moment().subtract(1, 'days'), moment() ],
							'Last 7 Days' : [ moment().subtract(6, 'days'), moment() ],
							'This Month' : [ moment().startOf('month'), moment().endOf('month') ],
							'Last Month' : [ moment().subtract(29, 'days'),moment() ]
						}
					};

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

						$scope.qs_params = '?id=' + $scope.selected_dcu.name.gateway_identifier+ '&start_date=' + start_date+ '&end_date=' +end_Date;
						console.log($scope.qs_params)

						eventFactory .getByID($scope.qs_params) .then( function(data) {
											// $scope.list = data.data;
											$scope.todos = data.data;

											$scope.list = [];
											$scope.itemsPerPage = 25;
											$scope.currentPage = 1;

											$scope.figureOutTodosToDisplay = function() {
												var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
												var end = begin
														+ $scope.itemsPerPage;
												$scope.list = $scope.todos
														.slice(begin, end);
											};

											$scope.figureOutTodosToDisplay();

											$scope.pageChanged = function() {
												$scope
														.figureOutTodosToDisplay();
											};
										});

					}

					
				    $scope.export_events = function(gateway_identifier) {
						  
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
						
						eventFactory.exportEventData($scope.qs_params).then(function(data) {
						 		//	$scope.list = data.data;
						});
					
				}
				    
					$scope.selecte_distict = '';
					$scope.districts = config.districts;

					$scope.getMandalOnSelect = function(selecte_distict) {
						eventFactory.getByMondal($scope.selecte_distict).then(
								function(data) {
									$scope.mandal_list = data.data;
								});
					}

					$scope.getGpOnSelect = function(select_mondal) {
						eventFactory.getByGp($scope.select_mondal).then(
								function(data) {
									$scope.gp_list = data.data;

								});
					}

					$scope.getVillageOnSelect = function(select_gp) {
						eventFactory.getByVillage($scope.select_gp).then(
								function(data) {
									$scope.village_list = data.data;
								});
					}

				});
