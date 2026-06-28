package com.vetsoft.ccms.netty.push.dcu;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.DCUConfiguration;
import com.vetsoft.ccms.netty.pojo.DeviceDownloadInitRequest;
import com.vetsoft.ccms.netty.pojo.DownloadContentRequest;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;

public class DCUConfiProcessor {

	public static Logger LOG =  LoggerFactory.getLogger(DCUConfiProcessor.class);
	
	public static String getDcuConfZeroByteResponse(
			DownloadContentRequest obj) {

		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			.append("001C") // pay load len need to update
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(obj.file_identifier)
			.append("000001A9") // chunk off set
			.append("0000")// 850/2 byte
						;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	}
	
	public static String getConfigurationResponseData(
			DownloadContentRequest obj, String conf_buff) {
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			.append("01C5") // pay load len need to update
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(obj.file_identifier)
			.append("00000000") // chunk off set
			.append(StringUtils.leftPad("" + "1A9", 4, "0"))// 850/2 byte
			.append(conf_buff)
			;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
		
	}
	
	public static String getConfigurationFileInitResponse(DeviceDownloadInitRequest obj, DeviceRequestDataRepository db_repose, String file_idetifier) {
		
		StringBuffer sb = new StringBuffer();
	
		try {
			
			sb.append("55AA55AA")
			.append(obj.protocol_version)
			.append(obj.flag)
			.append(obj.dcu_identifier)
			.append(obj.dsn)
			.append("00")// command identifier
			.append("001C") // pay load len
			.append(obj.duc_serial_no)
			.append("00")
			.append(obj.file_type)
			.append(file_idetifier)
			//.append(obj.file_identifier)
			//.append(StringUtils.leftPad("" + Integer.toHexString(total_file_size), 8, "0"))
			.append(StringUtils.leftPad("" + "1A9", 8, "0"))// 850/2 byte
			.append("0200")
			;
			String buffer= sb.toString();
			String CRC = Integer.toHexString(BaseUtil.calculateCRC(BaseUtil.convertHexToString(buffer.toString()).toCharArray(), BaseUtil.convertHexToString(buffer.toString()).length()));
			
			sb.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making buffer : "+e.getMessage());
		}
		return sb.toString();
	}

	public static String generateSysConfBufferForDevice(DCUConfiguration obj) {
		LOG.debug("OBJECT : "+obj);
StringBuffer sb = new StringBuffer();

try{
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
		
		
		
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getVoltage_max()), 8, "0"))	
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getCurrent_max()), 8, "0"))	
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getVoltage_min()), 8, "0"))
	//	.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getCurrent_min())), 8, "0"))
			.append(StringUtils.leftPad("" + getHexValuveOfMinCurrent(obj.getCurrent_min()), 8, "0"))
		
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getPhase_2_voltage_max()), 8, "0"))	
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getPhase_2_current_max()), 8, "0"))	
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getPhase_2_voltage_min()), 8, "0"))	
		//.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_2_current_min())), 8, "0"))	
		.append(StringUtils.leftPad("" + getHexValuveOfMinCurrent(obj.getPhase_2_current_min()), 8, "0"))	
		
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getPhase_3_voltage_max()), 8, "0"))	
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getPhase_3_current_max()), 8, "0"))	
		.append(StringUtils.leftPad("" + getFloatValuve(obj.getPhase_3_voltage_min()), 8, "0"))	
		//.append(StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(obj.getPhase_3_current_min())), 8, "0"))
			.append(StringUtils.leftPad("" + getHexValuveOfMinCurrent(obj.getPhase_3_current_min()), 8, "0"))
		
;
		
		LOG.debug("CONFIGURATION DATA : "+ sb.toString());
		System.out.println("BUFFER IN NEED :: " + sb.toString());
	
}catch(Exception e){
	LOG.error("Exception : "+e.getMessage());
}
	return sb.toString();

	}
	
	

	public static String getFloatValuve(String buff){
		
		try {
			
			 Float f = new Float(buff);
			 return Integer.toHexString(Float.floatToIntBits(f));
		} catch (Exception e) {
			System.out.println("Exeception while converting Float to hex : "+ e.getMessage());
		}
		
		return buff;
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


}
