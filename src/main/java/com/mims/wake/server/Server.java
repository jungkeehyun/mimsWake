package com.mims.wake.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mims.wake.server.inbound.InboundTcpSocketServer;
import com.mims.wake.server.outbound.OutboundServer;
import com.mims.wake.server.outbound.OutboundServerFactory;
import com.mims.wake.server.property.FileChannel;
import com.mims.wake.server.property.PushBaseProperty;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.property.ServerType;
import com.mims.wake.server.queue.InboundQueue;
import com.mims.wake.server.queue.InboundQueueChecker;
import com.mims.wake.server.queue.OutboundQueueChecker;
import com.mims.wake.server.queue.OutboundQueueManager;

/**
 * simple-push-server 서버 기동/종료 관리
 */
@Component
public class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private final Map<String, OutboundServer> outboundServers;	// Service ID를 key로 하는 OutboundServer collection
    private final Map<String, InboundQueue> inboundQueues;		// Service ID를 key로 하는 InboundQueue collection
    private final OutboundQueueManager outboundQueueManager;	// OutboundQueue 인스턴스 라이프사이클 관리자

    private OutboundQueueChecker outboundQueueChecker;	// OutboundQueue 상태 모니터링 쓰레드
    private InboundQueueChecker inboundQueueChecker;	// InboundQueue 상태 모니터링 쓰레드
    private InboundTcpSocketServer inboundServer;				// Push 요청을 수용하는 InboundServer

    public Server() {
        outboundServers = new HashMap<String, OutboundServer>();
        inboundQueues = new HashMap<String, InboundQueue>();
        outboundQueueManager = new OutboundQueueManager();
    }

    /**
     * 서버 기동
     * @param embedded embedded 모드로 기동할지 여부 (embedded 모드는 Inbound Server를 기동하지 않음)
     * @param baseProperty Push 서버 기본 속성
     * @param serviceProperties 개별 Push 서비스 속성 collection
     * @return Service ID를 key로 하는 InboundQueue collection
     */
    public Map<String, InboundQueue> startupServer(boolean embedded, PushBaseProperty baseProperty, Collection<PushServiceProperty> serviceProperties) {
        LOG.info("Wake Push Server STARTING...");

        if (!serviceProperties.isEmpty()) {
            // 개별 Push 서비스 속성에 따라 필요한 인스턴스 생성하고 Service ID를 key로 하는 collection에 저장
            serviceProperties.forEach(property -> {
                String serviceId = property.getServiceId();
                outboundServers.put(serviceId, OutboundServerFactory.getInstance(property, outboundQueueManager));
                inboundQueues.put(serviceId, new InboundQueue(serviceId, property.getInboundQueueCapacity(), outboundQueueManager));
                outboundQueueManager.addOutboundQueueGroup(serviceId);
                // [+] [YPK]
                if(property.getOutboundServerType().equals(ServerType.FILESOCKET)) {
                	FileChannel channel = new FileChannel(serviceId, property.getOutboundServerPath());
                	outboundQueueManager.startOutboundQueue(property.getServiceId(), property.getInboundQueueCapacity(), channel);
                }
                // [-]
            });
        }

        // startup OutboundServers
        outboundServers.forEach((serviceId, outboundServer) -> outboundServer.startup());

        // startup InboundQueue threads
        inboundQueues.forEach((serviceId, inboundQueue) -> inboundQueue.start());

        // startup OutboundQueueChecker
        outboundQueueChecker = new OutboundQueueChecker(outboundQueueManager, baseProperty.getOutboundQueueCheckInterval());
        outboundQueueChecker.start();

        // startup InboundQueueChecker
        inboundQueueChecker = new InboundQueueChecker(inboundQueues, baseProperty.getInboundQueueCheckInterval());
        inboundQueueChecker.start();

        // startup InboundServer
        if (!embedded) {
            inboundServer = new InboundTcpSocketServer(baseProperty.getInboundServerPort());
            inboundServer.startup(inboundQueues);
        }

        LOG.info("[simple-push-server] startup complete....");

        return inboundQueues;
    }

    /**
     * 서버 컴포넌트 종료
     */
    public void shutdownServer() {
        // shtudown InboundServer
        if (inboundServer != null) {
            inboundServer.shutdown();
        }

        // shtudown InboundQueueChecker
        if (inboundQueueChecker != null) {
            inboundQueueChecker.shutdown();
        }

        // shtudown OutboundQueueChecker
        if (outboundQueueChecker != null) {
            outboundQueueChecker.shutdown();
        }

        // shtudown InboundQueue threads
        if (inboundQueues != null) {
            inboundQueues.forEach((serviceId, inboundQueue) -> inboundQueue.shutdown());
        }

        // shtudown OutboundServers
        if (outboundServers != null) {
            outboundServers.forEach((serviceId, outboundServer) -> outboundServer.shutdown());
        }
    }

}
