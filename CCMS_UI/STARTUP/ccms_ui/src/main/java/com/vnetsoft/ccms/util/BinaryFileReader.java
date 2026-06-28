package com.vnetsoft.ccms.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vnetsoft.ccms.pojo.DCUConfiguration;

public class BinaryFileReader {
    public static void main(String[] args) {
       
    	String inputFile = "C:/Users/User/Desktop/CROSS FIDLES/configurations/scheduleconfig.bin";
        
    	 
        try (
            InputStream inputStream = new FileInputStream(inputFile);
       
        ) {
 
            long fileSize = new File(inputFile).length();
 
            byte[] allBytes = new byte[(int) fileSize];
            
            inputStream.read(allBytes);
            String sTextAsHex = BaseUtil.getHexString(new String(allBytes));
          System.out.println("FILE DATA : " + sTextAsHex);
         //System.out.println(BaseUtil.convertHexToString(sTextAsHex));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
 /*
    	// Creating a Mongo client 
        try {
			MongoClient mongo = new MongoClient( "localhost" , 27017 );
			 // Accessing the database 
		      MongoDatabase database = mongo.getDatabase("ccms");
	
     
        
     // Retrieving a collection 
        MongoCollection<Document> collection = database.getCollection("dcu_system_conf_details");
       
        System.out.println("Collection sampleCollection selected successfully"); 

        // Getting the iterable object 
        FindIterable<Document> iterDoc = collection.find(); 
        int i = 1; 

        // Getting the iterator 
        Iterator cur = iterDoc.iterator(); 
      
        while (cur.hasNext()) {  
        	  Document doc = (Document) cur.next();
             
              List list = new ArrayList(doc.values());
              String id = String.valueOf(list.get(0));
              System.out.println("id -- " + id);
             if(id.equalsIgnoreCase("1905HY3P3C000905")){
            	 System.out.println(doc);
            	 System.out.println(list);
            	 System.out.println("DATA : " + BaseUtil.getHexString(String.valueOf(list.get(0))));
            	 DCUConfiguration obj = new DCUConfiguration();
            	 
            	 obj.setDcu_id(id);
            	 System.out.println("OVER WRITE : "+list.get(1));
            	 obj.setOverwrite_flash(String.valueOf(list.get(2)));
            	 obj.setRetry_count(String.valueOf(list.get(3)));
            	 obj.setRetry_interval(String.valueOf(list.get(4)));
            	 obj.setApn_name(String.valueOf(list.get(5)));
            	 obj.setApn_user_name(String.valueOf(list.get(6)));
            	 obj.setApn_password(String.valueOf(list.get(7)));
            	 obj.setFrequency_band(String.valueOf(list.get(8)));
            	 obj.setMeter_polling_interval(String.valueOf(list.get(9)));
            	// System.out.println("IP : "+String.valueOf(list.get(10)));
            	 obj.setPrimary_server_ip(String.valueOf(list.get(10)));
            	 obj.setPrimary_server_port(String.valueOf(list.get(11)));
            	 obj.setPrimary_dns(String.valueOf(list.get(12)));
            	 obj.setSecondary_server_ip(String.valueOf(list.get(13)));
            	 obj.setSecondary_server_port(String.valueOf(list.get(14)));
            	 obj.setSecondary_dns(String.valueOf(list.get(15)));
            	 //System.out.println("PAN ID : "+ String.valueOf(list.get(15)));
            	 obj.setPan_id(Integer.parseInt(String.valueOf(list.get(16))));
            	 obj.setRf_frequency(String.valueOf(list.get(17)));
            	 obj.setTx_power(String.valueOf(list.get(18)));
            	 obj.setRf_idle_timeout(String.valueOf(list.get(19)));
            	 obj.setRf_retry_count(String.valueOf(list.get(20)));
            	 obj.setMod_value(String.valueOf(list.get(21)));
            	 obj.setStop_bit_1(String.valueOf(list.get(22)));
            	 obj.setBaud_rate_1(String.valueOf(list.get(23)));
            	// obj.setParity_1(String.valueOf(list.get(24)));
            	 obj.setData_bits_1(String.valueOf(list.get(24)));
            	 obj.setSerial_retry_count_1(String.valueOf(list.get(25)));
            	 obj.setSerial_retry_interval_1(String.valueOf(list.get(26)));
            	 
            	 obj.setStop_bits_2(String.valueOf(list.get(27)));
            	 obj.setBaud_rate_2(String.valueOf(list.get(28)));
            	 obj.setParity_2(String.valueOf(list.get(29)));
            	 obj.setData_bits_2(String.valueOf(list.get(30)));
            	 obj.setSerial_retry_count_2(String.valueOf(list.get(31)));
            	 obj.setVoltage_max(String.valueOf(list.get(32)));
            	 
            	 obj.setVoltage_min(String.valueOf(list.get(33)));
            	 obj.setCurrent_min(String.valueOf(list.get(34)));
            	 obj.setCurrent_max(String.valueOf(list.get(35)));
            	 obj.setPhase_2_voltage_min(String.valueOf(list.get(36)));
            	 obj.setPhase_2_voltage_max(String.valueOf(list.get(37)));
            	 obj.setPhase_2_current_min(String.valueOf(list.get(38)));
            	 
            	
            	 obj.setPhase_2_current_max(String.valueOf(list.get(39)));
            	 obj.setPhase_3_voltage_min(String.valueOf(list.get(40)));
            	 obj.setPhase_3_voltage_max(String.valueOf(list.get(41)));
            	 obj.setPhase_3_current_min(String.valueOf(list.get(42)));
            	 obj.setPhase_3_current_max(String.valueOf(list.get(43)));
            	 obj.setRepetative_alert_timer(String.valueOf(list.get(44)));
            	 
            	 
            	 obj.setRetry_count_for_io_recovery(String.valueOf(list.get(45)));
            	 obj.setMax_time_for_io_recovery_after_max_retry_count(String.valueOf(list.get(46)));
            	 obj.setPower_off_gsm_off(String.valueOf(list.get(47)));
            	 obj.setPower_off_gsm_on(String.valueOf(list.get(48)));
            	 obj.setNumber_1(Integer.parseInt(String.valueOf(list.get(49))));
            	 obj.setNumber_2(Integer.parseInt(String.valueOf(list.get(50))));
            	 obj.setNumber_3(Integer.parseInt(String.valueOf(list.get(51))));
            	 obj.setNumber_4(Integer.parseInt(String.valueOf(list.get(52))));
            	 obj.setNumber_5(Integer.parseInt(String.valueOf(list.get(53))));
            	 System.out.println("VALLLL - " + String.valueOf(list.get(56)));
            	 obj.setSerial_retry_interval_2(String.valueOf(list.get(56)));
            
            	 System.out.println(obj);
            	 
            	 
            	 generateSysConfBufferForDevice(obj);
             }
        i++; 
        }
        
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
      /*  String inputFile = "C:/Users/User/Desktop/CROSS FIDLES/configurations/systemconfig.bin";
       
 
        try (
            InputStream inputStream = new FileInputStream(inputFile);
       
        ) {
 
            long fileSize = new File(inputFile).length();
 
            byte[] allBytes = new byte[(int) fileSize];
            
            inputStream.read(allBytes);
            String sTextAsHex = BaseUtil.getHexString(new String(allBytes));
          System.out.println("FILE DATA : " + sTextAsHex);
         //System.out.println(BaseUtil.convertHexToString(sTextAsHex));
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

	private static String generateSysConfBufferForDevice(DCUConfiguration obj) {
		StringBuffer sb = new StringBuffer();
		
		sb.append( BaseUtil.getHexString(String.valueOf(obj.getDcu_id()))); // dcu id
		sb.append("05")// reserved
		.append("3131313131313131313100000000000000000000") // reserved 
		.append(StringUtils.leftPad("" +  Integer.toHexString(Integer.parseInt(obj.getOverwrite_flash())), 2, "0"))	
		.append(StringUtils.leftPad("" +   Integer.toHexString(Integer.parseInt(obj.getRetry_count())), 2, "0"))
		.append(StringUtils.leftPad("" +   Integer.toHexString(Integer.parseInt(obj.getRetry_interval())), 2, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getFrequency_band())), 32, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getApn_name())), 64, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getApn_user_name())), 32, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getApn_password())), 32, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getPrimary_server_ip())), 128, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getSecondary_server_ip())), 128, "0"))
		.append(StringUtils.rightPad("" + Integer.toHexString(Integer.parseInt(obj.getPrimary_server_port())), 4, "0"))
		.append(StringUtils.rightPad("" + Integer.toHexString(Integer.parseInt(obj.getSecondary_server_port())), 4, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf("TEST")), 128, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getPrimary_dns())), 32, "0"))
		.append(StringUtils.rightPad("" + BaseUtil.getHexString(String.valueOf(obj.getSecondary_dns())), 32, "0"))
	
		/*
		 * 
		 * Hard coding device settings as suggested by Maven Anil, bcz we dont have hardware IO specefic control
		 */
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getStop_bit_1())), 2, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getBaud_rate_1())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(0), 2, "0"))//PARAMSET_PARITY_1			
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getData_bits_1())), 2, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getSerial_retry_count_1())), 2, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getSerial_retry_interval_1())), 4, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getStop_bits_2())), 2, "0"))	
		//.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getRetry_count())), 2, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getBaud_rate_2())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(0), 2, "0"))//PARAMSET_PARITY_2	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getData_bits_2())), 2, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getSerial_retry_count_2())), 2, "0")) // URAT RETRY COUNT NEED TP PDATE PROPERLY	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getSerial_retry_interval_2())), 4, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(0), 4, "0"))	// reserved
		.append(StringUtils.leftPad("" + Integer.toHexString(0), 2, "0"))	// reserved
		.append(StringUtils.leftPad("" + Integer.toHexString(866), 4, "0"))	// reserved
		.append(StringUtils.leftPad("" + Integer.toHexString(0), 2, "0"))	// reserved
		.append(StringUtils.leftPad("" + Integer.toHexString(0), 2, "0"))	// reserved
		.append(StringUtils.leftPad("" + Integer.toHexString(5), 2, "0"))	// reserved
		.append(StringUtils.leftPad("" + Integer.toHexString(0), 2, "0"))	// reserved
		
		
		
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getVoltage_max())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getCurrent_max())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getVoltage_min())), 8, "0"))
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getCurrent_min())), 8, "0"))
		
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_2_voltage_max())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_2_current_max())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_2_voltage_min())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_2_current_min())), 8, "0"))	
		
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_3_voltage_max())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_3_current_max())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_3_voltage_min())), 8, "0"))	
		.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_3_current_min())), 8, "0"))	
		
;
		System.out.println("BUFFER IN NEED :: " + sb.toString());
		return sb.toString();
	}
}
