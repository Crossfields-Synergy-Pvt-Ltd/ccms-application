package com.vnetsoft.ccms.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vnetsoft.ccms.pojo.Event;
import com.vnetsoft.ccms.pojo.HandShake;
import com.vnetsoft.ccms.pojo.IOPojo;
import com.vnetsoft.ccms.pojo.SinglePhaseMeterData;
import com.vnetsoft.ccms.services.DCUServices;
@Component
public class ScheduledTasks {


	@Autowired
	DCUServices userServices;

   
	static final Logger logger = Logger.getLogger(ScheduledTasks.class);

	
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    
  
    // @Scheduled(cron="0 0 12 * * *")
    
    @Scheduled(cron="0 0/5 * * * ?") // evenry min
   // @Scheduled(cron="0 0 0 1 1/1 *") //once in a month
	public void reportCurrentTime() {
    	System.out.println("STARTED ....");
    	if(logger.isDebugEnabled())
			logger.debug("SCHEDULER :: The time is now {}"
				+ dateFormat.format(new Date()));
    	
		try {
			
			List<HandShake> hand_shake_list = userServices.getHandShakeList();
			if(logger.isDebugEnabled())
				logger.debug("NO OF DEVICES : " + hand_shake_list.size());
			for (HandShake tmp : hand_shake_list) {
				if(logger.isDebugEnabled())
					logger.debug("GENERATTING DATA FROM ID" + tmp.getId());
				
				genrateMeterDate(tmp);
				generateEventData(tmp);
				generateIOData(tmp);
				//syncHandShakeDataToDCU(tmp);
			}
			
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
			
		}
	}


    private void syncHandShakeDataToDCU(HandShake tmp) {
		
    	try {/*
			
    		DCU dcu_obj = new DCU();
    		
    		dcu_obj.setId(tmp.getId());
    		dcu_obj.setGateway_identifier(tmp.getGateway_identifier());
    		dcu_obj.setProtocol_version(tmp.getProtocol_version());
    		dcu_obj.setFlag(tmp.getFlag());
    		dcu_obj.setDsn(tmp.getDsn());
    		dcu_obj.setCommand_identifire(tmp.getCommand_identifire());
    		dcu_obj.setPayload_length(tmp.getPayload_length());
    		dcu_obj.setGateway_serial_number(tmp.getGateway_serial_number());
    		dcu_obj.setGateway_firmware_type(tmp.getGateway_firmware_type());
    		dcu_obj.setFirmware_version(tmp.getFirmware_version());
    		dcu_obj.setStorage_detection(tmp.getStorage_detection());
    		dcu_obj.setBattery_voltage(tmp.getBattery_voltage());
    		dcu_obj.setAsset_mode(tmp.getAsset_mode());
    		dcu_obj.setAsset_mode_timestamp(tmp.getAsset_mode_timestamp());
    		dcu_obj.setImei_length(tmp.getImei_length());
    		dcu_obj.setImei(tmp.getImei());
    		dcu_obj.setGsm_version_len(tmp.getGsm_version_len());
    		dcu_obj.setGsm_version(tmp.getGsm_version());
    		dcu_obj.setMobile_len(tmp.getMobile_len());
    		dcu_obj.setMobile_no(tmp.getMobile_no());
    		dcu_obj.setImsi_number(tmp.getImsi_number());
    		dcu_obj.setIccid_number(tmp.getIccid_number());
    		dcu_obj.setLat(tmp.getLat());
    		dcu_obj.setLang(tmp.getLang());
    		dcu_obj.setCrc(tmp.getCrc());
    		dcu_obj.setCsq(tmp.getCsq());
    		dcu_obj.setSchedules_name(tmp.getSchedules_name());
    	//	dcu_obj.setPanel_unique_id(tmp.getPanel_unique_id());
    		dcu_obj.setName(tmp.getName());
    		dcu_obj.setDate(tmp.getDate());
    		dcu_obj.setSerial_number(tmp.getSerial_number());
    		dcu_obj.setModel(tmp.getModel());
    		dcu_obj.setDescription(tmp.getDescription());
    		dcu_obj.setRfid(tmp.getRfid());
    		dcu_obj.setTime_zone_id(tmp.getTime_zone_id());
    		dcu_obj.setMobile_number(tmp.getMobile_number());
    		dcu_obj.setHs_time_stamp(tmp.getHs_time_stamp());
    		dcu_obj.setVillage(tmp.getVillage());
    		dcu_obj.setMondal(tmp.getMondal());
    		dcu_obj.setGp(tmp.getGp());
    		dcu_obj.setDistict(tmp.getDistict());
    		dcu_obj.setConnected_load(tmp.getConnected_load());
    	
    		
    		userServices.addDCU(dcu_obj);
    		
		*/} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
		
	}


