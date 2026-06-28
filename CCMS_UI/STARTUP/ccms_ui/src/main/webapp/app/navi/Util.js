/* 
    padding left
    */
String.prototype.padLeft = function (paddingChar, length) {

    var s = new String(this);

    if ((this.length < length) && (paddingChar.toString().length > 0)) {
        for (var i = 0; i < (length - this.length) ; i++)
            s = paddingChar.toString().charAt(0).concat(s);
    }

    return s;
};

/* 
padding right
*/
String.prototype.padRight = function (paddingChar, length) {

    var s = new String(this);

    if ((this.length < length) && (paddingChar.toString().length > 0)) {
        for (var i = 0; i < (length - this.length) ; i++)
            s = s.concat(paddingChar.toString().charAt(0));
    }

    return s;
};

/*
    This function set the alert message to outer message div and show the alert message
*/
function showOuterAlert(response, autoHide) {
    $("#outer-model-alert-message").removeClass('hidden-lg hidden-md hidden-sm hidden-xs');
    if (response.StatusCode == ApplicationConstants.SUCCESS) {
        if ($("#outer-model-alert-message").hasClass('alert-danger')) {
            $("#outer-model-alert-message").removeClass('alert-danger')
        }

        $("#outer-model-alert-message").addClass('alert-success')
    }
    else {
        if ($("#outer-model-alert-message").hasClass('alert-success')) {
            $("#outer-model-alert-message").removeClass('alert-success')
        }

        $("#outer-model-alert-message").addClass('alert-danger')
    }

    $("#outer-alert-message-content").html(response.Message);
    $("#outer-model-alert-message").show();
    if (undefined == autoHide || autoHide == true) {
        var myVar = setInterval(function () { myTimer(myVar) }, 7000);
    }
}

function myTimer(myVar) {
    $("#outer-model-alert-message").hide();
    clearTimeout(myVar);
}

/* This function is used to show the any HTTP error message*/
function showHTTPErroMessage(response) {
    var responseObj = {};
    responseObj.StatusCode = response.status;
    responseObj.Message = response.statusText;
    showOuterAlert(responseObj);
    //window.location.assign(window.location.origin + appBaseUrl + "User/Login");
}

function removeAlert() {
    $("#outer-model-alert-message").hide();
}





function getDisplayStringByNodeSubType(nodeSubType) {
    switch (nodeSubType) {
        case ApplicationConstants.IoType.IO_ALL_PHASE_RELAY:
            return "ALL";
            break;
        case ApplicationConstants.IoType.IO_R_RELAY:
            return "R_RELAY";
            break;
        case ApplicationConstants.IoType.IO_Y_RELAY:
            return "Y_RELAY";
            break;
        case ApplicationConstants.IoType.IO_B_RELAY:
            return "B_RELAY";
            break;
        case ApplicationConstants.IoType.IO_FAULT_LED:
            return "FaultLED";
            break;
        case ApplicationConstants.IoType.IO_CNTRCT_FAIL:
            return "ContractorFailure";
            break;
        case ApplicationConstants.IoType.IO_R_CNTRCT_FAIL:
            return "R_ContractorFailure";
            break;
        case ApplicationConstants.IoType.IO_Y_CNTRCT_FAIL:
            return "R_ContractorFailure";
            break;
        case ApplicationConstants.IoType.IO_B_CNTRCT_FAIL:
            return "B_ContractorFailure";
            break;
        case ApplicationConstants.IoType.IO_MCB_TRIP:
            return "MCB_trip";
            break;
        case ApplicationConstants.IoType.IO_DOOR_OPEN:
            return "Door_Open";
            break;
        case ApplicationConstants.IoType.IO_AUTO_MAN:
            return "IO_AUTO_MAN";
            break;
        case ApplicationConstants.IoType.IO_DIMMER:
            return "IO_DIMMER";
            break;
        default:
            return "Unknown";
            break;
    }
}

