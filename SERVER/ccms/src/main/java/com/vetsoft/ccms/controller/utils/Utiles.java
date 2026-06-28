package com.vetsoft.ccms.controller.utils;

import org.apache.commons.lang3.StringUtils;

public class Utiles {

	

	public static String getNodeConfInformationExchangePacket(String file_idetifier, String dcu_identifier) {
		
		StringBuffer sb = new StringBuffer();
		
		
		
		try {
			sb.append("55AA55AA")
			.append("01")
			.append("00")
			.append(StringUtils.leftPad("" + dcu_identifier,8, "0"))
			.append("3E")
			.append("02")
			.append("0006") // pay load len
			.append("01")
			.append("03")
			.append(file_idetifier)
			
			;
		} catch (Exception e) {
			System.out.println("Exception while making init command :"+ e.getMessage());
		}
		return sb.toString();
	}

	public static String getScheduleConfInformationExchangePacket(
			String file_idetifier, String dcu_identifier) {

		
		StringBuffer sb = new StringBuffer();
	
		try {
			sb.append("55AA55AA")
			.append("01")
			.append("00")
			.append(StringUtils.leftPad("" + dcu_identifier,8, "0"))
			.append("3E")
			.append("02")
			.append("0006") // pay load len
			.append("01")
			.append("04")
			.append(file_idetifier)
			
			;
		} catch (Exception e) {
			System.out.println("Exception while making init command :"+ e.getMessage());
		}
		return sb.toString();

	}
}
