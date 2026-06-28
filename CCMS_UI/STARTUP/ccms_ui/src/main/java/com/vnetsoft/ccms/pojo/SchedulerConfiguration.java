package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scheduler_details")
public class SchedulerConfiguration {
	
	@Id 
	private long 	scheduleId;
	private String schedules_name;
	private String valid_till;
	private String fault_detection;
	private String timezoneid;
	private String data_interval_collection;
	private Boolean enable_fault_detection;
	private Boolean Set_schedule_as_default_schedule;
	private String handle_0_time;
	private String handle_1_time;
	private String handle_2_time;
	private String handle_3_time;
	private String handle_4_time;
	private String handle_0_val;
	private String handle_1_val;
	private String handle_2_val;
	private String handle_3_val;
	private Boolean handle_0_apply_sunrise_sunset  = false;
	private Boolean handle_1_apply_sunrise_sunset  = false;
	private Boolean handle_2_apply_sunrise_sunset  = false;
	private Boolean handle_3_apply_sunrise_sunset  = false;
	private Boolean handle_4_apply_sunrise_sunset  = false;
	
	private String handle_0_status;
	private String handle_1_status;
	private String handle_2_status;
	private String handle_3_status;
	private String handle_4_status;
	
	private String schedul_0_dim_value;
	private String schedul_1_dim_value;
	private String schedul_3_dim_value;
	private String schedul_2_dim_value;
	private String schedul_4_dim_value;
	private String handle_0_sunset_offset;
	private String 	handle_0_sunrise_offset;
	private String handle_1_sunset_offset;
	private String 	handle_1_sunrise_offset;
	private String handle_2_sunset_offset;
	private String 	handle_2_sunrise_offset;
	private String handle_3_sunset_offset;
	private String 	handle_3_sunrise_offset;
	private String handle_4_sunset_offset;
	private String 	handle_4_sunrise_offset;
	private Boolean 	handle_0_sunrise = false;
	private Boolean 	handle_0_sunset = false;
	private Boolean 	handle_1_sunrise  = false;
	private Boolean 	handle_1_sunset  = false;
	private Boolean 	handle_2_sunrise  = false;
	private Boolean 	handle_2_sunset  = false;
	private Boolean 	handle_3_sunrise  = false;
	private Boolean 	handle_3_sunset  = false;
	private Boolean 	handle_4_sunrise  = false;
	private Boolean 	handle_4_sunset  = false;
	
	
	private boolean time_slot_1 = false;
	private boolean time_slot_2 = false;
	private boolean time_slot_3 = false;
	private boolean time_slot_4 = false;
	private boolean time_slot_5 = false;
	private String handle_details;
	public long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getSchedules_name() {
		return schedules_name;
	}
	public void setSchedules_name(String schedules_name) {
		this.schedules_name = schedules_name;
	}
	public String getValid_till() {
		return valid_till;
	}
	public void setValid_till(String valid_till) {
		this.valid_till = valid_till;
	}
	public String getFault_detection() {
		return fault_detection;
	}
	public void setFault_detection(String fault_detection) {
		this.fault_detection = fault_detection;
	}
	public String getTimezoneid() {
		return timezoneid;
	}
	public void setTimezoneid(String timezoneid) {
		this.timezoneid = timezoneid;
	}
	public String getData_interval_collection() {
		return data_interval_collection;
	}
	public void setData_interval_collection(String data_interval_collection) {
		this.data_interval_collection = data_interval_collection;
	}
	public Boolean getEnable_fault_detection() {
		return enable_fault_detection;
	}
	public void setEnable_fault_detection(Boolean enable_fault_detection) {
		this.enable_fault_detection = enable_fault_detection;
	}
	public Boolean getSet_schedule_as_default_schedule() {
		return Set_schedule_as_default_schedule;
	}
	public void setSet_schedule_as_default_schedule(
			Boolean set_schedule_as_default_schedule) {
		Set_schedule_as_default_schedule = set_schedule_as_default_schedule;
	}
	public String getHandle_0_time() {
		return handle_0_time;
	}
	public void setHandle_0_time(String handle_0_time) {
		this.handle_0_time = handle_0_time;
	}
	public String getHandle_1_time() {
		return handle_1_time;
	}
	public void setHandle_1_time(String handle_1_time) {
		this.handle_1_time = handle_1_time;
	}
	public String getHandle_2_time() {
		return handle_2_time;
	}
	public void setHandle_2_time(String handle_2_time) {
		this.handle_2_time = handle_2_time;
	}
	public String getHandle_3_time() {
		return handle_3_time;
	}
	public void setHandle_3_time(String handle_3_time) {
		this.handle_3_time = handle_3_time;
	}
	public String getHandle_4_time() {
		return handle_4_time;
	}
	public void setHandle_4_time(String handle_4_time) {
		this.handle_4_time = handle_4_time;
	}
	public String getHandle_0_val() {
		return handle_0_val;
	}
	public void setHandle_0_val(String handle_0_val) {
		this.handle_0_val = handle_0_val;
	}
	public String getHandle_1_val() {
		return handle_1_val;
	}
	public void setHandle_1_val(String handle_1_val) {
		this.handle_1_val = handle_1_val;
	}
	public String getHandle_2_val() {
		return handle_2_val;
	}
	public void setHandle_2_val(String handle_2_val) {
		this.handle_2_val = handle_2_val;
	}
	public String getHandle_3_val() {
		return handle_3_val;
	}
	public void setHandle_3_val(String handle_3_val) {
		this.handle_3_val = handle_3_val;
	}
	public Boolean getHandle_0_apply_sunrise_sunset() {
		return handle_0_apply_sunrise_sunset;
	}
	public void setHandle_0_apply_sunrise_sunset(
			Boolean handle_0_apply_sunrise_sunset) {
		this.handle_0_apply_sunrise_sunset = handle_0_apply_sunrise_sunset;
	}
	public Boolean getHandle_1_apply_sunrise_sunset() {
		return handle_1_apply_sunrise_sunset;
	}
	public void setHandle_1_apply_sunrise_sunset(
			Boolean handle_1_apply_sunrise_sunset) {
		this.handle_1_apply_sunrise_sunset = handle_1_apply_sunrise_sunset;
	}
	public Boolean getHandle_2_apply_sunrise_sunset() {
		return handle_2_apply_sunrise_sunset;
	}
	public void setHandle_2_apply_sunrise_sunset(
			Boolean handle_2_apply_sunrise_sunset) {
		this.handle_2_apply_sunrise_sunset = handle_2_apply_sunrise_sunset;
	}
	public Boolean getHandle_3_apply_sunrise_sunset() {
		return handle_3_apply_sunrise_sunset;
	}
	public void setHandle_3_apply_sunrise_sunset(
			Boolean handle_3_apply_sunrise_sunset) {
		this.handle_3_apply_sunrise_sunset = handle_3_apply_sunrise_sunset;
	}
	public Boolean getHandle_4_apply_sunrise_sunset() {
		return handle_4_apply_sunrise_sunset;
	}
	public void setHandle_4_apply_sunrise_sunset(
			Boolean handle_4_apply_sunrise_sunset) {
		this.handle_4_apply_sunrise_sunset = handle_4_apply_sunrise_sunset;
	}
	public String getHandle_0_status() {
		return handle_0_status;
	}
	public void setHandle_0_status(String handle_0_status) {
		this.handle_0_status = handle_0_status;
	}
	public String getHandle_1_status() {
		return handle_1_status;
	}
	public void setHandle_1_status(String handle_1_status) {
		this.handle_1_status = handle_1_status;
	}
	public String getHandle_2_status() {
		return handle_2_status;
	}
	public void setHandle_2_status(String handle_2_status) {
		this.handle_2_status = handle_2_status;
	}
	public String getHandle_3_status() {
		return handle_3_status;
	}
	public void setHandle_3_status(String handle_3_status) {
		this.handle_3_status = handle_3_status;
	}
	public String getHandle_4_status() {
		return handle_4_status;
	}
	public void setHandle_4_status(String handle_4_status) {
		this.handle_4_status = handle_4_status;
	}
	public String getSchedul_0_dim_value() {
		return schedul_0_dim_value;
	}
	public void setSchedul_0_dim_value(String schedul_0_dim_value) {
		this.schedul_0_dim_value = schedul_0_dim_value;
	}
	public String getSchedul_1_dim_value() {
		return schedul_1_dim_value;
	}
	public void setSchedul_1_dim_value(String schedul_1_dim_value) {
		this.schedul_1_dim_value = schedul_1_dim_value;
	}
	public String getSchedul_3_dim_value() {
		return schedul_3_dim_value;
	}
	public void setSchedul_3_dim_value(String schedul_3_dim_value) {
		this.schedul_3_dim_value = schedul_3_dim_value;
	}
	public String getSchedul_2_dim_value() {
		return schedul_2_dim_value;
	}
	public void setSchedul_2_dim_value(String schedul_2_dim_value) {
		this.schedul_2_dim_value = schedul_2_dim_value;
	}
	public String getSchedul_4_dim_value() {
		return schedul_4_dim_value;
	}
	public void setSchedul_4_dim_value(String schedul_4_dim_value) {
		this.schedul_4_dim_value = schedul_4_dim_value;
	}
	public String getHandle_0_sunset_offset() {
		return handle_0_sunset_offset;
	}
	public void setHandle_0_sunset_offset(String handle_0_sunset_offset) {
		this.handle_0_sunset_offset = handle_0_sunset_offset;
	}
	public String getHandle_0_sunrise_offset() {
		return handle_0_sunrise_offset;
	}
	public void setHandle_0_sunrise_offset(String handle_0_sunrise_offset) {
		this.handle_0_sunrise_offset = handle_0_sunrise_offset;
	}
	public String getHandle_1_sunset_offset() {
		return handle_1_sunset_offset;
	}
	public void setHandle_1_sunset_offset(String handle_1_sunset_offset) {
		this.handle_1_sunset_offset = handle_1_sunset_offset;
	}
	public String getHandle_1_sunrise_offset() {
		return handle_1_sunrise_offset;
	}
	public void setHandle_1_sunrise_offset(String handle_1_sunrise_offset) {
		this.handle_1_sunrise_offset = handle_1_sunrise_offset;
	}
	public String getHandle_2_sunset_offset() {
		return handle_2_sunset_offset;
	}
	public void setHandle_2_sunset_offset(String handle_2_sunset_offset) {
		this.handle_2_sunset_offset = handle_2_sunset_offset;
	}
	public String getHandle_2_sunrise_offset() {
		return handle_2_sunrise_offset;
	}
	public void setHandle_2_sunrise_offset(String handle_2_sunrise_offset) {
		this.handle_2_sunrise_offset = handle_2_sunrise_offset;
	}
	public String getHandle_3_sunset_offset() {
		return handle_3_sunset_offset;
	}
	public void setHandle_3_sunset_offset(String handle_3_sunset_offset) {
		this.handle_3_sunset_offset = handle_3_sunset_offset;
	}
	public String getHandle_3_sunrise_offset() {
		return handle_3_sunrise_offset;
	}
	public void setHandle_3_sunrise_offset(String handle_3_sunrise_offset) {
		this.handle_3_sunrise_offset = handle_3_sunrise_offset;
	}
	public String getHandle_4_sunset_offset() {
		return handle_4_sunset_offset;
	}
	public void setHandle_4_sunset_offset(String handle_4_sunset_offset) {
		this.handle_4_sunset_offset = handle_4_sunset_offset;
	}
	public String getHandle_4_sunrise_offset() {
		return handle_4_sunrise_offset;
	}
	public void setHandle_4_sunrise_offset(String handle_4_sunrise_offset) {
		this.handle_4_sunrise_offset = handle_4_sunrise_offset;
	}
	public Boolean getHandle_0_sunrise() {
		return handle_0_sunrise;
	}
	public void setHandle_0_sunrise(Boolean handle_0_sunrise) {
		this.handle_0_sunrise = handle_0_sunrise;
	}
	public Boolean getHandle_0_sunset() {
		return handle_0_sunset;
	}
	public void setHandle_0_sunset(Boolean handle_0_sunset) {
		this.handle_0_sunset = handle_0_sunset;
	}
	public Boolean getHandle_1_sunrise() {
		return handle_1_sunrise;
	}
	public void setHandle_1_sunrise(Boolean handle_1_sunrise) {
		this.handle_1_sunrise = handle_1_sunrise;
	}
	public Boolean getHandle_1_sunset() {
		return handle_1_sunset;
	}
	public void setHandle_1_sunset(Boolean handle_1_sunset) {
		this.handle_1_sunset = handle_1_sunset;
	}
	public Boolean getHandle_2_sunrise() {
		return handle_2_sunrise;
	}
	public void setHandle_2_sunrise(Boolean handle_2_sunrise) {
		this.handle_2_sunrise = handle_2_sunrise;
	}
	public Boolean getHandle_2_sunset() {
		return handle_2_sunset;
	}
	public void setHandle_2_sunset(Boolean handle_2_sunset) {
		this.handle_2_sunset = handle_2_sunset;
	}
	public Boolean getHandle_3_sunrise() {
		return handle_3_sunrise;
	}
	public void setHandle_3_sunrise(Boolean handle_3_sunrise) {
		this.handle_3_sunrise = handle_3_sunrise;
	}
	public Boolean getHandle_3_sunset() {
		return handle_3_sunset;
	}
	public void setHandle_3_sunset(Boolean handle_3_sunset) {
		this.handle_3_sunset = handle_3_sunset;
	}
	public Boolean getHandle_4_sunrise() {
		return handle_4_sunrise;
	}
	public void setHandle_4_sunrise(Boolean handle_4_sunrise) {
		this.handle_4_sunrise = handle_4_sunrise;
	}
	public Boolean getHandle_4_sunset() {
		return handle_4_sunset;
	}
	public void setHandle_4_sunset(Boolean handle_4_sunset) {
		this.handle_4_sunset = handle_4_sunset;
	}
	public boolean isTime_slot_1() {
		return time_slot_1;
	}
	public void setTime_slot_1(boolean time_slot_1) {
		this.time_slot_1 = time_slot_1;
	}
	public boolean isTime_slot_2() {
		return time_slot_2;
	}
	public void setTime_slot_2(boolean time_slot_2) {
		this.time_slot_2 = time_slot_2;
	}
	public boolean isTime_slot_3() {
		return time_slot_3;
	}
	public void setTime_slot_3(boolean time_slot_3) {
		this.time_slot_3 = time_slot_3;
	}
	public boolean isTime_slot_4() {
		return time_slot_4;
	}
	public void setTime_slot_4(boolean time_slot_4) {
		this.time_slot_4 = time_slot_4;
	}
	public boolean isTime_slot_5() {
		return time_slot_5;
	}
	public void setTime_slot_5(boolean time_slot_5) {
		this.time_slot_5 = time_slot_5;
	}
	public String getHandle_details() {
		return handle_details;
	}
	public void setHandle_details(String handle_details) {
		this.handle_details = handle_details;
	}
	@Override
	public String toString() {
		return "SchedulerConfiguration [scheduleId=" + scheduleId
				+ ", schedules_name=" + schedules_name + ", valid_till="
				+ valid_till + ", fault_detection=" + fault_detection
				+ ", timezoneid=" + timezoneid + ", data_interval_collection="
				+ data_interval_collection + ", enable_fault_detection="
				+ enable_fault_detection
				+ ", Set_schedule_as_default_schedule="
				+ Set_schedule_as_default_schedule + ", handle_0_time="
				+ handle_0_time + ", handle_1_time=" + handle_1_time
				+ ", handle_2_time=" + handle_2_time + ", handle_3_time="
				+ handle_3_time + ", handle_4_time=" + handle_4_time
				+ ", handle_0_val=" + handle_0_val + ", handle_1_val="
				+ handle_1_val + ", handle_2_val=" + handle_2_val
				+ ", handle_3_val=" + handle_3_val
				+ ", handle_0_apply_sunrise_sunset="
				+ handle_0_apply_sunrise_sunset
				+ ", handle_1_apply_sunrise_sunset="
				+ handle_1_apply_sunrise_sunset
				+ ", handle_2_apply_sunrise_sunset="
				+ handle_2_apply_sunrise_sunset
				+ ", handle_3_apply_sunrise_sunset="
				+ handle_3_apply_sunrise_sunset
				+ ", handle_4_apply_sunrise_sunset="
				+ handle_4_apply_sunrise_sunset + ", handle_0_status="
				+ handle_0_status + ", handle_1_status=" + handle_1_status
				+ ", handle_2_status=" + handle_2_status + ", handle_3_status="
				+ handle_3_status + ", handle_4_status=" + handle_4_status
				+ ", schedul_0_dim_value=" + schedul_0_dim_value
				+ ", schedul_1_dim_value=" + schedul_1_dim_value
				+ ", schedul_3_dim_value=" + schedul_3_dim_value
				+ ", schedul_2_dim_value=" + schedul_2_dim_value
				+ ", schedul_4_dim_value=" + schedul_4_dim_value
				+ ", handle_0_sunset_offset=" + handle_0_sunset_offset
				+ ", handle_0_sunrise_offset=" + handle_0_sunrise_offset
				+ ", handle_1_sunset_offset=" + handle_1_sunset_offset
				+ ", handle_1_sunrise_offset=" + handle_1_sunrise_offset
				+ ", handle_2_sunset_offset=" + handle_2_sunset_offset
				+ ", handle_2_sunrise_offset=" + handle_2_sunrise_offset
				+ ", handle_3_sunset_offset=" + handle_3_sunset_offset
				+ ", handle_3_sunrise_offset=" + handle_3_sunrise_offset
				+ ", handle_4_sunset_offset=" + handle_4_sunset_offset
				+ ", handle_4_sunrise_offset=" + handle_4_sunrise_offset
				+ ", handle_0_sunrise=" + handle_0_sunrise
				+ ", handle_0_sunset=" + handle_0_sunset
				+ ", handle_1_sunrise=" + handle_1_sunrise
				+ ", handle_1_sunset=" + handle_1_sunset
				+ ", handle_2_sunrise=" + handle_2_sunrise
				+ ", handle_2_sunset=" + handle_2_sunset
				+ ", handle_3_sunrise=" + handle_3_sunrise
				+ ", handle_3_sunset=" + handle_3_sunset
				+ ", handle_4_sunrise=" + handle_4_sunrise
				+ ", handle_4_sunset=" + handle_4_sunset + ", time_slot_1="
				+ time_slot_1 + ", time_slot_2=" + time_slot_2
				+ ", time_slot_3=" + time_slot_3 + ", time_slot_4="
				+ time_slot_4 + ", time_slot_5=" + time_slot_5
				+ ", handle_details=" + handle_details + "]";
	}
	
	
}