DateTimeUtils = {
    convertToJSDateString: function (dotNetDate) {
        var date = new Date(Date.parse(dotNetDate));
        var dateString = date.getFullYear() + "/" + (date.getMonth() + 1) + "/" + (date.getUTCDate()) + " " + (date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" : "") + (date.getMinutes());
        return dateString;
    },

    convertToJSDate: function (dotNetDate) {
        var date = new Date(Date.parse(dotNetDate));
        return date;
    },

    convertDateInSeconds: function (date) {
        var dd = new Date(1970, 0, 1, 0, 0, 0, 0);
        var year = date.getFullYear();
        var ee = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0, 0);
        var diffInSec = (ee.getTime() - dd.getTime()) / 1000;
        return diffInSec;
    },

    getDefaultStartDate: function () {
        var startDate = new moment();
        return startDate.format(ApplicationConstants.ReportDateFormats);
    },
    getDefaultStartDateWithDays: function (days) {
        var startDate = new moment().add(days, 'days');;
        return startDate.format(ApplicationConstants.ReportDateFormats);
    },
    getGraphFormat: function (date) {
        var graphReport = new moment(date);
        return graphReport.format(ApplicationConstants.GraphDateFormats);
    },
    getDefaultEndDate: function () {
        var todayDate = new moment();
        todayDate = moment(todayDate).add(1, 'year');
        return todayDate.format(ApplicationConstants.ReportDateFormats);
    },

    getNextYearDate: function () {
        var endDate = new Date();

        endDate.setMonth(endDate.getMonth() + 12);
        var year = endDate.getFullYear();
        var month = endDate.getMonth();
        var day = endDate.getDate();
        return day + '/' + month + '/' + year;
    },
    /*
    Converts total seconds in HH:mm:ss format
    */
    getTimeFromSeconds: function (totalSeconds, appendSeconds) {
        var hours = totalSeconds / 3600;
        hours = parseInt(hours.toString(), 10);

        var minutes = (totalSeconds / 60) - (hours * 60);
        minutes = parseInt(minutes.toString(), 10);

        var seconds = totalSeconds - (hours * 3600) - (minutes * 60);
        seconds = parseInt(seconds.toString(), 10);

        var formattedString = hours.toString().padLeft('0', 2) + ":" + minutes.toString().padLeft('0', 2);
        if (appendSeconds) {
            formattedString = hours.toString().padLeft('0', 2) + ":" + minutes.toString().padLeft('0', 2) + ":" + seconds.toString().padLeft('0', 2);
        }
        return formattedString;
    },
    /*
   Anil 1 oct Convert date into "HH:MM" format
   */
    convertFromDatetimeTodate: function (date) {
        return date.format(ApplicationConstants.TimeDisplayFormat);
    },
    /*
  Anil 1 oct Convert date into"DD/MM/YYYY" format
  */
    convertStartandEndDate: function (date) {
        return date.format(ApplicationConstants.ReportDateFormats);
    },
    /*
Anil 1 oct Convert time into slidervalue format (e.g 7.30 in slider value 7.50 )
 */
    converttimeintoslidertime: function (time) {
        var times = time.split(":");
        var totalsec = times[0] * 3600 + times[1] * 60;
        var finaltime = (totalsec / 3600);
        return finaltime;
    },

    /*
Anil 1 oct Convert time into seconds
*/
    convertTimeIntoApitime: function (time) {
        var times = time.split(":");
        var totalsec = times[0] * 3600 + times[1] * 60;
        return totalsec;
    },

    /*
        Check for the Invalid date and return NA
    */
    getValidDisplayDate: function (dateString) {
        if (dateString == "" || dateString == undefined) {
            return ApplicationConstants.NA;
        }
        var dateObj = moment(dateString);
        var year = dateObj.year();
        if (year == 01 || year == 2001) {
            return ApplicationConstants.NA;
        }

        return dateString;
    },

    /* 
    get total seconds
    */
    getTotalMiliSeconds: function (hours) {
        var totalMiliSeconds = hours * 60 * 60;
        return totalMiliSeconds;
    },

    getTimeDifference: function (getDateTime, getDCUTime) {
        //var diff = moment.utc(moment(currentdate).diff(moment(alertTime)));
        //return (moment.utc(moment(currentdate).diff(moment(alertTime))) / 1000);
        var currentdate = new Date(getDateTime);
        return parseInt((getDCUTime - currentdate) / (1000 * 60));
    },

    getTimeInDayHourMinsFormat: function (durationInSec) {

        var days = durationInSec / 86400;
        days = parseInt(days.toString(), 10);

        var hours = (durationInSec / 3600) - (days * 24);
        hours = parseInt(hours.toString(), 10);

        var minutes = (durationInSec / 60) - (days * 24 * 60) - (hours * 60);
        minutes = parseInt(minutes.toString(), 10);

        var finalDateString = "";
        if (days > 0) {
            finalDateString = days.toString() + " day " + hours.toString() + " hr " + minutes.toString() + " min";
        }
        else if (hours > 0) {
            finalDateString = hours.toString() + " hr " + minutes.toString() + " min";
        }
        else {
            finalDateString = minutes.toString() + " min";
        }
        return finalDateString;
    },

    convertDateTimeToString: function (date) {
        var stringDate = date.format(ApplicationConstants.DateTimeDisplayFormat);
        return stringDate;
    },
    getFormattedDate: function (date) {
        var year = date.getFullYear();
        var month = (1 + date.getMonth()).toString();
        month = month.length > 1 ? month : '0' + month;
        var day = date.getDate().toString();
        day = day.length > 1 ? day : '0' + day;
        return day + '/' + month + '/' + year;
    },
    getYesterdaysDate: function () {
        var todayDate = new moment();
        todayDate = moment(todayDate).add(-1, 'day');
        return todayDate.format(ApplicationConstants.ReportDateFormats);
    },
    getTimeInDayOrHourOrMinsFormat: function (durationInSec) {

        var days = durationInSec / 86400;
        days = parseInt(days.toString(), 10);

        var hours = (durationInSec / 3600) - (days * 24);
        hours = parseInt(hours.toString(), 10);

        var minutes = (durationInSec / 60) - (days * 24 * 60) - (hours * 60);
        minutes = parseInt(minutes.toString(), 10);

        var finalDateString = "";
        if (days > 0) {
            finalDateString = days.toString() + " day ";
        }
        else if (hours > 0) {
            finalDateString = hours.toString() + " hr ";
        }
        else {
            finalDateString = minutes.toString() + " min";
        }
        return finalDateString;
    },
    getLocalTime: function (dateTime) {
        //var stillUtc = moment.utc(dateTime).toDate();

        var ServerDate = new Date(dateTime);
        var formattedDate = ServerDate.getFullYear() + '-' + (ServerDate.getMonth() + 1) + '-' + ServerDate.getDate();
        var serverTime = moment(dateTime).format(ApplicationConstants.GraphDateFormats);
        var CurrentDate = moment().format(ApplicationConstants.GraphDateFormats);
        temp = "";
        if (CurrentDate == serverTime) {
            var local = moment(dateTime).format(ApplicationConstants.ToolTipTimeFormat);
            temp = ApplicationConstants.TODAY + local;
        }
        else {
            var local = dateTime.format(ApplicationConstants.ToolTipTimeFormat);
            temp = local;
        }
        return temp;
    }
}


