	
var schedulesCntl = angular.module('schedulesControllers', []);

schedulesCntl.controller('schedulesListControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, schedulesFactory) {
	 
	  $scope.sortType     = 'id'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.searchFish   = '';     // set the default search/filter term
	  
	 
	  schedulesFactory.getAll().then(function(data){
	        $scope.listData = data.data;
	    });
	  
	  		$scope.new_schedule= function () {
		  $state.go('dashboard.schedules_add', {schedules : $scope.schedules})
	  };
		    
	  $scope.delete = function(id){ 
		  schedulesFactory.delete(id);
	  }
	  
	  $scope.deleteconf = function (id) {	
	         var modalInstance = $modal.open({
	             templateUrl: 'app/common/delete.html',
	             controller: 'schedulesDeleteController',
	             resolve: {
	                 id: function () {
	                     return id;
	                 }
	             }
	         });
	     }
	  
	  $scope.update = function (obj) {
     	 $scope.schedule = obj;
     	 console.log($scope.schedule);
     	 $state.go('dashboard.schedules_edit', {schedule : $scope.schedule});
      }
});
schedulesCntl.controller('schedulesAddControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope, schedulesFactory) 	{
	
	
	  
	$scope.IsVisible = false;
	
	// for enable the first two handler//
	  $scope.time_slot_1 = true; 
	  $scope.time_slot_2 = true; 
	  $scope.time_slot_3 = false; 
	  $scope.time_slot_4 = false;
	  $scope.time_slot_5 = false;
	  
	// Two add handler
	$scope.add_handle = function () {
	    handle_details.push(1400);
	    handle_updated=handle_details
	    console.log('updated'+handle_updated)
	    $scope.showhide();
	    rangeSlider =update();
	}
	$scope.remove_handle = function () {
	    console.log(handle_details)
	    handle_details.pop();
	    handle_updated=handle_details
	    console.log('updated'+handle_updated)
	  //
	    // For enable and disable Time sloat
	    if(handle_details.length == 0){
	    	$scope.time_slot_1 = false;
	    	$scope.time_slot_2 = false;
	    	$scope.time_slot_3 = false;
	   		$scope.time_slot_4 = false;
	   		$scope.time_slot_5 = false;
	   	  }
	    
	    if(handle_details.length == 1){
	    	$scope.time_slot_2 = false;
	    	$scope.time_slot_3 = false;
	   		$scope.time_slot_4 = false;
	   		$scope.time_slot_5 = false;
	   	  }
	    
	    if(handle_details.length == 2){
	    	$scope.time_slot_3 = false;
	   		$scope.time_slot_4 = false;
	   		$scope.time_slot_5 = false;
	   	  }
	    
	    
	   	  if(handle_details.length == 3){
	   		$scope.time_slot_4 = false;
	   		$scope.time_slot_5 = false;
	   	  }
	   	  
	   	if(handle_details.length == 4){
	   		$scope.time_slot_5 = false;
	   	  }
	    rangeSlider =update();
	}
//     
// Add handler function
     $scope.showhide = function(){
    	 
    	$scope.time_slot_1 = true;
   		$scope.time_slot_2 = true;
    	 
   	  if(handle_details.length == 3){
   		$scope.time_slot_3 = true;
   	  }
   	if(handle_details.length == 4){
   		$scope.time_slot_4 = true;
   	  }
   	if(handle_details.length == 5){
   		$scope.time_slot_5 = true;
   	  }
     }
	//
     // Convertion Seconds to Minutes
	var aproximateHour = function (mins)
	{
	 var minutes = Math.round(mins % 60);
	  if (minutes == 60 || minutes == 0)
	  {
	    return mins / 60;
	  }
	  return Math.trunc (mins / 60) + minutes / 100;
	}

	function filter_hour(value, type) {
		  return (value % 60 == 0) ? 1 : 0;
		}
//
	
	// To Add first two Default Handler
	var handle_details = [360,1080];
	var rangeSlider = document.getElementById('slider-range');
	noUiSlider.create(rangeSlider, {
		  start : [360, 1080],
		  connect: false, 
		  behaviour: 'tap-drag', 
		  step: 15,
		  tooltips: true,
		  range : {'min': 0, 'max': 1440},
		  format:  wNumb({
				decimals: 2,
		    mark: ":",
				encoder: function(a){
		       return aproximateHour(a);
		      }
			}),
		  pips: {
		    mode : 'steps',  
		    format:  wNumb({
		    mark: ":",
		    decimals: 2,
				encoder: function(a ){
		        return aproximateHour(a);
		      }
				}),
		  	filter : filter_hour,
		  	stepped : true,
		    density:1
		  }
		});
	//
	
	// To get handler values
	rangeSlider = document.getElementById('slider-range');

	rangeSlider.noUiSlider.on('update', function (values, handle) {

		handle_details[handle] = values[handle];
		console.log('value : '+ handle_details)
		
		console.log('value : '+ handle_details[0])
		document.getElementById('handle_0_val').value = handle_details[0];
		
		console.log('value : '+ handle_details[1])
		document.getElementById('handle_1_val').value = handle_details[1];
		
		console.log('value : '+ handle_details[2])
		document.getElementById('handle_2_val').value = handle_details[2];
		
		console.log('value : '+ handle_details[3])
		document.getElementById('handle_3_val').value = handle_details[3];
		
		console.log('value : '+ handle_details[4])
		document.getElementById('handle_4_val').value = handle_details[4];
		
		console.log(handle_details)
		
	});
//
	
	// This is To get a updated Handler values and Assigning same to Add handler
	var values="";
	var rangeSlider = document.getElementById('slider-range');
	function update() {
		console.log('UPDATTING WITH ARRAY VALUES : '+ handle_details)
		console.log('ARRAY WITHOUT CONVERTING ARRAY VALUES : '+ handle_details[0])
		console.log('ARRAY WITHOUT CONVERTING ARRAY VALUES: '+ handle_details[1])
		console.log('ARRAY WITHOUT CONVERTING ARRAY VALUES : '+ handle_details[2])
		console.log('ARRAY WITHOUT CONVERTING ARRAY VALUES : '+ handle_details[3])
		console.log('ARRAY WITHOUT CONVERTING ARRAY VALUES : '+ handle_details[4])
		
		console.log($scope.time_slot_1)
		console.log($scope.time_slot_2)
		console.log($scope.time_slot_3)
		console.log($scope.time_slot_4)
		console.log($scope.time_slot_5)
		
		
		
		var updated_handler =[];
		if($scope.time_slot_1 == true){
		var a = handle_details[0].split(':'); 
		var seconds = ((+a[0]) * 60 + (+a[1])); 
		handle_details[0] = seconds;
		}
		
		if($scope.time_slot_2 == true){
		var a = handle_details[1].split(':'); 
		var seconds = ((+a[0]) * 60 + (+a[1])); 
		handle_details[1] = seconds;
		}
		
		console.log('ARRAY WITH CONVERTED VALUES : '+ updated_handler[0])
		console.log('ARRAY WITH CONVERTED VALUES  : '+ updated_handler[1])
		console.log('ARRAY WITH CONVERTED VALUES : '+ updated_handler[2])
		console.log('ARRAY WITH CONVERTED VALUES  : '+ updated_handler[3])
		console.log('ARRAY WITH CONVERTED VALUES : '+ updated_handler[4])
		console.log(handle_details)
		var rangeSlider = document.getElementById('slider-range');
		var button1 = document.getElementById('slider-range');
		
		rangeSlider.noUiSlider.destroy()
		var first_index = 240, second_index = 1080;
		
	 	document.getElementById("slider-range");
		rangeSlider = noUiSlider.create(rangeSlider, {
			  start : handle_details,
			  connect: false, 
			  behaviour: 'tap-drag', 
			  step: 15,
			  tooltips: true,
			  range : {'min': 0, 'max': 1440},
			  format:  wNumb({
					decimals: 2,
			    mark: ":",
					encoder: function(a){
			       return aproximateHour(a);
			      }
				}),
			  pips: {
			    mode : 'steps',  
			    format:  wNumb({
			    mark: ":",
			    decimals: 2,
					encoder: function(a ){
			        return aproximateHour(a);
			      }
					}),
			  	filter : filter_hour,
			  	stepped : true,
			    density:1
			  }
			});

	
	rangeSlider = document.getElementById('slider-range');

	rangeSlider.noUiSlider.on('update', function (values, handle) {
		
		console.log('value : '+ values[handle])
		
		handle_details[handle] = values[handle];
		console.log('value : '+ handle_details)
		
		
		console.log('value : '+ handle_details[0])
		
		document.getElementById('handle_0_val').value = handle_details[0];
		
		console.log('value : '+ handle_details[1])
		document.getElementById('handle_1_val').value = handle_details[1];
		
		console.log('value : '+ handle_details[2])
		document.getElementById('handle_2_val').value = handle_details[2];
		
		console.log('value : '+ handle_details[3])
		document.getElementById('handle_3_val').value = handle_details[3];
		
		console.log('value : '+ handle_details[4])
		document.getElementById('handle_4_val').value = handle_details[4];
		
		console.log(handle_details)
	});
	
	var tmp = handle_details[0]
	var s=document.getElementById('handle_0_val');
	 s.value = tmp;
	 console.log('UPDATED VALUE : '+ document.getElementById('handle_0_val').value)
	 $scope.handle_0_time = document.getElementById('handle_0_val').value;
		console.log($scope.handle_0_time)
	var tmp = handle_details[1]
	var s=document.getElementById('handle_1_val');
	 s.value = tmp;
	console.log('UPDATED VALUE : '+ document.getElementById('handle_1_val').value)
	
	var tmp = handle_details[2]
	var s=document.getElementById('handle_2_val');
	 s.value = tmp;
	console.log('UPDATED VALUE : '+ document.getElementById('handle_2_val').value)
	
	var tmp = handle_details[3]
	var s=document.getElementById('handle_3_val');
	 s.value = tmp;
	console.log('UPDATED VALUE : '+ document.getElementById('handle_3_val').value)
	
	var tmp = handle_details[4]
	var s=document.getElementById('handle_4_val');
	 s.value = tmp;
	console.log('UPDATED VALUE : '+ document.getElementById('handle_4_val').value)	
}
	 //
	
	// By default initializing variables
      $scope.schedules = 
		{
    		"schedules_name":"schedule_1",
  			"enable_fault_detection": false,
  			"data_interval_collection":15,
  			"enable_fault_detection":20,
  			"handle_0_apply_sunrise_sunset":false,
  			"handle_1_apply_sunrise_sunset":false,
  			"handle_2_apply_sunrise_sunset":false,
  			"handle_4_apply_sunrise_sunset":false,
  			"Set_schedule_as_default_schedule":false,
  			"enable_fault_detection":false,
  			"timezoneid":"India Standard Time(UTC+05:30)",
  			"fault_detection" :	60,
  			"valid_till":"2019-06-05T18:30:00.000Z",
  			"handle_0_time":0,
  			"handle_1_time":0,
  			"handle_2_time":0,
  			"handle_3_time":0,
  			"handle_4_time":0,
  			
  			"handle_0_sunrise":true,
  			"handle_0_sunset":false,
  			
  			"handle_1_sunrise":false,
  			"handle_1_sunset":true,
  			
  			"handle_2_sunrise":false,
  			"handle_2_sunset":false,
  			
  			"handle_3_sunrise":false,
  			"handle_3_sunset":false,
  			
  			"handle_4_sunrise":false,
  			"handle_4_sunset":false,
  			
  		"handle_0_sunset_offset":15,	
  		"handle_1_sunset_offset":15,
  		"handle_2_sunset_offset":15,	
  		"handle_3_sunset_offset":15,
  		"handle_4_sunset_offset":15,
  		
  		"handle_0_sunrise_offset":15,	
  		"handle_1_sunrise_offset":15,
  		"handle_2_sunrise_offset":15,	
  		"handle_3_sunrise_offset":15,
  		"handle_4_sunrise_offset":15,
		};	
      //
      
      $scope.scheduleone = function(status){
      $scope.schedules.handle_0_status = status;
      
      	console.log('handle zero status'+ $scope.schedules.handle_0_status);
      };
      
      
      $scope.scheduletwo = function(status) {
          $scope.schedules.handle_1_status = status;
          	console.log('handle one status'+ $scope.schedules.handle_1_status)
          	
       /* var property = document.getElementById(btn);
          	alert('done')
        if (status == 'ON') {
        	property.style.backgroundColor = "#660033"
        }
        if (status == 'OFF') {
        	property.style.backgroundColor = "#660033"
        }
        if (status == 'DIM') {
        	property.style.backgroundColor = "#660033"
        }*/
          	
          };
          
          $scope.schedulethree = function(status) {
              $scope.schedules.handle_2_status = status;
              	console.log('handle two status' + $scope.schedules.handle_2_status)
              };
              
              $scope.schedulefour = function(status) {
                  $scope.schedules.handle_3_status = status;
                  	console.log('handle three status'+ $scope.schedules.handle_3_status)
                  };
                  
                  $scope.schedulefive = function(status) {
                      $scope.schedules.handle_4_status = status;
                      	console.log('handle four status'+ $scope.schedules.handle_4_status)
                      };
            
		  	    	$scope.ok = function () {
		  	    	console.log($scope.schedules)
		  	    	console.log($scope.schedules.Set_schedule_as_default_schedule)
						
		  			$scope.handle_0_time=document.getElementById('handle_0_val').value
		  			console.log($scope.handle_0_time)
		  			
		  			$scope.handle_1_time = document.getElementById('handle_1_val').value
		  			console.log($scope.handle_1_time)
		  			
		  			$scope.handle_2_time = document.getElementById('handle_2_val').value
		  			console.log($scope.handle_2_time)
		  			
		  			$scope.handle_3_time = document.getElementById('handle_3_val').value
		  			console.log($scope.handle_3_time)
		  			
		  			$scope.handle_4_time = document.getElementById('handle_4_val').value
		  			console.log($scope.handle_4_time)
				
				$scope.schedules.handle_0_time  = $scope.handle_0_time;
				$scope.schedules.handle_1_time =	$scope.handle_1_time;
				$scope.schedules.handle_2_time  = $scope.handle_2_time;
				$scope.schedules.handle_3_time =$scope.handle_3_time;
				$scope.schedules.handle_4_time  = $scope.handle_4_time;
				
				$scope.schedules.time_slot_1  = $scope.time_slot_1;
				$scope.schedules.time_slot_2  = $scope.time_slot_2;
				$scope.schedules.time_slot_3  = $scope.time_slot_3;
				$scope.schedules.time_slot_4  = $scope.time_slot_4;
				$scope.schedules.time_slot_5  = $scope.time_slot_5;
				schedulesFactory.add($scope.schedules);
				$state.reload();			
				$state.go('dashboard.schedules');
		};
		 
				$scope.cancel = function () {
				$state.go('dashboard.schedules');
		};
      });
      
