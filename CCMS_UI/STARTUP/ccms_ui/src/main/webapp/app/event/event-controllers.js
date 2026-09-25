var eventCntl = angular.module('eventControllers', []);

eventCntl.controller('eventListControllers', function($scope, $rootScope, eventFactory, config, inform) {
    $scope.sortType = 'id'; $scope.sortReverse = false; $scope.searchFish = '';
    $scope.dcuId = ""; $scope.todos = []; $scope.list = [];
    $scope.itemsPerPage = 25; $scope.currentPage = 1; $scope.loading = false;
    $scope.exporting = false; $scope.errorMessage = null; $scope.filterValues = { village: null };

    function notify(message, type) {
        $scope.errorMessage = message;
        if (inform && inform.add) inform.add(message, {ttl: 4000, type: type || 'danger'});
    }
    function clearError() { $scope.errorMessage = null; }
    function selectedGateway() { return ($scope.dcuId || '').trim(); }
    function dateQuery() {
        return '?id=' + encodeURIComponent(selectedGateway()) +
            '&start_date=' + encodeURIComponent(moment($scope.datePicker.date.startDate).format('DD/MM/YYYY')) +
            '&end_date=' + encodeURIComponent(moment($scope.datePicker.date.endDate).format('DD/MM/YYYY'));
    }
    function validateSelection() {
        if (!selectedGateway()) { notify('Enter a DCU gateway serial number first.', 'warning'); return false; }
        if (!/^[A-Za-z0-9._-]+$/.test(selectedGateway())) { notify('Enter a valid DCU gateway serial number.', 'warning'); return false; }
        return true;
    }

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
        }).catch(function(error) {
            $scope.todos = []; $scope.list = [];
            if (error && error.status === 404) notify("DCU gateway serial number was not found.");
            else if (error && error.status === 400) notify("Enter a valid DCU gateway serial number.");
            else notify("Unable to load event data.");
        }).finally(function() { $scope.loading = false; });
    };

    $scope.export_events = function() {
        if (!validateSelection()) return;
        clearError(); $scope.exporting = true;
        eventFactory.exportEventData(dateQuery()).then(function(response) {
            if (response && response.status === 204) notify('No events found for the selected range.', 'warning');
        }).catch(function(error) {
            if (error && error.status === 404) notify('DCU gateway serial number was not found.');
            else if (error && error.status === 400) notify('Enter a valid DCU gateway serial number.');
            else if (!error || error.status !== 204) notify('Unable to export event data.');
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
    $scope.filter = function() { clearError(); };
});
