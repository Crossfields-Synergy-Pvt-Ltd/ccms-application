package com.vetsoft.ccms.netty.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.MainBootApp;
import com.vetsoft.ccms.netty.cfg.GWErrorResponse;
import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.CommonHeader;
import com.vetsoft.ccms.netty.pojo.DCUConfiguration;
import com.vetsoft.ccms.netty.pojo.DCUSysConfData;
import com.vetsoft.ccms.netty.pojo.DeviceDownloadInitRequest;
import com.vetsoft.ccms.netty.pojo.NodeConfData;
import com.vetsoft.ccms.netty.pojo.ScheduleConfData;
import com.vetsoft.ccms.netty.push.dcu.DCUConfiProcessor;
import com.vetsoft.ccms.netty.push.node.NodeConfProcessor;
import com.vetsoft.ccms.netty.push.schedule.ScheduleConfProcessor;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;

public class ConfigurationINITHandler {


	public static Logger LOG =  LoggerFactory.getLogger(ConfigurationINITHandler.class);
	
	public static String processConfigurationDownloadInitRequest(String buffer, CommonHeader common_obj){
		
		DeviceRequestDataRepository db_repose = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
		
		String response = null;
		
		DeviceDownloadInitRequest obj = getRequestType(buffer);
		
		if(obj.file_type.equals("01")){ // system conf 
			if(LOG.isInfoEnabled()){
				LOG.info("DCU CONF DOWNLOAD REQUEST : "+ obj.dcu_identifier);
			}
			
		
			
		//	String conf_buff = null;
			String dcu_serial_number = BaseUtil.convertHexToString(obj.duc_serial_no);
			LOG.debug(dcu_serial_number);
			DCUConfiguration dcu_conf =	 null;
			try {
				 dcu_conf =	db_repose.getDCUConfigurationByIdentifier(dcu_serial_number);
				 LOG.debug("DCU CONF OBJ : "+ dcu_conf);
				
			} catch (Exception e) {
				LOG.error("Exception : "+e.getMessage());
			}
			
			if(obj.file_identifier.equalsIgnoreCase(dcu_conf.getFile_identifier())){
				response = DCUConfiProcessor.getConfigurationFileInitResponse(obj, db_repose, dcu_conf.getFile_identifier());
			} else{
				LOG.error("UNKOWN FILE IDENTIFIER : "+ obj.file_identifier + " "+ dcu_conf.getFile_identifier());
				response = GWErrorResponse.getERRORResponse(buffer, common_obj, GWErrorResponse.UNKNOWN_FILE_TYPE);
			}
			LOG.debug("RESPONSE BUFF : "+ response);
			
			
		} else if(obj.file_type.equals("03")){ 
			if(LOG.isInfoEnabled()){
				LOG.info("NODE CONF DOWNLOAD REQUEST : "+ obj.dcu_identifier);
			}
			String dcu_serial_number = BaseUtil.convertHexToString(obj.duc_serial_no);
			LOG.debug(dcu_serial_number);
			
			NodeConfData node_conf_change_request = null;
			try {
				node_conf_change_request =	db_repose.getNodeConfData(dcu_serial_number);
			LOG.debug("DCU CONF OBJ : "+ node_conf_change_request);
				
			} catch (Exception e) {
				LOG.error("Exception : "+e.getMessage());
			}
			if(obj.file_identifier.equalsIgnoreCase(node_conf_change_request.getFile_identifier())){	
				response = NodeConfProcessor.getNODEConfigurationFileInitResponse(obj) ;
			} else {
			 	LOG.error("UNKOWN FILE IDENTIFIER : "+ obj.file_identifier + " "+ node_conf_change_request.getFile_identifier());
				response = GWErrorResponse.getERRORResponse(buffer, common_obj, GWErrorResponse.UNKNOWN_FILE_TYPE);
			}
		} else if(obj.file_type.equals("04")){ 
			 
				if(LOG.isInfoEnabled()){
					LOG.info("SCHEDULE CONF DOWNLOAD REQUEST : "+ obj.dcu_identifier);
				}
			
				String dcu_serial_number = BaseUtil.convertHexToString(obj.duc_serial_no);
				LOG.debug(dcu_serial_number);
				
				ScheduleConfData sche_conf_change_request = null;
				try {
					  sche_conf_change_request =	db_repose.getScheduleConfData(dcu_serial_number);
				LOG.debug("SCHEDULER CONF OBJ : "+ sche_conf_change_request);
					
				} catch (Exception e) {
					LOG.error("Exception : "+e.getMessage());
				}
				if(obj.file_identifier.equalsIgnoreCase(sche_conf_change_request.getFile_identifier())){
					response = ScheduleConfProcessor.getSchedulerConfigurationFileInitResponse(obj) ;
				} else {
					LOG.error("UNKOWN FILE IDENTIFIER : "+ obj.file_identifier + " "+ sche_conf_change_request.getFile_identifier());
					response = GWErrorResponse.getERRORResponse(buffer, common_obj, GWErrorResponse.UNKNOWN_FILE_TYPE);
				}
			}
		LOG.debug("RESPONSE BUFF : "+ response);
		return response;
	}


