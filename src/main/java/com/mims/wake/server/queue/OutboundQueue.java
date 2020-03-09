﻿package com.mims.wake.server.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mims.wake.common.PushConstant;
import com.mims.wake.common.PushMessage;
import com.mims.wake.server.property.ServiceType;

import io.netty.channel.Channel;

/**
 * Outbound Server에 연결된 클라이언트 채널에 따라 생성되는 큐<br>
 * -큐에 담긴 메시지를 클라이언트 채널로 전송하기 위한 쓰레드 동작
 */
public class OutboundQueue extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(OutboundQueue.class);

    private String serviceId;					// Push Service ID
    private BlockingQueue<PushMessage> queue;	// message queue
    private final int capacity;					// message queue capacity
    private Channel channel;					// Client Channel instance

    /**
     * constructor with parameters
     * @param serviceId Push Service ID
     * @param capacity message queue capacity
     * @param channel Netty Channel instance
     */
    public OutboundQueue(String serviceId, int capacity, Channel channel) {
        this.serviceId = serviceId;
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue<PushMessage>(capacity);
        this.channel = channel;
    }

    /**
     * 큐와 연관된 클라이언트 채널의 그룹ID를 반환한다.
     * @return 클라이언트 채널 속성 중 그룹ID
     */
    public String groupId() {
    	// [+] [YPK]
    	if(serviceId.equals(ServiceType.FILE_SERVER)) {
    		return this.serviceId;
    	}
    	// [-]
        // 그룹ID는 런타임에 변경될 수 있으므로 항상 채널에서 조회 필요
        return channel.attr(PushConstant.GROUP_ID).get();
    }

    /**
     * 큐와 연관된 클라이언트 채널의 클라이언트ID를 반환한다.
     * @return 클라이언트 채널 속성 중 클라이언트ID
     */
    public String clientId() {
    	// [+] [YPK]
    	if(serviceId.equals(ServiceType.FILE_SERVER)) {
    		return this.serviceId;
    	}
    	// [-]
        // 클라이언트ID는 런타임에 변경될 수 있으므로 항상 채널에서 조회 필요
        return channel.attr(PushConstant.CLIENT_ID).get();
    }

    /**
     * 큐에 메시지를 추가한다.
     * @param pushMessage Push 메시지
     */
    public void enqueue(PushMessage pushMessage) {
        if (!isValid(pushMessage)) {
            LOG.error("[OutboundQueue:{}] [{}] [{}] invalid message {}", serviceId, groupId(), clientId(), pushMessage);
            return;
        }

        if (!queue.offer(pushMessage)) {
            LOG.error("[OutboundQueue:{}] [{}] [{}] failed to enqueue {}", serviceId, groupId(), clientId(), pushMessage);
        }
    }

    private boolean isValid(PushMessage pushMessage) {
        if (pushMessage == null) {
            return false;
        }

        String msgServiceId = pushMessage.getServiceId();
        String msgGroupId = pushMessage.getGroupId();
        String msgClientId = pushMessage.getClientId();

        // [+] [YPK]
        if (msgServiceId != null && (msgServiceId.equals(ServiceType.TCPSOCKET) || msgServiceId.equals(ServiceType.FILE_SERVER))) {
            return true;
        }
        // [-]
        if (msgServiceId == null || !msgServiceId.equals(serviceId)) {
            return false;
        }
        if (msgGroupId != null && !msgGroupId.equals(groupId())) {
            return false;
        }
        if (msgClientId != null && !msgClientId.equals(clientId())) {
            return false;
        }

        return true;
    }

    /**
     * 큐의 현재 상태를 문자열로 반환한다.
     * @return 큐 상태 문자열
     */
    public String status() {
        return "groupId: " + groupId() + ", clientId: " + clientId() + ", channel: " + channel + ", capacity: " + capacity + ", current: " + queue.size();
    }

    /**
     * OutboundQueue 쓰레드가 종료되도록 한다.
     */
    public void shutdown() {
        this.interrupt();
    }

    /**
     * 큐에서 메시지를 추출하여 클라이언트 채널에 전송한다.
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        setName("OutboundQueueThread:" + serviceId);

        LOG.info("[{}] [{}] [{}] started", getName(), groupId(), clientId());

        PushMessage message = null;
        while (!isInterrupted()) {
            try {
                message = queue.take();
                LOG.info("[{}] [{}] [{}] take {}", getName(), groupId(), clientId(), message);
            } catch (InterruptedException e) {
                break;
            }

            if (message != null) {
                channel.writeAndFlush(message);
                message = null;
            }
        }

        LOG.info("[{}] [{}] [{}] shutdown", getName(), groupId(), clientId());
    }

}
