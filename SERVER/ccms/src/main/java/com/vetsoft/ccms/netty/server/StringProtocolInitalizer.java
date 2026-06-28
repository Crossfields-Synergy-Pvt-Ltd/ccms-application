package com.vetsoft.ccms.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("springProtocolInitializer")
public class StringProtocolInitalizer extends ChannelInitializer<SocketChannel> {


	@Autowired
	ServerHandler serverHandler;

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast("handler", serverHandler);
		
	}



	public ServerHandler getServerHandler() {
		return serverHandler;
	}

	public void setServerHandler(ServerHandler serverHandler) {
		this.serverHandler = serverHandler;
	}

}
