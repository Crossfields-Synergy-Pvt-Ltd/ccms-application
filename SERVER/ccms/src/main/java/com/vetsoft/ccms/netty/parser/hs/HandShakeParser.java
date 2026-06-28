package com.vetsoft.ccms.netty.parser.hs;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetsoft.ccms.MainBootApp;
import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.pojo.CommonHeader;
import com.vetsoft.ccms.netty.pojo.HandShake;
import com.vetsoft.ccms.netty.repos.DeviceRequestDataRepository;

public class HandShakeParser extends BaseUtil {
	

	
	private static final Logger LOG = LoggerFactory.getLogger(HandShakeParser.class);
	
	public static HandShake parseRequest(String buffer,
			CommonHeader obj) {

		
		HandShake hand_shake_request = new HandShake();
		
		try{
			hand_shake_request.setCommand_identifire(obj.getCommand_identifier());
			hand_shake_request.setProtocol_version(obj.getProtocol_version());
			hand_shake_request.setFlag(obj.getFlag());
			hand_shake_request.setGateway_identifier(obj.getGateway_identifier());
			hand_shake_request.setDsn(obj.getDsn());
			hand_shake_request.setCommand_identifire(obj.getCommand_identifier());
			hand_shake_request.setPayload_length(obj.getPayload_length());
		} catch(Exception e){
			LOG.error("Exception : while common header data [ "+ buffer + " ] COMMON [ "+ obj + " ] "+ e.getMessage());
		}
		
		try {
			int index = 28;

			
			hand_shake_request.setGateway_serial_number(BaseUtil
					.convertHexToString(buffer.substring(index, (index + 32))));
			index += 32;

			hand_shake_request.setFirmware_version(Integer.parseInt(
					buffer.substring(index, (index + 4)), 16));
			index += 4;

			hand_shake_request.setStorage_detection(Integer.parseInt(
					buffer.substring(index, (index + 2)), 16));
			index += 2;

			hand_shake_request.setCsq(Integer.parseInt(
					buffer.substring(index, (index + 2)), 16));
			index += 2;

			hand_shake_request.setBattery_voltage(Integer.parseInt(
					buffer.substring(index, (index + 8)), 16));
			index += 8;
			index += 2;// 3 Byte skipped for Gatway identifier : check it properlly
			hand_shake_request.setAsset_mode(Integer.parseInt(
					buffer.substring(index, (index + 2)), 16));
			index += 2;

			hand_shake_request.setAsset_mode_timestamp(Integer.parseInt(
					buffer.substring(index, (index + 8)), 16));
			index += 8;
			int imei_len = Integer.parseInt(
					buffer.substring(index, (index + 2)), 16);
		
			hand_shake_request.setImei_length(imei_len);
			index += 2;

			hand_shake_request.setImei(BaseUtil.convertHexToString(buffer
					.substring(index, (index + 32))));
			index = index + 32;

			hand_shake_request.setGsm_version_len(Integer.parseInt(
					buffer.substring(index, (index + 2)), 16));
			index += 2;

			hand_shake_request.setGsm_version(BaseUtil.convertHexToString(buffer
					.substring(index, (index + 32))));
			index += 32;

			hand_shake_request.setMobile_len(Integer.parseInt(
					buffer.substring(index, (index + 2)), 16));
			index += 2;

			hand_shake_request.setMobile_no(BaseUtil.convertHexToString(buffer
					.substring(index, (index + 20))));
			index += 20;

			hand_shake_request.setImsi_number(BaseUtil.convertHexToString(buffer
					.substring(index, (index + 30))));
			index += 30;

			hand_shake_request.setIccid_number(BaseUtil.convertHexToString(buffer
					.substring(index, (index + 30))));
			index += 30;

			hand_shake_request.setLat(Integer.parseInt(
					buffer.substring(index, (index + 8)), 16));
			index += 8;

			hand_shake_request.setLang(Integer.parseInt(
					buffer.substring(index, (index + 8)), 16));
			index += 8;

			hand_shake_request.setCrc(Integer.parseInt(
					buffer.substring(index, (index + 2)), 16));
			index += 2;
		} catch (Exception e) {
			LOG.error("Exception : while parssing hand shake request [ "+ buffer + " ] "+ e.getMessage());
			e.getStackTrace();
		}

		if(LOG.isDebugEnabled()){
			LOG.debug(hand_shake_request.toString());
		}
		
		
		try {
			DeviceRequestDataRepository personRepository = MainBootApp.context.getBean(DeviceRequestDataRepository.class);
		
			hand_shake_request.setId(hand_shake_request.getGateway_serial_number());
			hand_shake_request.setHs_time_stamp(String.valueOf(System.currentTimeMillis()));
			
			if(hand_shake_request.getGateway_serial_number().length() == 16) /* Some Empty device serial number handshake was happening to avoid that we added, we should check for 16*/
				{
					if(LOG.isDebugEnabled()){
						LOG.debug("SAVE HAND SHAKE DATA TO DB : "+ hand_shake_request);
					}
					
					hand_shake_request = personRepository.addOrUpdateHandShake(hand_shake_request);
				}	
			/*	
			if(hand_shake_request.getCsq() < 15){ // Poor GPRS communication Event
				try {
					

					Event event = new Event();
					event.setId(System.currentTimeMillis()/1000);
					event.setGateway_serial_number(hand_shake_request.getGateway_serial_number());
					event.setTime_stamp(Integer.parseInt(String.valueOf(System.currentTimeMillis()/1000)));
					event.setEvent_id(301);
					personRepository.addOrUpdateEvent(event);
				}catch(Exception e){
					LOG.error(e.getMessage());
					LOG.error("Exception while processing event : "+e.getStackTrace());
				}
			}*/
		}catch(Exception e){
			LOG.error("Exception while updatting db "+ e.getMessage());
		}
		return hand_shake_request;

	}
	
	


