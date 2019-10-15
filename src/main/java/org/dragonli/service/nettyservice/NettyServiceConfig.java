/**
 * 
 */
package org.dragonli.service.nettyservice;

import org.dragonli.service.nettyservice.websockethandle.INettySocketHandler;

import java.util.Map;


/**
 * @author freeangel
 *
 */
public class NettyServiceConfig {

	private int nettyPort = 8090;
	private int httpObjectAggregatorSize = 65536;
	private int maxFramePayloadLength = 65536;
	
	private boolean useWebSocketService;

	private Map<String,String> httpHandlers;
	private INettySocketHandler socketHandler;
	
	public NettyServiceConfig(int port, INettySocketHandler handler)
	{
		this.nettyPort = port;
		this.socketHandler = handler;
		this.useWebSocketService = this.socketHandler != null;
	}
	
	public INettySocketHandler getSocketHandler() {
		return socketHandler;
	}

	public void setSocketHandler(INettySocketHandler socketHandler) {
		this.socketHandler = socketHandler;
		useWebSocketService = socketHandler != null;
	}

	public Map<String, String> getHttpHandlers() {
		return httpHandlers;
	}

	public void setHttpHandlers(Map<String, String> httpHandlers) {
		this.httpHandlers = httpHandlers;
		useHttpService = httpHandlers != null && httpHandlers.size() != 0;
	}
	
	public int getHttpObjectAggregatorSize() {
		return httpObjectAggregatorSize;
	}

	public void setHttpObjectAggregatorSize(int httpObjectAggregatorSize) {
		this.httpObjectAggregatorSize = httpObjectAggregatorSize;
	}

	public boolean isUseWebSocketService() {
		return useWebSocketService;
	}

	public void setUseWebSocketService(boolean useWebSocketService) {
		this.useWebSocketService = useWebSocketService;
	}

	public boolean isUseHttpService() {
		return useHttpService;
	}

	public void setUseHttpService(boolean useHttpService) {
		this.useHttpService = useHttpService;
	}

	private boolean useHttpService;

	public int getNettyPort() {
		return nettyPort;
	}

	public void setNettyPort(int nettyPort) {
		this.nettyPort = nettyPort;
	}

	public int getMaxFramePayloadLength() {
		return maxFramePayloadLength;
	}

	public void setMaxFramePayloadLength(int maxFramePayloadLength) {
		this.maxFramePayloadLength = maxFramePayloadLength;
	}
}
