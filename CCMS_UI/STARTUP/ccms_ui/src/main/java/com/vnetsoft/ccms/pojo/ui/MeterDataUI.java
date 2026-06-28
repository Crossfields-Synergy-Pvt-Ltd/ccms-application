package com.vnetsoft.ccms.pojo.ui;


public class MeterDataUI {

	int index ;
	private String dcu_id;
	
	private String r_phase_voltage;
	
	private String current_line_1;
	
	private String pf_1;
	private String kwh_total;
	private String utc_date;
	private String dcu_name;
	private String consumption; // need to update
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getDcu_id() {
		return dcu_id;
	}
	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
	}
	
	public String getR_phase_voltage() {
		return r_phase_voltage;
	}
	public void setR_phase_voltage(String r_phase_voltage) {
		this.r_phase_voltage = r_phase_voltage;
	}
	
	public String getCurrent_line_1() {
		return current_line_1;
	}
	public void setCurrent_line_1(String current_line_1) {
		this.current_line_1 = current_line_1;
	}
	
	public String getPf_1() {
		return pf_1;
	}
	public void setPf_1(String pf_1) {
		this.pf_1 = pf_1;
	}
	public String getKwh_total() {
		return kwh_total;
	}
	public void setKwh_total(String kwh_total) {
		this.kwh_total = kwh_total;
	}
	public String getUtc_date() {
		return utc_date;
	}
	public void setUtc_date(String utc_date) {
		this.utc_date = utc_date;
	}
	public String getDcu_name() {
		return dcu_name;
	}
	public void setDcu_name(String dcu_name) {
		this.dcu_name = dcu_name;
	}
	
	
	public String getConsumption() {
		return consumption;
	}
	public void setConsumption(String consumption) {
		this.consumption = consumption;
	}
	@Override
	public String toString() {
		return "MeterDataUI [index=" + index + ", dcu_id=" + dcu_id
				+ ", r_phase_voltage=" + r_phase_voltage + ", current_line_1="
				+ current_line_1 + ", pf_1=" + pf_1 + ", kwh_total="
				+ kwh_total + ", utc_date=" + utc_date + ", dcu_name="
				+ dcu_name + ", consumption=" + consumption + "]";
	}
	
	

}