ApplicationUtils = {
    getValidDisplayValue: function (parameterValue) {
        if (parameterValue === "" || parameterValue == undefined || parameterValue == ApplicationConstants.NA) {
            return ApplicationConstants.NA;
        }
        var value = 0;
        try {
            value = parseFloat(parameterValue).toFixed(2);
            value = value + " kWh";

        } catch (e) {
            console.log(e);
            console.log(parameterValue)
        }
        return value;
    },
    converttimeinHrandMin: function (time) {
        if (time != undefined) {
            var timehrs = {};
            timehrs.hr = parseInt(time / 60);
            timehrs.min = parseInt(time - (timehrs.hr * 60));
            return timehrs;
        }

    },
    covertIntoMinuts: function (hrs, min) {
        if (hrs != undefined || min != undefined) {
            var data = hrs * 60 + min;

            return data;
        }



    },
    getDisplayStringByNodeType: function (nodeType) {
        switch (nodeType) {
            case ApplicationConstants.NodeType.NODE_TYPE_RTU:
                return "Gateway";
                break;

            case ApplicationConstants.NodeType.NODE_TYPE_RS485_1_METER:
                return "Meter RS485 1";
                break;

            case ApplicationConstants.NodeType.NODE_TYPE_RS485_2_METER:
                return "Meter RS485 1";
                break;

            case ApplicationConstants.NodeType.NODE_TYPE_RF_METER:
                return "Meter RF";
                break;

            case ApplicationConstants.NodeType.NODE_TYPE_REMOTE_DCU:
                return "Remote DCU";
                break;

            case ApplicationConstants.NodeType.NODE_TYPE_RF_LIGHT:
                return "Light";
                break;

            default:
                return "Unknow";
                break;
        }
    },
    /* This would return weather to display the node block on this control screen or not.*/
    displayNode: function (nodeType, nodeSubType) {
        if (nodeType == ApplicationConstants.NodeType.NODE_TYPE_RTU) {
            if (nodeSubType == ApplicationConstants.IoType.IO_ALL_PHASE_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_R_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_B_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_Y_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_DIMMER) {
                return true;
            }
        }
        else if (nodeType == ApplicationConstants.NodeType.NODE_TYPE_RS485_1_METER
            || nodeType == ApplicationConstants.NodeType.NODE_TYPE_RS485_2_METER
            || nodeType == ApplicationConstants.NodeType.NODE_TYPE_RF_METER) {
            return true;
        }
        return false;
    },
    /* This function return TRUE if the node is the relay OR Light */
    displayControlBlock: function (nodeType, nodeSubType) {
        if (nodeType == ApplicationConstants.NodeType.NODE_TYPE_RTU) {
            if (nodeSubType == ApplicationConstants.IoType.IO_ALL_PHASE_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_R_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_B_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_Y_RELAY
                || nodeSubType == ApplicationConstants.IoType.IO_DIMMER
                || nodeSubType == ApplicationConstants.IoType.DO_GENERIC) {
                return true;
            }
        }
        else if (nodeType == ApplicationConstants.NodeType.NODE_TYPE_RF_LIGHT) {
            return true;
        }
        return false;
    },

    showAlertBlock: function (key) {
        if ((key >= AlertParameterIdMinMaxRanges.alertEnumIdRange1Min && key <= AlertParameterIdMinMaxRanges.alertEnumIdRange1Max)
            || (key >= AlertParameterIdMinMaxRanges.alertEnumIdRange2Min && key <= AlertParameterIdMinMaxRanges.alertEnumIdRange2Max)
            || (key >= AlertParameterIdMinMaxRanges.alertEnumIdRange3Min && key <= AlertParameterIdMinMaxRanges.alertEnumIdRange3Max)
            || (key >= AlertParameterIdMinMaxRanges.alertEnumIdRange4Min && key <= AlertParameterIdMinMaxRanges.alertEnumIdRange4Max)
            || (key >= AlertParameterIdMinMaxRanges.alertEnumIdRange5Min && key <= AlertParameterIdMinMaxRanges.alertEnumIdRange5Max)) {
            return true;
        }
        return false;
    },
    /*Anil : Alert not shows on Monitor and control...convert string to int */
    isAlertPresent: function (key, value) {
        switch (parseInt(key)) {
            case ParameterEnum.RTU_MAINS://77
            case ParameterEnum.RED_MAINS_SUPPLY://78
            case ParameterEnum.YELLOW_MAINS_SUPPLY://79
            case ParameterEnum.BLUE_MAINS_SUPPLY://80
            case ParameterEnum.AUTO_MANUAL://62
                if (value == 0) {
                    return true;
                }
                break;
            case ParameterEnum.MCB_TRIP://58
            case ParameterEnum.DOOR_ALERT://59
            case ParameterEnum.CONTRACTOR_FAILURE://60
            case ParameterEnum.RED_CNTRCT_FAIL://71
            case ParameterEnum.YELLOW_CNTRCT_FAIL://72
            case ParameterEnum.BLUE_CNTRCT_FAIL://73
            case ParameterEnum.RED_PHASE_NO_OUTPUT://74
            case ParameterEnum.YELLOW_PHASE_NO_OUTPUT://75
            case ParameterEnum.BLUE_PHASE_NO_OUTPUT://76
            case ParameterEnum.MOTION_EVENT:
            case ParameterEnum.YELLOW_MCB_TRIP://102
            case ParameterEnum.BLUE_MCB_TRIP://103
            case ParameterEnum.COMMON_MCB_TRIP://104

            case ParameterEnum.R_SURGE_PRTCTR_TRIP://1001
            case ParameterEnum.Y_SURGE_PRTCTR_TRIP://1002
            case ParameterEnum.B_SURGE_PRTCTR_TRIP://1003
            case ParameterEnum.COMMON_SURGE_PRTCTR_TRIP://1004

                if (value == 1) {
                    return true;
                }
                break;
            case ParameterEnum.RED_THRESHOLD_CROSS_V://63
            case ParameterEnum.RED_THRESHOLD_CROSS_I://64
            case ParameterEnum.YELLOW_THRESHOLD_CROSS_V://65
            case ParameterEnum.YELLOW_THRESHOLD_CROSS_I://67
            case ParameterEnum.BLUE_THRESHOLD_CROSS_V://68
            case ParameterEnum.BLUE_THRESHOLD_CROSS_I://69
                if (value == 1 || value == 0) {
                    return true;
                }
                break;

                //case ParameterEnum.FAULTY_LIGHTS: //70
            case ParameterEnum.FAULTY_LIGHTS_2://81
            case ParameterEnum.YELLOW_FAULTY_LIGHTS_2://123
            case ParameterEnum.BLUE_FAULTY_LIGHTS_2://124

                if (value > 0) {
                    return true;
                }
                break;

        }

        return false;
    },

    getConnectionStatusDecription: function (ConnectionStatus) {
        var DisplayName = "";
        switch (ConnectionStatus) {
            case ApplicationConstants.ConnectionStatus.CONNECTED:
                DisplayName = ApplicationConstants.ConnectionStatusDescription.CONNECTED;
                break;
            case ApplicationConstants.ConnectionStatus.DISCONNECTED:
                DisplayName = ApplicationConstants.ConnectionStatusDescription.DISCONNECTED;
                break;
            default:
                break;
        }
        return DisplayName;
    },

    getActionTypeDecriptionforoldschedule: function (ActionType, actionValue, opcode) {
        var DisplayName = "";
        switch (ActionType) {
            case ScheduleConstants.ActionTypes.ON_OFF:
                if (actionValue == 1) {
                    DisplayName = ScheduleConstants.ActionTypesDescription.ON;
                }
                else if (actionValue == 0) {
                    DisplayName = ScheduleConstants.ActionTypesDescription.OFF;
                }
                break;
            case ScheduleConstants.ActionTypes.POLL:
                DisplayName = ScheduleConstants.ActionTypesDescription.POLL;
                DisplayName = DisplayName + " (" + opcode + ")";
                break;
            case ScheduleConstants.ActionTypes.DIM:
                DisplayName = ScheduleConstants.ActionTypesDescription.DIM;
                DisplayName = DisplayName + " (" + actionValue + "%)";
                break;
            default:
                break;
        }
        return DisplayName;
    },

    getScheduleActionTypeDescription: function (offset, type) {
        if (offset > 0) {
            offset = "+" + offset + ScheduleConstants.ScheduleActionDescription.Min;
        }
        if (type == ScheduleConstants.SunriseSunset.Sunrise) {
            return " Sunrise: " + offset;
        }
        else if (type == ScheduleConstants.SunriseSunset.Sunset) {
            return " Sunset: " + offset;
        }
    },

    getActionTypeDecription: function (ActionType, actionValue, opcode, sunrise, sunset, Interval, referencetime) {
        var DisplayName = "";
        if (sunrise > 0) {
            sunrise = "+" + sunrise;
        }
        if (sunset > 0) {
            sunset = "+" + sunset;
        }
        switch (ActionType) {
            case ScheduleConstants.ActionTypes.ON_OFF:
                if (actionValue == ScheduleConstants.UIActionTypes.ON) {
                    DisplayName = ScheduleConstants.ActionTypesDescription.ON + ScheduleConstants.ScheduleActionDescription.At;
                }
                else if (actionValue == ScheduleConstants.UIActionTypes.OFF) {
                    DisplayName = ScheduleConstants.ActionTypesDescription.OFF + ScheduleConstants.ScheduleActionDescription.At;
                }
                if (sunset == ScheduleConstants.SunriseSunset.DefaultValue && sunrise == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + referencetime;
                    break;
                }
                else if (sunset == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + ScheduleConstants.ScheduleActionDescription.Sunrise + sunrise + ScheduleConstants.ScheduleActionDescription.Min;
                }
                else if (sunrise == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + ScheduleConstants.ScheduleActionDescription.Sunset + sunset + ScheduleConstants.ScheduleActionDescription.Min;
                }

                break;
            case ScheduleConstants.ActionTypes.POLL:
                DisplayName = ScheduleConstants.ActionTypesDescription.POLL + " @ ";
                //DisplayName = DisplayName + "every " + Interval + " min (" + opcode + ")";
                DisplayName = DisplayName + "every " + Interval + " sec";
                break;
            case ScheduleConstants.ActionTypes.DIM:
                DisplayName = actionValue + "%";
                DisplayName = DisplayName + ScheduleConstants.ActionTypesDescription.DIM + ScheduleConstants.ScheduleActionDescription.At;
                if (sunset == ScheduleConstants.SunriseSunset.DefaultValue && sunrise == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + referencetime;
                    break;
                }
                else if (sunset == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + ScheduleConstants.ScheduleActionDescription.Sunrise + sunrise + ScheduleConstants.ScheduleActionDescription.Min;
                }
                else if (sunrise == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + ScheduleConstants.ScheduleActionDescription.Sunset + sunset + ScheduleConstants.ScheduleActionDescription.Min;
                }
                break;
            default:
                break;
        }
        return DisplayName;
    },

    getActionTypeDecriptionForPhilips: function (ActionType, actionValue, opcode, sunrise, sunset, Interval, referencetime) {
        var DisplayName = "";
        if (sunrise > 0) {
            sunrise = "+" + sunrise;
        }
        if (sunset > 0) {
            sunset = "+" + sunset;
        }
        switch (ActionType) {
            case ScheduleConstants.ActionTypes.POLL:
                DisplayName = ScheduleConstants.ActionTypesDescription.POLL + " @ ";
                //DisplayName = DisplayName + "every " + Interval + " min (" + opcode + ")";
                DisplayName = DisplayName + "every " + Interval + " sec";
                break;
            case ScheduleConstants.ActionTypes.DIM:
                if (actionValue == 100) {
                    DisplayName = ScheduleConstants.ActionTypesDescription.OFF;
                }
                else if (actionValue == 0) {
                    DisplayName = ScheduleConstants.ActionTypesDescription.ON;
                }
                else {
                    actionValue = philipsToggleFuntion(actionValue);
                    DisplayName = actionValue + "% Dim";
                }
                DisplayName = DisplayName + ScheduleConstants.ScheduleActionDescription.At;
                if (sunset == ScheduleConstants.SunriseSunset.DefaultValue && sunrise == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + referencetime;
                    break;
                }
                else if (sunset == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + ScheduleConstants.ScheduleActionDescription.Sunrise + sunrise + ScheduleConstants.ScheduleActionDescription.Min;
                }
                else if (sunrise == ScheduleConstants.SunriseSunset.DefaultValue) {
                    DisplayName = DisplayName + ScheduleConstants.ScheduleActionDescription.Sunset + sunset + ScheduleConstants.ScheduleActionDescription.Min;
                }
                break;
            default:
                break;
        }
        return DisplayName;
    },

    getFrequencyDescription: function (Frequency) {
        var DisplayName = "";
        switch (Frequency) {
            case ScheduleConstants.Frequency.Minutely:
                DisplayName = ScheduleConstants.FrequencyDescription.Minutely;
                break;
            case ScheduleConstants.Frequency.Daily:
                DisplayName = ScheduleConstants.FrequencyDescription.Daily;
                break;
            case ScheduleConstants.Frequency.Always:
                DisplayName = ScheduleConstants.FrequencyDescription.Always;
                break;
            default:
                break;
        }
        return DisplayName;
    },

    /*
    Client side export of data.
    Arguments:
    fileName: file name for the CSV file.
    csvDataArray: data to export including header at 1st index.
    */
    exportDataInCSV: function (fileName, csvDataArray) {

        // download stuff
        var buffer = csvDataArray.join("\n");
        var blob = new Blob([buffer], {
            "type": "text/csv;charset=utf8;"
        });
        var link = document.createElement("a");


        if (navigator.msSaveBlob) { // IE 10+
            navigator.msSaveBlob(blob, fileName);
        }
        else if (window.webkitURL != null) {
            // chrome
            // Browsers that support HTML5 download attribute
            link.setAttribute("href", window.webkitURL.createObjectURL(blob));
            link.setAttribute("download", fileName);
        } else {
            // firefox 
            link.setAttribute("href", window.URL.createObjectURL(blob));
            link.setAttribute("download", fileName);
            link.style.display = "none";
            document.body.appendChild(link);
        }


        link.innerHTML = "Export to CSV";
        //show link on UI
        link.click();
    },

    /* function to convert watts to kilo watts*/
    convertWattsToKiloWatts: function (value) {
        var convertedValue = 0;
        if (value != null && value != "N/A") {
            value = parseInt(value);
            convertedValue = value / 1000; /* TODO: take 1000 from constant*/
            convertedValue = convertedValue.toFixed(2);
        }
        else {
            convertedValue = "N/A";
        }
        return convertedValue;
    },

    applyColorClass: function (getAlertTime, getDCUTime) {

        var ColorClass = {};
        if (getAlertTime != undefined) {
            var durationInSec = DateTimeUtils.getTimeDifference(getAlertTime, getDCUTime);
            if (durationInSec > (48 * 60)) {
                ColorClass.Color = "redColor";
                ColorClass.Background = "redBackgroundColor";
            }
            else if (durationInSec > (24 * 60)) {
                ColorClass.Color = "pinkColor";
                ColorClass.Background = "pinkBackgroundColor";
            }
            else if (durationInSec > (12 * 60)) {
                ColorClass.Color = "blueColor";
                ColorClass.Background = "blueBackgroundColor";
            }
            else {
                ColorClass.Color = "yellowColor";
                ColorClass.Background = "yellowBackgroundColor";
            }
            return ColorClass;
        }
    },

    getUIAlertText: function (key, value) {
        if (!ApplicationConstants.UIParameterCollection.hasOwnProperty(key)) {
            return;
        }
        if (key != "FAULTY_LIGHTS" && key != "FAULTY_LIGHTS_2" && key != "YELLOW_FAULTY_LIGHTS_2" && key != "BLUE_FAULTY_LIGHTS_2") {
            return ApplicationConstants.UIParameterCollection[key][value];
        }
    },


    GetTypeOfDevice: function (device) {

        var deviceString = "";

        if (device == undefined) {
            return;
        }

        if (device.NodeType == ApplicationConstants.NodeType.NODE_TYPE_RF_LIGHT) {
            deviceString = "LIGHT ";

            if (device.SubType == ApplicationConstants.NODE_TYPE_RF_LIGHT.RF_LIGHT_ON_OFF_DIM_FAULT) {
                deviceString = deviceString + "ON OFF DIM FAULT 1";
            }
            else if (device.SubType == ApplicationConstants.NODE_TYPE_RF_LIGHT.RF_LIGHT_ON_OFF_DIM_FAULT1) {
                deviceString = deviceString + "ON OFF DIM FAULT 2";
            }
            else if (device.SubType == ApplicationConstants.NODE_TYPE_RF_LIGHT.WATER_PUMP) {
                deviceString = "WATER PUMP";
            }
            if (device.SubType == ApplicationConstants.NODE_TYPE_RF_LIGHT.RF_LIGHT_RELAY) {
                deviceString = deviceString;
            }
        }
        else if (device.NodeType == ApplicationConstants.NodeType.NODE_TYPE_RF_SENSOR) {
            deviceString = "SENSOR ";

            if (device.SubType == ApplicationConstants.NODE_TYPE_RF_SENSOR.RF_SNSR_TEMP) {
                deviceString = deviceString + "TEMP";
            }
            else if (device.SubType == ApplicationConstants.NODE_TYPE_RF_SENSOR.RF_SNSR_MOTION) {
                deviceString = deviceString + "MOTION";
            }

        }
        device.Description = deviceString;
        return device;
        //return deviceString;
    },


    GetTimeZoneInfo: function (timezoneList, timeZoneName) {
        for (var index = 0; index < timezoneList.length; index++) {
            if (timezoneList[index].TimeZoneName === timeZoneName) {
                return timezoneList[index];
            }
        }
    },
    setDefaultValue: function (value) {
        if (value == undefined) {
            return "N/A"
        }
        else {
            return value;
        }
    },
    getValueAlert: function (key, value, parameterName, monitorControlModel) {
        if (monitorControlModel == undefined) {
            return parameterName
        }
        switch (parseInt(key)) {
            case ParameterEnum.RED_THRESHOLD_CROSS_V:
                parameterName = getParameterDescriptionBasedOnKey(ParameterEnum.VOLTAGE, monitorControlModel, parameterName, value);
                break;
            case ParameterEnum.YELLOW_THRESHOLD_CROSS_V:
                parameterName = getParameterDescriptionBasedOnKey(ParameterEnum.Phase2Voltage, monitorControlModel, parameterName, value);
                break;
            case ParameterEnum.BLUE_THRESHOLD_CROSS_V:
                parameterName = getParameterDescriptionBasedOnKey(ParameterEnum.Phase3Voltage, monitorControlModel, parameterName, value);
                break;

            case ParameterEnum.RED_THRESHOLD_CROSS_I:
                parameterName = getParameterDescriptionBasedOnKey(ParameterEnum.PHASE_CURRENT, monitorControlModel, parameterName, value);
                break;
            case ParameterEnum.YELLOW_THRESHOLD_CROSS_I:
                parameterName = getParameterDescriptionBasedOnKey(ParameterEnum.Phase2Current, monitorControlModel, parameterName, value);
                break;
            case ParameterEnum.BLUE_THRESHOLD_CROSS_I:
                parameterName = getParameterDescriptionBasedOnKey(ParameterEnum.Phase3Current, monitorControlModel, parameterName, value);
                break;
        }
        return parameterName;
    }
}

