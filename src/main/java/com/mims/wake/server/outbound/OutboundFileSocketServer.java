// [YPK]
package com.mims.wake.server.outbound;

import com.mims.wake.common.PushConstant;
import com.mims.wake.common.PushMessageDecoder;
import com.mims.wake.common.PushMessageEncoder;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.queue.OutboundQueueManager;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * File 통신을 사용하는 Outbound Server 타입
 */
public class OutboundFileSocketServer extends OutboundServer {

	private final PushServiceProperty property; // Push Service property
	private final OutboundQueueManager outboundQueueManager; // OutboundQueue 인스턴스 관리자

	/**
	 * constructor with parameters
	 * 
	 * @param property             Push Service property
	 * @param outboundQueueManager OutboundQueue 인스턴스 관리자
	 */
	public OutboundFileSocketServer(PushServiceProperty property, OutboundQueueManager outboundQueueManager) {
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

}
