package com.vetsoft.ccms.netty.cfg;

import java.util.HashMap;
import java.util.Map;

public class EventIdInformation {
	
	public static Map<Integer, String> eventInformation = new HashMap<Integer, String>();
	
	static {
		eventInformation.put(0, "IO");
		eventInformation.put(1, "Meter");
		eventInformation.put(2, "Red mcb trip occured");
		eventInformation.put(3, "Door open");
		eventInformation.put(4, "Contractor failure");
		eventInformation.put(5, "Auto manual");
		eventInformation.put(6, "Red threshold cross v high");
		eventInformation.put(7, "Red threshold cross v low");
		eventInformation.put(8, "Red threshold cross i high");
		eventInformation.put(9, "Red threshold cross i low");
		eventInformation.put(10, "Yellow threshold cross v high");
		eventInformation.put(11, "Yellow threshold cross v low");
		eventInformation.put(12, "Yellow threshold cross i high");
		eventInformation.put(13, "Yellow threshold cross i low");
		eventInformation.put(14, "Blue threshold cross v high");
		eventInformation.put(15, "Blue threshold cross v low");
		eventInformation.put(16, "Blue threshold cross i high");
		eventInformation.put(17, "Blue threshold cross i low");
		eventInformation.put(18, "Red cntrct fail");
		eventInformation.put(19, "Yellow cntrct fail");
		eventInformation.put(20, "Blue cntrct fail");
		eventInformation.put(21, "Red phase no output");
		eventInformation.put(22, "Yellow phase no output");
		eventInformation.put(23, "Blue phase no output");
		eventInformation.put(24, "Door closed");
		eventInformation.put(25, "Cntrct fail resolved");
		eventInformation.put(26, "Red cntrct fail resolved");
		eventInformation.put(27, "Yellow cntrct fail resolved");
		eventInformation.put(28, "Blue cntrct fail resolved");
		eventInformation.put(29, "Man mode");
		eventInformation.put(30, "Red mcb trip resolved");
		eventInformation.put(31, "RTU mains off");
		eventInformation.put(32, "RTU mains on");
		eventInformation.put(33, "Red mains supply off");
		eventInformation.put(34, "Yellow mains supply off");
		eventInformation.put(35, "Blue mains supply off");
		eventInformation.put(36, "Red mains supply on");
		eventInformation.put(37, "Yellow mains supply on");
		eventInformation.put(38, "Blue mains supply on");
		eventInformation.put(41, "Red phase no output resolved");
		eventInformation.put(42, "Yellow phase no output resolved");
		eventInformation.put(43, "Blue phase no output resolved");
		eventInformation.put(44, "Red threshold cross v high resolved");
		eventInformation.put(45, "Red threshold cross i high resolved");
		eventInformation.put(46, "Yellow threshold cross v high resolved");
		eventInformation.put(47, "Yellow threshold cross i high resolved");
		eventInformation.put(48, "Blue threshold cross v high resolved");
		eventInformation.put(49, "Blue threshold cross i high resolved");
		eventInformation.put(52, "Red threshold cross v low resolved");
		eventInformation.put(53, "Red threshold cross i low resolved");
		eventInformation.put(54, "Yellow threshold cross v low resolved");
		eventInformation.put(55, "Yellow threshold cross i low resolved");
		eventInformation.put(56, "Blue threshold cross v low resolved");
		eventInformation.put(57, "Blue threshold cross i low resolved");
		eventInformation.put(58, "Yellow mcb trip occured");
		eventInformation.put(59, "Blue mcb trip occured");
		eventInformation.put(60, "Common mcb trip occured");
		eventInformation.put(61, "Yellow mcb trip resolved");
		eventInformation.put(62, "Blue mcb trip resolved");
		eventInformation.put(63, "Common mcb trip resolved");
		eventInformation.put(78, "UI event r surge prtctr trip");
		eventInformation.put(79, "UI event y surge prtctr trip");
		eventInformation.put(80, "UI event b surge prtctr trip");
		eventInformation.put(81, "UI event common surge prtctr trip");
		eventInformation.put(82, "UI event r surge prtctr trip resolved");
		eventInformation.put(83, "UI event y surge prtctr trip resolved");
		eventInformation.put(84, "UI event b surge prtctr trip resolved");
		eventInformation.put(85, "UI event common surge prtctr trip resolved");
		eventInformation.put(86, "unused");
		eventInformation.put(87, "event based all alerts notifications");

		eventInformation.put(300, "Good Gprs Connectivity");
		eventInformation.put(301, "Poor Gprs Connectivity");
		eventInformation.put(302, "Meter Communication started");
		eventInformation.put(303, "IO-ON");
		eventInformation.put(304, "IO-OFF");
		eventInformation.put(305, "IO-DIM");
		eventInformation.put(306, "IO-UNKOWN");

	}
	
	
}
