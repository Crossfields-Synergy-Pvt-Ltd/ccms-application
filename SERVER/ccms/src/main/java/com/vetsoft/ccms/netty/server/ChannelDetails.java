package com.vetsoft.ccms.netty.server;

import io.netty.channel.ChannelHandlerContext;

public class ChannelDetails {

	public ChannelHandlerContext ctx;
	public String ip;
	@Override
	public String toString() {
		return "ChannelDetails [ctx=" + ctx.channel().isActive() + ", ip=" + ip + "]";
	}
	
	
}
