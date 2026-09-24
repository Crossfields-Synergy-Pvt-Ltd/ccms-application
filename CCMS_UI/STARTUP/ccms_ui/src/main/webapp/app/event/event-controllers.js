var eventCntl = angular.module('eventControllers', []);

eventCntl.controller('eventListControllers', function($scope, $rootScope, eventFactory, config, inform) {
    $scope.sortType = 'id'; $scope.sortReverse = false; $scope.searchFish = '';
    $scope.selected_dcu = {}; $scope.dcu_data = []; $scope.todos = []; $scope.list = [];
    $scope.itemsPerPage = 25; $scope.currentPage = 1; $scope.loading = false;
    $scope.exporting = false; $scope.errorMessage = null; $scope.filterValues = { village: null };

    function notify(message, type) {
        $scope.errorMessage = message;
        if (inform && inform.add) inform.add(message, {ttl: 4000, type: type || 'danger'});
    }
    function clearError() { $scope.errorMessage = null; }
    function valueOrAll(value) { return value || 'ALL'; }
    function selectedGateway() {
        return $scope.selected_dcu && $scope.selected_dcu.name && $scope.selected_dcu.name.gateway_identifier;
    }
    function buildFilterQuery() {
        return '?district=' + encodeURIComponent(valueOrAll($scope.selectedDistrict)) +
            '&mandal=' + encodeURIComponent(valueOrAll($scope.selectedMandal)) +
            '&gp=' + encodeURIComponent(valueOrAll($scope.select_gp)) +
            '&village=' + encodeURIComponent(valueOrAll($scope.filterValues.village));
    }
    function dateQuery() {
        return '?id=' + encodeURIComponent(selectedGateway()) +
            '&start_date=' + encodeURIComponent(moment($scope.datePicker.date.startDate).format('DD/MM/YYYY')) +
            '&end_date=' + encodeURIComponent(moment($scope.datePicker.date.endDate).format('DD/MM/YYYY'));
    }
    function validateSelection() {
        if (!selectedGateway()) { notify('Please select a DCU first.', 'warning'); return false; }
        return true;
    }

    var privilege = $rootScope.privilege || {};
    var district = privilege.district || 'ALL', mandal = privilege.mandal || 'ALL', gp = privilege.gp || 'ALL';
    $scope.qs_params = '?district=' + encodeURIComponent(district) + '&mandal=' + encodeURIComponent(mandal) +
        '&gp=' + encodeURIComponent(gp) + '&village=ALL';
    eventFactory.getAllDcuNames($scope.qs_params).then(function(data) {
        $scope.dcu_data = data.data || [];
    }).catch(function() { notify('Unable to load DCU names.'); });

    $scope.filter = function() {
        clearError();
        eventFactory.getAllDcuNames(buildFilterQuery()).then(function(data) {
            $scope.dcu_data = data.data || [];
        }).catch(function() { notify('Unable to filter DCUs.'); });
    };

    $scope.datePicker = {date: {startDate: new Date(), endDate: new Date()}};
    $scope.opts = {
        locale: {applyClass: 'btn-green', applyLabel: 'Apply', fromLabel: 'From', format: 'YYYY-MM-DD',
            toLabel: 'To', cancelLabel: 'Cancel', customRangeLabel: 'Custom range'},
        ranges: {
            'Today': [moment().startOf('day'), moment()],
            'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            'Last 7 Days': [moment().subtract(6, 'days').startOf('day'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        }
    };

    $scope.figureOutTodosToDisplay = function() {
        var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
        $scope.list = ($scope.todos || []).slice(begin, begin + $scope.itemsPerPage);
    };
    $scope.pageChanged = function() { $scope.figureOutTodosToDisplay(); };

    $scope.showdate = function() {
        if (!validateSelection()) return;
        clearError(); $scope.loading = true;
        eventFactory.getByID(dateQuery()).then(function(data) {
            $scope.todos = data.data || []; $scope.currentPage = 1; $scope.figureOutTodosToDisplay();
        }).catch(function() {
            $scope.todos = []; $scope.list = []; notify('Unable to load event data.');
        }).finally(function() { $scope.loading = false; });
    };

    $scope.export_events = function() {
        if (!validateSelection()) return;
        clearError(); $scope.exporting = true;
        eventFactory.exportEventData(dateQuery()).then(function(response) {
            if (response && response.status === 204) notify('No events found for the selected range.', 'warning');
        }).catch(function(error) {
            if (!error || error.status !== 204) notify('Unable to export event data.');
        }).finally(function() { $scope.exporting = false; });
    };

    $scope.selectedDistrict = ''; $scope.districts = config.districts;
    $scope.getMandalOnSelect = function() {
        $scope.selectedMandal = null; $scope.select_gp = null; $scope.filterValues.village = null;
        $scope.gp_list = []; $scope.village_list = [];
        eventFactory.getByMandal($scope.selectedDistrict).then(function(data) {
            $scope.mandal_list = data.data || [];
        }).catch(function() { notify('Unable to load Mandals.'); });
    };
    $scope.getGpOnSelect = function() {
        $scope.select_gp = null; $scope.filterValues.village = null; $scope.village_list = [];
        eventFactory.getByGp($scope.selectedMandal).then(function(data) {
            $scope.gp_list = data.data || [];
        }).catch(function() { notify('Unable to load GPs.'); });
    };
    $scope.getVillageOnSelect = function() {
        $scope.filterValues.village = null;
        eventFactory.getByVillage($scope.select_gp).then(function(data) {
            $scope.village_list = data.data || [];
        }).catch(function() { notify('Unable to load Villages.'); });
    };
});
