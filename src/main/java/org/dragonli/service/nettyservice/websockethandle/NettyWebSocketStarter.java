/**
 * 
 */
package org.dragonli.service.nettyservice.websockethandle;

import org.dragonli.service.nettyservice.NettyService;
import org.dragonli.service.nettyservice.NettyServiceConfig;

/**
 * @author freeangel
 *
 */
public class NettyWebSocketStarter {

	public static NettyService startNetty(int port,INettySocketHandler handler) throws Exception
	{
		NettyServiceConfig config = new NettyServiceConfig(port,handler);
		return startNetty(config);
//		return null;
	}
	
	public static NettyService startNetty(NettyServiceConfig config) throws Exception
	{
		return new NettyService(config);
	}
}
