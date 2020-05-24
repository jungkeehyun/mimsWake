package com.mims.wake.server;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mims.wake.database.DBHelper;
import com.mims.wake.server.inbound.InboundFilePolling;
import com.mims.wake.server.inbound.InboundTcpSocketServer;
import com.mims.wake.server.outbound.OutboundServer;
import com.mims.wake.server.outbound.OutboundServerFactory;
import com.mims.wake.server.property.DBServiceProperty;
import com.mims.wake.server.property.PushBaseProperty;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.property.ServerType;
import com.mims.wake.server.property.ServiceType;
import com.mims.wake.server.queue.InboundQueue;
import com.mims.wake.server.queue.InboundQueueChecker;
import com.mims.wake.server.queue.OutboundQueueChecker;
import com.mims.wake.server.queue.OutboundQueueManager;

/**
 * simple-push-server 서버 기동/종료 관리
 */
@Component
public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);

    private final Map<String, OutboundServer> outboundServers;	// Service ID를 key로 하는 OutboundServer collection
    private final Map<String, InboundQueue> inboundQueues;		// Service ID를 key로 하는 InboundQueue collection
    private final OutboundQueueManager outboundQueueManager;	// OutboundQueue 인스턴스 라이프사이클 관리자

    private OutboundQueueChecker outboundQueueChecker;	// OutboundQueue 상태 모니터링 쓰레드
    private InboundQueueChecker inboundQueueChecker;	// InboundQueue 상태 모니터링 쓰레드
    private InboundTcpSocketServer inboundServer;				// Push 요청을 수용하는 InboundServer
    private InboundTcpSocketServer inboundFilePush;				// [YPK]
    private InboundFilePolling inboundFilePolling; 				// [YPK]
    
    DBHelper current;

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
    public Map<String, InboundQueue> startupServer(boolean embedded, PushBaseProperty baseProperty, Collection<PushServiceProperty> serviceProperties, DBServiceProperty dBServiceProperty) {
    	logger.info("Wake Push Server STARTING...");

    	// Test 
    	//commonDAO cdao = new commonDAO();
    	//logger.info("!@#!@$@!$@#%@#% [" + cdao.getCurrentDataTime() + "]");
    	//cdao.insertTest();
    	//
    	
        if (!serviceProperties.isEmpty()) {
            // 개별 Push 서비스 속성에 따라 필요한 인스턴스 생성하고 Service ID를 key로 하는 collection에 저장
            serviceProperties.forEach(property -> {
                String serviceId = property.getServiceId();
                outboundServers.put(serviceId, OutboundServerFactory.getInstance(property, outboundQueueManager));
				inboundQueues.put(serviceId,
						new InboundQueue(serviceId, property.getInboundQueueCapacity(), outboundQueueManager));
                outboundQueueManager.addOutboundQueueGroup(serviceId);
				outboundQueueManager.setStackProperty(property);
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

        // [YPK] Start by server type
        // startup InboundServer
		ServerType type = baseProperty.getInboundServerType();
        if (!embedded) {
			if (type == ServerType.TCPSOCKET) {
				inboundServer = new InboundTcpSocketServer(baseProperty);
				inboundServer.startup(inboundQueues);
				// file push
				OutboundServer outboundFilePush = outboundServers.get(ServiceType.FILESOCKET);
				if(outboundFilePush != null) {
					// file push first way
					outboundFilePush.regQueueForFilePush(outboundQueueManager);
					/*/ file push second way
					PushServiceProperty prop = outboundFilePush.getPushServiceProperty();
					inboundFilePush = new InboundTcpSocketServer(baseProperty);
					inboundFilePush.startupFilePush(inboundQueues, prop.getOutboundServerPort());*/
				}
			} else if (type == ServerType.FILESOCKET) {
				inboundFilePolling = new InboundFilePolling(baseProperty);
				inboundFilePolling.startup(inboundQueues);
			} else {
				logger.info("[Choose a server type TCPSOCKET or FILESOCKET] >>>>>>>>>> {}", type);
				return null;
        }
		}
        logger.info("[simple-push-server] startup complete.... >>>>>>>>>> {}", type);

        // DB Write
        current = DBHelper.getInstance(dBServiceProperty);
        if(current.open() != null)
        {
        	logger.info("[simple-push-server] DB open");
        }
        // End
 
        
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
        
		// shutdown InboundFilePush
		if (inboundFilePush != null) {
			inboundFilePush.shutdown();
		}
        
		// shutdown InboundFilePolling
		if (inboundFilePolling != null) {
			inboundFilePolling.shutdown();
		}
		
		// shutdown OutboundQueueManager
		if(outboundQueueManager != null) {
			outboundQueueManager.shutdownQstack();
		}
		
		// DB Connection close
		if(current != null) {
			current.close();
		}
    }

}
