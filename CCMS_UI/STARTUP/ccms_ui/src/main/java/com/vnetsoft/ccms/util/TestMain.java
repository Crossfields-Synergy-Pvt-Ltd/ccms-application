package com.vnetsoft.ccms.util;

import java.util.List;

import sun.awt.RepaintArea;

import com.vnetsoft.ccms.pojo.IOPojo;

public class TestMain {

	public static void main(String[] args) {

		System.out.println("DATA   " + Long.toHexString(Long.parseLong("4294967295")));
		
		double d = 0.2;
        long lng = Double.doubleToLongBits( d );
        
        System.out.printf( "%X", lng );
        System.out.println("FLOAT VAL");
        float x = 2f;
        int y = Float.floatToIntBits(x);
       System.out.println(Integer.toHexString(y));
        System.out.printf( "%X", y );
        
        
        System.out.println("DATA : " + getHexValuveOfMinCurrent("2"));
		List<IOStatusTmpPojo_delete> obj_list = IODataGenerator_delete.getMeterData("E:/CCMS_DATA/1905HY3P3C000904/2019/05/28/io_data.csv");
		System.out.println(obj_list);
		/*	 int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		List<IOPojo> io_list = new ArrayList<IOPojo>();

		int t = Integer.parseInt("1558256457");
		
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date((System.currentTimeMillis() - MILLIS_IN_DAY)));
		
			int yymmdd = Integer.parseInt(String.valueOf(calendar.get(Calendar.YEAR)) +  
					String.format("%02d", calendar.get(Calendar.MONTH) + 1) +  
					String.format("%02d", calendar.get(Calendar.DATE)) );
			System.out.println(yymmdd);
*/
		
	/*	final DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd MMM yyyy");
		//"yyyy-MM-dd HH:mm:ss"
		long seconds = Long.valueOf("1558342807236".substring(0, 10));
		
		final String formattedDtm = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).format(formatter);

		System.out.println(formattedDtm);
		int hour = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).getHour();
		int min = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).getMinute();*/
		//System.out.println(hour + ":"+min);
	/*	io_list.add(new IOPojo(t, 1, 0, 1));

		t = Integer.parseInt("1558240227");
		io_list.add(new IOPojo(t, 1, 1, 1));
		
		t = Integer.parseInt("1558240234");
		io_list.add(new IOPojo(t, 1, 1, 1));
		
		t = Integer.parseInt("1558240234");
		io_list.add(new IOPojo(t, 1, 1, 1));
		
		t = Integer.parseInt("1558254155");
		io_list.add(new IOPojo(t, 1, 0, 3));
		
		
		
		
		t = Integer.parseInt("1558254543");
		io_list.add(new IOPojo(t, 1, 1, 3));
		
		t = Integer.parseInt("1558255377");
		io_list.add(new IOPojo(t, 1, 1, 3));
		
		
		t = Integer.parseInt("1558256403");
		io_list.add(new IOPojo(t, 1, 0, 1));
		
		t = Integer.parseInt("1558256433");
		io_list.add(new IOPojo(t, 1, 0, 1));
		
		t = Integer.parseInt("1558256433");
		io_list.add(new IOPojo(t, 1, 0, 1));
		
		
		t = Integer.parseInt("1558256457");
		io_list.add(new IOPojo(t, 0, 0, 0));
		
		t = Integer.parseInt("1558256457");
		io_list.add(new IOPojo(t, 0, 0, 0));
		*/
	/*	List<IOStatusTmpPojo> tmp_io_list = new ArrayList<IOStatusTmpPojo>();
		
		for(IOPojo tmp : io_list) {
			IOStatusTmpPojo obj = getIODetails(tmp);
			if(obj != null)
				tmp_io_list.add(obj);
		}
		
		System.out.println(tmp_io_list);*/
		
		
		
	}

	private static String getHexValuveOfMinCurrent(String val) {
		String response = "0";
		try {
		if(val.contains(".")){
			  float x = Float.valueOf(val);
		        int y = Float.floatToIntBits(x);
		       response = Integer.toHexString(y);
		  
		} else {
			int x = Integer.parseInt(val);
			response = Integer.toHexString(x);
			
		}
		}catch(Exception e){
			System.out.println("Exception " + e.getMessage());
		}
		return response;
	}

	static int previous_Op_Val = 0;
	public static IOStatusTmpPojo_delete getIODetails(IOPojo obj) {

	/*	final DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd MMM yyyy");
		//"yyyy-MM-dd HH:mm:ss"
		long seconds = Long.valueOf(String.valueOf(obj.getId()).substring(0, 10));obj.getId();
		
		final String formattedDtm = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).format(formatter);

		int hour = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).getHour();
		int min = Instant.ofEpochSecond(seconds)
				.atZone(ZoneId.of("Asia/Kolkata")).getMinute();*/
		String status = null;
		if(obj.getOpration_value() == 0)
			status = "OFF";
		else 
			status = "ON";
		if(obj.getOpration_value() == previous_Op_Val)
			{
			previous_Op_Val = obj.getOpration_value();
				return null;
			} else {
				previous_Op_Val = obj.getOpration_value();
				//System.out.println(obj.getOpration_value());
			}
		
		//System.out.println(formattedDtm  + " 		"  + hour + ":"+ min + " 		 "+ status);
		
		IOStatusTmpPojo_delete tmp_obj = new IOStatusTmpPojo_delete();
	/*	tmp_obj.setDate(formattedDtm);
		tmp_obj.setHour(hour);
		tmp_obj.setMin(min);*/
		tmp_obj.setOpration_value(obj.getOpration_value());
		
		return tmp_obj;
	}
}