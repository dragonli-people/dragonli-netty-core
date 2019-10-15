/**
 * 
 */
package org.dragonli.service.nettyservice.websockethandle;

import io.netty.channel.Channel;

/**
 * @author freeangel
 *
 */
public class NettySocketSession {
	private String uuid;
	private int uid;
	private int pageId;//也许有uuid就可以了
	private int moduleId;
	private String ip;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	private Channel channel;
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public int getModuleId() {
		return moduleId;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
	private String serverName;
	
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	

	public void clear()
	{
		this.channel = null;
	}
}
