var mapCntl = angular.module('mapControllers', []);

mapCntl.controller('mapViewControllers', function($scope, $state, mapViewFactory, config) {
    var defaultCenter = { lat: 16.4792, lng: 80.5469 };
    var map;
        var markers = [];
    var infoWindow = new google.maps.InfoWindow({ content: '' });
    var requestSequence = 0;

    $scope.login = function() { $state.go('login'); };
    $scope.AssignedDate = Date;
    $scope.districts = config.districts;
    $scope.district = 'ALL';
    $scope.mandal = 'ALL';
    $scope.gp = 'ALL';
    $scope.selectedDistrict = 'ALL';
    $scope.selectedMandal = 'ALL';
    $scope.select_gp = 'ALL';
    $scope.mandal_list = ['ALL'];
    $scope.gp_list = ['ALL'];
    $scope.loading = false;
    $scope.errorMessage = '';
    $scope.hasResults = false;
    $scope.mapMarkerCategories = [];
    $scope.datePicker = { date: { startDate: null, endDate: null } };
    $scope.opts = {
        locale: { applyClass: 'btn-green', applyLabel: 'Apply', fromLabel: 'From', format: 'YYYY-MM-DD', toLabel: 'To', cancelLabel: 'Cancel', customRangeLabel: 'Custom range' },
        ranges: {
            'Today': [moment().startOf('day'), moment().endOf('day')],
            'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            'Last 7 Days': [moment().subtract(6, 'days').startOf('day'), moment().endOf('day')],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        }
    };

    function valueOrAll(value) { return value || 'ALL'; }

    function queryString() {
        var params = [
            'district=' + encodeURIComponent(valueOrAll($scope.selectedDistrict)),
            'mandal=' + encodeURIComponent(valueOrAll($scope.selectedMandal)),
            'gp=' + encodeURIComponent(valueOrAll($scope.select_gp))
        ];
        if ($scope.datePicker && $scope.datePicker.date && $scope.datePicker.date.startDate && $scope.datePicker.date.endDate) {
            params.push('start_date=' + encodeURIComponent(moment($scope.datePicker.date.startDate).format('YYYY-MM-DD')));
            params.push('end_date=' + encodeURIComponent(moment($scope.datePicker.date.endDate).format('YYYY-MM-DD')));
        }
        return '?' + params.join('&');
    }

    function validCoordinate(value) { return value !== null && value !== undefined && value !== '' && isFinite(parseFloat(value)); }

    function getPinIcon(color) {
        var svg = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="36" viewBox="0 0 24 36"><path d="M12 0C5.4 0 0 5.4 0 12c0 6.6 12 24 12 24s12-17.4 12-24C24 5.4 18.6 0 12 0z" fill="' + color + '" stroke="#333" stroke-width="0.5"/><circle cx="12" cy="12" r="4" fill="#fff"/></svg>';
        return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg);
    }

    function markerFor(value) {
        var categories = [], color = '#808080';
        if (value.light_status === 1 || value.light_status === '1') { categories.push('on'); color = '#00AA00'; }
        else if (value.light_status === 0 || value.light_status === '0') categories.push('off');
        if (value.manual_mode_status === 1 || value.manual_mode_status === '1') { categories.push('manual'); color = '#0000FF'; }
        if (value.mcb_trip === 1 || value.mcb_trip === '1') { categories.push('mcb_trip'); color = '#FF0000'; }
        if (value.high_voltage === 1 || value.high_voltage === '1') { categories.push('high_voltage'); color = '#800080'; }
        if (value.high_current === 1 || value.high_current === '1') { categories.push('high_current'); color = '#FFA500'; }
        if (value.offline === true || value.offline === 'true') { categories.push('offline'); color = '#000000'; }
        if (!categories.length) categories.push('all');
        return { categories: categories, icon: getPinIcon(color), title: value.name || value.id || "", content: value.info_details || "", lat: parseFloat(value.lat), lng: parseFloat(value.lang), color: color };
    }

    function clearMarkers() {
        angular.forEach(markers, function(marker) { marker.setMap(null); });
        markers = [];
    }

    function createMap(center) {
        var element = document.getElementById('map_canvas');
        map = new google.maps.Map(element, {
            zoom: 11, panControl: true, zoomControl: true, scrollwheel: true,
            zoomControlOptions: { style: google.maps.ZoomControlStyle.SMALL, position: google.maps.ControlPosition.LEFT_CENTER },
            mapTypeId: google.maps.MapTypeId.ROADMAP, mapTypeControl: true, scaleControl: true,
            mapTypeControlOptions: { style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR },
            navigationControl: true, navigationControlOptions: { style: google.maps.NavigationControlStyle.ZOOM_PAN },
            center: new google.maps.LatLng(center.lat, center.lng)
        });
    }

    function addMarker(definition) {
        var marker = new google.maps.Marker({
            title: definition.title,
            position: new google.maps.LatLng(definition.lat, definition.lng),
            categories: definition.categories,
            category: definition.categories[0],
            map: map,
            icon: { url: definition.icon, size: new google.maps.Size(24, 36), origin: new google.maps.Point(0, 0), anchor: new google.maps.Point(12, 36) }
        });
        google.maps.event.addListener(marker, 'click', function() {
            infoWindow.setContent(definition.content);
            infoWindow.open(map, marker);
            map.panTo(marker.getPosition ? marker.getPosition() : new google.maps.LatLng(definition.lat, definition.lng));
            map.setZoom(10);
        });
        markers.push(marker);
    }

    function renderMap(data) {
        clearMarkers();
        var definitions = [];
        var center = defaultCenter;
        angular.forEach(angular.isArray(data) ? data : [], function(value) {
            if (validCoordinate(value.lat) && validCoordinate(value.lang)) {
                definitions.push(markerFor(value));
                center = { lat: parseFloat(value.lat), lng: parseFloat(value.lang) };
            }
        });
        createMap(center);
        $scope.mapMarkerCategories = [];
        angular.forEach(definitions, addMarker);
        angular.forEach(definitions, function(definition) {
            $scope.mapMarkerCategories = $scope.mapMarkerCategories.concat(definition.categories);
        });
        $scope.hasResults = definitions.length > 0;
    }

    function loadData() {
        var params = queryString(), sequence = ++requestSequence;
        $scope.qs_params = params;
        $scope.loading = true;
        $scope.errorMessage = '';
        var countDone = false, mapDone = false;
        function updateLoading() { $scope.loading = !(countDone && mapDone); }
        mapViewFactory.getAllCount(params).then(function(response) {
            if (sequence === requestSequence) $scope.listData = response.data || {};
            countDone = true; updateLoading();
        }, function() {
            if (sequence === requestSequence) $scope.errorMessage = 'Unable to load monitor counts.';
            countDone = true; updateLoading();
        });
        mapViewFactory.getAllMapDashboardData(params).then(function(response) {
            if (sequence === requestSequence) renderMap(response.data);
            mapDone = true; updateLoading();
        }, function() {
            if (sequence === requestSequence) { renderMap([]); $scope.errorMessage = 'Unable to load monitor map data.'; }
            mapDone = true; updateLoading();
        });
    }

    $scope.search = loadData;
    $scope.filterMarkers = function(category) {
        angular.forEach(markers, function(marker) {
            marker.setVisible(category === 'all' || !category || (marker.categories && marker.categories.indexOf(category) !== -1));
        });
    };
    $scope.getMandalOnSelect = function() {
        $scope.selectedMandal = 'ALL'; $scope.select_gp = 'ALL'; $scope.gp_list = ['ALL'];
        mapViewFactory.getByMandal(valueOrAll($scope.selectedDistrict)).then(function(response) {
            $scope.mandal_list = ['ALL'].concat(response.data || []);
        }, function() { $scope.mandal_list = ['ALL']; $scope.errorMessage = 'Unable to load mandals.'; });
    };
    $scope.getGpOnSelect = function() {
        $scope.select_gp = 'ALL';
        mapViewFactory.getByGp(valueOrAll($scope.selectedMandal)).then(function(response) {
            $scope.gp_list = ['ALL'].concat(response.data || []);
        }, function() { $scope.gp_list = ['ALL']; $scope.errorMessage = 'Unable to load GPs.'; });
    };
    loadData();
});
