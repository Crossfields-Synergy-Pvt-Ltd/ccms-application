package com.vetsoft.ccms;

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
		if (gateway_serial_number == null || gateway_serial_number.trim().length() == 0 || ctx == null) {
			return null;
		}
		synchronized (channels_list) {
			ChannelDetails prv_ch_obj = channels_list.get(gateway_serial_number);
			if(prv_ch_obj != null) {
				if (current_client_ip != null && current_client_ip.equals(prv_ch_obj.ip)) {
				/*
				 * on Channel disconnect and after reconnect CTX will be updated
				 */
				ChannelDetails current_obj = channels_list.remove(gateway_serial_number);
				current_obj.ctx = ctx;
				current_obj.ip = current_client_ip;
				channels_list.put(gateway_serial_number, current_obj);
				return null;
				} else {
				ChannelDetails new_ch_obj = new ChannelDetails();
				new_ch_obj.ctx = ctx;
				new_ch_obj.ip = current_client_ip;
				hafe_opened_channels_list.put(gateway_serial_number, prv_ch_obj);
				channels_list.put(gateway_serial_number, new_ch_obj);
				return prv_ch_obj.ctx;
			}
			} else {
			ChannelDetails new_ch_obj = new ChannelDetails();
			new_ch_obj.ctx = ctx;
			new_ch_obj.ip = current_client_ip;
			channels_list.put(gateway_serial_number, new_ch_obj);
			}
		}
		return null;
	}

	/** Removes only registrations owned by the supplied channel. */
	public static void removeChannel(ChannelHandlerContext ctx) {
		if (ctx == null) {
			return;
		}
		synchronized (channels_list) {
			for (String key : channels_list.keySet()) {
				ChannelDetails details = channels_list.get(key);
				if (details != null && details.ctx == ctx) {
					channels_list.remove(key, details);
				}
			}
			for (String key : hafe_opened_channels_list.keySet()) {
				ChannelDetails details = hafe_opened_channels_list.get(key);
				if (details != null && details.ctx == ctx) {
					hafe_opened_channels_list.remove(key, details);
				}
			}
		}
	}
	 
}
