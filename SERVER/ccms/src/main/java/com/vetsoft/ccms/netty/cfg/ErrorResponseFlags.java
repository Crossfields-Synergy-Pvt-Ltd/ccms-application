package com.vetsoft.ccms.netty.cfg;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseFlags {


	public static Map<Integer, String> errorResponseFlags = new HashMap<Integer, String>();
	
	static {
		errorResponseFlags.put(1, "Unknown Gateway");
		errorResponseFlags.put(2, "Incorrect Packet Format");
		errorResponseFlags.put(3, "Unauthorized Gateway");
		errorResponseFlags.put(4, "Incorrect Request : field value in inform change");
		errorResponseFlags.put(5, "Unknown File Identifier");
		errorResponseFlags.put(6, "Unknown File Type");
		errorResponseFlags.put(7, "File Not Present");
		errorResponseFlags.put(8, "Not used");
		errorResponseFlags.put(9, "No handshake done before. This is generated if gateway sends any other packet without sending handshake request");
		errorResponseFlags.put(10, "Node Operation Error");
		errorResponseFlags.put(11, "Incorrect File Version");
		errorResponseFlags.put(12, "Incorrect Op-Code");
		errorResponseFlags.put(13, "Incorrect Event Identifier");
		
	}
}
