package com.vnetsoft.ccms.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "node_details")
public class Node {
	
	@Id
	private String	nodeid;
	
	private String	type;
	
	private String subtype;
	
	private String reserved; 
	
	private String  slaveid;
	
	private String io_channel_1;
	
	private String io_channel_2; 
	
	private String io_channel_3;
	
	private String file_identifier;
	
	
	public String getFile_identifier() {
		return file_identifier;
	}
	public void setFile_identifier(String file_identifier) {
		this.file_identifier = file_identifier;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	public String getSlaveid() {
		return slaveid;
	}
	public void setSlaveid(String slaveid) {
		this.slaveid = slaveid;
	}
	public String getIo_channel_1() {
		return io_channel_1;
	}
	public void setIo_channel_1(String io_channel_1) {
		this.io_channel_1 = io_channel_1;
	}
	public String getIo_channel_2() {
		return io_channel_2;
	}
	public void setIo_channel_2(String io_channel_2) {
		this.io_channel_2 = io_channel_2;
	}
	public String getIo_channel_3() {
		return io_channel_3;
	}
	public void setIo_channel_3(String io_channel_3) {
		this.io_channel_3 = io_channel_3;
	}
	
	
	@Override
	public String toString() {
		return "Node [nodeid=" + nodeid + ", type=" + type + ", subtype="
				+ subtype + ", reserved=" + reserved + ", slaveid=" + slaveid
				+ ", io_channel_1=" + io_channel_1 + ", io_channel_2="
				+ io_channel_2 + ", io_channel_3=" + io_channel_3
				+ ", file_identifier=" + file_identifier + "]";
	}
	
	

}