function showProgressBar(show, ngProgress) {
    if (show) {
        if (undefined != ngProgress && ApplicationConstants.PROGRESS_BAR_TYPE == 0) {
            showProgressBar(true, ngProgress);
        }
        else {
            $('#pageLoadProgressDivId').removeClass('hidden');
        }
        return;
    }
    if (undefined != ngProgress && ApplicationConstants.PROGRESS_BAR_TYPE == 0) {
        showProgressBar(false, ngProgress);
    }
    else {
        $('#pageLoadProgressDivId').addClass('hidden');
    }

}
AlertAssociation = {
    desideSMSAlertState: function (alert) {
        var state;
        if (alert.smsalarmstate && alert.smsresolvestate) {
            state = RoleAlertAssociation.Both;
        }
        else if (alert.smsalarmstate && !alert.smsresolvestate) {
            state = RoleAlertAssociation.Alarm;
        }
        else if (alert.smsresolvestate && !alert.smsalarmstate) {
            state = RoleAlertAssociation.Resolve;
        }
        return state;
    },
    desideEmailAlertState: function (alert) {
        var state;
        if (alert.emailalarmstate && alert.emailresolvestate) {
            state = RoleAlertAssociation.Both;
        }
        else if (alert.emailalarmstate && !alert.emailresolvestate) {
            state = RoleAlertAssociation.Alarm;
        }
        else if (alert.emailresolvestate && !alert.emailalarmstate) {
            state = RoleAlertAssociation.Resolve;
        }
        return state;
    },
    checkUncheckSMSCombobox: function (state) {

        if (state == RoleAlertAssociation.Alarm) {
            alertstate = {
                alarm: true,
                resolve: false
            }
        }
        else if (state == RoleAlertAssociation.Resolve) {
            alertstate = {
                alarm: false,
                resolve: true
            }
        }
        else if (state == RoleAlertAssociation.Both) {
            alertstate = {
                alarm: true,
                resolve: true
            }
        }
        return alertstate;
    }
}
function philipsToggleFuntion(value) {
    try {
        var valueToSend = 100 - parseInt(value);
    } catch (e) {
        console.log(e);
    }
    return valueToSend;
}
function activeAlertDescription(nodetype, status) {
    var DisplayStatus = "";
    if (nodetype == ApplicationConstants.NodeType.NODE_TYPE_RTU && status == ActiveAlertStatus.GatewayDisconnected) {
        DisplayStatus = FaultyAlertDescription.GatewayDisconnected;
    }
    else if (nodetype == ApplicationConstants.NodeType.NODE_TYPE_RF_LIGHT && status == ActiveAlertStatus.Fault) {
        DisplayStatus = FaultyAlertDescription.FaultyLight;
    }
    else if (nodetype == ApplicationConstants.NodeType.NODE_TYPE_RF_SENSOR && status == ActiveAlertStatus.Fault) {
        DisplayStatus = FaultyAlertDescription.FaultySensor;
    }
    else if (nodetype == ApplicationConstants.NodeType.NODE_TYPE_RF_LIGHT ||
        nodetype == ApplicationConstants.NodeType.NODE_TYPE_RF_SENSOR && status == ActiveAlertStatus.Unknown) {
        DisplayStatus = FaultyAlertDescription.Unknown;
    }
    return DisplayStatus;
}
function isActiveAlertPrasent(nodetype, status) {
    if ((nodetype == ApplicationConstants.NodeType.NODE_TYPE_RTU && status == ActiveAlertStatus.GatewayDisconnected) ||
        ((nodetype == ApplicationConstants.NodeType.NODE_TYPE_RF_LIGHT || nodetype == ApplicationConstants.NodeType.NODE_TYPE_RF_SENSOR) &&
        (status == ActiveAlertStatus.Fault || status == ActiveAlertStatus.Unknown))) {
        return true;
    }
    return false;;
}
RuleManagement = {
    convertToInputCriteria: function (actions) {
        var st = startDate.toString(ApplicationConstants.DateTimeDisplayFormat);
        var dateString = moment(startDate).format(ApplicationConstants.DateTimeDisplayFormat) + " to " + moment(endDate).format(ApplicationConstants.DateTimeDisplayFormat);
        return dateString;
    },
    convertToActionDescription: function (actions) {
        var displayString = "";
        switch (actions.OptType) {
            case ScheduleConstants.UIActionTypes.ON:
                displayString = "Switch on ";
                break;
            case ScheduleConstants.UIActionTypes.OFF:
                displayString = "Switch off ";
                break;
            case ScheduleConstants.UIActionTypes.DIM:
                displayString = actions.OptValue + " % Dim ";
                break;
            case ScheduleConstants.UIActionTypes.NOACTION:
                displayString = "No action taken ";
                break;
                return;
            default:
                break;
        }

        if (undefined != actions.Lights && actions.Lights.length > 0) {
            displayString += actions.Lights.length + " light";
        }
        if (undefined != actions.Lights && actions.Lights.length > 0 && undefined != actions.Groups && actions.Groups.length > 0) {
            displayString += " and ";
        }
        if (undefined != actions.Groups && actions.Groups.length > 0) {
            displayString += actions.Groups.length + " group";
        }
        if (actions.Interval > 0) {
            displayString += " after " + parseInt(actions.Interval / 60) + " min";
        }
        if ((undefined == actions.Lights || actions.Lights.length == 0) && (undefined == actions.Groups || actions.Groups.length == 0)) {
            displayString = "No lights associate";
        }

        return displayString;
    },
    InputCriteriaDescription: function (action) {
        var inputStringt = "";
        switch (action.IPCond) {
            case RuleManagementConstant.InputCondition.GREATERTHAN:
                if (RuleManagementConstant.UISensorDescription.LUX)
                {
                    inputStringt = RuleManagementConstant.UISensorDescription.LUX + " " + RuleManagementConstant.InputConditionDescription.GREATERTHAN + " " + action.IPVal;
                }
                else
                {
                inputStringt = RuleManagementConstant.UISensorDescription.TEMPERATURE + " " + RuleManagementConstant.InputConditionDescription.GREATERTHAN + " " + action.IPVal;
                }
                break;
            case RuleManagementConstant.InputCondition.LESSTHAN:
                if (RuleManagementConstant.UISensorDescription.LUX) {
                    inputStringt = RuleManagementConstant.UISensorDescription.LUX + " " + RuleManagementConstant.InputConditionDescription.LESSTHAN + " " + action.IPVal;
                }
                else
                {
                    inputStringt = RuleManagementConstant.UISensorDescription.TEMPERATURE + " " + RuleManagementConstant.InputConditionDescription.LESSTHAN + " " + action.IPVal;
                }
               break;
            case RuleManagementConstant.InputCondition.INBETWEEN:
                if (RuleManagementConstant.UISensorDescription.LUX)
                {
                    inputStringt = RuleManagementConstant.UISensorDescription.LUX + " " + RuleManagementConstant.InputConditionDescription.INBETWEEN + " " + action.IPVal;
                }
                else
                {
                    inputStringt = RuleManagementConstant.UISensorDescription.TEMPERATURE + " " + RuleManagementConstant.InputConditionDescription.INBETWEEN + " " + action.IPVal;
                }
                break;
        }
        if (action.IPCond == RuleManagementConstant.InputCondition.EQUALTO && action.IPVal == RuleManagementConstant.EventType.MOTIONDETECT) {
            inputStringt = RuleManagementConstant.UIEventTypeDescription.MOTIONDETECT;
        }
        else if (action.IPCond == RuleManagementConstant.InputCondition.EQUALTO && action.IPVal == RuleManagementConstant.EventType.MOTIONRELEASE) {
            inputStringt = RuleManagementConstant.UIEventTypeDescription.MOTIONRELEASE;
        }
        inputStringt += " on " + '"' + action.Name + '"';
        return inputStringt;
    },
    EndDateText: function (dateString) {
        var endDate = new Date(RuleManagementConstant.RULE_MAX_DATE_YEAR, 0, 1, 0, 0, 0, 0);
        var date = DateTimeUtils.convertStartandEndDate(moment(dateString));
        if (date != DateTimeUtils.convertStartandEndDate(moment(endDate))) {
            return dateString;

        }
        return "Always";
    }
}

