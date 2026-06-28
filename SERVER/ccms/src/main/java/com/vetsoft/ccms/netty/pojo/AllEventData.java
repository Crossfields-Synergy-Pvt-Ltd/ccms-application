package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "all_event_data")
public class AllEventData {
	@Id
	private String  dcu_id;
	private int time_stamp;
	
	private int red_phase_no_output;
	private int yellow_phase_no_output;
	private int blue_phase_no_output;
	private int red_threshold_cross_i_high;
	private int yellow_threshold_cross_i_high;
	private int blue_threshold_cross_i_high;
	private int red_threshold_cross_i;
	private int yellow_threshold_cross_i;
	private int blue_threshold_cross_i;
	private int red_threshold_cross_v_low;
	private int yellow_threshold_cross_v_low;
	private int blue_threshold_cross_v_low;
	private int red_threshold_cross_v;
	private int yellow_threshold_cross_v;
	private int blue_threshold_cross_v;
	private int red_mains_supply;
	private int yellow_mains_supply;
	private int blue_mains_supply;
	private int mcb_trip;
	private int red_cntrct_fail;
	private int yellow_cntrct_fail;
	private int blue_cntrct_fail;
	private int door_alert;
	private int contractor_failure;
	private int yellow_mcb_trip;
	private int blue_mcb_trip;
	private int common_mcb_trip;
	private int auto_manual;
	private int r_surge_prtctr_trip;
	private int y_surge_prtctr_trip;
	private int b_surge_prtctr_trip;
	private int common_surge_prtctr_trip;
	private int rtu_mains;
	
	private int io_r_relay;
	private int io_y_relay;
	private int io_b_relay;
	private int io_all_relay;
	
