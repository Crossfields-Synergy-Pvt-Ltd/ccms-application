package com.vetsoft.ccms.netty.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.MainBootApp;
import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.DCUConfiguration;
import com.vetsoft.ccms.netty.pojo.DCUSysConfData;
import com.vetsoft.ccms.netty.pojo.DownloadContentRequest;
import com.vetsoft.ccms.netty.pojo.NodeConfData;
import com.vetsoft.ccms.netty.pojo.ScheduleConfData;
import com.vetsoft.ccms.netty.push.dcu.DCUConfiProcessor;
import com.vetsoft.ccms.netty.push.node.NodeConfProcessor;
import com.vetsoft.ccms.netty.push.schedule.ScheduleConfProcessor;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;


public class ConfigurationDownloadHandler {
	
	public static Logger LOG =  LoggerFactory.getLogger(ConfigurationDownloadHandler.class);
	
	public static String processDownloadContentRequest(String buffer) {
		DeviceRequestDataRepository db_repose = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
		
		String response = null;
		
		DownloadContentRequest request_obj = getDownloadContentRequestObj(buffer);
		
		
		LOG.debug(request_obj.toString());
		if(request_obj.file_type.equals("03")){ // Node conf
			LOG.debug("NODE CONF SYN REQUEST : "+ request_obj);
			
			if(request_obj.file_offset.equals("00000000")){
				String dcu_serial_number = BaseUtil.convertHexToString(request_obj.duc_serial_no);
				NodeConfData obj = db_repose.getNodeConfData(dcu_serial_number);
				response = NodeConfProcessor.getNodeConfResponse(request_obj, obj.getData());
				LOG.debug("SENDING NODE DATA RESPONSE : "+ response);
				
			} else {
				LOG.debug("SENDING EMPTY RESPONSE : "+ response);
				response = NodeConfProcessor.getNodeConfZeroByteResponse(request_obj);
				String dcu_serial_number = BaseUtil.convertHexToString(request_obj.duc_serial_no);
				NodeConfData obj = db_repose.getNodeConfData(dcu_serial_number);
				obj.setStatus("DONE");
				obj.setLast_sync_time(System.currentTimeMillis());
				db_repose.saveNodeConfData(obj);
			}
			
		} else if(request_obj.file_type.equals("01")){ // Sys conf
			LOG.debug("DCU CONF SYN REQUEST : "+ request_obj);
			
			if(request_obj.file_offset.equals("00000000")){
				String conf_buff = null;
				String dcu_serial_number = BaseUtil.convertHexToString(request_obj.duc_serial_no);
				LOG.debug(dcu_serial_number);
				DCUConfiguration dcu_conf =	 null;
				try {
					 dcu_conf =	db_repose.getDCUConfigurationByIdentifier(dcu_serial_number);
				LOG.debug("DCU CONF OBJ : "+ dcu_conf);
					conf_buff = DCUConfiProcessor.generateSysConfBufferForDevice(dcu_conf);
					LOG.debug(conf_buff);
				} catch (Exception e) {
					LOG.error("Exception : "+e.getMessage());
				}
				
				response = DCUConfiProcessor.getConfigurationResponseData(request_obj, conf_buff);
			} else {
				
				LOG.debug("SENDING EMPTY RESPONSE : "+ response);
				response = DCUConfiProcessor.getDcuConfZeroByteResponse(request_obj);
				String dcu_serial_number = BaseUtil.convertHexToString(request_obj.duc_serial_no);
				DCUSysConfData obj = db_repose.getDCUConfData(dcu_serial_number);
				obj.setStatus("DONE");
				obj.setLast_sync_time(System.currentTimeMillis());
				db_repose.saveDCUConfData(obj);
				
			}
		}else if(request_obj.file_type.equals("04")){ // Schedule conf
			LOG.debug("SCHEDULER CONF SYN REQUEST : "+ request_obj);
			
			if(request_obj.file_offset.equals("00000000")){
				String dcu_serial_number = BaseUtil.convertHexToString(request_obj.duc_serial_no);
				ScheduleConfData obj = db_repose.getScheduleConfData(dcu_serial_number);
				response = ScheduleConfProcessor.getSchedulerConfResponse(request_obj, obj.getData());
				LOG.debug("SENDING SCHEDULER DATA RESPONSE : "+ response);
			} else {
				LOG.debug("SENDING SCHEDULER EMPTY RESPONSE : "+ response);
				response = ScheduleConfProcessor.getSchedulerConfZeroByteResponse(request_obj);
				String dcu_serial_number = BaseUtil.convertHexToString(request_obj.duc_serial_no);
				ScheduleConfData obj = db_repose.getScheduleConfData(dcu_serial_number);
				obj.setStatus("DONE");
				obj.setLast_sync_time(System.currentTimeMillis());
				db_repose.saveScheduleConfDataData(obj);
			}
			
		} else {
			LOG.error("UNKNOW FILE TYPE");
		}
		
		LOG.debug("RESPONSE " + response);
		return response;
	}


	private static DownloadContentRequest getDownloadContentRequestObj(
			String buffer) {
	

		
		DownloadContentRequest obj = new DownloadContentRequest();
		
		try {
			int index =0;
	    //	System.out.println(buffer.substring(index, (index + 8)));
	    	index += 8;
	    	obj.protocol_version = buffer.substring(index, (index + 2));
	    	index += 2;
	    	
	    	obj.flag = buffer.substring(index, (index + 2)); // need to check is Flag == 1 then only parse device serial number
	    	index += 2;
	    	
	    	obj.dcu_identifier = buffer.substring(index, (index + 8));
	    	index += 8;
	    	
	    	obj.dsn = buffer.substring(index, (index + 2));
	    	index += 2;
	    
	    	//buffer.substring(index, (index + 2)));
	    	index += 2;// dsn
	    	//System.out.println(buffer.substring(index, (index + 4)));
	    	index += 4; //pay load len
	    	if(obj.flag.equals("01")){
	    		
	    		obj.duc_serial_no = buffer.substring(index, (index + 32));
	    		index += 32;
	    	}
	    	
	    	obj.file_type = buffer.substring(index, (index + 2));
	    	index += 2;
	    	
	    
	    	obj.file_identifier = buffer.substring(index, (index + 8));
	    	index += 8;
	    	
	    	obj.file_offset = buffer.substring(index, (index + 8));
	    	System.out.println(obj);
	    	
	    
	    	System.out.println(obj.toString());
		} catch (Exception e) {
			System.out.println("" +e.getStackTrace());
		}
		return obj;
	
	}
	
	
}
