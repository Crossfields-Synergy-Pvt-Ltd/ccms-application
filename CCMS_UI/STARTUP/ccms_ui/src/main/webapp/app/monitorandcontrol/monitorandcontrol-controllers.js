var monitorandcontrolCntl = angular.module('monitorandcontrolControllers', []);

monitorandcontrolCntl.controller('monitorandcontrolListControllers', function($scope, $state, $modal, $rootScope, monitorandcontrolFactory, config) {
  $scope.sortType = 'id';
  $scope.sortReverse = false;
  $scope.searchFish = '';
  $scope.currentPage = 0;
  $scope.pageSize = 50;
  $scope.totalRecords = 0;
  $scope.handshake_Data = [];
  $scope.filteredData = [];
  $scope.loading = false;
  $scope.errorMessage = null;

  $scope.mcbtrip_check = true;
  $scope.contactorfailure_check = true;
  $scope.mainssupplyoff_check = true;
  $scope.dooropen_check = true;
  $scope.spdfailure_check = true;
  $scope.nooutput_check = true;
  $scope.manualmode_check = true;
  $scope.no_check = true;
  $scope.off_check = true;
  $scope.goodgprsconnectivity_check = true;
  $scope.poorgprsconnectivity_check = true;

  $scope.selectedDistrict = ($rootScope.privilege && $rootScope.privilege.district) || 'ALL';
  $scope.selectedMandal = ($rootScope.privilege && $rootScope.privilege.mandal) || 'ALL';
  $scope.select_gp = ($rootScope.privilege && $rootScope.privilege.gp) || 'ALL';
  $scope.selectedVillage = 'ALL';
  $scope.districts = config.districts;
  $scope.mandal_list = [];
  $scope.gp_list = [];
  $scope.village_list = [];
  $scope.datePicker = { date: { startDate: null, endDate: null } };
  $scope.opts = {
    locale: { applyClass: 'btn-green', applyLabel: 'Apply', fromLabel: 'From', format: 'YYYY-MM-DD', toLabel: 'To', cancelLabel: 'Cancel', customRangeLabel: 'Custom range' },
    ranges: {
      'Today': [moment().startOf('day'), moment()],
      'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
      'Last 7 Days': [moment().subtract(6, 'days').startOf('day'), moment()],
      'This Month': [moment().startOf('month'), moment().endOf('month')],
      'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    }
  };

  function buildQuery(dateParams) {
    return '?district=' + encodeURIComponent($scope.selectedDistrict) +
      '&mandal=' + encodeURIComponent($scope.selectedMandal) +
      '&gp=' + encodeURIComponent($scope.select_gp) +
      '&village=' + encodeURIComponent($scope.selectedVillage || 'ALL') + (dateParams || '');
  }

  $scope.qs_params = buildQuery();

  $scope.loadPage = function(page) {
    if ($scope.loading) return;
    $scope.loading = true;
    $scope.errorMessage = null;
    $scope.currentPage = page;
    var searchParam = ($scope.searchFish && $scope.searchFish.length >= 3) ? $scope.searchFish : null;
    monitorandcontrolFactory.getAllHandShake($scope.qs_params, page, $scope.pageSize, searchParam).then(function(data) {
      var newData = data.data || [];
      $scope.handshake_Data = page === 0 ? newData : $scope.handshake_Data.concat(newData);
      $scope.applyFilters();
    }, function(error) {
      $scope.handshake_Data = [];
      $scope.filteredData = [];
      if (error && (error.status === 401 || error.status === 403)) {
        $scope.errorMessage = "You are not authorized to view monitor data.";
      } else {
        $scope.errorMessage = "Unable to load monitor data. Please try again.";
      }
    }).finally(function() {
      $scope.loading = false;
    });
  };

  $scope.applyFilters = function() {
    var checks = [$scope.mcbtrip_check, $scope.contactorfailure_check, $scope.mainssupplyoff_check,
      $scope.dooropen_check, $scope.spdfailure_check, $scope.nooutput_check, $scope.manualmode_check,
      $scope.no_check, $scope.off_check, $scope.goodgprsconnectivity_check, $scope.poorgprsconnectivity_check];
    var searchText = ($scope.searchFish || '').toLowerCase();
    if (!$scope.handshake_Data) { $scope.filteredData = []; return; }
    if (checks.every(function(value) { return value === true; }) && !searchText) {
      $scope.filteredData = $scope.handshake_Data;
      return;
    }
    $scope.filteredData = [];
    $scope.handshake_Data.forEach(function(item) {
      if (!item || !item.dcu_details) return;
      if (searchText && searchText.length < 3) {
        var textMatch = (item.device_name && item.device_name.toLowerCase().indexOf(searchText) !== -1) ||
          (item.id && item.id.toLowerCase().indexOf(searchText) !== -1) ||
          (item.dcu_details.name && item.dcu_details.name.toLowerCase().indexOf(searchText) !== -1);
        if (!textMatch) return;
      }
      var details = item.dcu_details;
      var add = (checks[0] && details.mcb_trip == 1) || (checks[1] && details.cnt_status == 1) ||
        (checks[2] && details.main_supply_status == 1) || (checks[3] && details.door_status == 1) ||
        (checks[4] && details.spd_status == 1) || (checks[5] && details.red_phse_no_output == 1) ||
        (checks[6] && details.manual_mode_status == 1) || (checks[7] && details.light_status == 1) ||
        (checks[8] && details.light_status == 0) || (checks[9] && details.csq > 15) ||
        (checks[10] && details.csq < 15);
      if (add) $scope.filteredData.push(item);
    });
  };

  ['mcbtrip_check', 'contactorfailure_check', 'mainssupplyoff_check', 'dooropen_check', 'spdfailure_check',
    'nooutput_check', 'manualmode_check', 'no_check', 'off_check', 'goodgprsconnectivity_check',
    'poorgprsconnectivity_check', 'searchFish'].forEach(function(name) {
      $scope.$watch(name, $scope.applyFilters);
    });

  function refreshCount() {
    var searchParam = ($scope.searchFish && $scope.searchFish.length >= 3) ? $scope.searchFish : null;
    monitorandcontrolFactory.getAllCount($scope.qs_params, searchParam).then(function(data) {
      $scope.count_stats = data.data || {};
      $scope.totalRecords = $scope.count_stats.total_devices || 0;
    }, function(error) {
      if (!$scope.errorMessage) $scope.errorMessage = error && (error.status === 401 || error.status === 403) ? "You are not authorized to view monitor data." : "Unable to load monitor counts.";
    });
  }

  $scope.search = function() {
    var dateParams = '';
    if ($scope.datePicker.date.startDate && $scope.datePicker.date.endDate) {
      dateParams = '&start_date=' + moment($scope.datePicker.date.startDate).format('YYYY-MM-DD') +
        '&end_date=' + moment($scope.datePicker.date.endDate).format('YYYY-MM-DD');
    }
    $scope.qs_params = buildQuery(dateParams);
    $scope.errorMessage = null;
    $scope.handshake_Data = [];
    $scope.filteredData = [];
    refreshCount();
    $scope.loading = false;
    $scope.loadPage(0);
  };
  $scope.nextPage = function() { if ($scope.hasMoreData()) $scope.loadPage($scope.currentPage + 1); };
  $scope.prevPage = function() { if ($scope.currentPage > 0) $scope.loadPage($scope.currentPage - 1); };
  $scope.hasMoreData = function() { return $scope.totalRecords > ($scope.currentPage + 1) * $scope.pageSize; };

  $scope.gotoswitchpoint = function(obj) {
    var details = obj && obj.dcu_details;
    if (details && details.gateway_serial_number) {
      $state.go('dashboard.switchpoint', { gateway_serial_number: details.gateway_serial_number });
    }
  };

  $scope.toggle_light = function(obj) {
    if (!obj || !obj.dcu_details) return;
    $scope.obj = obj;
    var details = obj.dcu_details;
    var params = '?device_serial_number=' + encodeURIComponent(details.gateway_serial_number) +
      '&device_identifier=' + encodeURIComponent(details.serial_number);
    $scope.command_message = null;
    $scope.command_error = null;
    var request = details.light_status == 1 ? monitorandcontrolFactory.turnOffLights(params) : monitorandcontrolFactory.turnOnLights(params);
    request.then(function() {
      details.light_status = details.light_status == 1 ? 0 : 1;
      $scope.command_message = details.light_status == 1 ? 'Light turned on successfully.' : 'Light turned off successfully.';
    }, function() {
      $scope.command_error = 'Unable to change the light status.';
    });
  };

  $scope.deleteconf = function(id) {
    $modal.open({ templateUrl: 'app/common/delete.html', controller: 'dcuDeleteController', resolve: { id: function() { return id; } } });
  };
  $scope.update = function(obj) { $state.go('dashboard.dcu_edit', { dcu: obj }); };

  $scope.getMandalOnSelect = function() {
    $scope.selectedMandal = 'ALL'; $scope.select_gp = 'ALL'; $scope.selectedVillage = 'ALL';
    $scope.gp_list = []; $scope.village_list = [];
    monitorandcontrolFactory.getByMandal($scope.selectedDistrict).then(function(data) { $scope.mandal_list = data.data || []; });
  };
  $scope.getGpOnSelect = function() {
    $scope.select_gp = 'ALL'; $scope.selectedVillage = 'ALL'; $scope.village_list = [];
    monitorandcontrolFactory.getByGp($scope.selectedMandal).then(function(data) { $scope.gp_list = data.data || []; });
  };
  $scope.getVillageOnSelect = function() {
    $scope.selectedVillage = 'ALL'; $scope.village_list = [];
    if ($scope.select_gp && $scope.select_gp !== 'ALL') {
      monitorandcontrolFactory.getByVillage($scope.select_gp).then(function(data) { $scope.village_list = data.data || []; });
    }
  };

  refreshCount();
    $scope.loading = false;
  $scope.loadPage(0);
  $scope.getMandalOnSelect();
  $scope.getGpOnSelect();
});

monitorandcontrolCntl.filter('myFormat', function() { return function(items) { return items; }; });
