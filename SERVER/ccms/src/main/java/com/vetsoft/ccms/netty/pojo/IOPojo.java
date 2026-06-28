package com.vetsoft.ccms.netty.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "io_data")
public class IOPojo {
 
	@Id
	private long id;
	
//	private int status;
	private int opration_type; // (1 - ON_OFF, 2 - DIM)
	private int opration_value; //  (00=OFF, 01=ON, 02 - DIM)
	private int opration_resone;
	
	private int yymmdd;
	private String dcu_id;
	private long node_id;
	private long time_stamp;
	
	
	public long getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(long time_stamp) {
		this.time_stamp = time_stamp;
	}

	public String getDcu_id() {
		return dcu_id;
	}

	public void setDcu_id(String dcu_id) {
		this.dcu_id = dcu_id;
	}

	public long getNode_id() {
		return node_id;
	}

	public void setNode_id(long node_id) {
		this.node_id = node_id;
	}

	public int getYymmdd() {
		return yymmdd;
	}

	public void setYymmdd(int yymmdd) {
		this.yymmdd = yymmdd;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	public int getOpration_type() {
		return opration_type;
	}

	public void setOpration_type(int opration_type) {
		this.opration_type = opration_type;
	}

	public int getOpration_value() {
		return opration_value;
	}

	public void setOpration_value(int opration_value) {
		this.opration_value = opration_value;
	}

	public int getOpration_resone() {
		return opration_resone;
	}

	public void setOpration_resone(int opration_resone) {
		this.opration_resone = opration_resone;
	}

	@Override
	public String toString() {
		return "IOPojo [id=" + id + ", opration_type="
				+ opration_type + ", opration_value=" + opration_value
				+ ", opration_resone=" + opration_resone + ", yymmdd=" + yymmdd
				+ ", dcu_id=" + dcu_id + ", node_id=" + node_id + "]";
	}



}
