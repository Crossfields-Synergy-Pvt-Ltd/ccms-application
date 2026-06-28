package com.vetsoft.ccms.netty.cfg;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.CommonHeader;

public class GWErrorResponse {

	public static final String UNKNOWN_FILE_TYPE = "06";
	public static Logger LOG =  LoggerFactory.getLogger(GWErrorResponse.class);
	
	
	public static void main(String[] args) {
		
		
	}

	public static  String getERRORResponse(String buffer, CommonHeader obj, String error_code) {
		
		StringBuffer response = new StringBuffer();
		response.append(buffer.substring(0, 12)); // start packet , Protoclo version , Flag
		response.append(BaseUtil.getDeviceSerialNumberFromDecimalToHexWithPadding(obj.getGateway_identifier())); // Device Identifier 
		response.append(buffer.substring(20, 22)); // DSN 
		
		response.append("00"); // Command identifier
		
		StringBuffer sb = new StringBuffer();
		
		
		int index = 28;
		
		if(obj.getFlag() == 1){ // gateway serial number present
			
			sb.append(buffer.substring(index, (index + 32)));
			index += 32;
		} else {
			// ignore
		}
		
		sb.append(error_code); // status - 0 Success
		sb.append(buffer.substring(index, (index + 2))); // parmeter count 
		index +=2;
		sb.append("01"); // 1 Server time stamp key
		
		
		long UTC_date = (System.currentTimeMillis()/1000);
		
	
		sb.append(Long.toHexString(UTC_date)); //Server epochTime time
		
		

		String len =  Integer.toHexString(sb.toString().length()/2);
		
		
		String payload_len = String.format("%0"+ (4 - len.length() )+"d%s",0 ,len);
		
		response.append(payload_len);
		response.append(sb.toString());
		
		String resp_buff = response.toString() ;
		if (LOG.isDebugEnabled()) 
			LOG.debug("ERROR RESPONSE  " +resp_buff);
		
		String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(resp_buff.toString()).toCharArray(), BaseUtil.convertHexToString(resp_buff.toString()).length()));
		
	
		return (resp_buff + StringUtils.leftPad("" + CRC, 2, "0"));
	}
}

