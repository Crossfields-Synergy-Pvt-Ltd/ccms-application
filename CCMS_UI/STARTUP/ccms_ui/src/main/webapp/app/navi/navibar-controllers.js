/*	
var navbarCntl = angular.module('navbarControllers', []);

navbarCntl.controller('navbarControllers', function($scope, $state,$stateParams, $modal,$location, $http,$rootScope) {
	 alert('nav bar controller')
	//$(function () {
//  $('.side-nav').css({ minheight: $(window).innerheight() });
//  $(window).resize(function () {
//      $('.side-nav').css({ minheight: $(window).innerheight() });
//  });
//});
*/
var showCollapsibleMenu = false;
$(document).ready(function () {
 // ShowModal();
 /* handleAlertCloseEvent();
  if (document.cookie == null || document.cookie == "") {
      document.cookie = ApplicationConstants.menuStateCookieName + "=false; path=/;";

  }
  else {
      var menuStateValue = ReadCookie();
      RestoreMenuState(menuStateValue);
  }*/

  $('body').delegate('#config-tool-cog', 'click', function (event) {
      if ($("#config-tool").hasClass("opened")) {
          hideMainMenu();
      }
      else {
          showMainMenu();
      }
  });

  $('[data-toggle="tooltip"]').tooltip();
});


function hideMainMenu() {
  $("#config-tool").removeClass("opened");
  $("#wrapper").addClass("wrapperZeroPadding");
  $("#sidebarNavigation").addClass("customsidebar");
  $("#iconsNavigation").addClass("iconDisplay");
  $("#iconDND").removeClass("iconNotDisplay");
  $("#brandLogoDiv").addClass("hidden");
  $("#footerLogoDiv").addClass("hidden");
  $("#sidebarNavigation").addClass("defaultHidden");
  $("#iconsNavigation").addClass("defaultShow");
  $("#config-tool").addClass("defaultShow");
  dropDownMenuOpen();
  del_cookie("showCollapsibleMenu");
  document.cookie = ApplicationConstants.menuStateCookieName + "=true; path=/;";
}

function showMainMenu() {
  $("#config-tool").addClass("opened");
  $("#wrapper").removeClass("wrapperZeroPadding");
  $("#sidebarNavigation").removeClass("customsidebar");
  $("#iconsNavigation").removeClass("iconDisplay");
  $("#iconDND").addClass("iconNotDisplay");
  $("#brandLogoDiv").removeClass("hidden");
  $("#footerLogoDiv").removeClass("hidden");
  $("#sidebarNavigation").addClass("defaultShow");
  $("#iconsNavigation").addClass("defaultHidden");
  $("#config-tool").addClass("defaultShow");

  dropDownMenuOpen();

  del_cookie(ApplicationConstants.menuStateCookieName);
  document.cookie = ApplicationConstants.menuStateCookieName + "=false; path=/;";

}

function hideTabletMenu() {
  $('.navbar-collapse.in').collapse('hide');
}

function dropDownMenuOpen() {
  if ($("#reportDropDownMenu li.active").hasClass("active")) {
      $("#sidebarNavigation li#reportDropDownMenu").addClass("active");
      $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
      $("#iconsNavigation li#reportDropDownMenu").addClass("active");

  }
  if ($("#configDropDownMenu li").hasClass("active")) {
      $("#sidebarNavigation li#configDropDownMenu").addClass("active");
      $("#sidebarNavigation li#configDropDownMenu").addClass("open");
      $("#iconsNavigation li#configDropDownMenu").addClass("active");
  }
}
/*
*This function enable bootstrap tooltip 
*/
$(document).tooltip({
  selector: '[data-toggle="tooltip"]'
});
function deselectAlltheSubMenus() {
  $("#sidebarNavigation li.active").removeClass("active");
  $("#iconsNavigation li.active").removeClass("active");
  $("#sidebarNavigation li#configDropDownMenu").removeClass("open");
  $("#sidebarNavigation li#reportDropDownMenu").removeClass("open");

}

