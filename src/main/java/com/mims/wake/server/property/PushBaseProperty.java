package com.mims.wake.server.property;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

/**
 * Push 서버 모듈의 기본 속성 정의
 */
@ComponentScan
public class PushBaseProperty {

	@Value("inboundServerPort")
    private String inboundServerPort;				// Inbound Server listen port
	
	@Value("inboundQueueCheckInterval")
    private String inboundQueueCheckInterval;		// InboundQueue 상태 모니터링 주기 (초)
    
	@Value("outboundQueueCheckInterval")
	private String outboundQueueCheckInterval;		// OutboundQueue 상태 모니터링 주기 (초)

    @PostConstruct
    public void afterPropertiesSet() {
        if (Integer.parseInt(inboundServerPort) <= 0) {
            throw new IllegalArgumentException("The 'inboundServerPort' property is invalid [" + inboundServerPort + "]");
        }
        if (Integer.parseInt(inboundQueueCheckInterval) <= 0) {
            throw new IllegalArgumentException("The 'inboundQueueCheckInterval' property is invalid [" + inboundQueueCheckInterval + "]");
        }
        if (Integer.parseInt(outboundQueueCheckInterval) <= 0) {
            throw new IllegalArgumentException("The 'outboundQueueCheckInterval' property is invalid [" + outboundQueueCheckInterval + "]");
        }
    }

    public int getInboundServerPort() {
        return Integer.parseInt(inboundServerPort);
    }
    
    public void setInboundServerPort(int inboundServerPort) {
        this.inboundServerPort = Integer.toString(inboundServerPort);
    }

    public int getInboundQueueCheckInterval() {
        return Integer.parseInt(inboundQueueCheckInterval);
    }
    
    public void setInboundQueueCheckInterval(int inboundQueueCheckInterval) {
        this.inboundQueueCheckInterval = Integer.toString(inboundQueueCheckInterval);
    }

    public int getOutboundQueueCheckInterval() {
        return Integer.parseInt(outboundQueueCheckInterval);
    }
    
    public void setOutboundQueueCheckInterval(int outboundQueueCheckInterval) {
        this.outboundQueueCheckInterval = Integer.toString(outboundQueueCheckInterval);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append("[")
               .append("inboundServerPort=").append(inboundServerPort)
               .append(", inboundQueueCheckInterval=").append(inboundQueueCheckInterval)
               .append(", outboundQueueCheckInterval=").append(outboundQueueCheckInterval)
               .append("]");
        return builder.toString();
    }

}
