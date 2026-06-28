package com.vetsoft.ccms.netty.parser;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.pojo.CommonHeader;

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
	
	
	public static void main(String[] args) {
		String handshake_resp = "55AA55AA0101000007fcC6000019313930334D4331503143303039333839005d3e94ca000007fc";
		String CRC = Integer.toHexString(calculateCRC(convertHexToString(handshake_resp.toString()).toCharArray(), convertHexToString(handshake_resp.toString()).length()));
		System.out.println("CRC : "+ CRC);
	}
	public static int calculateCRC(char[] packet_U, int length_U) {
		int crc = 0;
		int iterator = 0;
		byte tempZeroCount = 0;
		byte tempFfCount = 0;
		try {
			for (iterator = 0; iterator < length_U; iterator++) {
				crc = (int)((crc ^ packet_U[iterator]));//crc ^ packet_U[iterator];
				if (packet_U[iterator] == 0x00) {
					tempZeroCount++;
				} else if (packet_U[iterator] == 0xFF) {
					tempFfCount++;
				}
			}
			crc =  (int)((crc ^ tempZeroCount));;//crc ^ tempZeroCount;
			crc = (int)((crc ^ tempFfCount));;//crc ^ tempFfCount;
		} catch (Exception e) {
			LOG.error("Exception ::  While Calculating CRC BUFFER [ "
					+ packet_U.toString() + " ] LEN [ " + length_U + " ] "
					+ e.getMessage());

		}
	
		if (LOG.isDebugEnabled()) {
			LOG.debug("BUFFER [ " + packet_U.toString() + " ] LEN [ "
					+ length_U + " CRC : " + crc );
		}
		if(crc < 0){
			crc = getProperCRC(crc);
		}
			
		return crc;
	}

	private static int getProperCRC(int val) {
		
		try {
			String tmp = Integer.toHexString(val);
			//System.out.println(tmp);
			int x = Integer.parseInt(tmp.substring(6,8), 16);
			//System.out.println(x);
			
			return x;
		}catch(Exception e){
			System.out.println("Exception : "+e.getMessage());
		}
		return val;
	}
	/*
	 * Working to avoid 3 digit CRC we have done changes
	 * public static int calculateCRC(char[] packet_U, int length_U) {
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
	}*/

	public static CommonHeader getCommonHeader(String buffer) {

		CommonHeader obj = new CommonHeader();
		try {
			String sof = buffer.substring(0, 8);
			obj.setSof(sof);
			obj.setProtocol_version(Integer.parseInt(buffer.substring(8, 10),
					16));
			obj.setFlag(Integer.parseInt(buffer.substring(10, 12), 16));
			obj.setGateway_identifier(Integer.parseInt(
					buffer.substring(12, 20), 16));
			obj.setDsn(Integer.parseInt(buffer.substring(20, 22), 16));
			obj.setCommand_identifier(Integer.parseInt(
					buffer.substring(22, 24), 16));
			obj.setPayload_length(Integer.parseInt(buffer.substring(24, 28), 16));

		} catch (Exception e) {
			LOG.error("Exception While parssing common header BUFF [ " + buffer
					+ " ] " + e.getMessage());
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("COMMON BUFF : " + obj);
		}

		return obj;
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

public static String byetArrayToHex(byte[] byteArray) {
	// TODO Auto-generated method stub
	StringBuilder sb = new StringBuilder();
	for(int index = 0; index < byteArray.length; index++){
		sb.append(byteToHex(byteArray[index]));
	}
	
	return sb.toString();
}
	
	public static String byteToHex(byte num) {
	    char[] hexDigits = new char[2];
	    hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
	    hexDigits[1] = Character.forDigit((num & 0xF), 16);
	    return new String(hexDigits);
	}
	
}