schedulesCntl.controller('schedulesUpdateControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope,schedulesFactory) 	{
	
	$scope.schedules = $stateParams.schedule;
	console.log($scope.schedules)
	console.log($scope.schedules.set_schedule_as_default_schedule)
	// To set Time slot and handler
       $scope.time_slot_1=$scope.schedules.time_slot_1;
		$scope.time_slot_2=$scope.schedules.time_slot_2;
		$scope.time_slot_3=$scope.schedules.time_slot_3;
		$scope.time_slot_4=$scope.schedules.time_slot_4;
		$scope.time_slot_5=$scope.schedules.time_slot_5;
		
		console.log($scope.time_slot_1)
		console.log($scope.time_slot_2)
		console.log($scope.time_slot_3)
		console.log($scope.time_slot_4)
		console.log($scope.time_slot_5)
		
	$scope.handle_0_time=$scope.schedules.handle_1_time;
	$scope.handle_1_time=$scope.schedules.handle_2_time;
	$scope.handle_2_time=$scope.schedules.handle_3_time;
	$scope.handle_3_time=$scope.schedules.handle_4_time;
	$scope.handle_4_time=$scope.schedules.handle_5_time;
	//
	// To set handler to an time with previously set time
	var handle_details =[];
	if ($scope.time_slot_1 == true) {
		var a = $scope.schedules.handle_0_time.split(':'); 
		var seconds = ((+a[0]) * 60 + (+a[1])); 
		handle_details[0] = seconds;
		console.log(handle_details[0])
		
	  } 
	
	if($scope.time_slot_2 == true){
		var a = $scope.schedules.handle_1_time.split(':'); 
		var seconds = ((+a[0]) * 60 + (+a[1]));
		handle_details[1] = seconds;
		console.log(handle_details[1])
	}
	
	if($scope.time_slot_3 == true){
		var a =$scope.schedules.handle_2_time.split(':'); 
		var seconds = ((+a[0]) * 60 + (+a[1]));
		handle_details[2] = seconds;		
	}
	
	if($scope.time_slot_4 == true){
		var a = $scope.schedules.handle_3_time.split(':'); 
		var seconds = ((+a[0]) * 60 + (+a[1]));
		handle_details[3] = seconds;	
	}
	
	if($scope.time_slot_5 == true){
		var a = $scope.schedules.handle_4_time.split(':'); 
		var seconds = ((+a[0]) * 60 + (+a[1]));
		handle_details[4] = seconds;	
	}
	
//	
	// Add handler Function
				$scope.add_handle = function () {
				    handle_details.push(1350);
				    console.log(handle_details)
				    handle_updated=handle_details
				    rangeSlider =update();
				    $scope.showhide();
				}
	// remove handler Function
				$scope.remove_handle = function () {
				    console.log(handle_details)
				    handle_details.pop();
				    handle_updated=handle_details
				    
				    if(handle_details.length == 0){
				    	$scope.time_slot_1 = false;
				    	$scope.time_slot_2 = false;
				    	$scope.time_slot_3 = false;
				   		$scope.time_slot_4 = false;
				   		$scope.time_slot_5 = false;
				   	  }
				    
				    if(handle_details.length == 1){
				    	$scope.time_slot_1 = true;
				    	$scope.time_slot_2 = false;
				    	$scope.time_slot_3 = false;
				   		$scope.time_slot_4 = false;
				   		$scope.time_slot_5 = false;
				   	  }
				    if(handle_details.length == 2){
				    	$scope.time_slot_1 = true;
				    	$scope.time_slot_2 = true;
				    	$scope.time_slot_3 = false;
				   		$scope.time_slot_4 = false;
				   		$scope.time_slot_5 = false;
				   	  }
				   	  if(handle_details.length == 3){
				   		$scope.time_slot_1 = true;
				    	$scope.time_slot_2 = true;
				    	$scope.time_slot_3 = true;
				   		$scope.time_slot_4 = false;
				   		$scope.time_slot_5 = false;
				   	  }
				   	  
				   	if(handle_details.length == 4){
				   		$scope.time_slot_1 = true;
				    	$scope.time_slot_2 = true;
				    	$scope.time_slot_3 = true;
				   		$scope.time_slot_4 = true;
				   		$scope.time_slot_5 = false;
				   	  }
				    rangeSlider =update();
				}
//			     
			     $scope.showhide = function(){
			    	$scope.time_slot_1 = true;
			   		$scope.time_slot_2 = true;
			    
			   	  if(handle_details.length == 3){
			   		$scope.time_slot_3 = true;
			   	  }
			   	if(handle_details.length == 4){
			   		$scope.time_slot_4 = true;
			   	  }
			   	
			   	if(handle_details.length == 5){
			   		$scope.time_slot_5 = true;
			   	  }
			     }
				
				var aproximateHour = function (mins){
				 var minutes = Math.round(mins % 60);
				  if (minutes == 60 || minutes == 0)
				  {
				    return mins / 60;
				  }
				  return Math.trunc (mins / 60) + minutes / 100;
				}

				function filter_hour(value, type) {
					  return (value % 60 == 0) ? 1 : 0;
					}
				
				/*var updated_handler =[];
				if($scope.time_slot_1 == true){
				var a = handle_details[0].split(':'); 
				var seconds = ((+a[0]) * 60 + (+a[1])); 
				handle_details[0] = seconds;
				}
				
				if($scope.time_slot_2 == true){
				var a = handle_details[1].split(':'); 
				var seconds = ((+a[0]) * 60 + (+a[1])); 
				handle_details[1] = seconds;
				}*/
				
				console.log(handle_details)
				
				var handle_details =handle_details;
				var rangeSlider = document.getElementById('slider-range');
				noUiSlider.create(rangeSlider, {
					  start : handle_details,
					  connect: false, 
					  behaviour: 'tap-drag', 
					  step: 15,
					  tooltips: true,
					  range : {'min': 0, 'max': 1440},
					  format:  wNumb({
							decimals: 2,
					    mark: ":",
							encoder: function(a){
					       return aproximateHour(a);
					      }
						}),
					  pips: {
					    mode : 'steps',  
					    format:  wNumb({
					    mark: ":",
					    decimals: 2,
							encoder: function(a ){
					        return aproximateHour(a);
					      }
							}),
					  	filter : filter_hour,
					  	stepped : true,
					    density:1
					  }
					});
				
				rangeSlider = document.getElementById('slider-range');


				rangeSlider.noUiSlider.on('update', function (values, handle) {

					handle_details[handle] = values[handle];
					console.log('value : '+ handle_details)
					
					console.log('value : '+ handle_details[0])
					document.getElementById('handle_0_val').value = handle_details[0];
					
					console.log('value : '+ handle_details[1])
					document.getElementById('handle_1_val').value = handle_details[1];
					
					console.log('value : '+ handle_details[2])
					document.getElementById('handle_2_val').value = handle_details[2];
					
					console.log('value : '+ handle_details[3])
					document.getElementById('handle_3_val').value = handle_details[3];
					
					console.log('value : '+ handle_details[4])
					document.getElementById('handle_4_val').value = handle_details[4];
					
				});

				var values="";
				var rangeSlider = document.getElementById('slider-range');
				function update() {
					console.log('UPDATTING WITH ARRAY VALUES : '+ handle_details)
					
// To set a handler to an previous set values when we add or remove timeslot
					var updated_handler =[];
					if($scope.time_slot_1 == true){
					var a = handle_details[0].split(':'); 
					var seconds = ((+a[0]) * 60 + (+a[1])); 
					handle_details[0] = seconds;
					}
					
					if($scope.time_slot_2 == true){
					var a = handle_details[1].split(':'); 
					var seconds = ((+a[0]) * 60 + (+a[1])); 
					handle_details[1] = seconds;
					}
					
					if($scope.time_slot_3 == true){
					var a = handle_details[2].split(':'); 
					var seconds = ((+a[0]) * 60 + (+a[1])); 
					handle_details[2] = seconds;
					} 
					
					if($scope.time_slot_4 == true){
					var a = handle_details[3].split(':'); 
					var seconds = ((+a[0]) * 60 + (+a[1])); 
					handle_details[3] = seconds;
					}
					
					if($scope.time_slot_5 == true){
					var a = handle_details[4].split(':'); 
					var seconds = ((+a[0]) * 60 + (+a[1])); 
					handle_details[4] = seconds;
					}
					
					var rangeSlider = document.getElementById('slider-range');
					var button1 = document.getElementById('slider-range');
					
					rangeSlider.noUiSlider.destroy()
					var first_index = 240, second_index = 1080;
					
				 	document.getElementById("slider-range");

					rangeSlider = noUiSlider.create(rangeSlider, {
						  start : handle_details,
						  connect: false, 
						  behaviour: 'tap-drag', 
						  step: 15,
						  tooltips: true,
						  range : {'min': 0, 'max': 1440},
						  format:  wNumb({
								decimals: 2,
						    mark: ":",
								encoder: function(a){
						       return aproximateHour(a);
						      }
							}),
						  pips: {
						    mode : 'steps',  
						    format:  wNumb({
						    mark: ":",
						    decimals: 2,
								encoder: function(a ){
						        return aproximateHour(a);
						      }
								}),
						  	filter : filter_hour,
						  	stepped : true,
						    density:1
						  }
						});

				
				rangeSlider = document.getElementById('slider-range');

				rangeSlider.noUiSlider.on('update', function (values, handle) {
					
					console.log('value : '+ values[handle])
					
					handle_details[handle] = values[handle];
					console.log('value : '+ handle_details)
					
					
					console.log('value : '+ handle_details[0])
					
					document.getElementById('handle_0_val').value = handle_details[0];
					
					console.log('value : '+ handle_details[1])
					document.getElementById('handle_1_val').value = handle_details[1];
					
					console.log('value : '+ handle_details[2])
					document.getElementById('handle_2_val').value = handle_details[2];
					
					console.log('value : '+ handle_details[3])
					document.getElementById('handle_3_val').value = handle_details[3];
					
					console.log('value : '+ handle_details[4])
					document.getElementById('handle_4_val').value = handle_details[4];
				});

			
				var tmp = handle_details[0]
				var s=document.getElementById('handle_0_val');
				 s.value = tmp;
				 console.log('UPDATED VALUE : '+ document.getElementById('handle_0_val').value)
				 $scope.handle_0_time = document.getElementById('handle_0_val').value;
					console.log($scope.handle_0_time)
				var tmp = handle_details[1]
				var s=document.getElementById('handle_1_val');
				 s.value = tmp;
				console.log('UPDATED VALUE : '+ document.getElementById('handle_1_val').value)
				
				var tmp = handle_details[2]
				var s=document.getElementById('handle_2_val');
				 s.value = tmp;
				console.log('UPDATED VALUE : '+ document.getElementById('handle_2_val').value)
				
				var tmp = handle_details[3]
				var s=document.getElementById('handle_3_val');
				 s.value = tmp;
				console.log('UPDATED VALUE : '+ document.getElementById('handle_3_val').value)
				
				var tmp = handle_details[4]
				var s=document.getElementById('handle_4_val');
				 s.value = tmp;
				console.log('UPDATED VALUE : '+ document.getElementById('handle_4_val').value)	
			}
					  
				$scope.scheduleone = function(status) {
				      $scope.schedules.handle_0_status = status;
				      	console.log('handle zero status'+ $scope.schedules.handle_0_status)
				      };
				      
				      $scope.scheduletwo = function(status) {
				          $scope.schedules.handle_1_status = status;
				          	console.log('handle one status'+ $scope.schedules.handle_1_status)
				          };
				          
				          $scope.schedulethree = function(status) {
				              $scope.schedules.handle_2_status = status;
				              	console.log('handle two status' + $scope.schedules.handle_2_status)
				              };
				              
				              $scope.schedulefour = function(status) {
				                  $scope.schedules.handle_3_status = status;
				                  	console.log('handle three status'+ $scope.schedules.handle_3_status)
				                  };
				                  
				                  $scope.schedulefive = function(status) {
				                      $scope.schedules.handle_4_status = status;
				                      	console.log('handle four status'+ $scope.schedules.handle_4_status)
				                      };
				                      
			      				$scope.update=function(){
					  			$scope.handle_0_time=document.getElementById('handle_0_val').value
					  			$scope.handle_1_time = document.getElementById('handle_1_val').value
					  			$scope.handle_2_time = document.getElementById('handle_2_val').value
					  			$scope.handle_3_time = document.getElementById('handle_3_val').value
					  			$scope.handle_4_time = document.getElementById('handle_4_val').value
							   
				$scope.schedules;
				$scope.schedules.time_slot_1 =  $scope.time_slot_1;
				$scope.schedules.time_slot_2 =  $scope.time_slot_2;
				$scope.schedules.time_slot_3 =  $scope.time_slot_3;
				$scope.schedules.time_slot_4 =  $scope.time_slot_4;
				$scope.schedules.time_slot_5 =  $scope.time_slot_5;
				
				$scope.schedules.handle_0_time = $scope.handle_0_time;
				$scope.schedules.handle_1_time = $scope.handle_1_time;
				$scope.schedules.handle_2_time = $scope.handle_2_time;
				$scope.schedules.handle_3_time = $scope.handle_3_time;
				$scope.schedules.handle_4_time = $scope.handle_4_time;
				
				
				console.log($scope.schedules)
				console.log($scope.schedules.time_slot_1)
				console.log($scope.schedules.time_slot_2)
				console.log($scope.schedules.time_slot_3)
				console.log($scope.schedules.time_slot_4)
				console.log($scope.schedules.time_slot_5)
				
				console.log($scope.schedules.handle_0_time)
				console.log($scope.schedules.handle_1_time)
				console.log($scope.schedules.handle_2_time)
				console.log($scope.schedules.handle_3_time)
				console.log($scope.schedules.handle_5_time)
				 	
				schedulesFactory.add($scope.schedules);
				$state.reload();
				
				$state.go('dashboard.schedules');
		};
				$scope.close = function () {
				$state.go('dashboard.schedules');
	  };
});

schedulesCntl.controller('schedulesDeleteController', function ($scope, $state, $modalInstance, id, schedulesFactory) {

				$scope.ok = function () {
				schedulesFactory.delete(id);
				$modalInstance.close($scope.schedules);
				$state.reload();
	  };

	  			$scope.cancel = function () {
	  			$modalInstance.dismiss('cancel');
	  };
	});