	public int getRed_phase_no_output() {
		return red_phase_no_output;
	}
	public void setRed_phase_no_output(int red_phase_no_output) {
		this.red_phase_no_output = red_phase_no_output;
	}
	public int getYellow_phase_no_output() {
		return yellow_phase_no_output;
	}
	public void setYellow_phase_no_output(int yellow_phase_no_output) {
		this.yellow_phase_no_output = yellow_phase_no_output;
	}
	public int getBlue_phase_no_output() {
		return blue_phase_no_output;
	}
	public void setBlue_phase_no_output(int blue_phase_no_output) {
		this.blue_phase_no_output = blue_phase_no_output;
	}
	public int getRed_threshold_cross_i_high() {
		return red_threshold_cross_i_high;
	}
	public void setRed_threshold_cross_i_high(int red_threshold_cross_i_high) {
		this.red_threshold_cross_i_high = red_threshold_cross_i_high;
	}
	public int getYellow_threshold_cross_i_high() {
		return yellow_threshold_cross_i_high;
	}
	public void setYellow_threshold_cross_i_high(int yellow_threshold_cross_i_high) {
		this.yellow_threshold_cross_i_high = yellow_threshold_cross_i_high;
	}
	public int getBlue_threshold_cross_i_high() {
		return blue_threshold_cross_i_high;
	}
	public void setBlue_threshold_cross_i_high(int blue_threshold_cross_i_high) {
		this.blue_threshold_cross_i_high = blue_threshold_cross_i_high;
	}
	public int getRed_threshold_cross_i() {
		return red_threshold_cross_i;
	}
	public void setRed_threshold_cross_i(int red_threshold_cross_i) {
		this.red_threshold_cross_i = red_threshold_cross_i;
	}
	public int getYellow_threshold_cross_i() {
		return yellow_threshold_cross_i;
	}
	public void setYellow_threshold_cross_i(int yellow_threshold_cross_i) {
		this.yellow_threshold_cross_i = yellow_threshold_cross_i;
	}
	public int getBlue_threshold_cross_i() {
		return blue_threshold_cross_i;
	}
	public void setBlue_threshold_cross_i(int blue_threshold_cross_i) {
		this.blue_threshold_cross_i = blue_threshold_cross_i;
	}
	public int getRed_threshold_cross_v_low() {
		return red_threshold_cross_v_low;
	}
	public void setRed_threshold_cross_v_low(int red_threshold_cross_v_low) {
		this.red_threshold_cross_v_low = red_threshold_cross_v_low;
	}
	public int getYellow_threshold_cross_v_low() {
		return yellow_threshold_cross_v_low;
	}
	public void setYellow_threshold_cross_v_low(int yellow_threshold_cross_v_low) {
		this.yellow_threshold_cross_v_low = yellow_threshold_cross_v_low;
	}
	public int getBlue_threshold_cross_v_low() {
		return blue_threshold_cross_v_low;
	}
	public void setBlue_threshold_cross_v_low(int blue_threshold_cross_v_low) {
		this.blue_threshold_cross_v_low = blue_threshold_cross_v_low;
	}
	public int getRed_threshold_cross_v() {
		return red_threshold_cross_v;
	}
	public void setRed_threshold_cross_v(int red_threshold_cross_v) {
		this.red_threshold_cross_v = red_threshold_cross_v;
	}
	public int getYellow_threshold_cross_v() {
		return yellow_threshold_cross_v;
	}
	public void setYellow_threshold_cross_v(int yellow_threshold_cross_v) {
		this.yellow_threshold_cross_v = yellow_threshold_cross_v;
	}
	public int getBlue_threshold_cross_v() {
		return blue_threshold_cross_v;
	}
	public void setBlue_threshold_cross_v(int blue_threshold_cross_v) {
		this.blue_threshold_cross_v = blue_threshold_cross_v;
	}
	public int getRed_mains_supply() {
		return red_mains_supply;
	}
	public void setRed_mains_supply(int red_mains_supply) {
		this.red_mains_supply = red_mains_supply;
	}
	public int getYellow_mains_supply() {
		return yellow_mains_supply;
	}
	public void setYellow_mains_supply(int yellow_mains_supply) {
		this.yellow_mains_supply = yellow_mains_supply;
	}
	public int getBlue_mains_supply() {
		return blue_mains_supply;
	}
	public void setBlue_mains_supply(int blue_mains_supply) {
		this.blue_mains_supply = blue_mains_supply;
	}
	public int getMcb_trip() {
		return mcb_trip;
	}
	public void setMcb_trip(int mcb_trip) {
		this.mcb_trip = mcb_trip;
	}
	public int getRed_cntrct_fail() {
		return red_cntrct_fail;
	}
	public void setRed_cntrct_fail(int red_cntrct_fail) {
		this.red_cntrct_fail = red_cntrct_fail;
	}
	public int getYellow_cntrct_fail() {
		return yellow_cntrct_fail;
	}
	public void setYellow_cntrct_fail(int yellow_cntrct_fail) {
		this.yellow_cntrct_fail = yellow_cntrct_fail;
	}
	public int getBlue_cntrct_fail() {
		return blue_cntrct_fail;
	}
	public void setBlue_cntrct_fail(int blue_cntrct_fail) {
		this.blue_cntrct_fail = blue_cntrct_fail;
	}
	public int getDoor_alert() {
		return door_alert;
	}
	public void setDoor_alert(int door_alert) {
		this.door_alert = door_alert;
	}
	public int getContractor_failure() {
		return contractor_failure;
	}
	public void setContractor_failure(int contractor_failure) {
		this.contractor_failure = contractor_failure;
	}
	public int getYellow_mcb_trip() {
		return yellow_mcb_trip;
	}
	public void setYellow_mcb_trip(int yellow_mcb_trip) {
		this.yellow_mcb_trip = yellow_mcb_trip;
	}
	public int getBlue_mcb_trip() {
		return blue_mcb_trip;
	}
	public void setBlue_mcb_trip(int blue_mcb_trip) {
		this.blue_mcb_trip = blue_mcb_trip;
	}
	public int getCommon_mcb_trip() {
		return common_mcb_trip;
	}
	public void setCommon_mcb_trip(int common_mcb_trip) {
		this.common_mcb_trip = common_mcb_trip;
	}
	public int getAuto_manual() {
		return auto_manual;
	}
	public void setAuto_manual(int auto_manual) {
		this.auto_manual = auto_manual;
	}
	public int getR_surge_prtctr_trip() {
		return r_surge_prtctr_trip;
	}
	public void setR_surge_prtctr_trip(int r_surge_prtctr_trip) {
		this.r_surge_prtctr_trip = r_surge_prtctr_trip;
	}
	public int getY_surge_prtctr_trip() {
		return y_surge_prtctr_trip;
	}
	public void setY_surge_prtctr_trip(int y_surge_prtctr_trip) {
		this.y_surge_prtctr_trip = y_surge_prtctr_trip;
	}
	public int getB_surge_prtctr_trip() {
		return b_surge_prtctr_trip;
	}
	public void setB_surge_prtctr_trip(int b_surge_prtctr_trip) {
		this.b_surge_prtctr_trip = b_surge_prtctr_trip;
	}
	public int getCommon_surge_prtctr_trip() {
		return common_surge_prtctr_trip;
	}
	public void setCommon_surge_prtctr_trip(int common_surge_prtctr_trip) {
		this.common_surge_prtctr_trip = common_surge_prtctr_trip;
	}
	public int getRtu_mains() {
		return rtu_mains;
	}
	public void setRtu_mains(int rtu_mains) {
		this.rtu_mains = rtu_mains;
	}
	
