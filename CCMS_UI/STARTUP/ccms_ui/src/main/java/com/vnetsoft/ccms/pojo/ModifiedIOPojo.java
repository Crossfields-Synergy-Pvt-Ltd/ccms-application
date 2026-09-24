package com.vnetsoft.ccms.pojo;

public class ModifiedIOPojo {

	private String dcu_id;
	private String node;
	private String date;
	private int operation_type;
	private int operation_value;
	private int operation_reason;

	public String getDcu_id() { return dcu_id; }
	public void setDcu_id(String dcu_id) { this.dcu_id = dcu_id; }
	public String getNode() { return node; }
	public void setNode(String node) { this.node = node; }
	public String getDate() { return date; }
	public void setDate(String date) { this.date = date; }
	public int getOperation_type() { return operation_type; }
	public void setOperation_type(int operation_type) { this.operation_type = operation_type; }
	public int getOperation_value() { return operation_value; }
	public void setOperation_value(int operation_value) { this.operation_value = operation_value; }
	public int getOperation_reason() { return operation_reason; }
	public void setOperation_reason(int operation_reason) { this.operation_reason = operation_reason; }
}
