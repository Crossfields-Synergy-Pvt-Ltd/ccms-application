package com.vetsoft.ccms.controller.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.parser.BaseUtil;

public class ManuvalCommands {

	private final static Logger LOG = LoggerFactory.getLogger(ManuvalCommands.class);

	public static void main(String[] args) {

		System.out.println(turnOnLight(BaseUtil.getHexString("1905HY1P1C009534"), 2043));
		
		System.out.println(turnOffLight(BaseUtil.getHexString("1905HY1P1C009534"), 2043));
	}
	public static String turnOnLight(String dcu_serial_number, int dcu_identifier) {
		
	StringBuffer sb = new StringBuffer();
		
	
		try {
		
			sb.append("55AA55AA")
			.append("01")// version
			.append("00") // flag
			.append(StringUtils.leftPad("" + Integer.toHexString(dcu_identifier),8, "0"))
			.append("01") // DNS
			.append("06")// command identifier
			.append("0008") // pay load len need to update
		//	.append(dcu_serial_number)
		//	.append("00") // response flage
			.append("01") // opration type
			.append("0001") // opration value
			.append("01") // node count
			.append("00000001") // node id
		
				;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	}
	public static String turnOffLight(String dcu_serial_number, int dcu_identifier) {
		
	StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("55AA55AA")
			.append("01")// version
			.append("00") // flag
			.append(StringUtils.leftPad("" + Integer.toHexString(dcu_identifier),8, "0"))
			.append("01") // DNS
			.append("06")// command identifier
			.append("0008") // pay load len need to update
		//	.append(dcu_serial_number)
		//	.append("00") // response flage
			.append("01") // opration type
			.append("0000") // opration value
			.append("01") // node count
			.append("00000001") // node id
		
				;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	}
}
