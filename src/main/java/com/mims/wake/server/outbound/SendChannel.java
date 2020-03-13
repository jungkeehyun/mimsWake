﻿// [YPK]
package com.mims.wake.server.outbound;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mims.wake.common.PushMessage;
import com.mims.wake.server.property.ServiceType;
import com.mims.wake.util.commonUtil;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class SendChannel implements Channel {
	private static final Logger LOG = LoggerFactory.getLogger(SendChannel.class);
	
	private final SendChannelId channelId;
	private final String outboundServerWsUri;
	private final OutboundServer outboundServer;
	
	public SendChannel(String serviceId, String outboundServerWsUri, OutboundServer outboundServer) {
		this.channelId = new SendChannelId(serviceId);
		this.outboundServerWsUri = outboundServerWsUri;
		this.outboundServer = outboundServer;
	}
	
	private void messageHandler(PushMessage msg) {
		if(msg.getServiceId().equals(ServiceType.TCPSOCKET)) {
			LOG.info("[Outbound TCPSOCKET Push] >>>>>>>>>> {}", msg);
			tcpSend(msg);
		}
		else if(msg.getServiceId().equals(ServiceType.FILESOCKET)) {
			LOG.info("[Outbound FILESOCKET Push] >>>>>>>>>> {}", msg);
			filePush(msg);
		}
	}
	
	private void filePush(PushMessage msg) {
		try {
			String targetPath;
			if(commonUtil.isFullPathName(outboundServerWsUri))
				targetPath = outboundServerWsUri;
			else
				targetPath = commonUtil.getCurrentPath(outboundServerWsUri);
			String token = commonUtil.pathToken();
			commonUtil.makeFolder(targetPath);
			String fileName = msg.getGroupId() + "_" + msg.getClientId() + "." + ServiceType.EXE_PUSH_SIDE;
			String pathFile = targetPath + token + fileName;

			File file = new File(pathFile);
			FileWriter fw = new FileWriter(file);
			fw.write(msg.getMessage());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void tcpSend(PushMessage msg) {
		if(outboundServer != null)
			outboundServer.send(msg);
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg) {
		// TODO file put
		 PushMessage message = (PushMessage)msg;
		 try {
			 messageHandler(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> boolean hasAttr(AttributeKey<T> key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture disconnect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture close() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture deregister() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture disconnect(ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture close(ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture deregister(ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture write(Object msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture write(Object msg, ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelPromise newPromise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelProgressivePromise newProgressivePromise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture newSucceededFuture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture newFailedFuture(Throwable cause) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelPromise voidPromise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Channel o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ChannelId id() {
		// TODO Auto-generated method stub
		return channelId;
	}

	@Override
	public EventLoop eventLoop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel parent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelConfig config() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRegistered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ChannelMetadata metadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketAddress localAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketAddress remoteAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelFuture closeFuture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWritable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long bytesBeforeUnwritable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long bytesBeforeWritable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Unsafe unsafe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChannelPipeline pipeline() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBufAllocator alloc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel read() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel flush() {
		// TODO Auto-generated method stub
		return null;
	}
}
