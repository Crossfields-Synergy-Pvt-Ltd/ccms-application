var historyCntl = angular.module('historyControllers', []);

historyCntl.controller('historyListControllers', function($scope, $rootScope, historyFactory, config, inform) {
    $scope.sortType = 'utc_date';
    $scope.sortReverse = false;
    $scope.searchFish = '';
    $scope.dcuId = "";
    $scope.todos = [];
    $scope.list = [];
    $scope.itemsPerPage = 25;
    $scope.currentPage = 1;
    $scope.loading = false;
    $scope.exporting = false;
    $scope.error = null;
    $scope.filterValues = { village: null };

    var privilege = $rootScope.privilege || {};
    var initialDistrict = privilege.district || 'ALL';
    var initialMandal = privilege.mandal || 'ALL';
    var initialGp = privilege.gp || 'ALL';

    function notify(message, type) {
        inform.add(message, {ttl: 4000, type: type || 'danger'});
    }

    function reportError(message) {
        $scope.error = message;
        notify(message);
    }

    function valueOrAll(value) {
        return value || 'ALL';
    }

    function buildFilterQuery() {
        return '?district=' + encodeURIComponent(valueOrAll($scope.selectedDistrict)) +
            '&mandal=' + encodeURIComponent(valueOrAll($scope.selectedMandal)) +
            '&gp=' + encodeURIComponent(valueOrAll($scope.select_gp)) +
            '&village=' + encodeURIComponent(valueOrAll($scope.filterValues.village));
    }

    function buildDateQuery() {
        var startDate = moment($scope.datePicker.date.startDate).format('DD/MM/YYYY');
        var endDate = moment($scope.datePicker.date.endDate).format('DD/MM/YYYY');
        return '?id=' + encodeURIComponent(($scope.dcuId || "").trim()) +
            '&start_date=' + encodeURIComponent(startDate) +
            '&end_date=' + encodeURIComponent(endDate);
    }

    function validateSelection() {
        if (!($scope.dcuId || "").trim()) {
            notify('Please select a DCU first.', 'warning');
            return false;
        }
        return true;
    }

    $scope.datePicker = {date: {startDate: new Date(), endDate: new Date()}};
    $scope.opts = {
        locale: {
            applyClass: 'btn-green',
            applyLabel: 'Apply',
            fromLabel: 'From',
            format: 'YYYY-MM-DD',
            toLabel: 'To',
            cancelLabel: 'Cancel',
            customRangeLabel: 'Custom range'
        },
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

    $scope.pageChanged = function() {
        $scope.figureOutTodosToDisplay();
    };

    $scope.showdate = function() {
        if (!validateSelection()) {
            return;
        }

        $scope.loading = true;
        $scope.error = null;
        historyFactory.getByID(buildDateQuery()).then(function(data) {
            $scope.todos = data.data || [];
            $scope.currentPage = 1;
            $scope.figureOutTodosToDisplay();
        }).catch(function() {
            $scope.todos = [];
            $scope.list = [];
            reportError('Unable to load history data.');
        }).finally(function() {
            $scope.loading = false;
        });
    };

    $scope.export_history = function() {
        if (!validateSelection()) {
            return;
        }

        $scope.exporting = true;
        $scope.error = null;
        historyFactory.getByIDhistory(buildDateQuery()).catch(function() {
            reportError('Unable to export history data.');
        }).finally(function() {
            $scope.exporting = false;
        });
    };

    $scope.selectedDistrict = '';
    $scope.districts = config.districts;

    $scope.getMandalOnSelect = function() {
        $scope.selectedMandal = null;
        $scope.select_gp = null;
        $scope.filterValues.village = null;
        $scope.gp_list = [];
        $scope.village_list = [];
        historyFactory.getByMandal($scope.selectedDistrict).then(function(data) {
            $scope.mandal_list = data.data || [];
        }).catch(function() {
            reportError('Unable to load Mandals.');
        });
    };

    $scope.getGpOnSelect = function() {
        $scope.select_gp = null;
        $scope.filterValues.village = null;
        $scope.village_list = [];
        historyFactory.getByGp($scope.selectedMandal).then(function(data) {
            $scope.gp_list = data.data || [];
        }).catch(function() {
            reportError('Unable to load GPs.');
        });
    };

    $scope.getVillageOnSelect = function() {
        $scope.filterValues.village = null;
        historyFactory.getByVillage($scope.select_gp).then(function(data) {
            $scope.village_list = data.data || [];
        }).catch(function() {
            reportError('Unable to load Villages.');
        });
    };
});
