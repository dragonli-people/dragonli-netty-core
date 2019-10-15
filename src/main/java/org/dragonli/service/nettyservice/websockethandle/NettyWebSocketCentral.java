package org.dragonli.service.nettyservice.websockethandle;

import java.util.Map;

import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;
import org.dragonli.service.nettyservice.NettyServiceConfig;

@Sharable//NettyWebSocketCentral是无状态的
public class NettyWebSocketCentral extends ChannelInboundHandlerAdapter implements INettySocketCentral {
	public static Logger logger = Logger.getLogger(NettyWebSocketCentral.class);
//	private WebSocketServerHandshaker handshaker = null;
//	private static final AttributeKey<String> channelIdAttrKey =  AttributeKey.valueOf("channelId");
	private INettySocketHandler handler;
	protected NettyServiceConfig nettyServiceConfig;
	public NettyWebSocketCentral(INettySocketHandler handler) throws Exception
	{
		if( handler == null )
			throw new Exception("NettySocketHandler cant be null when u try add web socket service in netty");
		this.handler = handler;
		handler.setCentral(this);
	}
	
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		if (msg instanceof FullHttpRequest && Tool.ifWebsocketInitRequest((FullHttpRequest)msg)) {
			// 处理HTTP请求
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		} else if (msg instanceof WebSocketFrame ) {
//			logger.info("socket channelRead :  msg:"+msg.toString());
			handleWebSocketRequest(ctx, (WebSocketFrame) msg);
		} 
		//PingWebSocketFrame
//		PongWebSocketFrame
		else {
			// 继续流转
			ctx.fireChannelRead(msg);
		}
	}

	/**
	 * 处理Websocket 请求
	 *
	 */
	private void handleWebSocketRequest(ChannelHandlerContext ctx,
			WebSocketFrame req) {
		logger.info("socket handleWebSocket.......");
		if (req instanceof CloseWebSocketFrame) {
			logger.info("socket handleWebSocket : Frame Type-->CloseWebSocketFrame");
			ctx.channel().close();
//			handshaker.close(ctx.channel(), (CloseWebSocketFrame) req);
			return;
		}
		if (req instanceof PongWebSocketFrame) {
			logger.info("socket handleWebSocket : Frame Type-->PongWebSocketFrame");
			ctx.channel().write(new PongWebSocketFrame(req.content()));
			ctx.channel().closeFuture();
			return;
		}
		
		if (req instanceof PingWebSocketFrame) {
			logger.info("socket handleWebSocket : Frame Type-->PingWebSocketFrame");
		   ctx.channel().writeAndFlush(new PongWebSocketFrame(req.content()));  
			return ;
		}

		if(req instanceof TextWebSocketFrame){
//			logger.info("socket handleWebSocket : Frame Type-->TextWebSocketFrame:" + ((TextWebSocketFrame) req).text());
			String r = handler.receive(ctx.channel(), ((TextWebSocketFrame) req).text());
			if(r!=null && !"".equals(r))
				ctx.channel().writeAndFlush(new TextWebSocketFrame(r));
			return;
			
		}
		//图片格式
		if (!(req instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(req.getClass().getName()
					+ " frame not supported");
		}
		
		Channel ch = ctx.channel();
		ch.writeAndFlush(new TextWebSocketFrame("{'a':22}"));
		
		boolean f = true;
		if( f )
			return ;

		/*
		final TextWebSocketFrame textWSFrame = (TextWebSocketFrame) req;
		final MessageEntity message = JSONUtil.parse(textWSFrame.text(),MessageEntity.class);
		//心跳
		if (message.getType() == HttpParams.MESSAGE_TYPE_HEART_BEAT){
			ctx.channel().writeAndFlush(new TextWebSocketFrame(HttpParams.HART_BEAT));
		} else{
			message.setTime(new Date());
			message.setMsgIdServer(Utils.getRandomToken());
			// 将消息放入redis
			RedisManager.lpush(RedisConstants.CHAT_MESSAGE_KEY, JSONUtil.toJSONString(message));
			final String sessionId = message.getSessionId();
			if(StringUtils.isNotEmpty(sessionId)){
				Set<String> channelIdList = RedisManager.smembers(RedisConstants.PRE_SESSION + sessionId);
				if(!CollectionUtils.isEmpty(channelIdList)){
					for(final String channelId : channelIdList){
						//如果是在当前的机器上，直接推送过去
						Channel localChannel = WebSocketPushChannelCache.getChannelByChannelId(channelId);
						if(localChannel != null){
							ChannelFuture future = localChannel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJSONString(message)));
							future.addListener(new GenericFutureListener<Future<? super Void>>() {
									@Override
									public void operationComplete(
											Future<? super Void> future)
											throws Exception {
										logger.info("WebSocketHandler:write message success,channelId=" + channelId + ",message=" + textWSFrame.text());
										//客户端接收失败，将失败的消息放入失败队列
										if(!future.isSuccess()){
											RedisManager.lpush(RedisConstants.CHAT_MESSAGE_FAILED_KEY, channelId + FAILD_RECORD_SPLITTER + message.getMsgIdServer()+ FAILD_RECORD_SPLITTER  + sessionId);
										}
									}
							});
//							continue;
						}else {
							RedisManager.lpush(RedisConstants.CHAT_MESSAGE_FAILED_KEY, channelId + FAILD_RECORD_SPLITTER + message.getMsgIdServer()+ FAILD_RECORD_SPLITTER  + sessionId);
						}
						//如果要聊天的人不在当前的机器上，那么就通过TCP把聊天内容推过去
//						String serverHost = RedisManager.get(RedisManager.PRE_CLIENT_IP + channelId);
//						if(StringUtils.isEmpty(serverHost)){
//							continue;
//						}
//						Channel serverChannel = TcpPushChannelCache.getChannelByToken(serverHost);
//						if(serverChannel != null){
//							//要推送到的channelID
//							message.setChannelId(channelId);
//							message.setMsgIdServer(Utils.getRandomToken());
//							serverChannel.writeAndFlush(JSONUtil.toJSONString(message));
//							logger.info("message : " + message.toString() + ",wirte to  : " + serverHost);
//						}
					}
				}
			}
		}
		*/
	}

	/**
	 * 处理HTTP请求
	 *
	 */
	private void handleHttpRequest(final ChannelHandlerContext ctx,final FullHttpRequest req) {
		try 
		{
			if (!handler.valicodeSocket(req) )
			{
				//发送相关信息
				return;
			}
			
			handlerWPush(ctx, req);
		} 
		catch (Exception e) 
		{
			logger.error(e.getMessage(), e);
			if (ctx.channel().isActive()) 
				ctx.channel().close();
		}
	}

	/**
	 * 握手Websocket 的push
	 * 

	 */
	private void handlerWPush(final ChannelHandlerContext ctx,
			final FullHttpRequest req) throws Exception {
		// Handshake
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				getWebSocketLocation(ctx.pipeline(), req), null, false,nettyServiceConfig != null ? nettyServiceConfig.getMaxFramePayloadLength() : 65536 );
		WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
//		logger.info("create new websocket:"+nettyServiceConfig.getMaxFramePayloadLength());
		if (handshaker == null)
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		else 
		{
			logger.info("create new websocket ----- maxFramePayloadLength:"+handshaker.maxFramePayloadLength());
			handshaker.handshake( ctx.channel(), req); // 握手，并返回握手结果
			
			//通知外部建立socket连接
			handler.notifyConnected(req,ctx.channel());
		}

	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		logger.warn("RamoteAddress : " + ctx.channel().remoteAddress()
				+ " active !");
		
		//通知外部建立socket连接
//		handler.notifyClosed(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		handler.notifyClosed(ctx.channel());
//		boolean f = true;
//		if( f )
//			return ;
		
		/*
		final Channel channel = ctx.channel();
		String channelId = null;
		Attribute<String> attr = channel.attr(channelIdAttrKey);
		if(attr != null && attr.get() != null){
			channelId = attr.get();
			//清理这个channel的对象缓存
			WebSocketPushChannelCache.removeChannelInfo(channelId);
			//将channel的状态置为非活跃
			RedisManager.hset(RedisConstants.PRE_CHANNEL + channelId, RedisConstants.CHANNEL_STATUS_FIELD, CHANNEL_STATUS_INACTIVE);
		}
//		RedisManager.del(RedisManager.PRE_CLIENT_IP + channelId);
		channel.closeFuture().addListener(
				new GenericFutureListener<Future<? super Void>>() {
					@Override
					public void operationComplete(Future<? super Void> future)
							throws Exception {
						logger.warn("RamoteAddress : "+ channel.remoteAddress() + " is inactive !");
					}
				});
		*/
	}

	private String getWebSocketLocation(ChannelPipeline cp, HttpRequest req) {
		String protocol = "ws";
		if (cp.get(SslHandler.class) != null) {
			protocol = "wss";
		}
		String path = protocol + "://" + req.headers().get(HttpHeaders.Names.HOST);
		return path;
				
	}
	
	public void send(Object msg,Map<String,Object> para)
	{
		
	}
	
	public void close(Map<String,Object> para)
	{
		
	}


}
