package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dcu_details")
public class DCU {

	@Id
	private String  id;
	
	private int gateway_identifier;
	
	private int protocol_version;
	private int flag;
	private int dsn;
	private int command_identifire;
	private int payload_length;
	private String gateway_serial_number;
	private int gateway_firmware_type; //0x00: Single phase feeder panel gateway 
	private int firmware_version;
	private int storage_detection;
	private int battery_voltage;
	private int asset_mode;
	private long asset_mode_timestamp;
	private int imei_length;
	private String imei;
	private int gsm_version_len;
	private String gsm_version;
	private int mobile_len;
	private String mobile_no ;
	private String imsi_number;
	private String iccid_number;
	private int lat;
	private int lang;
	private int crc;
	private int csq;
	private String schedules_name	;
	
	
	
	// UI RELATED 
	
	private int panel_unique_id;
	private String name;
	private String date;
	private String serial_number = "00000000";
	private String model;
	private String description;
	private String rfid;
	private String time_zone_id;
	private String mobile_number;
	private String hs_time_stamp;
	
	
	private int village;
	private int mondal;
	private int gp;
	private int distict;
	private int connected_load;
	

	public String getSchedules_name() {
		return schedules_name;
	}
	public void setSchedules_name(String schedules_name) {
		this.schedules_name = schedules_name;
	}
	public int getVillage() {
		return village;
	}
	public void setVillage(int village) {
		this.village = village;
	}
	public int getMondal() {
		return mondal;
	}
	public void setMondal(int mondal) {
		this.mondal = mondal;
	}
	public int getGp() {
		return gp;
	}
	public void setGp(int gp) {
		this.gp = gp;
	}
	public int getDistict() {
		return distict;
	}
	public void setDistict(int distict) {
		this.distict = distict;
	}
	public int getConnected_load() {
		return connected_load;
	}
	public void setConnected_load(int connected_load) {
		this.connected_load = connected_load;
	}
	public String getHs_time_stamp() {
		return hs_time_stamp;
	}
	public void setHs_time_stamp(String hs_time_stamp) {
		this.hs_time_stamp = hs_time_stamp;
	}	
	
