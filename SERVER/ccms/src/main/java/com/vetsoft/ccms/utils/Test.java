package com.vetsoft.ccms.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.DeviceDownloadInitRequest;



public class Test {

/*	public static void main(String[] args) {
		
		String buff = "55AA55AA0100000007FB3E02000601045cfa032645";
		System.out.println(buff.substring(32, 40));
		String buff = "000000010001FFFFFFFF0000FF000000020002FFFFFFFF0001FF000000030003FFFFFFFF0002FF000000040006FFFFFFFF0005FF000000050007FFFFFFFF0006FF000000060008FFFFFFFF0007FF00000007000FFFFFFFFF0008FF000000080004FFFFFFFF0003FF00000009000AFFFFFFFF0004FF0000000A000BFFFFFFFF0009FF00000064011CFFFFFFFFAB07FF";
		
		System.out.println(buff.length()/2);
		long UTC_date = (System.currentTimeMillis()/1000);
		
		System.out.println(Long.toHexString(UTC_date));
		
		System.out.println(StringUtils.leftPad("" + Integer.toHexString(2043),8, "0"));
	}*/
	

	public static long getCurrentYYMMDDHHmm() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
	
		long yymmdd = Long.parseLong(String.valueOf(calendar.get(Calendar.YEAR)) +  
				String.format("%02d", calendar.get(Calendar.MONTH) + 1) +  
				String.format("%02d", calendar.get(Calendar.DATE))+
				String.format("%02d", calendar.get(Calendar.HOUR)) +
				String.format("%02d", calendar.get(Calendar.MINUTE))
				);
		return yymmdd;
	}
	
	    public static void main(String[] args) {
	    	System.out.println("RUNNING");
	    	System.out.println(getCurrentYYMMDDHHmm());
	    }

	    public static void getRequestType(String buffer) {
			
			DeviceDownloadInitRequest obj = new DeviceDownloadInitRequest();
			
			try {
				int index =0;
		    	System.out.println(buffer.substring(index, (index + 8)));
		    	index += 8;
		    	System.out.println(buffer.substring(index, (index + 2)));
		    	index += 2;
		    	System.out.println(buffer.substring(index, (index + 2)));
		    	index += 2;
		    	System.out.println(buffer.substring(index, (index + 8)));
		    	index += 8;
		    	System.out.println(buffer.substring(index, (index + 2)));
		    	index += 2;
		    	System.out.println(buffer.substring(index, (index + 2)));
		    	index += 2;
		    	System.out.println(buffer.substring(index, (index + 4)));
		    	index += 4;
		    	System.out.println(buffer.substring(index, (index + 32)));
		    	obj.duc_serial_no = BaseUtil.convertHexToString(buffer.substring(index, (index + 32)));
		    	index += 32;
		    	System.out.println(buffer.substring(index, (index + 2)));
		    	//obj.file_id = Integer.parseInt(	buffer.substring(index, (index + 2)), 16);
		    	index += 2;
		    	System.out.println(buffer.substring(index, (index + 8)));
		    	obj.file_type = String.valueOf(Long.parseLong(buffer.substring(index, (index + 8)), 16));
		    	index += 8;
		    	System.out.println(buffer.substring(index, (index + 4)));
		    	System.out.println( "OBJ : " + obj);
			} catch (Exception e) {
				System.out.println("Exception : "+ e.getMessage());
				e.getStackTrace();
			}
		}
	
	public static String getMeterDecimalData(String val, int devision_factor) {
		double d = 0;
		try {
			d = ((double) (Long.parseLong(val, 16)) / (double) devision_factor);
			System.out.println(d);

		} catch (Exception e) {
			System.out.println("Exception while converting hex to decimal");
		}

		return String.valueOf(d);
	}
}
