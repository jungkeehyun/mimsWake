package com.mims.wake.server.outbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mims.wake.common.PushMessage;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.property.ServerType;
import com.mims.wake.server.queue.OutboundQueueManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 클라이언트 연결을 수용하는 Outbound Server의 abstract type<br>
 * -Outbound Server 유형에 따라 하위 타입 정의
 */
public abstract class OutboundServer {

	private static final Logger LOG = LoggerFactory.getLogger(OutboundServer.class);

	private final PushServiceProperty property; // Push Service property

	private EventLoopGroup bossGroup; // EventLoopGroup that accepts an incoming connection
	private EventLoopGroup workerGroup; // EventLoopGroup that handles the traffic of the accepted connection

	/**
	 * constructor with a parameter
	 * 
	 * @param property Push Service property
	 */
	public OutboundServer(PushServiceProperty property) {
		this.property = property;
	}

	/**
	 * OutboundServer 인스턴스를 기동한다.<br>
	 * -소켓채널에 대한 이벤트 핸들러 지정<br>
	 * -소켓옵션 지정
	 */
	public void startup() {
		LOG.info("[OutboundServer:{}] starting...", property.getServiceId());
		// [YPK] file push first way
		if(property.getOutboundServerType().equals(ServerType.FILESOCKET)) {
			// do nothing
		}
		else
			bind();
		}

	public void bind() {	
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(getChannelInitializer())
					.option(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true);

			bootstrap.bind(property.getOutboundServerPort()).sync();

			LOG.info("[OutboundServer:{}] started, listening on port {}", property.getServiceId(),
					property.getOutboundServerPort());

		} catch (InterruptedException e) {
			LOG.error("[OutboundServer:" + property.getServiceId() + "] failed to startup", e);
			shutdown();
		}
	}

	// [YPK]
	public void send(PushMessage msg) {
		// do nothing
	}

	// [YPK]
	public void regQueueForFilePush(OutboundQueueManager outboundQueueManager) {
		SendChannel channel = new SendChannel(property.getServiceId(), property.getOutboundServerWsUri(), this);
		outboundQueueManager.startOutboundQueue(property.getServiceId(), property.getInboundQueueCapacity(), channel);
	}
	
	// [YPK]
	public PushServiceProperty getPushServiceProperty() {
		return property;
	}

	/**
	 * OutboundServer 인스턴스를 중지한다.<br>
	 * -shutdown worker EventLoopGroup<br>
	 * -shutdown boss EventLoopGroup
	 */
	public void shutdown() {
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}

		LOG.info("[OutboundServer:{}] shutdown", property.getServiceId());
	}

	/**
	 * 채널에 이벤트 핸들러를 설정하는 ChannelInitializer 인스턴스를 생성한다.
	 * 
	 * @return ChannelInitializer 인스턴스
	 */
	protected abstract ChannelInitializer<SocketChannel> getChannelInitializer();
}