	public int getPanel_unique_id() {
		return panel_unique_id;
	}
	public void setPanel_unique_id(int panel_unique_id) {
		this.panel_unique_id = panel_unique_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRfid() {
		return rfid;
	}
	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
	public String getTime_zone_id() {
		return time_zone_id;
	}
	public void setTime_zone_id(String time_zone_id) {
		this.time_zone_id = time_zone_id;
	}
	public String getMobile_number() {
		return mobile_number;
	}
	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getGateway_firmware_type() {
		return gateway_firmware_type;
	}
	public void setGateway_firmware_type(int gateway_firmware_type) {
		this.gateway_firmware_type = gateway_firmware_type;
	}
	public int getCsq() {
		return csq;
	}
	public void setCsq(int csq) {
		this.csq = csq;
	}
	public int getProtocol_version() {
		return protocol_version;
	}
	public void setProtocol_version(int protocol_version) {
		this.protocol_version = protocol_version;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getGateway_identifier() {
		return gateway_identifier;
	}
	public void setGateway_identifier(int gateway_identifier) {
		this.gateway_identifier = gateway_identifier;
	}
	public int getDsn() {
		return dsn;
	}
	public void setDsn(int dsn) {
		this.dsn = dsn;
	}
	public int getCommand_identifire() {
		return command_identifire;
	}
	public void setCommand_identifire(int command_identifire) {
		this.command_identifire = command_identifire;
	}
	public int getPayload_length() {
		return payload_length;
	}
	public void setPayload_length(int payload_length) {
		this.payload_length = payload_length;
	}
	public String getGateway_serial_number() {
		return gateway_serial_number;
	}
	public void setGateway_serial_number(String gateway_serial_number) {
		this.gateway_serial_number = gateway_serial_number;
	}
	public int getFirmware_version() {
		return firmware_version;
	}
	public void setFirmware_version(int firmware_version) {
		this.firmware_version = firmware_version;
	}
	public int getStorage_detection() {
		return storage_detection;
	}
	public void setStorage_detection(int storage_detection) {
		this.storage_detection = storage_detection;
	}
	public int getBattery_voltage() {
		return battery_voltage;
	}
	public void setBattery_voltage(int battery_voltage) {
		this.battery_voltage = battery_voltage;
	}
	public int getAsset_mode() {
		return asset_mode;
	}
	public void setAsset_mode(int asset_mode) {
		this.asset_mode = asset_mode;
	}
	public long getAsset_mode_timestamp() {
		return asset_mode_timestamp;
	}
	public void setAsset_mode_timestamp(long asset_mode_timestamp) {
		this.asset_mode_timestamp = asset_mode_timestamp;
	}
	public int getImei_length() {
		return imei_length;
	}
	public void setImei_length(int imei_length) {
		this.imei_length = imei_length;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public int getGsm_version_len() {
		return gsm_version_len;
	}
	public void setGsm_version_len(int gsm_version_len) {
		this.gsm_version_len = gsm_version_len;
	}
	public String getGsm_version() {
		return gsm_version;
	}
	public void setGsm_version(String gsm_version) {
		this.gsm_version = gsm_version;
	}
	public int getMobile_len() {
		return mobile_len;
	}
	public void setMobile_len(int mobile_len) {
		this.mobile_len = mobile_len;
	}
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	public String getImsi_number() {
		return imsi_number;
	}
	public void setImsi_number(String imsi_number) {
		this.imsi_number = imsi_number;
	}
	public String getIccid_number() {
		return iccid_number;
	}
	public void setIccid_number(String iccid_number) {
		this.iccid_number = iccid_number;
	}
	
	public int getLat() {
		return lat;
	}
	public void setLat(int lat) {
		this.lat = lat;
	}
	public int getLang() {
		return lang;
	}
	public void setLang(int lang) {
		this.lang = lang;
	}
	public int getCrc() {
		return crc;
	}
	public void setCrc(int crc) {
		this.crc = crc;
	}
	@Override
	public String toString() {
		return "HandShake [id=" + id + ", gateway_identifier="
				+ gateway_identifier + ", protocol_version=" + protocol_version
				+ ", flag=" + flag + ", dsn=" + dsn + ", command_identifire="
				+ command_identifire + ", payload_length=" + payload_length
				+ ", gateway_serial_number=" + gateway_serial_number
				+ ", gateway_firmware_type=" + gateway_firmware_type
				+ ", firmware_version=" + firmware_version
				+ ", storage_detection=" + storage_detection
				+ ", battery_voltage=" + battery_voltage + ", asset_mode="
				+ asset_mode + ", asset_mode_timestamp=" + asset_mode_timestamp
				+ ", imei_length=" + imei_length + ", imei=" + imei
				+ ", gsm_version_len=" + gsm_version_len + ", gsm_version="
				+ gsm_version + ", mobile_len=" + mobile_len + ", mobile_no="
				+ mobile_no + ", imsi_number=" + imsi_number
				+ ", iccid_number=" + iccid_number + ", lat=" + lat + ", lang="
				+ lang + ", crc=" + crc + ", csq=" + csq + ", schedules_name="
				+ schedules_name + ", panel_unique_id=" + panel_unique_id
				+ ", name=" + name + ", date=" + date + ", serial_number="
				+ serial_number + ", model=" + model + ", description="
				+ description + ", rfid=" + rfid + ", time_zone_id="
				+ time_zone_id + ", mobile_number=" + mobile_number
				+ ", hs_time_stamp=" + hs_time_stamp + ", village=" + village
				+ ", mondal=" + mondal + ", gp=" + gp + ", distict=" + distict
				+ ", connected_load=" + connected_load + "]";
	}
	
	
	
	
	
}
