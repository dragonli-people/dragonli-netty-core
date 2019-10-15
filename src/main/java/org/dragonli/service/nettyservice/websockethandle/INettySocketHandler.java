/**
 * 
 */
package org.dragonli.service.nettyservice.websockethandle;

import java.util.Map;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author freeangel
 *
 */
public interface INettySocketHandler {
	void setCentral(INettySocketCentral central);
	void notifyConnected(HttpRequest req,Channel one) throws Exception;
	void notifyClosed(Channel one)  throws Exception;
	String receive(Channel channel,String msg);
	boolean send(Object msg,int moduleId,Map<String,Object> para);
	boolean send(String uuid,String msg,int moduleId);
	int sendToAll(String msg,int moduleId);
	void close(int moduleId,Map<String,Object> para);
	boolean valicodeSocket(HttpRequest req) throws Exception;
	int getConnectCount();
}
