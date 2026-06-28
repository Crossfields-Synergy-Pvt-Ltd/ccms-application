package com.vetsoft.ccms;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.vetsoft.ccms.netty.server.ChannelDetails;
import com.vetsoft.ccms.netty.server.ServerHandler;


@SpringBootApplication
public class MainBootApp implements CommandLineRunner {

	public static ConfigurableApplicationContext context;
	
	
	public static ConcurrentHashMap <String, ChannelDetails> channels_list = new ConcurrentHashMap <String, ChannelDetails>();

	public static ConcurrentHashMap <String, ChannelDetails> hafe_opened_channels_list = new ConcurrentHashMap <String, ChannelDetails>();

	@Autowired
	ServerHandler serverHandler;
	
	public static void main(String[] args) {
	
	
		 
		context = new FileSystemXmlApplicationContext("file:/home/CCMS/roadmap/conf/applicationContext.xml");
		// context = new FileSystemXmlApplicationContext("file:E://ccms//ccms//conf//applicationContext.xml");
		
		SpringApplication.run(MainBootApp.class, args);
	
	}
	
	@Override
	 public void run(String... param) throws UnsupportedEncodingException {
		
	 
	 }

	public static ChannelHandlerContext trackChannels(String gateway_serial_number, String current_client_ip, ChannelHandlerContext ctx) {
	//	System.out.println("TRACK CHANNEL : "+ gateway_serial_number);
	//	System.out.println("LIST SIZE : "+ channels_list);
		if(channels_list.containsKey(gateway_serial_number)) {
			System.out.println("EXIST IN LIST .. ");
			ChannelDetails prv_ch_obj = channels_list.get(gateway_serial_number);
			if(prv_ch_obj.ip.equals(current_client_ip)) {
				/*
				 * on Channel disconnect and after reconnect CTX will be updated
				 */
				ChannelDetails current_obj = channels_list.remove(gateway_serial_number);
				current_obj.ctx = ctx;
				current_obj.ip = current_client_ip;
				channels_list.put(gateway_serial_number, current_obj);
				System.out.println("IP ADDRESS MATCHING : "+ prv_ch_obj.ip + " | "+ current_client_ip);
				return null;
			} else {
				System.out.println("NEW CONNECTION WITH IP DEFRRENT IP : "+ prv_ch_obj.ip + " | "+ current_client_ip);
				ChannelDetails new_ch_obj = new ChannelDetails();
				new_ch_obj.ctx = ctx;
				new_ch_obj.ip = current_client_ip;
				hafe_opened_channels_list.put(gateway_serial_number, prv_ch_obj);
				channels_list.put(gateway_serial_number, new_ch_obj);
				try{
					/* ITS HAFE OPENED CONNECTIOS
					 * as per test observation when network ip changes device was creatting new connection witout 
					 * closing prev connection so we are checking ip address and trying to close same, to achive 1 connection per client/device
					 */
					
					prv_ch_obj.ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) .addListener(ChannelFutureListener.CLOSE);;
					
				
					prv_ch_obj.ctx.channel().closeFuture();
					prv_ch_obj.ctx.channel().deregister();
					prv_ch_obj.ctx.channel().pipeline().close();
					prv_ch_obj.ctx.channel().disconnect();
					prv_ch_obj.ctx.channel().close();
					prv_ch_obj.ctx.channel().flush();
					prv_ch_obj.ctx.close();
					prv_ch_obj.ctx.flush();
				
					System.out.println("ERROR :: OLD CONNECTION CLOSED : "+prv_ch_obj.ip );
					return prv_ch_obj.ctx;
				} catch(Exception e){
					System.out.println("Exception while clossing old connection : "+ prv_ch_obj.ip);
				}
			}
		} else {
			System.out.println("NEW CONNECTION : "+ current_client_ip);
			ChannelDetails new_ch_obj = new ChannelDetails();
			new_ch_obj.ctx = ctx;
			new_ch_obj.ip = current_client_ip;
			channels_list.put(gateway_serial_number, new_ch_obj);
		}
		
		return null;
	}
	 
}