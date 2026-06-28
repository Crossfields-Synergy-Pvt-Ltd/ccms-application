package com.vetsoft.ccms.netty.cfg;

public class Constants {

	public static final int CI_PingRequest = 10;
	public static int CI_Handshake_Response_Server_Gateway = 0;
	public static int CI_Handshake_request_Gateway_Server = 9;
	public static int CI_File_Information_Change = 2;
	public static int CI_Download_init_Request_Packet_Gateway_Server = 3;
	public static int CI_Download_Content_Gateway_Server = 4;
	public static int CI_Status_Gateway_Server = 5;
	public static int CI_Light_ON_OFF_Server_Gateway = 6;

	public static int FT_SG_System_Configuration = 1; // 0x01
	public static int FT_SG_Node_Configuration = 3; // 0x03
	public static int FT_GS_Event_Data = 7; // 0x07
	public static int FT_GS_Meter_Data = 13; // 0x0D

	public static int Frequency_DF = 10;
	public static int Voltage_DF = 10;
	public static int Current_DF = 1000;
	public static int PowerFactor_DF = 100;
	public static int ActivePower_DF = 1000;
	public static int ApparentPower_DF = 1000;
	public static int ReactivePower_DF = 1000;
	public static int KWHTOTAL_DF = 1000;

}
