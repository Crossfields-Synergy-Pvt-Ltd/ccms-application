package com.vetsoft.ccms.netty.server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vetsoft.ccms.MainBootApp;
import com.vetsoft.ccms.netty.cfg.Constants;
import com.vetsoft.ccms.netty.cfg.GWErrorResponse;
import com.vetsoft.ccms.netty.parser.BaseUtil;
import com.vetsoft.ccms.netty.parser.events.EventRequestHandler;
import com.vetsoft.ccms.netty.parser.hs.HandShakeParser;
import com.vetsoft.ccms.netty.parser.ping.PingRequestParser;
import com.vetsoft.ccms.netty.pojo.CommonHeader;
import com.vetsoft.ccms.netty.pojo.EventStatusRequest;
import com.vetsoft.ccms.netty.pojo.HandShake;
import com.vetsoft.ccms.netty.push.ConfigurationDownloadHandler;
import com.vetsoft.ccms.netty.push.ConfigurationINITHandler;


@Component
@Qualifier("serverHandler")
@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	
	
	public static Logger LOG =  LoggerFactory.getLogger(ServerHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		String response = "";
		try {
			
			
			ByteBuf inBuffer = (ByteBuf) msg;
			
			String received_hex = inBuffer.toString(CharsetUtil.ISO_8859_1);
			
			 
			String sTextAsHex = BaseUtil.getHexString(received_hex);
	
			if(LOG.isDebugEnabled())
				LOG.debug("\n================================================== NEW REQUEST =======================================");
						
			if(LOG.isDebugEnabled())
				LOG.debug(sTextAsHex);
			
			CommonHeader obj = BaseUtil.getCommonHeader(sTextAsHex);
			if(!BaseUtil.validatPacket(sTextAsHex))
			{
				if(LOG.isDebugEnabled())
					LOG.debug("INVALIDE PACKET"); // whats should be the response check with anil
				/*ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) .addListener(ChannelFutureListener.CLOSE);;
				ctx.close();*/
				response = GWErrorResponse.getERRORResponse(sTextAsHex, obj, "02");
				response = BaseUtil.convertHexToString(response);
				ctx.writeAndFlush(Unpooled.copiedBuffer(
				          response, CharsetUtil.ISO_8859_1));
				
				ctx.flush();
			} else {
				
		
				
			if(LOG.isDebugEnabled()){
				LOG.debug("CMMON OBJ : "+ obj);
			}
			
			if(obj.getCommand_identifier() == Constants.CI_Handshake_request_Gateway_Server){
				
				
				HandShake hs_req = HandShakeParser.parseRequest(sTextAsHex, obj);
				
				response = HandShakeParser.getHandshakeResponseData(sTextAsHex, hs_req);
				
				if(LOG.isDebugEnabled()){
					LOG.debug(response);
				}
				response = BaseUtil.convertHexToString(response);
				
				ChannelHandlerContext prv_ctx =	MainBootApp.trackChannels(hs_req.getGateway_serial_number(), ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress(), ctx);
				if(prv_ctx != null){
					try {
						prv_ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) .addListener(ChannelFutureListener.CLOSE);;
						prv_ctx.close();
						prv_ctx.flush();
						super.channelInactive(prv_ctx);
						super.channelUnregistered(prv_ctx);
						//LOG.error("INFO :: CLOSING OLD CONNECTION");
					}catch(Exception e){
						LOG.error("ERROR :: Exception while closing old connection : "+ e.getMessage());
					}
				}
				
				ctx.writeAndFlush(Unpooled.copiedBuffer(
				          response, CharsetUtil.ISO_8859_1));
				
				ctx.flush();
				
			} else if(obj.getCommand_identifier() == Constants.CI_Status_Gateway_Server){
				if(LOG.isDebugEnabled()){
					LOG.debug("EVENTS / STATUS REQUEST FROM G/W");
				}
				
				EventStatusRequest event_status_request = new EventStatusRequest();
				event_status_request.setCommand_identifire(obj.getCommand_identifier());
				event_status_request.setProtocol_version(obj.getProtocol_version());
				event_status_request.setFlag(obj.getFlag());
				event_status_request.setGateway_identifier(obj.getGateway_identifier());
				event_status_request.setDsn(obj.getDsn());
				event_status_request.setCommand_identifire(obj.getCommand_identifier());
				event_status_request.setPayload_length(obj.getPayload_length());
				
				
				event_status_request = EventRequestHandler.handleEventRequest(sTextAsHex, event_status_request);
				
				
				response = EventRequestHandler.getResponseBuffer(sTextAsHex, obj, event_status_request);
				
				if(LOG.isDebugEnabled()) {
					LOG.debug(event_status_request.toString());
					LOG.debug("RESPONSE : " + response);
				}
				
				response = BaseUtil.convertHexToString(response);
				ctx.writeAndFlush(Unpooled.copiedBuffer(
				          response, CharsetUtil.ISO_8859_1));
				
				ctx.flush();
			} else if(obj.getCommand_identifier() == Constants.CI_Download_init_Request_Packet_Gateway_Server){
				if(LOG.isDebugEnabled())
					LOG.debug("DOWNLOAD INIT REQUEST FROM G/W");
				response = ConfigurationINITHandler.processConfigurationDownloadInitRequest(sTextAsHex, obj);
				
				if(LOG.isDebugEnabled())
					LOG.debug("DOWNLOAD INIT RESPONSE : "+ response);
				response = BaseUtil.convertHexToString(response);
				
				ByteBuf tmp = Unpooled.copiedBuffer(
				          response, CharsetUtil.ISO_8859_1);
				
				String tmpreceived_hex = tmp.toString(CharsetUtil.ISO_8859_1);
				
				 
				String tmp_sTextAsHex = BaseUtil.getHexString(tmpreceived_hex);
				if(LOG.isDebugEnabled())
					LOG.debug("SENT OVER NETWORK : " + tmp_sTextAsHex);
				ctx.writeAndFlush(Unpooled.copiedBuffer(
				          response, CharsetUtil.ISO_8859_1));
				
				ctx.flush();
				
			} else if(obj.getCommand_identifier() == Constants.CI_Download_Content_Gateway_Server){
				if(LOG.isDebugEnabled())
					LOG.debug("DOWNLOAD CONTENT REQUEST FROM G/W");
				
				response = ConfigurationDownloadHandler.processDownloadContentRequest(sTextAsHex);
				response = BaseUtil.convertHexToString(response);
				ctx.writeAndFlush(Unpooled.copiedBuffer(
				          response, CharsetUtil.ISO_8859_1));
				
				ctx.flush();
				
			} else if(obj.getCommand_identifier() == Constants.CI_PingRequest){
				if(LOG.isDebugEnabled())
					LOG.debug("PING REQUEST ");
			
				PingRequestParser ping = new PingRequestParser();
				response = ping.getPingResponse(sTextAsHex, obj);
			
				response = BaseUtil.convertHexToString(response);
				ctx.writeAndFlush(Unpooled.copiedBuffer(
				          response, CharsetUtil.ISO_8859_1));
				
				ctx.flush();
			}  else {
				LOG.error("OTHER COMMAND : "+ obj.getCommand_identifier());
			
			}
			
			
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("CHANNEL :: NO OF CONNECTED CHANNELS : " + MainBootApp.channels_list.size());
				LOG.debug("CHANNEL NAME :: "+ ctx.name());
				LOG.debug("CHANNEL ACTIVE :: "+ ctx.channel());
				LOG.debug("CHANNEL OPEN :: "+ ctx.channel().isOpen());
				LOG.debug("CHANNEL WRITTABLE :: "+ ctx.channel().isWritable());
				LOG.debug(" CLIENT IP ADDRESS :: "+ ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress());
			/*	if(MainBootApp.channels_list.get(obj.getGateway_identifier()).equals(ctx)){
					LOG.debug("CHANNEL :: SAME CHANNEL ");
				} else {
					LOG.debug("CHANNEL :: DIFFRENT CHANNEL ");
				}*/
				
				try {
					
					Set<String> keys = MainBootApp.channels_list.keySet();
					
					for(String key : keys){
						
						ChannelHandlerContext channel = MainBootApp.channels_list.get(key).ctx;
						LOG.debug("===================");
						LOG.debug("CHANNEL KEY :: "+ key);
						LOG.debug("CHANNEL NAME :: "+ channel.name());
						LOG.debug("CHANNEL ACTIVE :: "+ channel.channel().isActive());
						LOG.debug("CHANNEL OPEN :: "+ channel.channel().isOpen());
						LOG.debug("CHANNEL WRITTABLE :: "+ channel.channel().isWritable());
						
					
						
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					LOG.error("Exception :: "+e.getStackTrace());
					ctx.writeAndFlush(Unpooled.copiedBuffer(
					          response, CharsetUtil.ISO_8859_1));
					
					ctx.flush();
				}
			}
			
			
			}
			inBuffer.release();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.error("Exception :: "+e.getStackTrace());
			ctx.writeAndFlush(Unpooled.copiedBuffer(
			          response, CharsetUtil.ISO_8859_1));
			
			ctx.flush();
		}
	}



	public void sendMessageToClient(String packet, String channel_id, int conf_type) {
		
		try {
				ConfigurationINITHandler.saveConfChangeRequestData(packet, channel_id, conf_type);
				LOG.debug("MESSAGE TO CLIENT " + channel_id);
				ChannelHandlerContext tmp = MainBootApp.channels_list.get(channel_id).ctx;
				LOG.debug("PACKET : "+ packet);
				if(tmp != null){
					LOG.info("DEVICE IS ONLINE SYSNC SYS CONFIGURATIONS : "+ channel_id + " DEVICE IP : "+ MainBootApp.channels_list.get(channel_id).ip + " DEVICE STATUS "+ tmp.channel().isActive());
					tmp.writeAndFlush(Unpooled.copiedBuffer(
							BaseUtil.convertHexToString(packet), CharsetUtil.ISO_8859_1));
				} else {
					LOG.error("ERROR :: DEVICE IS OFFLINE UNABLE TO SYNC THE SYS CONF : "+ channel_id);
				}
		}catch(Exception e){
			LOG.error("Exception  : "+ e.getMessage() + channel_id);
		}
	}

	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {

		LOG.error("CONNECTION CLOSED BY CLIENT");
		MainBootApp.channels_list.remove(ctx);
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) .addListener(ChannelFutureListener.CLOSE);;
		ctx.close();
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if(LOG.isDebugEnabled())
			LOG.debug("Channel is active\n");
		
		
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(LOG.isDebugEnabled())
			LOG.debug("Channel is disconnected");
		
		try {
		MainBootApp.channels_list.remove(ctx);
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) .addListener(ChannelFutureListener.CLOSE);;
		

		ctx.channel().closeFuture();
		ctx.channel().deregister();
		ctx.channel().pipeline().close();
		ctx.channel().disconnect();
		ctx.channel().close();
		ctx.channel().flush();
		ctx.close();
		ctx.flush();
		
		super.channelInactive(ctx);
		}catch(Exception e){
			LOG.error("Exception  while disconnecting channel: "+ e.getMessage());
		}
		}


	public void closeHafeOpenedConnections(ChannelHandlerContext ctx)
			throws Exception {
	
		try {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) .addListener(ChannelFutureListener.CLOSE);;
	
	
		
		ctx.channel().closeFuture();
		ctx.channel().deregister();
		ctx.channel().pipeline().close();
		ctx.channel().disconnect();
		ctx.channel().close();
		ctx.channel().flush();
		ctx.disconnect();
		ctx.close();
		ctx.flush();
		
		LOG.error("CLOSING HAFE OPENED CONNECTION ");
		}catch(Exception e){
			LOG.error("EXCEPTION WHILE CLOSING  HAFE OPENED CONNECTION "+ e.getMessage());
		}
	}

