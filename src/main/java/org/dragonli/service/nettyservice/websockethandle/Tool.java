/**
 * 
 */
package org.dragonli.service.nettyservice.websockethandle;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author freeangel
 *
 */
public class Tool
{
	
	public static boolean ifWebsocketInitRequest(final HttpRequest req)
	{
		if( HttpMethod.GET.equals( req.method() ) && "websocket".equalsIgnoreCase( req.headers().get("Upgrade") ) )
            return true;
		return false;
	}
}
