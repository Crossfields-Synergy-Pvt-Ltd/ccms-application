package com.vnetsoft.ccms.pojo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "handshake_info")
public class HandShake {

	@Id
	private String  id;
	private int gateway_identifier;
	/*private int protocol_version;
	private int flag;
	private int dsn;
	private int command_identifier;
	private int payload_length;*/
	private String gateway_serial_number;
	/*private int gateway_firmware_type; //0x00: Single phase feeder panel gateway 
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
	private String imsi_number;*/
	private String iccid_number;
	private String lat;
	private String lang;
	private int crc;
	private int csq;
	// UI RELATED 
	private String panel_unique_id;
	private String name;
	private String date;
	private Date installation_date;
	private String serial_number = "00000000";
	private String model;
	private String description;
	/*private String rfid;
	private String time_zone_id;*/
	private String mobile_number;
	private String hs_time_stamp;
	private int connection_status;
	private int yymmddhhmm;
	private String village  = "Other";
	private String mandal = "Other";
	private String gp  = "Other";
	private String district = "Other";
	private int connected_load;
	private String schedules_name	;
	private int light_status; // 0 - OFF, 1 - ON 
	private long light_time_stamp;
	private int mcb_trip; // 0 - Resolved , 1 - Occured
	private long mcb_trip_time_stamp;
	private int door_status; // 0 - Closed, 1 - Open
	private long door_time_stamp;
	private int cnt_status; // 0 - resolved, 1 - Occured
	private long cnt_time_stamp;
	private int main_supply_status; // 0 - On, 1 - Off
	private long main_supply_time_stamp;
	private int spd_status; // 0 - SPD resolved, 1 - Occured
	private long spd_time_stamp;
	private int manual_mode_status; // 0 - Auto mode, 1 - Manual mode
	private long manual_mode_time_stamp;
	private int high_current; // 1 - high current, 0 Normal/resolved
	private long high_voltage; // 1 - high Voltage, 0 - normal/ resolved
	private long device_time_yymmddhhmm; //yymmddhhmm - just comapre this time with current time stamp if its less than 5 min device is off line.
	private int red_phse_no_output; // 0 - red phase out resolved, 1 - no out put
	
