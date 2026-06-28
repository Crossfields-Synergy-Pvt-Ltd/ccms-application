package com.vnetsoft.ccms.pojo;

public class IOUIObject {

	private String dcu_id
	;
	private String date;

	private String on_hour_min;

	private String on_status;

	private String on_hours;
	
	private String cumulative_on_hour;

	private String off_hour_min;

	private String off_status;

	private String off_hours;
	
	private String cumulative_off_hour;

	public String getOn_hours() {
		return on_hours;
	}

	public void setOn_hours(String on_hours) {
		this.on_hours = on_hours;
	}

	public String getOff_hours() {
		return off_hours;
	}

	public void setOff_hours(String off_hours) {
		this.off_hours = off_hours;
	}

	public String getDcu_id() {
		return dcu_id;
	}

	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOn_hour_min() {
		return on_hour_min;
	}

	public void setOn_hour_min(String on_hour_min) {
		this.on_hour_min = on_hour_min;
	}

	public String getOn_status() {
		return on_status;
	}

	public void setOn_status(String on_status) {
		this.on_status = on_status;
	}

	public String getCumulative_on_hour() {
		return cumulative_on_hour;
	}

	public void setCumulative_on_hour(String cumulative_on_hour) {
		this.cumulative_on_hour = cumulative_on_hour;
	}

	public String getOff_hour_min() {
		return off_hour_min;
	}

	public void setOff_hour_min(String off_hour_min) {
		this.off_hour_min = off_hour_min;
	}

	public String getOff_status() {
		return off_status;
	}

	public void setOff_status(String off_status) {
		this.off_status = off_status;
	}

	public String getCumulative_off_hour() {
		return cumulative_off_hour;
	}

	public void setCumulative_off_hour(String cumulative_off_hour) {
		this.cumulative_off_hour = cumulative_off_hour;
	}

	@Override
	public String toString() {
		return " on_hour_min=" + on_hour_min 
				+ ", off_hour_min=" + off_hour_min
				+ ", on_hours=" + on_hours + ", cumulative_on_hour="
				+ cumulative_on_hour 
				+", off_hours=" + off_hours
				+ ", cumulative_off_hour=" + cumulative_off_hour + "\n";
	}

	


}
