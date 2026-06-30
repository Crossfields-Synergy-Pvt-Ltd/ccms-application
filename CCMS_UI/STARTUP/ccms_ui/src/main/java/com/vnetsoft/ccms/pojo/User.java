package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ccms_user_details")
public class User {

	@Id
	private String firstName;
	
	private String lastName;
	
	private String password;

	private String mobnum1;

	private String email;

	private String role;

	private String status;

	private String full_name;

	private String address;

	private String taluq;

	private String district;

	private String state;

	private String pin_code;

	private String village;
	
	private String mandal;
	
	private String gp;
	

	private boolean monitor_and_controller;
	private boolean history;
	private boolean event;
	private boolean switching_point_summary;
	private boolean operational_hour;
	private boolean light_status;
	private boolean schedule ;
	private boolean settings;
	private boolean default_settings;
	private boolean filter;
	private boolean node;
	private boolean dcu;
	
	private boolean user;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobnum1() {
		return mobnum1;
	}

	public void setMobnum1(String mobnum1) {
		this.mobnum1 = mobnum1;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTaluq() {
		return taluq;
	}

	public void setTaluq(String taluq) {
		this.taluq = taluq;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPin_code() {
		return pin_code;
	}

	public void setPin_code(String pin_code) {
		this.pin_code = pin_code;
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

	public boolean isMonitor_and_controller() {
		return monitor_and_controller;
	}

	public void setMonitor_and_controller(boolean monitor_and_controller) {
		this.monitor_and_controller = monitor_and_controller;
	}

	public boolean isHistory() {
		return history;
	}

	public void setHistory(boolean history) {
		this.history = history;
	}

	public boolean isEvent() {
		return event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	public boolean isSwitching_point_summary() {
		return switching_point_summary;
	}

	public void setSwitching_point_summary(boolean switching_point_summary) {
		this.switching_point_summary = switching_point_summary;
	}

	public boolean isOperational_hour() {
		return operational_hour;
	}

	public void setOperational_hour(boolean operational_hour) {
		this.operational_hour = operational_hour;
	}

	public boolean isLight_status() {
		return light_status;
	}

	public void setLight_status(boolean light_status) {
		this.light_status = light_status;
	}

	public boolean isSchedule() {
		return schedule;
	}

	public void setSchedule(boolean schedule) {
		this.schedule = schedule;
	}

	public boolean isSettings() {
		return settings;
	}

	public void setSettings(boolean settings) {
		this.settings = settings;
	}

	public boolean isDefault_settings() {
		return default_settings;
	}

	public void setDefault_settings(boolean default_settings) {
		this.default_settings = default_settings;
	}

	public boolean isFilter() {
		return filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	public boolean isNode() {
		return node;
	}

	public void setNode(boolean node) {
		this.node = node;
	}

	public boolean isDcu() {
		return dcu;
	}

	public void setDcu(boolean dcu) {
		this.dcu = dcu;
	}

	public boolean isUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName
				+ ", password=" + password + ", mobnum1=" + mobnum1
				+ ", email=" + email + ", role=" + role + ", status=" + status
				+ ", full_name=" + full_name + ", address=" + address
				+ ", taluq=" + taluq + ", district=" + district + ", state=" + state
				+ ", pin_code=" + pin_code + ", village=" + village
				+ ", mandal=" + mandal + ", gp=" + gp
				+ ", monitor_and_controller=" + monitor_and_controller
				+ ", history=" + history + ", event=" + event
				+ ", switching_point_summary=" + switching_point_summary
				+ ", operational_hour=" + operational_hour + ", light_status="
				+ light_status + ", schedule=" + schedule + ", settings="
				+ settings + ", default_settings=" + default_settings
				+ ", filter=" + filter + ", node=" + node + ", dcu=" + dcu
				+ ", user=" + user + "]";
	}
	
	

}
