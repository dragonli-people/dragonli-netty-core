package org.dragonli.service.nettyservice;

import java.util.List;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyServerInitializer extends
		ChannelInitializer<SocketChannel> {
	
	private List<ChannelHandler> channelHandlers;
	private NettyServiceConfig config;
	public NettyServerInitializer(List<ChannelHandler> channelHandlers,NettyServiceConfig config)
	{
		this.channelHandlers = channelHandlers;
		this.config = config;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HttpServerCodec());
		// 聚合http片段
		pipeline.addLast(new HttpObjectAggregator(config.getHttpObjectAggregatorSize()));//config.getHttpObjectAggregatorSize()));
		// 支持HTML5 文本传输
		pipeline.addLast(new ChunkedWriteHandler());
		for( ChannelHandler current : channelHandlers )
			pipeline.addLast(current);
//		pipeline.addLast(new HttpServerCodec());
//		// 聚合http片段
//		pipeline.addLast(new HttpObjectAggregator(65536));
//		// 支持HTML5 文本传输
//		pipeline.addLast(new ChunkedWriteHandler());
//		pipeline.addLast(new WebSocketHandler());
//		pipeline.addLast(new HttpServiceHandler());
	}
}
