Lighting platform
Gateway - Server communication protocol
INTRODUCTION2
COMMUNICATION PROTOCOL2
BINARY PACKET STRUCTURE2
QUICK REFERENCE5
HOW TO INTERPRET COMMANDS6
COMMANDS7
HANDSHAKE7
INFORM CHANGE9
INITIALIZE DOWNLOAD PROCESS10
DOWNLOAD CONTENT12
UPLOAD DATA14
FILE CONTENT21
SYSTEM CONFIGURATION21
NODE CONFIGURATION22
SCHEDULE CONFIGURATION23
NODE OPERATION COMMAND23
ERROR RESPONSE FLAGS25
OPCODE PARSING26
OPCODE1061 (TYPE A)26
OPCODE 1073 (TYPE B)27
Introduction
The purpose of the document is to explain the communication protocol between the field gateway (RTU/DCU) and server.
Gateways periodically connect to the server over Ethernet / GPRS. Once the connection is established, gateways perform following actions:
Get the latest configuration if server has any new configuration with it.
Transfer all the information / data collected from the field devices.
Perform action as per user action.
Communication protocol
The protocol between server and the gateway is a custom binary protocol. The binary packets are communicated over TCP to the configured URL on configured port. The binary data in the packet is in big endian format.
Note: The communication protocol is request-response type of protocol and each request must have a response. In case response is not received at specified interval (may vary depending on the speed of communication media like Ethernet, GPRS etc.) the requester has to resend the request.
Binary packet structure
This section explains the structure of the binary packet. Each packet has three sections header, payload and footer.
Header and footer of any packet have fix size and format. Payload varies as per the purpose of the packet. Below table explains the packet structure.
Fields
Description
Header
Start of packet
4 bytes fix value of 0x55AA55AA
Protocol version
1 byte
Flags
1 byte value which indicates presence various
fields at start of payload
Gateway identifier
4 bytes unique id for each gateway
DSN
1 byte unique number for each packet
Command identifier
1 byte value indicating purpose of the packet
Payload length
2 bytes
Payload
Payload
Variable length
Footer
CRC
1 byte
Start of packet: Four-byte field indicates start of every packet. The value is always 0x55AA55AA. It is used to distinguish the packets when multiple packets are received as a single chunk of bytes.
Protocol version: One-byte field to specify the version of the protocol in use. Version will decide the packet structure. At any given moment, a device will support single protocol version and server shall support multiple protocol versions, as it has to communicate with multiple gateways on field. These gateways may support different protocol versions. Present protocol version is 0x02.
Flags: This field indicates whether the start of the payload includes some special fields. Each bit of 1 byte value indicates different fields. If bit is set to 1 then the field is present else not. If multiple fields are present then the sequence of fields are as per the bits mentioned below. Following are the special fields supported on the each bit.
Bit
Description
0 (LSB)
16 bytes gateway serial number is included in the payload and is
present at the start of the payload.
Not used.
Not used.
Not used.
Not used.
Not used.
Not used.
7 (MSB)
Not used.
Gateway identifier: 4 byte gateway identifier, which uniquely identifies the gateway.
DSN: It is data sequence number. It is one-byte field, which identifies the packet uniquely. Requester of the packet fills this field in request packet; responder copies the same value in the response packet. This way on receiving the response the requestors is able to map the response with the request sent.
Command identifier: One-byte field specifies the command identifier. The parsing of the payload is done based on the command identifier. In case of request packet, this field is non- zero and in case of response, it is always zero.
Payload length: Two-byte field specifies the length of the payload in bytes. It specifies number of bytes followed by header till the CRC field. So the header and CRC are not included in payload length.
Payload: It is the actual content to be transferred depending on the command identifier.
CRC: One byte field which is calculated by XORing all the bytes then XORing the count of ‘0x00’ valued bytes in packet and then XORing the count of ‘0xFF’ valued bytes in packet.
XORing is to be done for all bytes of header and the payload.
Following is the code snippet for calculating the CRC in ‘C’ programming language.
int CRC = 0;int Iterator = 0;int TempZeroCount = 0; int TempFFCount = 0;/* Parse bytes from start index to end index. */ for(Iterator = 0; Iterator < Length; Iterator++){CalculateCRC(unsignedchar*Packet,unsignedshortcharunsigned Length){
int CRC = 0;
int Iterator = 0;
int TempZeroCount = 0; int TempFFCount = 0;
/* Parse bytes from start index to end index. */ for(Iterator = 0; Iterator < Length; Iterator++)
{
CalculateCRC(unsignedchar*Packet,unsignedshort
char
unsigned Length)
{
/* XOR all the bytes from start index to end index. */ CRC = CRC ^ Packet[Iterator];/* Calculate total 0 in the packet. */ if(Packet[Iterator] == 0x00){TempZeroCount++;}/* Calculate total 0xFF in the packet. */ else if(Packet[Iterator] == 0xFF){TempFFCount++;}}/* XOR with total count of zeros in the packet. */ CRC = CRC ^ TempZeroCount;/* XOR with total count of 0xFF in the packet. */ CRC = CRC ^ TempFFCount;return CRC;}
/* XOR all the bytes from start index to end index. */ CRC = CRC ^ Packet[Iterator];
/* Calculate total 0 in the packet. */ if(Packet[Iterator] == 0x00)
{
TempZeroCount++;
}
/* Calculate total 0xFF in the packet. */ else if(Packet[Iterator] == 0xFF)
{
TempFFCount++;
}
}
/* XOR with total count of zeros in the packet. */ CRC = CRC ^ TempZeroCount;
/* XOR with total count of 0xFF in the packet. */ CRC = CRC ^ TempFFCount;
return CRC;
}
Quick reference
Below are few important keywords for easy understanding of subsequent sections.
Date time stamp: Every date time mentioned in any of the payload is four-byte value in seconds from epoch 1st Jan 1970. It is in UTC.
Node identifier: Each light / sensor / meter / measurement node is been uniquely identified by server and gateway, this unique identifier is referred as node identifier which is generated by server. Any communication between server and gateway for the node has to be done using the node identifier.
Input-Output (IO) channel identifier: A node can have multiple IO channels, e.g. one for on / off relay, another for dimming etc. Each IO channel of node is referred uniquely by IO channel identifier which is hardware depended. Following are the supported IO channels types:
For on / off control.
For dimming.
For fault detection.
Dimming value: The range of value for dimming the light is from 0x00 to 0x64.
File type: Gateway can transfer various types of data i.e. node data, configuration files etc. Each type of data is referred as file type. Similarly server transfers various configuration files. Following are the files supported with corresponding file type:
File type
Description
Sender and receiver
0x00
Not used.
0x01
System configuration.
Server to gateway.
0x02
Not used.
0x03
Node configuration.
Server to gateway.
0x04
Not used.
0x05
Not used.
0x06
Not used.
0x07
Event data.
Gateway to server.
0x08
Not used.
0x09
Not used.
0x0A
Not used.
0x0B
Not used.
0x0C
Not used.
0x0D
Meter data collected by gateway.
Gateway to server.
0x0E
Not used.
0x0F
Not used.
Note: These file types are referred in download and upload commands explain in subsequent sections.
How to interpret commands
Who is sendingthe request?Who is sendingthe response?Whichcommand is in consideration?What is to besent in request payload?What is to besent in response payload in case of success?It is the defaultvalue of thisfield.This is how multiple occurrence of a set of bytes isrepresented. These bytes are occurred as per thecount mentioned before the recurring occurrence.The subsequent sections explain different commands. This section explains how to interpret the subsequent sections.
Who is sending
the request?
Who is sending
the response?
Which
command is in consideration?
What is to be
sent in request payload?
What is to be
sent in response payload in case of success?
It is the default
value of this
field.
This is how multiple occurrence of a set of bytes is
represented. These bytes are occurred as per the
count mentioned before the recurring occurrence.
What is to be sent in response payload in case of failure?
What is to be sent in response payload in case of failure?
Commands
This section lists down all the supported commands between server and the gateway.
Handshake
Handshake packet is the first packet sent by the gateway to the server after the connection is established. It’s an updated handshake packet format.
Requester
Gateway
Responder
Server
Command identifier
0x09
Request
Firmware version (2 bytes) Storage detection flag (1 byte) CSQ (1 byte)
Battery voltage(4 byte) Gateway firmware type (1 byte) Asset Mode (1 byte)
Mode timestamp (4 byte) IMEI length (1 byte)
IMEI (based on IMEI length) GSM version (1 byte)
GSM Version (based on GSM version length) Mobile number length(1 byte)
Mobile number (based on length) IMSI (15 byte)
ICCID (20 byte)
Latitude (4 byte)
Longitude (4 byte)
Success response
Response flag (1 byte) [0x00] UTC date time (4 bytes)
Gateway identifier (4 bytes)
Error response
Response flag (1 byte) [Non zero value]
Firmware version: Gateway sends its firmware version in the payload; this is to inform server the current version number.
Storage detection flag: If the value is 0x01 then it means storage is detected else not.
UTC date time: It is the current date time stamp of the server, which shall be used to synchronize gateway’s RTC.
Gateway identifier: At production the 4 byte gateway won’t have the gateway identifier, but shall have a 16 byte serial number. So gateway shall send the serial number in the packet and shall specify the gateway identifier as 0xFFFFFFFF.
In every handshake response server shall send the gateway identifier which can be used for further packets by the gateway.
CSQ: Quality of GPRS connection. It is applicable only in case of GPRS communication media.
Gateway firmware type: Server supports following firmware types:
0x00: Single phase feeder panel gateway
Asset Mode: Specifies the current mode of RTU
0x04: Deployed
Any other value contact with Moschip
Mode Timestamp:
IMEI: Identity valid device GSM version: GSM version IMSI: SIM IMSI number ICCID: SIM ICCID number
Latitude: Latitude based on SIM operator
Longitude: Longitude based on SIM operator
Handshake request packet (Gateway to server)
55 AA 55 AA 01 01 00 00 00 00 35 09 00 76 31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36 01 68 01 0A
40 83 C6 A8 00 04 5A CD 50 00 10 38 36 34 34 39 35 30 33 32 33 34 32 38 37 32 00 10 31 33 30 38 42 30
39 53 49 4D 38 30 30 4D 33 32 0A 31 31 31 31 31 31 31 31 31 31 34 30 34 34 39 30 31 39 39 38 36 39 30
36 34 38 39 39 31 34 39 30 31 39 30 30 30 39 38 36 39 30 36 34 34 00 00 00 00 00 00 00 00 9B
Breakup of Handshake request Packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 00 00
Gateway identifier
DSN
Command identifier
00 76
Payload length
31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36
Gateway serial number
01 68
Firmware version
Storage detection flag
0A
CSQ
40 83 C6 A8
Battery voltage
Gateway identifier
Asset mode
5A CD 50 00
Asset mode timestamp
IMEI length
38 36 34 34 39 35 30 33 32 33 34 32 38 37 32 00
IMEI
GSM version length
31 33 30 38 42 30 39 53 49 4D 38 30 30 4D 33 32
GSM version
0A
Mobile number length
31 31 31 31 31 31 31 31 31 31
Mobile number
34 30 34 34 39 30 31 39 39 38 36 39 30 36 34
IMSI number
38 39 39 31 34 39 30 31 39 30 30 30 39 38 36 39
30 36 34 34
ICCID number
00 00 00 00
Latitude
00 00 00 00
Longitude
9B
CRC
Handshake response packet (Server to gateway)
55 AA 55 AA 01 01 00 00 09 F7 01 00 00 19 31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36 00 57 E4 D9
40 00 00 09 F7 59
Break up of Handshake response packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 09 F7
Gateway identifier
DSN
Command identifier
00 19
Payload length
31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36
Gateway serial number
Response flag
57 E4 D9 40
UTC date time
00 00 09 F7
Gateway identifier
CRC
Inform change
On change of any files (e.g. node configuration, schedule file) from user interface, server informs the change to gateway via this request. On receiving this request, gateway shall initiate the download process to download the changed file.
Requester
Server
Responder
Gateway
Command identifier
0x02
Request
Count (1 byte) [
File Type (1 byte)
File identifier (4 bytes)
]+
Success response
Response flag (1 byte) [0x00]
Error response
Response flag (1 byte) [Non zero value]
Count: Number of file types in which changes are present.
File type: Refer section ‘Quick reference’ for more details.
File identifier: Unique number of a file for specific file type used for downloading the file. On any change in file content, server changes the file identifier. The gateway shall download the file only if the file identifier of the file present on gateway doesn’t match with file identifier provided by server.
Inform change request packet (Server to Gateway device)
55 AA 55 AA 01 00 00 00 09 F7 3E 02 00 1F 06 03 57 E4 D9 04 01 57 E4 D5 12 0E 57 E4 D5 12 04 57 E4 D9
07 16 57 E4 D2 4F 17 57 E3 B5 68 93
Break up of inform change request packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 09 F7
Gateway identifier
3E
DSN
Command id
00 1F
Payload length
Count
File type
57 E4 D9 04
File identifier
File type
57 E4 D5 12
File identifier
0E
File type
57 E4 D5 12
File identifier
File type
57 E4 D9 07
File identifier
File type
57 E4 D2 4F
File identifier
File type
57 E3 B5 68
File identifier
CRC
Inform change response packet (Gateway device to server)
55 AA 55 AA 01 01 00 00 09 F7 3E 00 00 11 31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36 00 B8
Break up of inform change response packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 09 F7
Gateway identifier
3E
DSN
Command id
00 11
Payload length
31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36
Gateway serial number
Response flag
B8
CRC
Initialize download process
On receiving change in file from server, gateway shall initiate the download process. The process has two steps; first step is to initiate the download and second step is to carry out the transfer of file content. This section explains the download initialization process. Next section explains the downloading of file content.
The response of initialization helps the gateway to get the file details like file size etc.
Requester
Gateway
Responder
Server
Command identifier
0x03
Request
File type (1 byte)
File identifier (4 bytes)
File version (2 bytes)
Success response
Response flag (1 byte) [0x00] File type (1 byte)
File identifier (4 bytes) Total file size (4 bytes)
Chunk size (2 bytes)
Error response
Response flag (1 byte) [Non zero value]
File identifier: Unique number of a file for specific file type used for downloading the file. On any change in file content, server change the file identifier.
File type: Refer section ‘Quick reference’ for more details.
File version: File version indicates the format of the file content.
Total file size: Total size of the file to be transferred in bytes.
Chunk size: In most of the case the file size is larger and can’t be sent in a single packet. In which case, the file is to be transferred in chunks. This field specifies the chunk size preferred by gateway. Gateway decides the chunk size on the basis of the MTU (maximum transmission unit) of communication media.
Download init request packet (Gateway device to server)
55 AA 55 AA 01 01 00 00 09 F7 03 03 00 17 31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36 03 57 E4 D9
04 00 02 EE
Break up of Download init request packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 09 F7
Gateway identifier
DSN
Command identifier
00 17
Payload length
31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36
Gateway serial number
File type
57 E4 D9 04
File identifier
00 02
File version
EE
CRC
Download init response packet (Server to gateway device)
55 AA 55 AA 01 01 00 00 09 F7 03 00 00 1C 31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36 00 03 57 E4
D9 04 00 00 00 8C 02 00 67
Break up of Download init response packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 09 F7
Gateway identifier
DSN
Command identifier
00 1C
Payload length
31 36 30 39 51 41 54 41 52 30 30 30 30 30 33
Gateway serial number
Response flag
File Type
57 E4 D9 04
File identifier
00 00 00 8C
Total file size
02 00
Chunk size
CRC
Download content
This section explains the downloading of file content.
Note: This request is to be sent only on receiving the success response of ‘Initialize download process’.
Requester
Gateway
Responder
Server
Command identifier
0x04
Request
File type (1 byte)
File identifier (4 bytes) File offset (4 bytes)
Chunk length (2 bytes)
Success response
Response flag (1 byte) [0x00] File type (1 byte)
File identifier (4 bytes) Chunk offset (4 bytes) Chunk length (2 bytes)
File content (variable bytes as specified in chunk length field)
Error response
Response flag (1 byte) [Non zero value]
File type: Refer section ‘Quick reference’ for more details.
File identifier: Unique number of a file for specific file type used for downloading the file. On any change in file content, server change the file identifier.
Chunk offset: The file is transferred in chunks. The gateway shall start with offset zero and shall increment the offset on receiving those many bytes from the server. E.g. of the chunk size is 100 bytes then offset in first request is 0x00000000 and off
set in second request is 0x00000064 etc.
Chunk length: It specifies the number of bytes in response. In above example it is 0x0064.
File content: The actual bytes / content of the file.
Download content request packet (Gateway device to server)
55 AA 55 AA 01 01 00 00 09 F7 05 04 00 1B 31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36 03 57 E4 D9
04 00 00 00 00 02 00 EF
Breakup of download data request packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 09 F7
Gateway identifier
DSN
Command identifier
00 1B
Payload length
31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36
Gateway serial number
File type
57 E4 D9 04
File Identifier
00 00 00 00
File offset
02 00
Chunk length
EF
CRC
Download content response packet (Server to gateway device)
55 AA 55 AA 01 01 00 00 09 F7 05 00 00 A8 31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36 00 03 57 E4
D9 04 00 00 00 00 00 8C 00 00 1B 23 03 11 00 00 02 3B 01 07 FF FF 00 00 1B 24 03 11 00 00 02 3B FF 07
FF FF 00 00 1B 25 03 11 00 00 02 3B 03 07 FF FF 00 00 1B 26 03 11 00 00 02 3B 04 07 FF FF 00 00 1B 27
03 11 00 00 02 3B 05 07 FF FF 00 00 1B 28 03 11 00 00 02 3B 06 07 FF FF 00 00 1B 29 03 11 00 00 02 3B
07 07 FF FF 00 00 1B 2A 03 11 00 00 02 3B 08 07 FF FF 00 00 1B 2B 03 11 00 00 02 3B 09 07 FF FF 00 00
1B 2C 03 11 00 00 02 3B 0A 07 FF FF 00
Break up of Download data response packet:
55 AA 55 AA
SOF
Protocol version
Flag
00 00 09 F7
Gateway identifier
DSN
Command identifier
00 A8
Payload length
31 36 30 39 51 41 54 41 52 30 30 30 30 30 33 36
Gateway serial number
Response flag
File Type
57 E4 D9 04
File Identifier
00 00 00 00
Chunk offset
00 8C
Chunk length
00 00 1B 23 03 11 00 00 02 3B 01 07 FF FF 00 00 1B 24 03 11 00
00 02 3B FF 07 FF FF 00 00 1B 25 03 11 00 00 02 3B 03 07 FF FF 00
00 1B 26 03 11 00 00 02 3B 04 07 FF FF 00 00 1B 27 03 11 00 00
02 3B 05 07 FF FF 00 00 1B 28 03 11 00 00 02 3B 06 07 FF FF 00 00
Data (Please refer the File contents section for the interpretation of
this data based on the
1B 29 03 11 00 00 02 3B 07 07 FF FF 00 00 1B 2A 03 11 00 00 02
3B 08 07 FF FF 00 00 1B 2B 03 11 00 00 02 3B 09 07 FF FF 00 00 1B
2C 03 11 00 00 02 3B 0A 07 FF FF
file type)
CRC
Upload data
Gateway collects the node data and stores it on the storage memory. On connection with server, gateway transfers this data using this packet.
Event data is:
Change in on / off status of the light.
Change in dimming value of the light.
Change in different alerts.I.e. MCB trip, Red mains supply off.
Meter data is:
Data collected from meter periodically.
Requester
Gateway
Responder
Server
Command identifier
0x05
Request
Transaction identifier (4 bytes) File type (1 byte)
File version (2 byte) Total file size (4 bytes) Chunk offset (4 bytes) Chunk length (2 bytes)
File content (variable bytes as specified in chunk length field)
Success response
Response flag (1 byte) [0x00] Transaction identifier (4 bytes) File type (1 byte)
File version (2 byte)
Received chunk offset (4 bytes)
Received chunk length (2 bytes)
Error response
Response flag (1 byte) [Non zero value]
Transaction identifier: If the connection with server is not present for long time, then gateway shall have many history records in the file. Gateway would not send all the records in one go i.e. gateway shall not send the entire file in one go. It will send fix number of records say ‘100’ records in a single transaction. Number of records in a single transaction depends on size of record. Each transaction is identified by a unique number which known as transaction identifier.
File type: Specifies type of Data
0x0F- Event Data (Refer section 3.5.2) 0x0D- Meter Data (Refer section 3.5.3)
File version: File version indicates the format of the file content.
Total file size: Total size of the file to be transferred in bytes.
Chunk offset: For a particular transaction the content is transferred in chunks. The gateway shall start with offset zero and shall increment the offset on receiving those many bytes from the server. E.g. of the
chunk size is 100 bytes then offset in first request is 0x00000000 and offset in second request is 0x00000064 etc.
Chunk length: It specifies the number of bytes in current chunk. In above example it is 0x0064.
File content: The actual bytes / content of the file.
Received chunk offset: It specifies the chunk offset received in request.
Received chunk length: It specifies the number of bytes in current chunk received in request.
Upload request packet (Gateway device to server)
Light ON OFF status packet
55 AA 55 AA 01 00 00 00 00 02 02 05 00 22 00 00 00 01 07 00 01 00 00 00 11 00 00 00 00 00 11 57 F3 6E
46 00 00 00 17 00 00 00 05 00 01 00 01 00 76
55 AA 55 AA
SOF
Protocol version
Flag
00 00 00 02
Gateway identifier
DSN
Command identifier
00 22
Payload length
00 00 00 01
Transaction id
File type
00 01
File version
00 00 00 11
Total file size
00 00 00 00
Chunk offset
00 11
Chunk length
57 F3 6E 46
Timestamp
00 00 00 17
Node identifier
00 00
Event identifier
00 05
Event data length
Status
Operation type (1 = ON_OFF, 2 = DIM)
00 01
Operation value
Reason
CRC
Event Data
Gateway captures various events of nodes. That event data is uploaded from gateway to server.
File version: 0x02.
Records in a file: Multiple records in a file.
Record details: Multiple fields in a single record.
Field
Bytes
Remarks
Time stamp
4 bytes
Time stamp in UTC at which the event
occurred.
Node identifier
4 bytes
Node identifier for which the event is captured.
Event identifier
2 bytes
Type of event occurred for generation of the data.
0x00: light on / off operation
Event data length
2 bytes
Length of event data.
Event data
Variable
length
Refer section 3.5.2.1
Event Id information
Event
Id
Event
Information
IO
Event id when IO control event occurred
Refer section 3.5.2.2
METER
Event id when data is requested
RED_MCB_TRIP_OCCURED
Event id when MCB is tripped
DOOR_OPEN
Event id when door is opened
CONTRACTOR_FAILURE
Event id when contactor fail occurs
AUTO_MANUAL
Event id when panel switch from
auto to manual mode
RED_THRESHOLD_CROSS_V_HIGH
Event id when meter data voltage
cross highest threshold value
RED_THRESHOLD_CROSS_V_LOW
Event id when meter data voltage
cross lowest threshold value
RED_THRESHOLD_CROSS_I_HIGH
Event id when meter data current
cross highest threshold value
RED_THRESHOLD_CROSS_I_LOW
Event id when meter data current
cross lowest threshold value
YELLOW_THRESHOLD_CROSS_V_HIGH
Event id when meter data voltage
cross highest threshold value
YELLOW_THRESHOLD_CROSS_V_LOW
Event id when meter data voltage
cross lowest threshold value
YELLOW_THRESHOLD_CROSS_I_HIGH
Event id when meter data current
cross highest threshold value
YELLOW_THRESHOLD_CROSS_I_LOW
Event id when meter data current
cross lowest threshold value
BLUE_THRESHOLD_CROSS_V_HIGH
Event id when meter data voltage
cross highest threshold value
BLUE_THRESHOLD_CROSS_V_LOW
Event id when meter data voltage
cross lowest threshold value
BLUE_THRESHOLD_CROSS_I_HIGH
Event id when meter data current
cross highest threshold value
BLUE_THRESHOLD_CROSS_I_LOW
Event id when meter data current
cross lowest threshold value
RED_CNTRCT_FAIL
Event id when R contactor fail
occurs
YELLOW_CNTRCT_FAIL
Event id when Y contactor fail
occurs
BLUE_CNTRCT_FAIL
Event id when B contactor fail
occurs
RED_PHASE_NO_OUTPUT
Event id when there is no output on
R phase
YELLOW_PHASE_NO_OUTPUT
Event id when there is no output on
Y phase
BLUE_PHASE_NO_OUTPUT
Event id when there is no output on
B phase
DOOR_CLOSED
Event id when door is closed
CNTRCT_FAIL_RESOLVED
Event id when contactor phase
resolved
RED_CNTRCT_FAIL_RESOLVED
Event id when R contactor resolved
YELLOW_CNTRCT_FAIL_RESOLVED
Event id when Y contactor resolved
BLUE_CNTRCT_FAIL_RESOLVED
Event id when B contactor resolved
MAN_MODE
Event id manual mode resolved
RED_MCB_TRIP_RESOLVED
Event id when MCB trip resolved
RTU_MAINS_OFF
Event id when mains off detected
RTU_MAINS_ON
Event id when mains on detected
RED_MAINS_SUPPLY_OFF
Event id when mails supply from utility company if OFF
YELLOW_MAINS_SUPPLY_OFF
Event id when mails supply from
utility company if OFF
BLUE_MAINS_SUPPLY_OFF
Event id when mails supply from
utility company if OFF
RED_MAINS_SUPPLY_ON
Event id when mails supply from
utility company if OFF
YELLOW_MAINS_SUPPLY_ON
Event id when mails supply from utility company if OFF
BLUE_MAINS_SUPPLY_ON
Event id when mails supply from
utility company if OFF
39-40
UNUSED
RED_PHASE_NO_OUTPUT_RESOLVED
YELLOW_PHASE_NO_OUTPUT_RESOLVED
BLUE_PHASE_NO_OUTPUT_RESOLVED
RED_THRESHOLD_CROSS_V_HIGH_RESOLVED
Event id when meter data voltage crosshighestthresholdvalue
resolved
RED_THRESHOLD_CROSS_I_HIGH_RESOLVED
Event id when meter data current crosshighestthresholdvalue
resolved
YELLOW_THRESHOLD_CROSS_V_HIGH_RESOLVED
Event id when meter data voltage crosshighestthresholdvalue
resolved
YELLOW_THRESHOLD_CROSS_I_HIGH_RESOLVED
Event id when meter data current crosshighestthresholdvalue
resolved
BLUE_THRESHOLD_CROSS_V_HIGH_RESOLVED
Event id when meter data voltage crosshighestthresholdvalue
resolved
BLUE_THRESHOLD_CROSS_I_HIGH_RESOLVED
Event id when meter data current crosshighestthresholdvalue
resolved
50-51
UNUSED
RED_THRESHOLD_CROSS_V_LOW_RESOLVED
Event id when R threshold cross low
voltage resolved
RED_THRESHOLD_CROSS_I_LOW_RESOLVED
Event id when R threshold cross low
current resolved
YELLOW_THRESHOLD_CROSS_V_LOW_RESOLVED
Event id when Y threshold cross low
voltage resolved
YELLOW_THRESHOLD_CROSS_I_LOW_RESOLVED
Event id when Y threshold cross low
current resolved
BLUE_THRESHOLD_CROSS_V_LOW_RESOLVED
Event id when B threshold cross low
voltage resolved
BLUE_THRESHOLD_CROSS_I_LOW_RESOLVED
Event id when B threshold cross low
current resolved
YELLOW_MCB_TRIP_OCCURED
Event id when MCB is tripped
BLUE_MCB_TRIP_OCCURED
Event id when MCB is tripped
COMMON_MCB_TRIP_OCCURED
Event id when MCB is tripped
YELLOW_MCB_TRIP_RESOLVED
Event id when MCB is resolved
BLUE_MCB_TRIP_RESOLVED
Event id when MCB is resolved
COMMON_MCB_TRIP_RESOLVED
Event id when MCB is resolved
64 - 77
UNUSED
UI_EVENT_R_SURGE_PRTCTR_TRIP
Event id is used to show R surge
protector trip
UI_EVENT_Y_SURGE_PRTCTR_TRIP
Event id is used to show Y surge
protector trip
UI_EVENT_B_SURGE_PRTCTR_TRIP
Event id is used to show B surge
protector trip
UI_EVENT_COMMON_SURGE_PRTCTR_TRIP
Event id is used to show common
surge protector trip
UI_EVENT_R_SURGE_PRTCTR_TRIP_RESOLVED
Event id is used to show R surge
protector trip resolved
UI_EVENT_Y_SURGE_PRTCTR_TRIP_RESOLVED
Event id is used to show Y surge
protector trip resolved
UI_EVENT_B_SURGE_PRTCTR_TRIP_RESOLVED
Event id is used to show B surge
protector trip resolved
UI_EVENT_COMMON_SURGE_PRTCTR_TRIP_RESOLVE
D
Event id is used to show common
surge protector trip resolved
UNUSED
EVENT_BASED_ALL_ALERTS_NOTIFICATIONS
Refer section 3.5.2.2
Event based Sub parsing
Event Id
Event
Parsing
IO
status (1 byte) operation type (1 byte)
operation value (2 bytes) operation reason (1 byte)
if operation reason is IO_CNTRL_FROM_RULE(4) then executedRuleId (4 byte)
phase reason (1 byte)
Please refer section 4.4 for opcode wise meter data parsing
EVENT_BASED_ALL_ALERT S_NOTIFICATIONS
1 byte - Alert count
Alert data - 1 byte status of each alert Here alert sequence is as follows.
RED_PHASE_NO_OUTPUT (0-Resolved, 1-Occured) YELLOW_PHASE_NO_OUTPUT (0-Resolved, 1-Occured) BLUE_PHASE_NO_OUTPUT (0-Resolved, 1-Occured) RED_THRESHOLD_CROSS_I_HIGH (1-Occured) YELLOW_THRESHOLD_CROSS_I_HIGH (1-Occured) BLUE_THRESHOLD_CROSS_I_HIGH (1-Occured) RED_THRESHOLD_CROSS_I (1-Occured) YELLOW_THRESHOLD_CROSS_I (1-Occured) BLUE_THRESHOLD_CROSS_I (1-Occured) RED_THRESHOLD_CROSS_V_LOW (1-Occured) YELLOW_THRESHOLD_CROSS_V_LOW (1-Occured) BLUE_THRESHOLD_CROSS_V_LOW (1-Occured) RED_THRESHOLD_CROSS_V (1-Occured) YELLOW_THRESHOLD_CROSS_V (1-Occured)
BLUE_THRESHOLD_CROSS_V (1-Occured)
RED_MAINS_SUPPLY (1-OFF,0-ON) YELLOW_MAINS_SUPPLY (1-OFF,0-ON) BLUE_MAINS_SUPPLY (1-OFF,0-ON)
MCB_TRIP (1-Occurred, 0-Resolved) RED_CNTRCT_FAIL (1-Occurred, 0-Resolved) YELLOW_CNTRCT_FAIL (1-Occurred, 0-Resolved) BLUE_CNTRCT_FAIL (1-Occurred, 0-Resolved) DOOR_ALERT (1-Open,0-Close) CONTRACTOR_FAILURE (1-Occurred, 0-Resolved) YELLOW_MCB_TRIP (1-Occurred, 0-Resolved) BLUE_MCB_TRIP (1-Occurred, 0-Resolved) COMMON_MCB_TRIP (1-Occurred, 0-Resolved) AUTO_MANUAL (1-Manual mode, 0- Auto mode) R_SURGE_PRTCTR_TRIP (1-Occurred, 0-Resolved) Y_SURGE_PRTCTR_TRIP (1-Occurred, 0-Resolved) B_SURGE_PRTCTR_TRIP (1-Occurred, 0-Resolved)
COMMON_SURGE_PRTCTR_TRIP (1-Occurred, 0-Resolved)
RTU_MAINS (1-On battery, 0-On mains)
1 byte-Total relay count
Relay data= relay count * 2 (relay state of each relay in short)
IO_R_RELAY (1-ON, 0-OFF) IO_Y_RELAY (1-ON, 0-OFF) IO_B_RELAY (1-ON, 0-OFF) IO_ALL_PHASE_RELAY (1-ON, 0-OFF)
Node data
Gateway captures data from various nodes. That node data is uploaded from gateway to server.
File version: 0x01.
Records in a file: Multiple records in a file.
Record details: Multiple fields in a single record.
Field
Bytes
Remarks
Time stamp
4 bytes
Time stamp in UTC at which the event
occurred.
Node identifier
4 bytes
Node identifier for which the data is captured.
Opcode
2 bytes
Please refer section 7 for support meters and
its associated opcodes.
Data length
2 bytes
Length of event data.
Data
Variable
length
Please refer section 7.x for the interpretation of
the data based on the opcode.
File content
This section explains content of each file type. These are binary files and the content is stored as multiple records. Each record has fix size. So for a file, if the record size is 100 bytes and the record count is 10 then the total size of the file is 100 * 10 bytes i.e. 1000 bytes.
System configuration
System configuration stores the basic configuration of gateway which is used for communication and execution. There is single record in the file.
File version: 0x02.
Records in a file: Single record in a file.
Record details: Multiple fields in a single record.
Field
Bytes
Remarks
Gateway identifier
16 bytes
Alpha numeric.
Reserved
1 byte
All zeros. Not used.
Reserved
20 bytes
All zeros. Not used.
Overwrite data on storage full
1 byte
Flag which indicates whether to overwrite the data on storage full condition. 0x01 in case
overwrite is allowed and 0x00 otherwise.
Server communication retry count
1 byte
Valid values are 0x01 to 0x0A.
Server communication retry interval
1 byte
In seconds.
GPRS frequency band
16 bytes
Only to be used for GPRS communication.
GPRS APN name
32 bytes
Only to be used for GPRS communication.
GPRS APN user name
16 bytes
Only to be used for GPRS communication.
GPRS APN password
16 bytes
Only to be used for GPRS communication.
Server primary URL / IP
64 bytes
It can be URL or IP.
Server secondary URL / IP
64 bytes
It can be URL or IP. To be used only in case
multiple servers are to be supported.
Server port number
2 bytes
TCP port number.
Server secondary port
2 bytes
TCP port number
Reserved
64 bytes
All zeros. Not used.
Primary DNS
16 bytes
IP of primary DNS server. To be used in case of
resolution of URL to IP.
Secondary DNS
16 bytes
IP of secondary DNS server. To be used in case
of resolution of URL to IP.
UART-1 stop bits
1 byte
Valid values are 0x00, 0x01 and 0x02.
UART-1 baud rate
4 bytes
Valid values are 2400, 9600 and 115200.
UART-1 parity
1 byte
Valid values are 0x00 (None), 0x01 (Odd) and 0x02 (Even).
UART-1 data bits
1 byte
Valid values are 0x07 and 0x08.
UART-1 retry count
1 byte
Valid values are 0x01 to 0x05.
UART-1 retry interval
2 bytes
In milliseconds.
UART-2 stop bits
1 byte
Valid values are 0x00, 0x01 and 0x02.
UART-2 baud rate
4 bytes
Valid values are 2400, 9600 and 115200.
UART-2 parity
1 byte
Valid values are 0x00 (None), 0x01 (Odd) and
0x02 (Even).
UART-2 data bits
1 byte
Valid values are 0x07 and 0x08.
UART-2 retry count
1 byte
Valid values are 0x01 to 0x05.
UART-2 retry interval
2 byte
In milliseconds.
Reserved
2 bytes
-
Reserved
1 byte
-
Reserved
2 bytes
-
Reserved
1 byte
-
Reserved
1 byte
-
Reserved
1 byte
-
Reserved
1 byte
-
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
4 bytes
All zeros. Not used.
Reserved
2 Bytes
-
Reserved
2 Bytes
-
Reserved
1 Bytes
-
Reserved
2 Bytes
-
Reserved
2 Bytes
-
Reserved
2 Bytes
-
Node configuration
Node configuration allows the details of the nodes to be transferred to gateway. As there can be multiple nodes in a network so this file supports multiple records.
File version: 0x02.
Records in a file: Multiple records in a file.
Record details: Multiple fields in a single record.
Field
Bytes
Remarks
Node identifier
4 bytes
Valid values are 0x0000001 to 0xFFFFFFFF.
Node type
1 byte
0x00: Gateway
0x01: Wired primary meter 0x02: Wired secondary meter
Node sub type
1 byte
For Gateway: 0x00: Reserved
RF identifier
4 bytes
0x00. Not used.
Reserved
1 byte
0x00. Not used.
Channel-1
1 byte
For Gateway: 0x00: Reserved
Channel-2
1 byte
0x00. Not used.
Channel-3
1 byte
0x00. Not used.
Schedule configuration
Field
Bytes
Remarks
ScheduleId
4 bytes
Unique identifier for each schedule.
Action
1 byte
Unique identification of action to be taken
Action value
2 bytes
ON_OFF = 1,
DIM = 2,
POLL = 3,
Start date
4 bytes
Epoch time
End date
4 bytes
Epoch time
Frequency
1 byte
Minutely = 1,
Daily = 2,
Always = 3
Priority
1 byte
Value = 1
Reference time
4 bytes
Time of the day, when the schedule is
supposed to execute.
Interval
2 bytes
Validity time
2 bytes
Opcode
2 bytes
Sunset offset
2 bytes
Sunrise offset
2 bytes
Group count
1 byte
Group ids
20 bytes
4 byte of group id – such 5 group ids to add. If there are less than 5 groups then others bytes
are to be append as zero.
Node count
1 byte
Number of lights / meters associated in this
schedule
Node ids
40 bytes
4 byte of node id – such 10 nodes can be added in the one entry. If there are less nodes other
bytes should be appended as zero.
Node Operation command
Requester
Server
Responder
Gateway
Command identifier
0x06
Request
Operation Type (1 byte) Operation Value (2 bytes) Node Count (1 byte)
{
Node Id (4 byte)
}
Success response
Response flag (1 byte) [0x00]
Error response
Response flag (1 byte) [Non zero value]
Light ON/OFF packet (Server to Gateway)
55 AA 55 AA 01 00 00 00 07 85 00 06 00 08 01 00 00 01 00 00 18 B3 2C
55 AA 55 AA
SOF
Protocol version
Flag
00 00 07 85
Gateway identifier
DSN
Command identifier
00 08
Payload length
Operation Type
ON_OFF = 1,
00 00
Operation Value
If operation type = ON_OFF
ON = 1,
OFF = 0
Node Count
00 00 18 B3
Node id
2C
CRC
Light ON/OFF packet (Gateway to Server)
55 AA 55 AA 01 00 00 00 07 85 00 00 00 00 2E
55 AA 55 AA
SOF
Protocol version
Flag
00 00 07 85
Gateway identifier
DSN
Command identifier
00 00
Payload length
2E
CRC
Error response flags
This section list down all the error response flags:
Response code
Description
Corrective action to be taken
0x01
Unknown gateway.
Reason: The Gateway is not configured on the cloud interface with the serial number mentioned in the handshake request packet.
Corrective action: You have to login to the cloud application. Go to Switch Point menu
-> Add new Switch points with the
associated serial number.
0x02
Incorrect packet format.
Reason: The request packet is incorrect. Corrective action: Most of the cases – there might be issue with the calculation of the payload length and data length. Refer the Communication protocol document for
the respective request.
0x03
Unauthorized gateway.
Reason: Error at Server side when the gateway send handshake.
Corrective action: Corrective action to be
taken at the server side.
0x04
Incorrect field value in inform change request.
Reason: If because of some reason the server could not generate the inform change request.
Corrective action: Try resending the handshake command. It issue still persist then corrective action is to be taken at
server side.
0x05
Unknown file identifier.
Reason: If gateway send the incorrect combination of the file type and file Id while downloading the system configuration/ node configuration.
Corrective action: Check the file type and file id received from the server in the response of the “Inform change”. Gateway should send valid combination of the File type and file Id while downloading
configuration.
0x06
Unknown file type.
Reason: When gateway sends wrong file type in the download in it and downloads configuration details.
Corrective action: Check the file type and file id received from the server in the
response of the “Inform change”. Gateway
should send valid combination of the File type and file Id while downloading
configuration.
0x07
File not present.
Reason: Requested configuration file is not present on server. Most of the time this issue observed for the firmware download. Corrective action: Corrective action to be
taken at server side.
0x08
Not used.
0x09
No handshake done before. This is generated if gateway sends any other packet without sending handshake request.
Reason: If gateway send any other request without sending the handshake request.
Gateway restarts and sends the Download configuration request, without handshake. Corrective action: The gateway should sent the handshake request after reboot or when it establish initial connection with the
server.
0x0A
Node operation error.
Reason: Request packet generation error, when user tries to control the lights from the server.
Corrective action: Action required at the
server side.
0x0B
Incorrectfileversion.Whengateway
uploads data with incorrect file version.
0x0C
Incorrect op-code.
0x0D
Incorrect event identifier. Gateway uploads
event to the server with incorrect event identifier.
Opcode parsing
This section explains the parsing of various OPCODES.
Opcode1061 (Type A)
Field
Bytes
Remarks
Slave ID
1 byte
Function code
1 byte
Frequency
4 byte
Skip bytes
4 byte
Skip bytes
4 byte
Skip bytes
4 byte
Skip bytes
4 byte
R phase voltage
4 byte
Y phase voltage
4 byte
B phase voltage
4 byte
3 phase eq current
4 byte
Current Line 1
4 byte
Current Line 2
4 byte
Current Line 3
4 byte
3 phase eq power
4 byte
Power factor 1
4 byte
Power factor 2
4 byte
Power factor 3
4 byte
Active power
4 byte
Apparent power
4 byte
Reactive power
4 byte
Skip byte
4 byte
Skip byte
4 byte
Skip byte
4 byte
Skip byte
4 byte
KWH Total
4 byte
Skip byte
4 byte
Skip byte
4 byte
Earth Tamper
4 byte
Reverse Tamper
4 byte
Cover open Tamper
4 byte
Magnetic Tamper
4 byte
Current MD
4 byte
Year
1 byte
Month
1 byte
Day
1 byte
Hour
1 byte
Min
1 byte
Prev MD`
4 byte
Year
1 byte
Month
1 byte
Day
1 byte
Hour
1 byte
Min
1 byte
Opcode 1073 (Type B)
Field
Bytes
Remarks
Start byte
Length 1
Remove as string
Data length
Length 2
Remove as string
Function code
Length 2
Remove as string
Meter serial number
Length 8
Remove as string
RTC date ( date part)
Length 6
Remove as string
RTC date ( time part)
Length 6
Remove as string
Instantaneous Voltage
Length 3
Remove as string
Instantaneous Current
Length 5
Remove as string
Instantaneous Power
Length 7
Remove as string
Instantaneous Power Factor
Length 6
Remove as string
Cumulative Active Energy
Length 7
Remove as string
LBP1 Cumulative Active Energy
Length 7
Remove as string
LBP2 Cumulative Active Energy
Length 7
Remove as string
LBP3 Cumulative Active Energy
Length 7
Remove as string
LBP4 Cumulative Active Energy
Length 7
Remove as string
LBP5 Cumulative Active Energy
Length 7
Remove as string
LBP6 Cumulative Active Energy
Length 7
Remove as string
CT Reverse Tamper Status
Length 1
Remove as string
Earth Load Tamper Status
Length 1
Remove as string
Cover Open Tamper Status
Length 1
Remove as string
Magnetic Influence Tamper Status
Length 1
Remove as string
Single Wire Tamper Status
Length 1
Remove as string
Skip byte Checksum
Length 4
Remove as string
Skip byte End byte
Length 2
Remove as string
