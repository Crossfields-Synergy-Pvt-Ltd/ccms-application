var modified_operationalCntl = angular.module('modified_operationalControllers', []);

modified_operationalCntl.controller('modified_operationalListControllers', function($scope, modified_operationalFactory) {
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

  function selectedGateway() {
    return $scope.selected_dcu && $scope.selected_dcu.name && $scope.selected_dcu.name.gateway_identifier;
  }
  function showError(message) { $scope.errorMessage = message; }
  function clearError() { $scope.errorMessage = null; }
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

  modified_operationalFactory.getAllDcuNames().then(function(data) {
    $scope.dcu_data = data.data;
  }).catch(function() { showError('Unable to load DCU names.'); });

  $scope.showdate = function() {
    if (!selectedGateway()) { showError('Please select a DCU first.'); return; }
    clearError();
    $scope.loading = true;
    modified_operationalFactory.getAllByDate(dateParams()).then(function(data) {
      $scope.todos = data.data || [];
      $scope.currentPage = 1;
      $scope.figureOutTodosToDisplay();
    }).catch(function() {
      $scope.todos = [];
      $scope.list = [];
      showError('Unable to load light status data.');
    }).finally(function() { $scope.loading = false; });
  };

  $scope.exportLightStatus = function() {
    if (!selectedGateway()) { showError('Please select a DCU first.'); return; }
    clearError();
    $scope.loading = true;
    modified_operationalFactory.exportLightStatus(dateParams()).catch(function() {
      showError('Unable to export light status data.');
    }).finally(function() { $scope.loading = false; });
  };
});
