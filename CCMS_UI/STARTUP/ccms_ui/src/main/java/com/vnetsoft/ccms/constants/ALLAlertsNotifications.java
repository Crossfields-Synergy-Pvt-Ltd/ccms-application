package com.vnetsoft.ccms.constants;

import java.util.HashMap;
import java.util.Map;

public class ALLAlertsNotifications {

	
public static Map<Integer, String> allAllertsNotificationSubparrsing = new HashMap<Integer, String>();
	
	static {
		
		allAllertsNotificationSubparrsing.put(1,"Red Phase No OutPut");  // 0-Resolved, 1-Occured								
		allAllertsNotificationSubparrsing.put(2,"Yellow Phase no output");  //          0-Resolved, 1-Occured
		allAllertsNotificationSubparrsing.put(3,"Blue phase no output");  //          0-Resolved, 1-Occured
		allAllertsNotificationSubparrsing.put(4,"Red threshold cross i high");  //          1-Occured
		allAllertsNotificationSubparrsing.put(5,"Yellow threshold cross i high");  //          1-Occured
		allAllertsNotificationSubparrsing.put(6,"Blue threshold cross i high");  //          1-Occured
		allAllertsNotificationSubparrsing.put(7,"Red threshold cross i");  //          1-Occured
		allAllertsNotificationSubparrsing.put(8,"Yellow threshold cross i");  //          1-Occured
		allAllertsNotificationSubparrsing.put(9,"Blue threshold cross i");  //          1-Occured
		allAllertsNotificationSubparrsing.put(10,"Red threshold cross v low");  //         1-Occured
		allAllertsNotificationSubparrsing.put(11,"Yellow threshold cross v low");  //         1-Occured
		allAllertsNotificationSubparrsing.put(12,"Blue threshold cross v low");  //         1-Occured
		allAllertsNotificationSubparrsing.put(13,"Red threshold cross v");  //         1-Occured
		allAllertsNotificationSubparrsing.put(14,"Yellow threshold cross v");  //         1-Occured
		allAllertsNotificationSubparrsing.put(15,"Blue threshold cross v");  //         1-Occured
		allAllertsNotificationSubparrsing.put(16,"Red mains supply");  //         1-OFF,0-ON
		allAllertsNotificationSubparrsing.put(17,"Yellow mains supply");  //         1-OFF,0-ON
		allAllertsNotificationSubparrsing.put(18,"Blue mains supply");  //         1-OFF,0-ON
		allAllertsNotificationSubparrsing.put(19,"Mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(20,"Red cntrct fail");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(21,"Yellow cntrct fail");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(22,"Blue cntrct fail");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(23,"Door alert");  //         1-Open,0-Close
		allAllertsNotificationSubparrsing.put(24,"Contractor failure");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(25,"Yellow mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(26,"Blue mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(27,"Common mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(28,"Auto manual");  //         1-Manual mode, 0- Auto mode
		allAllertsNotificationSubparrsing.put(29,"R surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(30,"Y surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(31,"B surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(32,"Common surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(33,"RTU mains");  //         1-On battery, 0-On mains
	}
}
