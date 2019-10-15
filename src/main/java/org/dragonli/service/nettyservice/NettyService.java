/**
 * 
 */
package org.dragonli.service.nettyservice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.dragonli.service.nettyservice.websockethandle.INettySocketHandler;
import org.dragonli.service.nettyservice.websockethandle.NettyWebSocketCentral;

import java.util.ArrayList;
import java.util.List;

/**
 * @author freeangel
 *
 */
public class NettyService extends Thread
{
	public static final Logger logger = Logger.getLogger(NettyService.class);
	
	protected NettyServiceConfig config;
	public List<ChannelHandler> channelHandlers;
	
	public NettyService() throws Exception
	{
		//need call init next
	}
	
	public NettyService(NettyServiceConfig config) throws Exception
	{
		init(config);
	}
	
	public void init(NettyServiceConfig config) throws Exception
	{
		if( config == null )
			throw new Exception("netty service config cant ba null!");
		
		this.config = config;
		
		this.channelHandlers = new ArrayList<ChannelHandler>();
		
		if( config.isUseWebSocketService() )
			channelHandlers.add(createNettyWebSocketCentral(config.getSocketHandler()));
		
		this.start();
	}
	
	protected NettyWebSocketCentral createNettyWebSocketCentral(INettySocketHandler handler) throws Exception
	{
		return new NettyWebSocketCentral(config.getSocketHandler());
	}
	
	@Override public void run()
	{
		initNettyServer();
	}
	
	/**
	 * init initWebSocketServer
	 */
	public void initNettyServer() {
		final EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workderGroup = new NioEventLoopGroup();
		try {

			ServerBootstrap pushServer = new ServerBootstrap();
			pushServer.group(bossGroup, workderGroup)
					.channel(NioServerSocketChannel.class)// 用它来建立新accept的连接，用于构造serversocketchannel的工厂类
					.childHandler(new NettyServerInitializer(channelHandlers,config));
			final Channel channel = pushServer.bind(config.getNettyPort()).sync().channel();
			logger.info("===init initWebSocketServer success : " + config.getNettyPort() + "===");
			channel.closeFuture().sync();
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		} 
		finally 
		{
			bossGroup.shutdownGracefully();
			workderGroup.shutdownGracefully();
		}
	}
}