AlertNotifier = {
    Notifier: function (NodeName, Alert) {
        if (!window.Notification) {
            alert("Sorry, notifications are not supported.");
        }
        else {
            Notification.requestPermission(function (p) {
                if (p === 'denied') {
                    alert("You have denied notifications.");
                }
                else if (p === 'granted') {
                    notify = new Notification(NodeName + ": " + Alert);
                }
            });
        }
    }
}

function lightTooltipText(currentState) {
    if (currentState == ApplicationConstants.LightState.ON) {
        return "Click to turn OFF";
    }
    return "Click to turn ON";
}

function getIORelayStatusText(status, dateTimeDotNet) {
    var dateTimeString = DateTimeUtils.convertToJSDateString(dateTimeDotNet);;
    /* 1: RF retry over, 2: serial failure*/
    if (status == 1) {
        return 'RF retry over at ' + dateTimeString;
    }
    else if (status == 2) {
        return 'Serial failure at ' + dateTimeString;
    }
    return undefined;
}
function getIORealayStatus(status) {
    if (status == 1) {
        return 'RF retry over';
    }
    else if (status == 2) {
        return 'Serial failure';
    }
    return undefined;
}
/* Venkatesh m.*/
/* return text for tooltip by checking ON/OFF by schedule,manually  0 = by schedule, 1 = by manual  */
function lightScheduleTooltipText(currentState, IOReason, dateTime) {
    var tooltipText = "";
    if (currentState == ApplicationConstants.LightState.ON) {
        tooltipText = "Click to turn OFF (ON @ " + dateTime;
    }
    else if (currentState == ApplicationConstants.LightState.OFF) {
        tooltipText = "Click to turn ON (OFF @ " + dateTime;
    }

    tooltipText = tooltipText + " " + IOReason + ")";
    return tooltipText;

}
function getParameterDescriptionBasedOnKey(key, monitorControlModel, parameterName, value) {
    if (monitorControlModel.ParametersCollection[key] != null && (value == 1 || value == 0)) {
        var valueInCollection = monitorControlModel.ParametersCollection[key].ParameterData;
        if (valueInCollection != 'N/A') {
            parameterName = parameterName + " (" + valueInCollection + ")";
        }
    }
    return parameterName;
}

