package com.vnetsoft.ccms.scheduler;

public class IOStatusTmpPojo {
	private String dcu_id;

	private String date;

	private int hour;

	private int min;

	private int opration_value;
	
	private String status;

	private int opration_resone;

	public String getDcu_id() {
		return dcu_id;
	}

	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOpration_resone() {
		return opration_resone;
	}

	public void setOpration_resone(int opration_resone) {
		this.opration_resone = opration_resone;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getOpration_value() {
		return opration_value;
	}

	public void setOpration_value(int opration_value) {
		this.opration_value = opration_value;
	}

	@Override
	public String toString() {
		return "IOStatusTmpPojo [dcu_id=" + dcu_id + ", date=" + date
				+ ", hour=" + hour + ", min=" + min + ", opration_value="
				+ opration_value + ", status=" + status + ", opration_resone="
				+ opration_resone + "]";
	}



}