	private int no_of_lights;
	private String division = "Other";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getGateway_identifier() {
		return gateway_identifier;
	}
	public void setGateway_identifier(int gateway_identifier) {
		this.gateway_identifier = gateway_identifier;
	}
	public String getGateway_serial_number() {
		return gateway_serial_number;
	}
	public void setGateway_serial_number(String gateway_serial_number) {
		this.gateway_serial_number = gateway_serial_number;
	}
	public String getIccid_number() {
		return iccid_number;
	}
	public void setIccid_number(String iccid_number) {
		this.iccid_number = iccid_number;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public int getCrc() {
		return crc;
	}
	public void setCrc(int crc) {
		this.crc = crc;
	}
	public int getCsq() {
		return csq;
	}
	public void setCsq(int csq) {
		this.csq = csq;
	}
	public String getPanel_unique_id() {
		return panel_unique_id;
	}
	public void setPanel_unique_id(String panel_unique_id) {
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
	public Date getInstallation_date() {
		return installation_date;
	}
	public void setInstallation_date(Date installation_date) {
		this.installation_date = installation_date;
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
	public String getMobile_number() {
		return mobile_number;
	}
	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}
	public String getHs_time_stamp() {
		return hs_time_stamp;
	}
	public void setHs_time_stamp(String hs_time_stamp) {
		this.hs_time_stamp = hs_time_stamp;
	}
	public int getConnection_status() {
		return connection_status;
	}
	public void setConnection_status(int connection_status) {
		this.connection_status = connection_status;
	}
	public int getYymmddhhmm() {
		return yymmddhhmm;
	}
	public void setYymmddhhmm(int yymmddhhmm) {
		this.yymmddhhmm = yymmddhhmm;
	}
	public String getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village = village;
	}
	public String getMandal() {
		return mandal;
	}
	public void setMandal(String mandal) {
		this.mandal = mandal;
	}
	public String getGp() {
		return gp;
	}
	public void setGp(String gp) {
		this.gp = gp;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public int getConnected_load() {
		return connected_load;
	}
	public void setConnected_load(int connected_load) {
		this.connected_load = connected_load;
	}
	public String getSchedules_name() {
		return schedules_name;
	}
	public void setSchedules_name(String schedules_name) {
		this.schedules_name = schedules_name;
	}
	public int getLight_status() {
		return light_status;
	}
	public void setLight_status(int light_status) {
		this.light_status = light_status;
	}
	public long getLight_time_stamp() {
		return light_time_stamp;
	}
	public void setLight_time_stamp(long light_time_stamp) {
		this.light_time_stamp = light_time_stamp;
	}
	public int getMcb_trip() {
		return mcb_trip;
	}
	public void setMcb_trip(int mcb_trip) {
		this.mcb_trip = mcb_trip;
	}
	public long getMcb_trip_time_stamp() {
		return mcb_trip_time_stamp;
	}
	public void setMcb_trip_time_stamp(long mcb_trip_time_stamp) {
		this.mcb_trip_time_stamp = mcb_trip_time_stamp;
	}
	public int getDoor_status() {
		return door_status;
	}
	public void setDoor_status(int door_status) {
		this.door_status = door_status;
	}
	public long getDoor_time_stamp() {
		return door_time_stamp;
	}
	public void setDoor_time_stamp(long door_time_stamp) {
		this.door_time_stamp = door_time_stamp;
	}
	public int getCnt_status() {
		return cnt_status;
	}
	public void setCnt_status(int cnt_status) {
		this.cnt_status = cnt_status;
	}
	public long getCnt_time_stamp() {
		return cnt_time_stamp;
	}
	public void setCnt_time_stamp(long cnt_time_stamp) {
		this.cnt_time_stamp = cnt_time_stamp;
	}
	public int getMain_supply_status() {
		return main_supply_status;
	}
	public void setMain_supply_status(int main_supply_status) {
		this.main_supply_status = main_supply_status;
	}
	public long getMain_supply_time_stamp() {
		return main_supply_time_stamp;
	}
	public void setMain_supply_time_stamp(long main_supply_time_stamp) {
		this.main_supply_time_stamp = main_supply_time_stamp;
	}
	public int getSpd_status() {
		return spd_status;
	}
	public void setSpd_status(int spd_status) {
		this.spd_status = spd_status;
	}
	public long getSpd_time_stamp() {
		return spd_time_stamp;
	}
	public void setSpd_time_stamp(long spd_time_stamp) {
		this.spd_time_stamp = spd_time_stamp;
	}
	public int getManual_mode_status() {
		return manual_mode_status;
	}
	public void setManual_mode_status(int manual_mode_status) {
		this.manual_mode_status = manual_mode_status;
	}
	public long getManual_mode_time_stamp() {
		return manual_mode_time_stamp;
	}
	public void setManual_mode_time_stamp(long manual_mode_time_stamp) {
		this.manual_mode_time_stamp = manual_mode_time_stamp;
	}
	public int getHigh_current() {
		return high_current;
	}
	public void setHigh_current(int high_current) {
		this.high_current = high_current;
	}
	public long getHigh_voltage() {
		return high_voltage;
	}
	public void setHigh_voltage(long high_voltage) {
		this.high_voltage = high_voltage;
	}
	public long getDevice_time_yymmddhhmm() {
		return device_time_yymmddhhmm;
	}
	public void setDevice_time_yymmddhhmm(long device_time_yymmddhhmm) {
		this.device_time_yymmddhhmm = device_time_yymmddhhmm;
	}
	public int getRed_phse_no_output() {
		return red_phse_no_output;
	}
	public void setRed_phse_no_output(int red_phse_no_output) {
		this.red_phse_no_output = red_phse_no_output;
	}
	public int getNo_of_lights() {
		return no_of_lights;
	}
	public void setNo_of_lights(int no_of_lights) {
		this.no_of_lights = no_of_lights;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	@Override
	public String toString() {
		return "HandShake [id=" + id + ", gateway_identifier="
				+ gateway_identifier + ", gateway_serial_number="
				+ gateway_serial_number + ", iccid_number=" + iccid_number
				+ ", lat=" + lat + ", lang=" + lang + ", crc=" + crc + ", csq="
				+ csq + ", panel_unique_id=" + panel_unique_id + ", name="
				+ name + ", date=" + date + ", installation_date=" + installation_date + ", serial_number=" + serial_number
				+ ", model=" + model + ", description=" + description
				+ ", mobile_number=" + mobile_number + ", hs_time_stamp="
				+ hs_time_stamp + ", connection_status=" + connection_status
				+ ", yymmddhhmm=" + yymmddhhmm + ", village=" + village
				+ ", mandal=" + mandal + ", gp=" + gp + ", district=" + district
				+ ", connected_load=" + connected_load + ", schedules_name="
				+ schedules_name + ", light_status=" + light_status
				+ ", light_time_stamp=" + light_time_stamp + ", mcb_trip="
				+ mcb_trip + ", mcb_trip_time_stamp=" + mcb_trip_time_stamp
				+ ", door_status=" + door_status + ", door_time_stamp="
				+ door_time_stamp + ", cnt_status=" + cnt_status
				+ ", cnt_time_stamp=" + cnt_time_stamp
				+ ", main_supply_status=" + main_supply_status
				+ ", main_supply_time_stamp=" + main_supply_time_stamp
				+ ", spd_status=" + spd_status + ", spd_time_stamp="
				+ spd_time_stamp + ", manual_mode_status=" + manual_mode_status
				+ ", manual_mode_time_stamp=" + manual_mode_time_stamp
				+ ", high_current=" + high_current + ", high_voltage="
				+ high_voltage + ", device_time_yymmddhhmm="
				+ device_time_yymmddhhmm + ", red_phse_no_output="
				+ red_phse_no_output + ", no_of_lights=" + no_of_lights
				+ ", division=" + division + "]";
	}
	
}