/*	public void userMessage() {
		System.out.println("USER MESSAGE");
	}
*/

	public void sendNodeConfigurationChangeRequestToClient(
			String chnage_request_packet, String dcu_id, String file_idetifier,
			String node_data, String dcu_identifier) {
		try {
			ConfigurationINITHandler.saveNodeConfRequestData(node_data, dcu_id,
					file_idetifier, dcu_identifier);
			LOG.debug("MESSAGE TO CLIENT " + dcu_id);
			ChannelHandlerContext tmp = MainBootApp.channels_list.get(dcu_id).ctx;
			LOG.debug("PACKET : " + chnage_request_packet + " FILE ID "
					+ file_idetifier);
			if (tmp != null) {
				LOG.info("DEVICE IS ONLINE SYSNC NODE CONFIGURATIONS : "
						+ dcu_id + " DEVICE IP : "
						+ MainBootApp.channels_list.get(dcu_id).ip
						+ " DEVICE STATUS " + tmp.channel().isActive());
				tmp.writeAndFlush(Unpooled.copiedBuffer(
						BaseUtil.convertHexToString(chnage_request_packet),
						CharsetUtil.ISO_8859_1));
			} else {
				LOG.error("ERROR :: DEVICE IS OFFLINE UNABLE TO SYNC THE SYS CONF : "
						+ dcu_id);
			}
		} catch (Exception e) {
			LOG.error("Exception  : " + e.getMessage() + dcu_id);
		}
	}
	
	public void sendDcuConfSyncMesageToClient(
			String chnage_request_packet, String dcu_id, String file_idetifier,
			String node_data, String dcu_identifier) {
			
			try {
					ConfigurationINITHandler.saveDcuConfRequestData(node_data, dcu_id,
							file_idetifier, dcu_identifier);
					LOG.debug("MESSAGE TO CLIENT " + dcu_id);
					ChannelHandlerContext tmp = MainBootApp.channels_list.get(dcu_id).ctx;
					LOG.debug("PACKET : "+ chnage_request_packet);
					if(tmp != null){
						LOG.info("DEVICE IS ONLINE SYSNC SYS CONFIGURATIONS : "+ dcu_id + " DEVICE IP : "+ MainBootApp.channels_list.get(dcu_id).ip + " DEVICE STATUS "+ tmp.channel().isActive());
						tmp.writeAndFlush(Unpooled.copiedBuffer(
								BaseUtil.convertHexToString(chnage_request_packet), CharsetUtil.ISO_8859_1));
					} else {
						LOG.error("ERROR :: DEVICE IS OFFLINE UNABLE TO SYNC THE SYS CONF : "+ dcu_id);
					}
			}catch(Exception e){
				LOG.error("Exception  : "+ e.getMessage() + dcu_id);
			}
		}



	public void sendSchedulerConfigurationChangeRequestToClient(String chnage_request_packet,
			String dcu_id, String file_idetifier, String node_data,
			String dcu_identifier) {

		try {
			ConfigurationINITHandler.saveSchedulerConfRequestData(node_data, dcu_id,
					file_idetifier, dcu_identifier);
			LOG.debug("MESSAGE TO CLIENT " + dcu_id);
			ChannelHandlerContext tmp = MainBootApp.channels_list.get(dcu_id).ctx;
			LOG.debug("PACKET : " + chnage_request_packet + " FILE ID "
					+ file_idetifier);
			if (tmp != null) {
				LOG.info("DEVICE IS ONLINE SYSNC NODE CONFIGURATIONS : "
						+ dcu_id + " DEVICE IP : "
						+ MainBootApp.channels_list.get(dcu_id).ip
						+ " DEVICE STATUS " + tmp.channel().isActive());
				tmp.writeAndFlush(Unpooled.copiedBuffer(
						BaseUtil.convertHexToString(chnage_request_packet),
						CharsetUtil.ISO_8859_1));
			} else {
				LOG.error("ERROR :: DEVICE IS OFFLINE UNABLE TO SYNC THE SYS CONF : "
						+ dcu_id);
			}
		} catch (Exception e) {
			LOG.error("Exception  : " + e.getMessage() + dcu_id);
		}
	
	}



	public void sendManuvalOnOffRequest(String buffer, String dcu_id) {
		try {
			
			LOG.debug("MESSAGE TO CLIENT " + dcu_id);
			ChannelHandlerContext tmp = MainBootApp.channels_list.get(dcu_id).ctx;
			LOG.debug("PACKET : " + buffer);
			if (tmp != null) {
				LOG.info("DEVICE IS ONLINE SYSNC MANUVAL ON/OFF CONFIGURATIONS : "
						+ dcu_id + " DEVICE IP : "
						+ MainBootApp.channels_list.get(dcu_id).ip
						+ " DEVICE STATUS " + tmp.channel().isActive());
				/*ByteBuf data = Unpooled.copiedBuffer(
						BaseUtil.convertHexToString(buffer),
						CharsetUtil.ISO_8859_1);
				//String sTextAsHex =  BaseUtil.byetArrayToHex(data.array());
				String received_hex = data.toString(CharsetUtil.ISO_8859_1);
				
				 
				String sTextAsHex = BaseUtil.getHexString(received_hex);
				
				
				LOG.debug("DATA " + sTextAsHex);*/
				tmp.writeAndFlush(Unpooled.copiedBuffer(
						BaseUtil.convertHexToString(buffer),
						CharsetUtil.ISO_8859_1));
			} else {
				LOG.error("ERROR :: DEVICE IS OFFLINE UNABLE TO SYNC THE  MANUVAL ON/OFF CONFIGURATIONS : "
						+ dcu_id);
			}
			
		} catch (Exception e) {
			LOG.error("Exception  : " + e.getMessage() + dcu_id);
		}
		
	}

	

}