package com.vnetsoft.ccms.util;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseUtil {

	private static final Logger LOG = LoggerFactory.getLogger(BaseUtil.class);

	

	/*public static String getHexToString(String sText) {
		String sTextAsHex = "";
		try {
			for (int nIter = 0; nIter < sText.length(); nIter++) {
				String sHex = String.format("%02x", (int) sText.charAt(nIter));
				sHex = sHex.toUpperCase();
				sTextAsHex += sHex;
			}
		} catch (Exception e) {
			LOG.error("EXCEPTION :: While Converting String to Hex [ " + sText
					+ " ] " + e.getMessage());
		}
		return sTextAsHex;
	}
*/
	
	
	
	public static int calculateCRC(char[] packet_U, int length_U) {
		int crc = 0;
		int iterator = 0;
		int tempZeroCount = 0;
		int tempFfCount = 0;
		try {
			for (iterator = 0; iterator < length_U; iterator++) {
				crc = crc ^ packet_U[iterator];
				if (packet_U[iterator] == 0x00) {
					tempZeroCount++;
				} else if (packet_U[iterator] == 0xFF) {
					tempFfCount++;
				}
			}
			crc = crc ^ tempZeroCount;
			crc = crc ^ tempFfCount;
		} catch (Exception e) {
			LOG.error("Exception ::  While Calculating CRC BUFFER [ "
					+ packet_U.toString() + " ] LEN [ " + length_U + " ] "
					+ e.getMessage());

		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("BUFFER [ " + packet_U.toString() + " ] LEN [ "
					+ length_U + " CRC : " + crc);
		}
		return crc;
	}

	
	

	 public static String convertHexToString(String hex){

		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
		  for( int i=0; i<hex.length()-1; i+=2 ){
		      String output = hex.substring(i, (i + 2));
		      int decimal = Integer.parseInt(output, 16);
		      sb.append((char)decimal);
		      temp.append(decimal);
		  }
		  return sb.toString();
	  }
	 
	 public static  boolean validatPacket(String buffer){
			try {
				String tmp = buffer.substring(0, (buffer.length() - 2));
				int server_crc = Integer.parseInt(buffer.substring((buffer.length() - 2), buffer.length()), 16);
				int device_crc = calculateCRC(convertHexToString(tmp).toCharArray(), convertHexToString(tmp).length());
				
				if(LOG.isDebugEnabled())
					LOG.debug("SERVER CRC : "+ server_crc + " DEVICE CRC : "+ device_crc);
				
				if(server_crc == device_crc)
					return true;
				else 
					return false;
							
				
			} catch (Exception e) {
				System.out.println("Exception while validating packet");
			}
			
			return true;
		}
	
	
	public static String getHexString(String sText) {
		
		String sTextAsHex = "";
		//System.out.println("LEN : " + sText.length());
		for (int nIter = 0; nIter < sText.length(); nIter++) {
			String sHex = String.format("%02x", (int) sText.charAt(nIter));
			//System.out.println("INT [ " + (int) sText.charAt(nIter) + " ] sHex [ " + sHex + " ] " + Integer.toHexString(sText.charAt(nIter)));
			sHex = sHex.toUpperCase();
			sTextAsHex += sHex;

		}
		//System.out.println(sTextAsHex);
		return sTextAsHex;
	}
	
	
	public static  String getDeviceSerialNumberFromDecimalToHexWithPadding(int device_serial_number) {
		String str = String.format("%8s",Integer.toHexString(device_serial_number));
		str = str.replace(' ','0');
		return str;
	}


	public static int getCurrentYYMMDD() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
	
		int yymmdd = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)) +  
				String.format("%02d", calendar.get(Calendar.MONTH) + 1) +  
				String.format("%02d", calendar.get(Calendar.DATE)) );
		return yymmdd;
	}

}