	public static String getHandshakeResponseData(String buffer, HandShake hs_req){
	
		String gateway_identifier = StringUtils.leftPad("" + Integer.toHexString(Integer.parseInt(hs_req.getSerial_number())), 8, "0");
		StringBuffer handshake_resp = new StringBuffer();
		try {
			handshake_resp.append(buffer.substring(0, 12)); //SOF(8), Protocol version(2), Flag(2)
			
			handshake_resp.append(gateway_identifier); // Device Identifier Currently Hard coded need to change as per UI (4 byte)
			
			handshake_resp.append(buffer.substring(20, 22)); // DSN 
			
			handshake_resp.append("00"); //Command identifier its fixed  as its in request
			
			handshake_resp.append("0019"); // Pay load Length its fixed  as its in request
			
			handshake_resp.append(buffer.substring(28, 60)); // Gateway serial number fixed as its in request
			
			handshake_resp.append("00");  // Response flag need to check with Anil
			
			handshake_resp.append(Long.toHexString(System.currentTimeMillis()/1000)); // UTC Date
			
			handshake_resp.append(gateway_identifier); // Device Identifier 
			
			String CRC = Integer.toHexString(calculateCRC(convertHexToString(handshake_resp.toString()).toCharArray(), convertHexToString(handshake_resp.toString()).length()));
			handshake_resp.append(StringUtils.leftPad("" + CRC, 2, "0"));
		} catch (Exception e) {
			LOG.error("Exception while making Hand shake response : [ "+ buffer + " ] "+ e.getMessage());
		}
		
		if(LOG.isDebugEnabled()){
			LOG.debug(handshake_resp.toString());
		}
		return handshake_resp.toString();
	}

	/*
	public static void main(String[] args) {
		
		
		try {
			String sTextAsHex = "55AA55AA0101000009F7A3090076313831324859335033433036313231380177011F40839DB2000459C5A4001038363434393530333237383133383400103133303842303953494D3830304D33320A313131313131313131313430343232333030363035393333383839393131333030303030343638343138303831419416C842938E5CFE";
			CommonHeader obj = BaseUtil.getCommonHeader(sTextAsHex);
			parseRequest(sTextAsHex, obj);
		//	System.out.println(getHandshakeResponseData(sTextAsHex));
			System.out.println(getHandshakeResponseDatax(sTextAsHex, obj));
			// response 55AA55AA0101000009F71E00001931393033485933503343303031333538005cb62ad8000009F70
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	

	public static String getHandshakeResponseDatax(String buffer, CommonHeader obj){
		
		StringBuffer response = new StringBuffer();
		response.append(buffer.substring(0, 12));
		response.append("000009F7"); // Device Identifier 
		//response.append(obj.getDsn());
		response.append(buffer.substring(20, 22)); // DSN 
		//System.out.println("DSN : "+ buffer.substring(20, 22));
		response.append("00"); //Command identifier
		response.append("0019"); // Pay load Length
		response.append(buffer.substring(28, 60));
		//response.append(buffer.substring(28, 60));
		long UTC_date = (System.currentTimeMillis()/1000);
		//System.out.println("UTC DATE : "+ UTC_date + " " + Long.toHexString(UTC_date));
		response.append("00");
		response.append(Long.toHexString(UTC_date));
		
		response.append("000009F7");
		
		String resp_buff = response.toString();
	//	System.out.println("BUFFER  " +resp_buff);
		String CRC = Integer.toHexString(calculateCRC(convertHexToString(resp_buff).toCharArray(), convertHexToString(resp_buff).length()));
		//System.out.println("CRC : "+ CRC);
		return (resp_buff + CRC);
	//	return convertHexToString(response.toString());
	}
	*/

}
