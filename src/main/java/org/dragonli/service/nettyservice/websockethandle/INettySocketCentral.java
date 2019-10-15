/**
 * 
 */
package org.dragonli.service.nettyservice.websockethandle;

import java.util.Map;

/**
 * @author freeangel
 *
 */
public interface INettySocketCentral {
	public void send(Object msg,Map<String,Object> para);
	public void close(Map<String,Object> para);
}