	private void generateEventData(HandShake tmp) {

    	
		try {
			
			List<Event> event_data_list = userServices
					.getByEventDataByDCUSerialNumber(tmp.getGateway_serial_number());
			if(logger.isDebugEnabled())
				logger.debug("EVENT DATA ENTRY : " + event_data_list.size());

			for(Event event_obj : event_data_list){
				//System.out.println(meter_obj);
				try {
					String base_dir = FileSystemManager.touchPath(event_obj.getGateway_serial_number(), event_obj.getTime_stamp());
					if(FileLogger.logEventData(event_obj, base_dir, tmp.getName()))
					{
						
						userServices.deleteEventDateByID(event_obj.getId());
					}
				}catch(Exception e){
					logger.error("Exception : "+e.getMessage());
					logger.error(e.getStackTrace());
				}
			}
			
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
	
	}

	private void genrateMeterDate(HandShake tmp) {
	
		try {
			
			List<SinglePhaseMeterData> meter_list = userServices
					.getBySinglePhaseMeterDataByDCUSerialNumber(tmp.getId());
			if(logger.isDebugEnabled())
				logger.debug("METER DATA ENTRY : " + meter_list.size());

			for(SinglePhaseMeterData meter_obj : meter_list){
				try {
					
					String base_dir = FileSystemManager.touchPath(meter_obj.getDcu_id(), meter_obj.getTime_stamp());
					if(FileLogger.logMeterData(meter_obj, base_dir, tmp.getName()))
					{
						
						userServices.deleteMeterDateByID(meter_obj.getId());
					}
				}catch(Exception e){
					logger.error("Exception : "+e.getMessage());
					logger.error(e.getStackTrace());
				}
			}
			
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
			logger.error(e.getStackTrace());
		}
	}
    
	
	  private void generateIOData(HandShake tmp) {

	    	Set<String> io_data_files = new HashSet<String>();
			try {
				
				List<IOPojo> io_data_list = userServices
						.getByIODataByDCUSerialNumber(tmp.getGateway_serial_number());
				if(logger.isDebugEnabled())
					logger.debug("IO DATA ENTRY : " + io_data_list.size());

				for(IOPojo io_obj : io_data_list){
					//System.out.println(meter_obj);
					try {
						String base_dir = FileSystemManager.touchPath(io_obj.getDcu_id(), io_obj.getId());
						if(FileLogger.logIOData(io_obj, base_dir, tmp.getName()))
						{
							
							userServices.deleteIODateByID(io_obj.getId());
						}
						io_data_files.add(base_dir);
					
					}catch(Exception e){
						logger.error("Exception : "+e.getMessage());
						logger.error(e.getStackTrace());
					}
				}
				
				
			} catch (Exception e) {
				logger.error("Exception : "+e.getMessage());
				logger.error(e.getStackTrace());
			}
		
			/*try {
				
				for(String file : io_data_files){
					FileLogger.logIOUIData(file);
				}
			} catch (Exception e) {
				logger.error("Exception : "+e.getMessage());
				logger.error(e.getStackTrace());
			}*/
		}
   
    
    
}