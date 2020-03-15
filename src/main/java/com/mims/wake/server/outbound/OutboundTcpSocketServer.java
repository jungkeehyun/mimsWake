package com.mims.wake.server.outbound;

import com.mims.wake.common.PushConstant;
import com.mims.wake.common.PushMessage;
import com.mims.wake.common.PushMessageDecoder;
import com.mims.wake.common.PushMessageEncoder;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.queue.OutboundQueueManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * TCP Socket 통신을 사용하는 Outbound Server 타입
 */
public class OutboundTcpSocketServer extends OutboundServer {

	private final PushServiceProperty property; // Push Service property
	private final OutboundQueueManager outboundQueueManager; // OutboundQueue 인스턴스 관리자

	/**
	 * constructor with parameters
	 * 
	 * @param property             Push Service property
	 * @param outboundQueueManager OutboundQueue 인스턴스 관리자
	 */
	public OutboundTcpSocketServer(PushServiceProperty property, OutboundQueueManager outboundQueueManager) {
		super(property);
		this.property = property;
		this.outboundQueueManager = outboundQueueManager;
	}

	/**
	 * TCP Socket 통신용 이벤트 핸들러를 설정하는 ChannelInitializer 인스턴스를 생성한다.<br>
	 * 
	 * @return ChannelInitializer 인스턴스
	 * @see chess.push.server.outbound.OutboundServer#getChannelInitializer()
	 */
	@Override
	protected ChannelInitializer<SocketChannel> getChannelInitializer() {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel socketChannel) {
				ChannelPipeline pipeline = socketChannel.pipeline();
				pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, PushConstant.DEFAULT_DELIMITER));
				pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8));
				pipeline.addLast(new PushMessageDecoder(), new PushMessageEncoder(PushConstant.DEFAULT_DELIMITER_STR));
				pipeline.addLast(new OutboundServerHandler(property, outboundQueueManager));
			}
		};
	}
	
	// [YPK]
	@Override
	public void send(PushMessage msg) {
		String serviceId = msg.getServiceId();
		String groupId = msg.getGroupId();
		String clientId = msg.getClientId();
		String inboundServerHost = property.getOutboundServerWsUri().replaceAll("/", "");
		int inboundServerPort = property.getOutboundServerPort();

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
					pipeline.addLast(new PushMessageEncoder(PushConstant.DEFAULT_DELIMITER_STR));
					pipeline.addLast(new OutboundServerHandler(property, outboundQueueManager));
				}
			});

			ChannelFuture future = bootstrap.connect(inboundServerHost, inboundServerPort);

			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					PushMessage message = new PushMessage(serviceId, groupId, clientId, msg.getMessage());
					future.channel().writeAndFlush(message);
					Thread.sleep(10L);
				}
			}).addListener(ChannelFutureListener.CLOSE).sync();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			group.shutdownGracefully();
		}
	}
}
