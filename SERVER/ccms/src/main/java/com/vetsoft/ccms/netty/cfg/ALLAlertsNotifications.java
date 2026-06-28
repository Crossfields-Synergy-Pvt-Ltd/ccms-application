package com.vetsoft.ccms.netty.cfg;

import java.util.HashMap;
import java.util.Map;

public class ALLAlertsNotifications {

	
public static Map<Integer, String> allAllertsNotificationSubparrsing = new HashMap<Integer, String>();
	
	static {
		
		allAllertsNotificationSubparrsing.put(1,"Red Phase No OutPut");  // 0-Resolved, 1-Occured								
		allAllertsNotificationSubparrsing.put(2,"Yellow Phase no output");  //          0-Resolved, 1-Occured
		allAllertsNotificationSubparrsing.put(3,"blue phase no output");  //          0-Resolved, 1-Occured
		allAllertsNotificationSubparrsing.put(4,"red threshold cross i high");  //          1-Occured
		allAllertsNotificationSubparrsing.put(5,"yellow threshold cross i high");  //          1-Occured
		allAllertsNotificationSubparrsing.put(6,"blue threshold cross i high");  //          1-Occured
		allAllertsNotificationSubparrsing.put(7,"red threshold cross i");  //          1-Occured
		allAllertsNotificationSubparrsing.put(8,"yellow threshold cross i");  //          1-Occured
		allAllertsNotificationSubparrsing.put(9,"blue threshold cross i");  //          1-Occured
		allAllertsNotificationSubparrsing.put(10,"red threshold cross v low");  //         1-Occured
		allAllertsNotificationSubparrsing.put(11,"yellow threshold cross v low");  //         1-Occured
		allAllertsNotificationSubparrsing.put(12,"blue threshold cross v low");  //         1-Occured
		allAllertsNotificationSubparrsing.put(13,"red threshold cross v");  //         1-Occured
		allAllertsNotificationSubparrsing.put(14,"yellow threshold cross v");  //         1-Occured
		allAllertsNotificationSubparrsing.put(15,"blue threshold cross v");  //         1-Occured
		allAllertsNotificationSubparrsing.put(16,"red mains supply");  //         1-OFF,0-ON
		allAllertsNotificationSubparrsing.put(17,"yellow mains supply");  //         1-OFF,0-ON
		allAllertsNotificationSubparrsing.put(18,"blue mains supply");  //         1-OFF,0-ON
		allAllertsNotificationSubparrsing.put(19,"mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(20,"red cntrct fail");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(21,"yellow cntrct fail");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(22,"blue cntrct fail");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(23,"door alert");  //         1-Open,0-Close
		allAllertsNotificationSubparrsing.put(24,"contractor failure");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(25,"yellow mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(26,"blue mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(27,"common mcb trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(28,"auto manual");  //         1-Manual mode, 0- Auto mode
		allAllertsNotificationSubparrsing.put(29,"r surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(30,"y surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(31,"b surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(32,"common surge prtctr trip");  //         1-Occurred, 0-Resolved
		allAllertsNotificationSubparrsing.put(33,"rtu mains");  //         1-On battery, 0-On mains
	}
}
