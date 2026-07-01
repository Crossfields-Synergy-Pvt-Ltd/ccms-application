
var mapCntl = angular.module('mapControllers', []);

mapCntl.controller('mapViewControllers', function($scope, $filter,$rootScope,$interval ,$state,$stateParams, $modal,$location, $http,inform,$rootScope,  mapViewFactory,$window , config) {
	
	 $('.select2').select2();
	
	$scope.login = function () {
			$state.go('login')
	  };
	  
	  $scope.AssignedDate = Date; // 'Date' could be assigned too of course:)
	    
	    $interval(function(){
	        // nothing is required here, interval triggers digest automaticaly
	    },1000)
	  
	    
	    
	  var gmarkers1 = [];
	  var markers1 = [];
  var infowindow = new google.maps.InfoWindow({
      content: ''
  });

  function getPinIcon(color) {
      var svg = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="36" viewBox="0 0 24 36">' +
          '<path d="M12 0C5.4 0 0 5.4 0 12c0 6.6 12 24 12 24s12-17.4 12-24C24 5.4 18.6 0 12 0z" fill="' + color + '" stroke="#333" stroke-width="0.5"/>' +
          '<circle cx="12" cy="12" r="4" fill="#fff"/>' +
          '</svg>';
      return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg);
  }

	 
	  // Our markers
	  markers1 = [ ];

	  $scope.district = 'ALL';
	  $scope.mandal = 'ALL';
	  $scope.gp = 'ALL';
  $scope.selectedDistrict = $scope.district;
  $scope.selectedMandal =  $scope.mandal;
  $scope.select_gp =  $scope.gp;
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
	
	  console.log($scope.qs_params)
		mapViewFactory.getAllCount($scope.qs_params).then(function(data){
		        $scope.listData = data.data;
		  });
		 
	  mapViewFactory.getAllMapDashboardData($scope.qs_params).then(function(data){
        $scope.dashboardData = data.data;
        var mid_lat, mid_lang;
        angular.forEach($scope.dashboardData,function(value,index){
        	
        	var details = value.info_details
        	
        	/*	" ID - " +value.id + 
        	"<br> Lights Connected - " +value.mcb_trip 
        	+ "<br> Non-Glowing Lights -"+value.mcb_trip 
        	+ "<br> Connected Load -"+value.connected_load 
        	+ "<br> Latitude - " +value.lat + "<br> Longitude -" +value.lang ;*/
        	
			var url =  "<br> "+ details ;
			mid_lat =  value.lat;
			mid_lang = value.lang;
			
//			if(typeof(value.lat) == "undefined" || typeof(value.lang) == "undefined"){
//				return
//			}
			
			if(value.mcb_trip == '1'){
				markers1.push(['0',url, value.lat, value.lang, 'mcb_trip', getPinIcon('#FF0000')]);
			}
			
		/*	if(value.manual_mode_status == '1'){
					markers1.push(['0',url, value.lat, value.lang, 'manual', getPinIcon('#0000FF')]);
				}
			*/
			if(value.high_current == '1'){
				markers1.push(['0',url, value.lat, value.lang, 'high_curent', getPinIcon('#FFA500')]);
			}
        	
			if(value.light_status == '1') {
				markers1.push(['0',url, value.lat, value.lang, 'on', getPinIcon('#00AA00')]);
			}
			
			if(value.light_status == '0'){
				markers1.push(['0',url, value.lat, value.lang, 'off', getPinIcon('#808080')]);
			} 
        })
	        
	        map = new google.maps.Map(document.getElementById('map_canvas'), {
	        	zoom: 11,
	            panControl: true, //enable pan Control
	            zoomControl: true, //enable zoom control
	            scrollwheel: true,
	            zoomControlOptions: {
	                style: google.maps.ZoomControlStyle.SMALL, //zoom control size
	                position: google.maps.ControlPosition.LEFT_CENTER
	            },
	            mapTypeId: google.maps.MapTypeId.ROADMAP,
	            mapTypeControl: true,
	            scaleControl: true,
	            mapTypeControlOptions: {
	                style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR
	            },
	            navigationControl: true,
	            navigationControlOptions: {
	                style: google.maps.NavigationControlStyle.ZOOM_PAN
	            },
	  		    center: new google.maps.LatLng(16.4792, 80.5469)
			});
	        
	        for (i = 0; i < markers1.length; i++) {
	            addMarker(markers1[i]);
	        }
	        
			});
		  
      

      /**
       * Function to add marker to map
       */

      function addMarker(marker) {
          var category = marker[4];
          var title = marker[1];
          var pos = new google.maps.LatLng(marker[2], marker[3]);

          var content = marker[1];

          marker1 = new google.maps.Marker({
              title: title,
              position: pos,
              category: category,
              map: map,
              icon: {
                  url: marker[5],
                  size: new google.maps.Size(24, 36),
                  origin: new google.maps.Point(0, 0),
                  anchor: new google.maps.Point(12, 36)
              }
          });

          gmarkers1.push(marker1);

          // Marker click listener
          google.maps.event.addListener(marker1, 'click', (function (marker1, content) {
              return function () {
                  infowindow.setContent(content);
                  infowindow.open(map, marker1);
                  map.panTo(this.getPosition());
                  map.setZoom(10);
              }
          })(marker1, content));
      }

      /**
       * Function to filter markers by category
       */

      $scope.filterMarkers = function (category) {
    	  
          for (i = 0; i < markers1.length; i++) {
              marker = gmarkers1[i];
              // If is same category or category not picked
              if (marker.category == category || category.length === 0) {
                  marker.setVisible(true);
              } else if(category == 'all'){
                  marker.setVisible(true);
              }
              // Categories don't match 
              else {
                  marker.setVisible(false);
              }
          }
      }
	 
	
		
		$scope.search = function () {
		for (var i = 0; i < gmarkers1.length; i++) {
			gmarkers1[i].setMap(null);
		}
		gmarkers1 = [];
		markers1 = [];
		var dateParams = '';
		if ($scope.datePicker && $scope.datePicker.date && $scope.datePicker.date.startDate && $scope.datePicker.date.endDate) {
			var startDate = moment($scope.datePicker.date.startDate).format('YYYY-MM-DD');
			var endDate = moment($scope.datePicker.date.endDate).format('YYYY-MM-DD');
			dateParams = '&start_date=' + startDate + '&end_date=' + endDate;
		}
		$scope.qs_params = '?district='+$scope.selectedDistrict+ '&mandal='+$scope.selectedMandal+'&gp='+$scope.select_gp + dateParams;
		
		 mapViewFactory.getAllCount($scope.qs_params).then(function(data){
		        $scope.listData = data.data;
		  });
		 
		mapViewFactory.getAllMapDashboardData($scope.qs_params).then(function(data){
	        $scope.dashboardData = data.data;
	        var mid_lat, mid_lang;
	        angular.forEach($scope.dashboardData,function(value,index){
	        	
	        	var details =  " ID - " +value.id + "<br> Lights Connected - " +value.mcb_trip + "<br> Non-Glowing Lights -" +value.mcb_trip + "<br> Connected Load -" +value.connected_load + "<br> Latitude - " +value.lat + "<br> Longitude -" +value.lang ;
				var url =  "<br> "+ details ;
				  mid_lat =  value.lat;
					mid_lang = value.lang;
					
				
				if(value.mcb_trip == '1'){
					markers1.push(['0',url, value.lat, value.lang, 'mcb_trip', getPinIcon('#FF0000')]);
				}
				if(value.manual_mode_status == '1'){
					markers1.push(['0',url, value.lat, value.lang, 'manual', getPinIcon('#0000FF')]);
				}
				if(value.high_current == '1'){
					markers1.push(['0',url, value.lat, value.lang, 'high_curent', getPinIcon('#FFA500')]);
				}
				if(value.light_status == '1') {
					markers1.push(['0',url, value.lat, value.lang, 'on', getPinIcon('#00AA00')]);
				}
				if(value.light_status == '0'){
					markers1.push(['0',url, value.lat, value.lang, 'off', getPinIcon('#808080')]);
				} 
	        	
	        })
	        map = new google.maps.Map(document.getElementById('map_canvas'), {
	        	zoom: 11,
	            panControl: true, //enable pan Control
	            zoomControl: true, //enable zoom control
	            scrollwheel: true,
	            zoomControlOptions: {
	                style: google.maps.ZoomControlStyle.SMALL, //zoom control size
	                position: google.maps.ControlPosition.LEFT_CENTER
	            },
	            mapTypeId: google.maps.MapTypeId.ROADMAP,
	            mapTypeControl: true,
	            scaleControl: true,
	            mapTypeControlOptions: {
	                style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR
	            },
	            navigationControl: true,
	            navigationControlOptions: {
	                style: google.maps.NavigationControlStyle.ZOOM_PAN
	            },
		center: new google.maps.LatLng(
			(mid_lat && isFinite(mid_lat)) ? parseFloat(mid_lat) : 16.4792,
			(mid_lang && isFinite(mid_lang)) ? parseFloat(mid_lang) : 80.5469
		)
			});
	        
	        for (i = 0; i < markers1.length; i++) {
	            addMarker(markers1[i]);
	        }
	        
			});
		  
		  }

	 
	 $scope.getMandalOnSelect = function(selectedDistrict) {
		 mapViewFactory.getByMandal($scope.selectedDistrict).then(function(data) {
				$scope.mandal_list = data.data;
			});
		}

		$scope.getGpOnSelect = function(selectedMandal) {
			mapViewFactory.getByGp($scope.selectedMandal).then(function(data) {
				$scope.gp_list = data.data;
				
			});
		}
		$scope.getMandalOnSelect($scope.selectedDistrict);
		$scope.getGpOnSelect($scope.selectedMandal);
	/*$scope.districts = [{
   		state : "Srikakulam-11",
   		code : "Srikakulam-11"
   	},
   	
   	{
   		state : "Visakhapatnam-13",
   		code : "Visakhapatnam-13"
   	},
   	
   	{
   		state : "Prakasam-18",
   		code : "Prakasam-18"
   	},
   	
   	{
   		state : "Nellore-19",
   		code : "Nellore-19"
   	},
   	
   	{
   		state : "Kadapa-20",
   		code : "Kadapa-20"
   	},
   	
   	{
   		state : "Kurnool-21",	
   		code : "Kurnool-21"
   	},
	{
   		state : "Guntur-17",
   		code : "Guntur-17"
   	},
	{
   		state : "West Godavari-15",
   		code : "West Godavari-15"
   	}];*/
	  
	 /* $('#mySelect2').on('select2:select', function (e) {
		    $scope.district = e.params.data;
		    console.log($scope.district.id);
		    mapViewFactory.getByMandal($scope.district.id).then(function(data) {
				$scope.mandal_list = data.data;
			});
		});
	 
	 $('#mySelect').on('select2:select', function (e) {
		    $scope.mandal = e.params.data;
		    console.log($scope.mandal.id);
		    mapViewFactory.getByGp($scope.mandal.id).then(function(data) {
				$scope.gp_list = data.data;
			});
		    
		});
	 
	 $('#my').on('select2:select', function (e) {
		 $scope.gp = e.params.data;
		    console.log($scope.gp.id);
		    
		});*/
	 

	 
});