package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event_instant_details")
public class InstantEventData {
	
	@Id
	private int event_id;
	
	private String gateway_serial_number;
	
	private int time_stamp;
	
	private long node_identifier;

	private int event_status;

	public int getEvent_id() {
		return event_id;
	}

	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}

	public String getGateway_serial_number() {
		return gateway_serial_number;
	}

	public void setGateway_serial_number(String gateway_serial_number) {
		this.gateway_serial_number = gateway_serial_number;
	}

	public int getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(int time_stamp) {
		this.time_stamp = time_stamp;
	}

	public long getNode_identifier() {
		return node_identifier;
	}

	public void setNode_identifier(long node_identifier) {
		this.node_identifier = node_identifier;
	}

	public int getEvent_status() {
		return event_status;
	}

	public void setEvent_status(int event_status) {
		this.event_status = event_status;
	}

	@Override
	public String toString() {
		return "InstantEventData [event_id=" + event_id
				+ ", gateway_serial_number=" + gateway_serial_number
				+ ", time_stamp=" + time_stamp + ", node_identifier="
				+ node_identifier + ", event_status=" + event_status + "]";
	}

	
	/*

	@Id
	private String id; // dcu serial number + yymmdd bcz we will one day
						// communication data

	private String dcu_serial_number;

	private int yymmdd;

	private long door_event_time_stamp;
	private int door_alert; // 1-Open,0-Close

	private long common_mcb_trip_time_stamp;
	private int common_mcb_trip_alert; // 1-Occurred, 0-Resolved

	private long contract_failure_time_stamp;
	private int contract_failure_alert; // 1-Occurred, 0-Resolved

	private long main_supply_time_stamp;
	private int main_supply_alert; // 1-OFF,0-ON we are considering red main
									// supply

	private long manual_mode_time_stamp;
	private int manual_mode_alert; // 1-Manual mode, 0- Auto mode

	private long spd_failure_time_stamp;
	private int spd_failure_alert; // 1-Occurred, 0-Resolved we are considering
									// Common surge prtctr trip

	private long no_output_time_stamp;
	private int no_output_alert; // 0-Resolved, 1-Occured we are considering Red
									// Phase No OutPut

	private long on_time_stamp; // need to confirm witch field to use
	private int on_alert;

	private long off_time_stamp;// need to confirm witch field to use
	private int off_alert;

	public int getYymmdd() {
		return yymmdd;
	}

	public void setYymmdd(int yymmdd) {
		this.yymmdd = yymmdd;
	}

	public long getDoor_event_time_stamp() {
		return door_event_time_stamp;
	}

	public void setDoor_event_time_stamp(long door_event_time_stamp) {
		this.door_event_time_stamp = door_event_time_stamp;
	}

	public int getDoor_alert() {
		return door_alert;
	}

	public void setDoor_alert(int door_alert) {
		this.door_alert = door_alert;
	}

	public long getCommon_mcb_trip_time_stamp() {
		return common_mcb_trip_time_stamp;
	}

	public void setCommon_mcb_trip_time_stamp(long common_mcb_trip_time_stamp) {
		this.common_mcb_trip_time_stamp = common_mcb_trip_time_stamp;
	}

	public int getCommon_mcb_trip_alert() {
		return common_mcb_trip_alert;
	}

	public void setCommon_mcb_trip_alert(int common_mcb_trip_alert) {
		this.common_mcb_trip_alert = common_mcb_trip_alert;
	}

	public long getContract_failure_time_stamp() {
		return contract_failure_time_stamp;
	}

	public void setContract_failure_time_stamp(long contract_failure_time_stamp) {
		this.contract_failure_time_stamp = contract_failure_time_stamp;
	}

	public int getContract_failure_alert() {
		return contract_failure_alert;
	}

	public void setContract_failure_alert(int contract_failure_alert) {
		this.contract_failure_alert = contract_failure_alert;
	}

	public long getMain_supply_time_stamp() {
		return main_supply_time_stamp;
	}

	public void setMain_supply_time_stamp(long main_supply_time_stamp) {
		this.main_supply_time_stamp = main_supply_time_stamp;
	}

	public int getMain_supply_alert() {
		return main_supply_alert;
	}

	public void setMain_supply_alert(int main_supply_alert) {
		this.main_supply_alert = main_supply_alert;
	}

	public long getManual_mode_time_stamp() {
		return manual_mode_time_stamp;
	}

	public void setManual_mode_time_stamp(long manual_mode_time_stamp) {
		this.manual_mode_time_stamp = manual_mode_time_stamp;
	}

	public int getManual_mode_alert() {
		return manual_mode_alert;
	}

	public void setManual_mode_alert(int manual_mode_alert) {
		this.manual_mode_alert = manual_mode_alert;
	}

	public long getSpd_failure_time_stamp() {
		return spd_failure_time_stamp;
	}

	public void setSpd_failure_time_stamp(long spd_failure_time_stamp) {
		this.spd_failure_time_stamp = spd_failure_time_stamp;
	}

	public int getSpd_failure_alert() {
		return spd_failure_alert;
	}

	public void setSpd_failure_alert(int spd_failure_alert) {
		this.spd_failure_alert = spd_failure_alert;
	}

	public long getNo_output_time_stamp() {
		return no_output_time_stamp;
	}

	public void setNo_output_time_stamp(long no_output_time_stamp) {
		this.no_output_time_stamp = no_output_time_stamp;
	}

	public int getNo_output_alert() {
		return no_output_alert;
	}

	public void setNo_output_alert(int no_output_alert) {
		this.no_output_alert = no_output_alert;
	}

	public long getOn_time_stamp() {
		return on_time_stamp;
	}

	public void setOn_time_stamp(long on_time_stamp) {
		this.on_time_stamp = on_time_stamp;
	}

	public int getOn_alert() {
		return on_alert;
	}

	public void setOn_alert(int on_alert) {
		this.on_alert = on_alert;
	}

	public long getOff_time_stamp() {
		return off_time_stamp;
	}

	public void setOff_time_stamp(long off_time_stamp) {
		this.off_time_stamp = off_time_stamp;
	}

	public int getOff_alert() {
		return off_alert;
	}

	public void setOff_alert(int off_alert) {
		this.off_alert = off_alert;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDcu_serial_number() {
		return dcu_serial_number;
	}

	public void setDcu_serial_number(String dcu_serial_number) {
		this.dcu_serial_number = dcu_serial_number;
	}

	@Override
	public String toString() {
		return "InstantEventData [id=" + id + ", dcu_serial_number="
				+ dcu_serial_number + ", yymmdd=" + yymmdd
				+ ", door_event_time_stamp=" + door_event_time_stamp
				+ ", door_alert=" + door_alert
				+ ", common_mcb_trip_time_stamp=" + common_mcb_trip_time_stamp
				+ ", common_mcb_trip_alert=" + common_mcb_trip_alert
				+ ", contract_failure_time_stamp="
				+ contract_failure_time_stamp + ", contract_failure_alert="
				+ contract_failure_alert + ", main_supply_time_stamp="
				+ main_supply_time_stamp + ", main_supply_alert="
				+ main_supply_alert + ", manual_mode_time_stamp="
				+ manual_mode_time_stamp + ", manual_mode_alert="
				+ manual_mode_alert + ", spd_failure_time_stamp="
				+ spd_failure_time_stamp + ", spd_failure_alert="
				+ spd_failure_alert + ", no_output_time_stamp="
				+ no_output_time_stamp + ", no_output_alert=" + no_output_alert
				+ ", on_time_stamp=" + on_time_stamp + ", on_alert=" + on_alert
				+ ", off_time_stamp=" + off_time_stamp + ", off_alert="
				+ off_alert + "]";
	}

*/}