	public static DeviceDownloadInitRequest getRequestType(String buffer) {
		
		DeviceDownloadInitRequest obj = new DeviceDownloadInitRequest();
		
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
	    	
	    	obj.file_version = buffer.substring(index, (index + 4));
	    	System.out.println(obj);
	    	
	    
	    	if(LOG.isDebugEnabled())
	    		LOG.debug(obj.toString());
		} catch (Exception e) {
			LOG.error("Exception while parssing download init request : "+ e.getMessage());
			LOG.error("" +e.getStackTrace());
		}
		return obj;
	}




	public static void saveConfChangeRequestData(String packet,
			String channel_id, int conf_type) {
	
		DeviceRequestDataRepository db_repose = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
		
		
		if(conf_type == 1){ // sys conf
			DCUSysConfData obj = new DCUSysConfData();
			obj.setData(packet);
			obj.setDevice_serial_number(channel_id);
			obj.setFile_identifier(packet.substring(32, 40));
			obj.setStatus("PROCESSING-INIT");
			db_repose.saveDCUConfData(obj);
		} else if(conf_type == 3){
			NodeConfData obj = new NodeConfData();
			obj.setData(packet);
			obj.setDevice_serial_number(channel_id);
			obj.setFile_identifier(packet.substring(32, 40));
			obj.setStatus("PROCESSING-INIT");
			db_repose.saveNodeConfData(obj);
			
		} else if(conf_type == 4){
			ScheduleConfData obj = new ScheduleConfData();
			obj.setData(packet);
			obj.setDevice_serial_number(channel_id);
			obj.setFile_identifier(packet.substring(32, 40));
			db_repose.saveScheduleConfDataData(obj);
		}
	}


	public static void saveNodeConfRequestData(String node_data, String dcu_id,
			String file_idetifier, String dcu_identifier) {
		
		try {
			DeviceRequestDataRepository db_repose = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
			
			
			NodeConfData obj = new NodeConfData();
			obj.setData(node_data);
			obj.setDevice_serial_number(dcu_id);
			obj.setFile_identifier(file_idetifier);
			obj.setStatus("PENDDING");
			obj.setDevice_identifier(dcu_identifier);
			obj.setRequest_sent_time(System.currentTimeMillis());
			db_repose.saveNodeConfData(obj);
			
			if(LOG.isDebugEnabled()){
				LOG.debug("SAVING NODE CONF REQUEST : "+ obj);
			}
		}catch(Exception e){
			LOG.error("Exception  : "+ e.getMessage());
			LOG.error("" +e.getStackTrace());
		}
	}

	
	
	public static void saveDcuConfRequestData(String node_data, String dcu_id,
			String file_idetifier, String dcu_identifier) {
		
		try {
			DeviceRequestDataRepository db_repose = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
			
			
			DCUSysConfData obj = new DCUSysConfData();
			obj.setData(node_data);
			obj.setDevice_serial_number(dcu_id);
			obj.setFile_identifier(file_idetifier);
			obj.setStatus("PENDDING");
			obj.setDevice_identifier(dcu_identifier);
			obj.setRequest_sent_time(System.currentTimeMillis());
			db_repose.saveDCUConfData(obj);
			
			if(LOG.isDebugEnabled()){
				LOG.debug("SAVING NODE CONF REQUEST : "+ obj);
			}
		}catch(Exception e){
			LOG.error("Exception  : "+ e.getMessage());
			LOG.error("" +e.getStackTrace());
		}
	}


	public static void saveSchedulerConfRequestData(String node_data,
			String dcu_id, String file_idetifier, String dcu_identifier) {

		try {
			DeviceRequestDataRepository db_repose = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
			
			
			ScheduleConfData obj = new ScheduleConfData();
			obj.setData(node_data);
			obj.setDevice_serial_number(dcu_id);
			obj.setFile_identifier(file_idetifier);
			obj.setStatus("PENDDING");
			obj.setDevice_identifier(dcu_identifier);
			obj.setRequest_sent_time(System.currentTimeMillis());
			db_repose.saveSchedulerConfData(obj);
			
			if(LOG.isDebugEnabled()){
				LOG.debug("SAVING SCHEDULER CONF CONF REQUEST : "+ obj);
			}
		}catch(Exception e){
			LOG.error("Exception  : "+ e.getMessage());
			LOG.error("" +e.getStackTrace());
		}
	}
}