	public int getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(int time_stamp) {
		this.time_stamp = time_stamp;
	}
	
	
	
	public String getDcu_id() {
		return dcu_id;
	}
	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
	}
	public int getIo_r_relay() {
		return io_r_relay;
	}
	public void setIo_r_relay(int io_r_relay) {
		this.io_r_relay = io_r_relay;
	}
	public int getIo_y_relay() {
		return io_y_relay;
	}
	public void setIo_y_relay(int io_y_relay) {
		this.io_y_relay = io_y_relay;
	}
	public int getIo_b_relay() {
		return io_b_relay;
	}
	public void setIo_b_relay(int io_b_relay) {
		this.io_b_relay = io_b_relay;
	}
	public int getIo_all_relay() {
		return io_all_relay;
	}
	public void setIo_all_relay(int io_all_relay) {
		this.io_all_relay = io_all_relay;
	}
	@Override
	public String toString() {
		return "AllEventData [id=" + dcu_id + ", time_stamp=" + time_stamp
				+ ", red_phase_no_output=" + red_phase_no_output
				+ ", yellow_phase_no_output=" + yellow_phase_no_output
				+ ", blue_phase_no_output=" + blue_phase_no_output
				+ ", red_threshold_cross_i_high=" + red_threshold_cross_i_high
				+ ", yellow_threshold_cross_i_high="
				+ yellow_threshold_cross_i_high
				+ ", blue_threshold_cross_i_high="
				+ blue_threshold_cross_i_high + ", red_threshold_cross_i="
				+ red_threshold_cross_i + ", yellow_threshold_cross_i="
				+ yellow_threshold_cross_i + ", blue_threshold_cross_i="
				+ blue_threshold_cross_i + ", red_threshold_cross_v_low="
				+ red_threshold_cross_v_low + ", yellow_threshold_cross_v_low="
				+ yellow_threshold_cross_v_low
				+ ", blue_threshold_cross_v_low=" + blue_threshold_cross_v_low
				+ ", red_threshold_cross_v=" + red_threshold_cross_v
				+ ", yellow_threshold_cross_v=" + yellow_threshold_cross_v
				+ ", blue_threshold_cross_v=" + blue_threshold_cross_v
				+ ", red_mains_supply=" + red_mains_supply
				+ ", yellow_mains_supply=" + yellow_mains_supply
				+ ", blue_mains_supply=" + blue_mains_supply + ", mcb_trip="
				+ mcb_trip + ", red_cntrct_fail=" + red_cntrct_fail
				+ ", yellow_cntrct_fail=" + yellow_cntrct_fail
				+ ", blue_cntrct_fail=" + blue_cntrct_fail + ", door_alert="
				+ door_alert + ", contractor_failure=" + contractor_failure
				+ ", yellow_mcb_trip=" + yellow_mcb_trip + ", blue_mcb_trip="
				+ blue_mcb_trip + ", common_mcb_trip=" + common_mcb_trip
				+ ", auto_manual=" + auto_manual + ", r_surge_prtctr_trip="
				+ r_surge_prtctr_trip + ", y_surge_prtctr_trip="
				+ y_surge_prtctr_trip + ", b_surge_prtctr_trip="
				+ b_surge_prtctr_trip + ", common_surge_prtctr_trip="
				+ common_surge_prtctr_trip + ", rtu_mains=" + rtu_mains
				+ ", io_r_relay=" + io_r_relay + ", io_y_relay=" + io_y_relay
				+ ", io_b_relay=" + io_b_relay + ", io_all_relay="
				+ io_all_relay + "]";
	}
	
	
	

}