function GetIOReason(realTimeNodeDataInfo, IOObject) {
    //Set light on reason
    if (realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_UI] != undefined && IOObject.NodeDateTime == realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_UI].NodeTime) {
        IOObject.IOReason = "by UI";
    }
    else if (realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_SCH] != undefined && IOObject.NodeDateTime == realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_SCH].NodeTime) {
        IOObject.IOReason = "by schedule";
    }
    else if (realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_SMS] != undefined && IOObject.NodeDateTime == realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_SMS].NodeTime) {
        IOObject.IOReason = "by SMS";
    }
    else if (realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_IO] != undefined && IOObject.NodeDateTime == realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_IO].NodeTime) {
        IOObject.IOReason = "by boot up"
    }
    else if (realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_UPDATED_DUE_TO_ALERT] != undefined && IOObject.NodeDateTime == realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_UPDATED_DUE_TO_ALERT].NodeTime) {
        IOObject.IOReason = "due to alert";
    }
    else if (realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_RULE] != undefined && IOObject.NodeDateTime == realTimeNodeDataInfo.ParametersCollection[ParameterEnum.IO_CNTRL_FROM_RULE].NodeTime) {
        IOObject.IOReason = "by rule";
    }
}

function getAlertsCode(alertList) {
    alertCodes = [];
    for (key in alertList) {
        alert = alertList[key];
        if (alert.Selected == true) {
            if (alert.AlertName == "MCB Trip") {
                alertCodes.push(58);
                alertCodes.push(102);
                alertCodes.push(103);
                alertCodes.push(104);
            }
            else if (alert.AlertName == "Contactor failure") {
                alertCodes.push(60);
                alertCodes.push(71);
                alertCodes.push(72);
                alertCodes.push(73);
            }
            else if (alert.AlertName == "Mains supply off") {
                alertCodes.push(78);
                alertCodes.push(79);
                alertCodes.push(80);
            }
            else if (alert.AlertName == "Door open") {
                alertCodes.push(59);
            }
            else if (alert.AlertName == "SPD failure") {
                alertCodes.push(1001);
                alertCodes.push(1002);
                alertCodes.push(1003);
                alertCodes.push(1004);
            }
            else if (alert.AlertName == "No output") {
                alertCodes.push(74);
                alertCodes.push(75);
                alertCodes.push(76);
            }
            else if (alert.AlertName == "Manual mode") {
                alertCodes.push(62);
            }
            else if (alert.AlertName == "Faulty lights") {
                alertCodes.push(81);
                alertCodes.push(123);
                alertCodes.push(124);
            }
            //For ON/OFF send parameter value to count ON/OFF panels
            else if (alert.AlertName == "ON") {
                alertCodes.push(1);
            }
            else if (alert.AlertName == "OFF") {
                alertCodes.push(0);
            }
        }

    }
    return alertCodes;
}