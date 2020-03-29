package com.mims.wake.server.queue;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * InboundQueue 상태 모니터링 쓰레드
 */
public class InboundQueueChecker extends Thread {

    private static final Logger logger = LogManager.getLogger(InboundQueueChecker.class);

    private final Map<String, InboundQueue> inboundQueues;	// Service ID를 key로 하는 InboundQueue collection
    private final int inboundQueueCheckInterval;			// InboundQueue 모니터링 주기 (초)

    /**
     * constructor with parameters
     * @param inboundQueues Service ID를 key로 하는 InboundQueue collection
     * @param inboundQueueCheckInterval InboundQueue 모니터링 주기 (초)
     */
    public InboundQueueChecker(Map<String, InboundQueue> inboundQueues, int inboundQueueCheckInterval) {
        this.inboundQueues = inboundQueues;
        this.inboundQueueCheckInterval = inboundQueueCheckInterval;
    }

    /**
     * InboundQueueChecker 쓰레드가 종료되도록 한다.
     */
    public void shutdown() {
        this.interrupt();
    }

    /**
     * 주기적으로 InboundQueue 상태정보를 로깅한다.
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        setName("InboundQueueCheckerThread");

        logger.info("[{}] started [interval: {}]", getName(), inboundQueueCheckInterval);

        while (!isInterrupted()) {
            StringBuilder builder = new StringBuilder();
            if (inboundQueues.isEmpty()) {
                builder.append("No Inbound Queue\n");
            } else {
                inboundQueues.forEach((serviceId, inboundQueue) -> {
                    builder.append("[").append(serviceId).append("] ").append(inboundQueue.status()).append("\n");
                });
            }
            logger.info("\n* Inbound Queue Status\n{}", builder);

            if (isInterrupted()) {
                break;
            }

            try {
                TimeUnit.SECONDS.sleep(inboundQueueCheckInterval);
            } catch (InterruptedException e) {
                break;
            }
        }

        logger.info("[{}] shutdown", getName());
    }

}
