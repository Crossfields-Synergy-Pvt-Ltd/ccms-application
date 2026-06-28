package com.vnetsoft.ccms.pojo.ui;

public class EventUICount {

	private int event_id;
	private int count;
	public int getEvent_id() {
		return event_id;
	}
	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "EventUICount [event_id=" + event_id + ", count=" + count + "]";
	}
	
	
	
}
