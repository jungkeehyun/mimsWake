﻿package com.mims.wake.server.outbound;

import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.property.ServerType;
import com.mims.wake.server.queue.OutboundQueueManager;

/**
 * Push 서비스 속성의 서버 유형에 따라 Outbound Server 인스턴스를 생성하는 factory
 */
public class OutboundServerFactory {

    /**
     * Push 서비스 속성에 따른 Outbound Server 인스턴스를 생성한다.
     * @param property Push 서비스 속성 정보
     * @param outboundQueueManager OutboundQueue 인스턴스 관리자
     * @return Outbound Server 인스턴스
     */
    public static OutboundServer getInstance(PushServiceProperty property, OutboundQueueManager outboundQueueManager) {
        if (property == null) {
            throw new IllegalArgumentException("The PushServiceProperty argument is null");
        }

        ServerType type = property.getOutboundServerType();
        if (type == null) {
            throw new IllegalArgumentException("The outbound server type is null");
        }

        switch (type) {
            case TCPSOCKET:
                return new OutboundTcpSocketServer(property, outboundQueueManager);
            case WEBSOCKET:
                return new OutboundWebSocketServer(property, outboundQueueManager);
            case FILESOCKET: // [YPK]
            	return new OutboundFileSocketServer(property, outboundQueueManager);
            default:
                throw new IllegalArgumentException("Unknown server type [" + type + "]");
        }
    }

}
