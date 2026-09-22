var operationalCntl = angular.module('operationalControllers', []);

operationalCntl.controller('operationalListControllers', function($scope, operationalFactory, config) {
  $scope.sortType = 'id';
  $scope.sortReverse = false;
  $scope.searchFish = '';
  $scope.selected_dcu = {};
  $scope.loading = false;
  $scope.errorMessage = null;
  $scope.todos = [];
  $scope.list = [];
  $scope.itemsPerPage = 25;
  $scope.currentPage = 1;

  function showError(message) { $scope.errorMessage = message; }
  function clearError() { $scope.errorMessage = null; }
  function selectedGateway() {
    return $scope.selected_dcu && $scope.selected_dcu.name && $scope.selected_dcu.name.gateway_identifier;
  }
  function dateParams() {
    var start = moment($scope.datePicker.date.startDate).format('DD/MM/YYYY');
    var end = moment($scope.datePicker.date.endDate).format('DD/MM/YYYY');
    return '?id=' + encodeURIComponent(selectedGateway()) + '&start_date=' + encodeURIComponent(start) + '&end_date=' + encodeURIComponent(end);
  }

  $scope.figureOutTodosToDisplay = function() {
    var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
    $scope.list = $scope.todos.slice(begin, begin + $scope.itemsPerPage);
  };
  $scope.pageChanged = function() { $scope.figureOutTodosToDisplay(); };

  $scope.filterValues = {};
  $scope.filter = function() {
    var qs = '?district=' + encodeURIComponent($scope.selectedDistrict || 'ALL') +
      '&mandal=' + encodeURIComponent($scope.selectedMandal || 'ALL') +
      '&gp=' + encodeURIComponent($scope.select_gp || 'ALL') +
      '&village=' + encodeURIComponent($scope.filterValues.village || 'ALL');
    clearError();
    operationalFactory.getAllDcuNames(qs).then(function(data) {
      $scope.dcu_data = data.data;
    }).catch(function() { showError('Unable to load DCU names.'); });
  };
  operationalFactory.getAllDcuNames().then(function(data) {
    $scope.dcu_data = data.data;
  }).catch(function() { showError('Unable to load DCU names.'); });

  $scope.datePicker = { date: {startDate: new Date(), endDate: new Date()} };
  $scope.opts = {
    locale: { applyClass: 'btn-green', applyLabel: 'Apply', fromLabel: 'From', format: 'YYYY-MM-DD', toLabel: 'To', cancelLabel: 'Cancel', customRangeLabel: 'Custom range' },
    ranges: {
      'Today': [moment().startOf('day'), moment()],
      'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
      'Last 7 Days': [moment().subtract(6, 'days'), moment()],
      'This Month': [moment().startOf('month'), moment().endOf('month')],
      'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    }
  };

  $scope.showdate = function() {
    if (!selectedGateway()) { showError('Please select a DCU first.'); return; }
    clearError();
    $scope.loading = true;
    operationalFactory.getAllOperationalHourByDate(dateParams()).then(function(data) {
      $scope.todos = data.data || [];
      $scope.currentPage = 1;
      $scope.figureOutTodosToDisplay();
    }).catch(function() {
      $scope.todos = [];
      $scope.list = [];
      showError('Unable to load operational hours.');
    }).finally(function() { $scope.loading = false; });
  };

  $scope.export_operationalhour = function() {
    if (!selectedGateway()) { showError('Please select a DCU first.'); return; }
    clearError();
    $scope.loading = true;
    operationalFactory.getAllExport(dateParams()).catch(function() {
      showError('Unable to export operational hours.');
    }).finally(function() { $scope.loading = false; });
  };

  $scope.selectedDistrict = '';
  $scope.districts = config.districts;
  $scope.getMandalOnSelect = function() {
    $scope.selectedMandal = null; $scope.select_gp = null; $scope.filterValues.village = null;
    $scope.gp_list = []; $scope.village_list = [];
    operationalFactory.getByMandal($scope.selectedDistrict).then(function(data) { $scope.mandal_list = data.data; }).catch(function() { showError('Unable to load Mandals.'); });
  };
  $scope.getGpOnSelect = function() {
    $scope.select_gp = null; $scope.filterValues.village = null; $scope.village_list = [];
    operationalFactory.getByGp($scope.selectedMandal).then(function(data) { $scope.gp_list = data.data; }).catch(function() { showError('Unable to load GPs.'); });
  };
  $scope.getVillageOnSelect = function() {
    $scope.filterValues.village = null;
    operationalFactory.getByVillage($scope.select_gp).then(function(data) { $scope.village_list = data.data; }).catch(function() { showError('Unable to load Villages.'); });
  };
});
