package com.vnetsoft.ccms.pojo;

public class EventsUICount {

	private String time_stamp;

	private int mcb_trip_count;

	private int cnt_failure_count;
	private int mains_supply_off_count;
	private int door_open_count;
	private int spd_failure_count;
	private int no_output_count;
	private int manual_mode_count;
	private int on_count;
	private int off_count;
	private int good_gprs_count;
	private int poor_gprs_count;

	public String getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(String time_stamp) {
		this.time_stamp = time_stamp;
	}

	public int getMcb_trip_count() {
		return mcb_trip_count;
	}

	public void setMcb_trip_count(int mcb_trip_count) {
		this.mcb_trip_count = mcb_trip_count;
	}

	public int getCnt_failure_count() {
		return cnt_failure_count;
	}

	public void setCnt_failure_count(int cnt_failure_count) {
		this.cnt_failure_count = cnt_failure_count;
	}

	public int getMains_supply_off_count() {
		return mains_supply_off_count;
	}

	public void setMains_supply_off_count(int mains_supply_off_count) {
		this.mains_supply_off_count = mains_supply_off_count;
	}

	public int getDoor_open_count() {
		return door_open_count;
	}

	public void setDoor_open_count(int door_open_count) {
		this.door_open_count = door_open_count;
	}

	public int getSpd_failure_count() {
		return spd_failure_count;
	}

	public void setSpd_failure_count(int spd_failure_count) {
		this.spd_failure_count = spd_failure_count;
	}

	public int getNo_output_count() {
		return no_output_count;
	}

	public void setNo_output_count(int no_output_count) {
		this.no_output_count = no_output_count;
	}

	public int getManual_mode_count() {
		return manual_mode_count;
	}

	public void setManual_mode_count(int manual_mode_count) {
		this.manual_mode_count = manual_mode_count;
	}

	public int getOn_count() {
		return on_count;
	}

	public void setOn_count(int on_count) {
		this.on_count = on_count;
	}

	public int getOff_count() {
		return off_count;
	}

	public void setOff_count(int off_count) {
		this.off_count = off_count;
	}

	public int getGood_gprs_count() {
		return good_gprs_count;
	}

	public void setGood_gprs_count(int good_gprs_count) {
		this.good_gprs_count = good_gprs_count;
	}

	public int getPoor_gprs_count() {
		return poor_gprs_count;
	}

	public void setPoor_gprs_count(int poor_gprs_count) {
		this.poor_gprs_count = poor_gprs_count;
	}

	@Override
	public String toString() {
		return "EventsUICount [time_stamp=" + time_stamp + ", mcb_trip_count="
				+ mcb_trip_count + ", cnt_failure_count=" + cnt_failure_count
				+ ", mains_supply_off_count=" + mains_supply_off_count
				+ ", door_open_count=" + door_open_count
				+ ", spd_failure_count=" + spd_failure_count
				+ ", no_output_count=" + no_output_count
				+ ", manual_mode_count=" + manual_mode_count + ", on_count="
				+ on_count + ", off_count=" + off_count + ", good_gprs_count="
				+ good_gprs_count + ", poor_gprs_count=" + poor_gprs_count
				+ "]";
	}

}