/*
* This function add the active class to the selected menu.
* This function is called from the all the controllers - on page load
*/
function selecteSidebarMenu(routeURL) {
  //RestoreMenuState(showCollapsibleMenu);
  hideTabletMenu();
  deselectAlltheSubMenus();
  switch (routeURL) {
      case AngularJSRouteURL.Customer.customer:
      case AngularJSRouteURL.Customer.createCustomer:
      case AngularJSRouteURL.Customer.updateCustomer:
          $("#sidebarNavigation li#customerMenu").addClass("active");
          $("#iconsNavigation li#customerMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");

          break;

      case AngularJSRouteURL.User.user:
      case AngularJSRouteURL.User.createUser:
      case AngularJSRouteURL.User.updateUser:
          $("#sidebarNavigation li#userMenu").addClass("active");
          $("#iconsNavigation li#userMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.Role.roles:
      case AngularJSRouteURL.Role.createRole:
          $("#sidebarNavigation li#roleMenu").addClass("active");
          $("#iconsNavigation li#roleMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.Tag.taghierarchy:
      case AngularJSRouteURL.Tag.taghierarchyCreate:
          $("#sidebarNavigation li#tagMenu").addClass("active");
          $("#iconsNavigation li#tagMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;
      case AngularJSRouteURL.Tag.createTaghierarchy:
      case AngularJSRouteURL.Tag.taghierarchyAssociation:
          $("#sidebarNavigation li#siteConfigurationMenu").addClass("active");
          $("#iconsNavigation li#siteConfigurationMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.Node.node:
      case AngularJSRouteURL.Node.createNode:
      case AngularJSRouteURL.Node.updateNode:
          $("#sidebarNavigation li#nodeMenu").addClass("active");
          $("#iconsNavigation li#nodeMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;
      case AngularJSRouteURL.Node.repeaterListView:
          $("#sidebarNavigation li#repeaterMenu").addClass("active");
          $("#iconsNavigation li#repeaterMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;
      case AngularJSRouteURL.Node.light:
      case AngularJSRouteURL.Node.createLight:
      case AngularJSRouteURL.Node.updateLight:
          $("#sidebarNavigation li#lightMenu").addClass("active");
          $("#iconsNavigation li#lightMenu").addClass("active");
          break;

      case AngularJSRouteURL.Feature.feature:
      case AngularJSRouteURL.Feature.associateFeaturesToCustomer:
      case AngularJSRouteURL.Feature.updateNode:
          $("#sidebarNavigation li#featureMenu").addClass("active");
          $("#iconsNavigation li#featureMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.AlertNotify.alert:
          $("#sidebarNavigation li#alertMenu").addClass("active");
          $("#iconsNavigation li#alertMenu").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.control:
      case AngularJSRouteURL.ControlScreen.readingsGridView:
      case AngularJSRouteURL.ControlScreen.monitorAndControl:
          $("#sidebarNavigation li#controlSubMenu1").addClass("active");
          $("#iconsNavigation li#controlSubMenu1").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.mapView:
          $("#sidebarNavigation li#mapView").addClass("active");
          $("#iconsNavigation li#mapView").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.historyDataView:
          $("#sidebarNavigation li#ListView").addClass("active");
          $("#iconsNavigation li#ListView").addClass("active");
          break;
      case AngularJSRouteURL.Graph.graph:
          $("#sidebarNavigation li#graphView").addClass("active");
          $("#iconsNavigation li#graphView").addClass("active");
          break;

      case AngularJSRouteURL.Firmware.applyFirmwareForCustomer:
      case AngularJSRouteURL.Firmware.uploadfirmware:
          $("#sidebarNavigation li#firmwareMenu").addClass("active");
          $("#iconsNavigation li#firmwareMenu").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;
      case AngularJSRouteURL.Firmware.applyFirmwareForSuperAdmin:
          $("#sidebarNavigation li#firmwareMenuUpload").addClass("active");
          $("#iconsNavigation li#firmwareMenuUpload").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");

          break;
      case AngularJSRouteURL.ControlScreen.meterListView:
          $("#sidebarNavigation li#MeterListView").addClass("active");
          $("#iconsNavigation li#MeterListView").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.switchPointSinglePhaseSummary:
          $("#sidebarNavigation li#SinglePhaseSwitchPointSummary").addClass("active");
          $("#iconsNavigation li#SinglePhaseSwitchPointSummary").addClass("active");
          break;
      case AngularJSRouteURL.Report.ProjectSummaryTemplate:
          $("#sidebarNavigation li#reportProjectSummaryMenu").addClass("active");
          $("#iconsNavigation li#ProjectSummarySubmenu").addClass("active");
          break;
      case ReportConstants.ReportTypes.FAULTY_LIGHTS:
          $("#sidebarNavigation li#reportLampFailureMenu").addClass("active");
          $("#iconsNavigation li#reportSubMenuLamp").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");

          break;
      case ReportConstants.ReportTypes.Lamp_Failure:
          $("#sidebarNavigation li#reportLampHoursMenu").addClass("active");
          $("#iconsNavigation li#reportLampHoursMenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");

          break;

      case ReportConstants.ReportTypes.OPERATIONAL_HOURS:
          $("#sidebarNavigation li#reportOerationalMenu").addClass("active");
          $("#iconsNavigation li#reportSubMenuOperation").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;
      case ReportConstants.ReportTypes.SCHEDULEWISE_OPERATIONAL_HOURS:
          $("#sidebarNavigation li#schedulewiseReportOerationalMenu").addClass("active");
          $("#iconsNavigation li#schedulewiseReportSubMenuOperation").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;
      case ReportConstants.ReportTypes.BURN_HOUR:
          $("#sidebarNavigation li#burnHourReportMenu").addClass("active");
          $("#iconsNavigation li#burnHourReportSubMenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;
      case ReportConstants.ReportTypes.VOLTAGE_VS_TIME:
          $("#sidebarNavigation li#reportVoltageVTMenu").addClass("active");
          $("#iconsNavigation li#voltageTimeSubMenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case ReportConstants.ReportTypes.CURRENT_VS_TIME:
          $("#sidebarNavigation li#reportCurrentVtMenu").addClass("active");
          $("#iconsNavigation li#currentSumMenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case ReportConstants.ReportTypes.PF_VS_TIME:
          $("#sidebarNavigation li#reportPfVsTimeMenu").addClass("active");
          $("#iconsNavigation li#pfvsTimeSubmenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          break;
      case ReportConstants.ReportTypes.KW_Vs_Time:
          $("#sidebarNavigation li#reportKWVsTimeMenu").addClass("active");
          $("#iconsNavigation li#kwsTimeSubmenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case ReportConstants.ReportTypes.FAULTY_LOAD:
          $("#sidebarNavigation li#reportFaultyLoadMenu").addClass("active");
          $("#iconsNavigation li#faultySubmenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case ReportConstants.ReportTypes.UP_TIME:
          $("#sidebarNavigation li#reportUpTimeMenu").addClass("active");
          $("#iconsNavigation li#reportUpTimeSubmenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case ReportConstants.ReportTypes.ENERGY_SAVING:
          $("#sidebarNavigation li#reportEnergySavingMenu").addClass("active");
          $("#iconsNavigation li#reportEnergySavingSubmenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;
      case ReportConstants.ReportTypes.CALCULATED_DAILY_CONSUMPTION:
          $("#sidebarNavigation li#reportCalculatedDailyConsumptionMenu").addClass("active");
          $("#iconsNavigation li#reportSubMenuCalculatedDailyConsumption").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case ReportConstants.ReportTypes.DAILY_CONSUMPTION:
          $("#sidebarNavigation li#reportDailyConsumptionMenu").addClass("active");
          $("#iconsNavigation li#reportDailyConsumptionMenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;
      case ReportConstants.ReportTypes.FEEDER_DAILY_CONSUMPTION:
          $("#sidebarNavigation li#reportDailyConsumptionMenuFeeder").addClass("active");
          $("#iconsNavigation li#reportDailyConsumptionMenuFeeder").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.lightsMonitorControlScreen:
          $("#sidebarNavigation li#lightMonitorAndControl").addClass("active");
          $("#iconsNavigation li#lightMonitorAndControl").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.lightsMonitorControlScreen2:
          $("#sidebarNavigation li#lightMonitorAndControl2").addClass("active");
          $("#iconsNavigation li#lightMonitorAndControl2").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.philipsLightsMonitorControlScreen:
          $("#sidebarNavigation li#PhilipsLightControl").addClass("active");
          $("#iconsNavigation li#PhilipsLightControl").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.meterMonitorAndControl:
          $("#sidebarNavigation li#meterRelayControlMenu").addClass("active");
          $("#iconsNavigation li#meterRelayControlMenu").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.waterMeter:
          $("#sidebarNavigation li#WaterMeter").addClass("active");
          $("#iconsNavigation li#WaterMeterMenuLink").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.valveControl:
          $("#sidebarNavigation li#ValveControl").addClass("active");
          $("#iconsNavigation li#ValveControlLink").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.meteringListView:
          $("#sidebarNavigation li#meteringListViewMenu").addClass("active");
          $("#iconsNavigation li#meteringListViewMenu").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.meteringHistoryView:
          $("#sidebarNavigation li#meteringHistoryViewMenu").addClass("active");
          $("#iconsNavigation li#meteringHistoryViewMenu").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.deviceHistoryView:
          $("#sidebarNavigation li#deviceHistoryViewMenu").addClass("active");
          $("#iconsNavigation li#deviceHistoryViewMenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.groupHistoryView:
          $("#sidebarNavigation li#groupHistoryViewMenu").addClass("active");
          $("#iconsNavigation li#groupHistoryViewMenu").addClass("active");
          break;

      case AngularJSRouteURL.DashboardScreen.dashboardScreen:
          $("#sidebarNavigation li#dashboardMenu").addClass("active");
          $("#iconsNavigation li#dashboardMenuSubMenu").addClass("active");
          break;

      case AngularJSRouteURL.Graph.meteringGraph:
          $("#sidebarNavigation li#AnalyticsViewMenu").addClass("active");
          $("#iconsNavigation li#AnalyticsViewMenu").addClass("active");
          break;
      case AngularJSRouteURL.Graph.meteringGraphNew:
          $("#sidebarNavigation li#AnalyticsViewMenuNew").addClass("active");
          $("#iconsNavigation li#AnalyticsViewMenuNew").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.deviceMonitorView:
          $("#sidebarNavigation li#deviceMonitor").addClass("active");
          $("#iconsNavigation li#deviceMonitor").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.meterMonitoring:
          $("#sidebarNavigation li#MeterMonitoringMenu").addClass("active");
          $("#iconsNavigation li#MeterMonitoringMenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.outletMonitorControl:
          $("#sidebarNavigation li#outletMonitorAndControlMenu").addClass("active");
          $("#iconsNavigation li#outletMonitorAndControlMenu").addClass("active");
          break;
      case AngularJSRouteURL.UploadNode.importLight:
          $("#sidebarNavigation li#lightImportMenu").addClass("active");
          $("#iconsNavigation li#lightImportMenuSubmenu").addClass("active");
          $("#sidebarNavigation li#uploadDropDownMenu").addClass("open");
          $("#iconsNavigation li#uploadDropDownMenu").addClass("active");
          break;
      case AngularJSRouteURL.Tag.multipleTagNodeAssociation:
          $("#sidebarNavigation li#nodeTagAssociationmenu").addClass("active");
          $("#iconsNavigation li#nodeTagAssociationmenu").addClass("active");
          break;
      case AngularJSRouteURL.Tag.feederGroupAssociation:
          $("#sidebarNavigation li#feederGroupMenu").addClass("active");
          $("#iconsNavigation li#feederGroupMenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.lightsMonitorControlPaginationScreen:
          $("#sidebarNavigation li#lightMonitorAndControlPagination").addClass("active");
          $("#iconsNavigation li#lightControlPaginationView").addClass("active");
          break;
      case AngularJSRouteURL.DashboardScreen.groupDashboardScreen:
          $("#sidebarNavigation li#groupDashboardMenu").addClass("active");
          $("#iconsNavigation li#groupDashboardMenuSubMenu").addClass("active");
          break;

      case AngularJSRouteURL.Metering.MeteringTopology:
          $("#sidebarNavigation li#MeteringTopology").addClass("active");
          $("#iconsNavigation li#MeteringTopology").addClass("active");
          break;

      case AngularJSRouteURL.ControlScreen.lightsMapView:
          $("#sidebarNavigation li#mapView").addClass("active");
          $("#iconsNavigation li#mapView").addClass("active");
          break;


      case AngularJSRouteURL.DashboardScreen.APDashboardScreen:
      case AngularJSRouteURL.DashboardScreen.cmDashboardScreen:
          $("#sidebarNavigation li#APDashboardMenu").addClass("active");
          $("#iconsNavigation li#APDashboardMenu").addClass("active");
          break;

      case AngularJSRouteURL.DashboardScreen.APDashboardScreen2: //shraddha
          $("#sidebarNavigation li#APDashboardNewMenu").addClass("active");//shraddha
          $("#iconsNavigation li#APDashboardNewMenu").addClass("active");//shraddha
          break;

      case AngularJSRouteURL.ControlScreen.transparentMode:
          $("#sidebarNavigation li#transparentModeMenu").addClass("active");
          $("#iconsNavigation li#transparentModeMenu").addClass("active");
          break;

      case AngularJSRouteURL.AssetConfig.panelIntegrator:
          $("#sidebarNavigation li#PanelIntegtratorMenu").addClass("active");
          $("#iconsNavigation li#PanelIntegtratorMenu").addClass("active");
          $("#sidebarNavigation li#uploadDropDownMenu").addClass("open");
          $("#iconsNavigation li#uploadDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.DashboardScreen.GujaratDashboardScreen:
          $("#sidebarNavigation li#GujaratDashboardMenu").addClass("active");
          $("#iconsNavigation li#GujaratDashboardMenu").addClass("active");
          break;

      case AngularJSRouteURL.FieldDiagnostics.fieldDiagnosticsCounterData:
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#sidebarNavigation li#fieldDiagnosticMenu").addClass("active");
          $("#iconsNavigation li#fieldDiagnosticMenu").addClass("active");
          break;

      case AngularJSRouteURL.LORAWANRestAPI.getLatestMeterData:
          $("#sidebarNavigation li#MonitorMenu").addClass("active");
          $("#iconsNavigation li#MonitorMenu").addClass("active");
          break;

      case AngularJSRouteURL.DashboardScreen.lightingDashboardScreen:
          $("#sidebarNavigation li#LightingDashboardMenu").addClass("active");
          $("#iconsNavigation li#LightingDashboardMenu").addClass("active");
          break;

      case AngularJSRouteURL.DashboardScreen.ghmcDashboardScreen:
          $("#sidebarNavigation li#GHMCDashboardMenu").addClass("active");
          $("#iconsNavigation li#GHMCDashboardMenu").addClass("active");
          break;
      case AngularJSRouteURL.DashboardScreen.ghmcInaugurationcreen:
          $("#sidebarNavigation li#GHMCInaugurationMenu").addClass("active");
          $("#iconsNavigation li#GHMCInaugurationdMenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.monitorAndControlOptimizedAlert:
          $("#sidebarNavigation li#MonitorControlWithAlertSummary").addClass("active");
          $("#iconsNavigation li#MonitorControlWithAlertSummarySub").addClass("active");
          break;

      case AngularJSRouteURL.Utility.Utility:
          $("#sidebarNavigation li#UtilityMenu").addClass("active");
          $("#iconsNavigation li#UtilityMenu").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.RTUReplacement.RTUReplacement:
          $("#sidebarNavigation li#RTUReplacementMenu").addClass("active");
          $("#iconsNavigation li#RTUReplacementMenu").addClass("active");         
          break;

      case AngularJSRouteURL.ControlScreen.energyWithRelayMonitorAndControl:
          $("#sidebarNavigation li#MonitorControlMenu").addClass("active");
          $("#iconsNavigation li#MonitorControlMenu").addClass("active");
          break;
          //sangita : added configMenu 
      case AngularJSRouteURL.ControlScreen.transparentMode:
          $("#sidebarNavigation li#configMenu").addClass("active");
          $("#iconsNavigation li#configMenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.newMapViewTemplate:
          $("#sidebarNavigation li#MapMenu").addClass("active");
          $("#iconsNavigation li#MapScreenMenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.CCMSDashboardTemplate:
          $("#sidebarNavigation li#ghmcMapMenuLink").addClass("active");
          $("#iconsNavigation li#ghmcMapSubmenu").addClass("active");
          break;
      case ReportConstants.ReportTypes.PREMIUM_AGING_REPORT:
          $("#sidebarNavigation li#reportPremiumAgeingReport").addClass("active");
          $("#iconsNavigation li#reportPremiumAgeingSubMenuReport").addClass("active");
          $("#sidebarNavigation li#reportDropDownMenu").addClass("open");
          $("#iconsNavigation li#reportDropDownMenu").addClass("active");
          break;

      case AngularJSRouteURL.DashboardScreen.dashboardDetails:
          $("#sidebarNavigation li#UlbMenuLink").addClass("active");
          $("#iconsNavigation li#UlbSubmenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.panelDetailsDashboard:
          $("#sidebarNavigation li#dashboardViewMenu").addClass("active");
          $("#iconsNavigation li#dashboardViewMenuSubMenu").addClass("active");
          break;
      case AngularJSRouteURL.ControlScreen.TelanganaMapViewTemplate:
          $("#sidebarNavigation li#telanganaMapMenuLink").addClass("active");
          $("#iconsNavigation li#telanganaMapSubmenuLink").addClass("active");
          break;
      case AngularJSRouteURL.AlertNotify.invalidAlertInfo:
          $("#sidebarNavigation li#ValidateAlerts").addClass("active");
          $("#iconsNavigation li#ValidateAlerts").addClass("active");
          $("#sidebarNavigation li#configDropDownMenu").addClass("open");
          $("#iconsNavigation li#configDropDownMenu").addClass("active");
          break;
      case AngularJSRouteURL.AssetConfig.updatePanel:
          $("#sidebarNavigation li#updatePanelMenu").addClass("active");
          $("#iconsNavigation li#updatePanelMenu").addClass("active");
          break;
      default:
  }
}

/**-eof*selecteSidebarMenu**/

/**dropdown menu maintain state on outside click**/
$('.dropdown.menuDropDownHeader').on({
  "shown.bs.dropdown": function () { this.closable = false; },
  "click": function () { this.closable = true; },
  "hide.bs.dropdown": function () { return this.closable; }
});
//$('#reportDropDownMenu').on('shown.bs.dropdown', function () {
//  allowClose = false;
//});
//$('#reportDropDownMenu').on('click', function () {
//  allowClose = true;
//});
//$('#reportDropDownMenu').on('hide.bs.dropdown', function () {
//  if (!allowClose)
//      return false;
//});

//$('#configDropDownMenu').on('shown.bs.dropdown', function () {
//  allowClose = false;
//});
//$('#configDropDownMenu').on('click', function () {
//  allowClose = true;
//});
//$('#configDropDownMenu').on('hide.bs.dropdown', function () {
//  if (!allowClose)
//      return false;
//});
/*-eof*dropdown menu maintain state on outside click**/

/**forgot password on complete sending mail to user **/
function forgotPasswordComplete(data) {
  var status = JSON.parse(data.responseText);
  showOuterAlert(status);
  //  $("#loginBack").click();
}
/**change password complete**/
function changePasswordComplete(data) {
  var status = JSON.parse(data.responseText);
  showOuterAlert(status);
  ShowModal();
  if (status.StatusCode == ApplicationConstants.SUCCESS) {
  CloseModalPopup();
  }
}
/**set a new password after clicking mail link*/
function resetPasswordComplete(data) {
  var status = JSON.parse(data.responseText);
  showOuterAlert(status);
  $("#loginBack").click();
}

/***Close  modal popup***/
function CloseModalPopup() {
  $('#ChangePassword').modal('hide');

}


/*
    CLEAR  MODAL POPUP
*/
function handleModalPopupEmpty() {
  $('body').delegate('#ChangePassword', function (e) {

      $(".form-control").empty();
     
  });

}
/*
  This function take the state of the and show/hide meain menu or collapisble menu
*/
function RestoreMenuState(showCollapsibleMenu) {
  if (!showCollapsibleMenu) {
      if ($("#config-tool").hasClass("opened")) {
          showMainMenu();
      }
      //else {
      //    hideMainMenu();
      //}
  }
  else if (showCollapsibleMenu) {
      if ($("#config-tool").hasClass("opened")) {
          hideMainMenu();
      }
  }
  else {
      showMainMenu();
  }
}


/*
  This function read the cooki from the brower and check for the menu state cookie.
  Check the value of the and return the value
*/
function ReadCookie() {
  var allcookies = document.cookie;
  cookiearray = allcookies.split(';');

  for (var i = 0; i < cookiearray.length; i++) {
      var cookie = cookiearray[i];
      name = (cookie.split('=')[0]).trim();
      value = (cookie.split('=')[1]).trim();

      if (name == ApplicationConstants.menuStateCookieName) {
          if (value === 'true') {
              return true;
          }
          return false;
      }
  }
}

function del_cookie(name) {
  document.cookie = name +
  '=; path=/; expires=' + new Date(0).toUTCString();

  ///deleteAllCookies();
}

function deleteAllCookies() {
  var cookies = document.cookie.split(";");

  for (var i = 0; i < cookies.length; i++) {
      var cookie = cookies[i];
      var eqPos = cookie.indexOf("=");
      var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
      document.cookie = name + "=; path=/; expires=" + new Date(0).toUTCString();
  }
}

function defaultHidden() {
  $("#sidebarNavigation").addClass("defaultHidden");
  $("#iconsNavigation").addClass("defaultHidden");
}

function changeColor() {
  less.modifyVars({ '@colorGreen': "#ddd" });
  less.refreshStyles();
}



//$scope.changeValue = function (wholeObj, taghierarchy) {

//  alert("hii");

//};


	 

		 /* });*/