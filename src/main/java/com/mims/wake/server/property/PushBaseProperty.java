package com.mims.wake.server.property;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

/**
 * Push 서버 모듈의 기본 속성 정의
 */
@ComponentScan
public class PushBaseProperty {

    private ServerType inboundServerType;			// Inbound Server Type
	
	@Value("inboundServerPort")
    private String inboundServerPort;				// Inbound Server listen port
	
	@Value("inboundQueueCheckInterval")
    private String inboundQueueCheckInterval;		// InboundQueue 상태 모니터링 주기 (초)
    
	@Value("outboundQueueCheckInterval")
	private String outboundQueueCheckInterval;		// OutboundQueue 상태 모니터링 주기 (초)

	@Value("outboundServerWsUri")
	private String outboundServerWsUri;				// Outbound Server Connection IP

	@Value("inboundPollingInterval")
	private String inboundPollingInterval;			// Inbound File Polling Interval

    @PostConstruct
    public void afterPropertiesSet() {
        if (inboundServerType == null) {
            throw new IllegalArgumentException("The 'inboundServerType' property is invalid [" + inboundServerType + "]");
        }
        if (Integer.parseInt(inboundServerPort) <= 0) {
            throw new IllegalArgumentException("The 'inboundServerPort' property is invalid [" + inboundServerPort + "]");
        }
        if (Integer.parseInt(inboundQueueCheckInterval) <= 0) {
            throw new IllegalArgumentException("The 'inboundQueueCheckInterval' property is invalid [" + inboundQueueCheckInterval + "]");
        }
        if (Integer.parseInt(outboundQueueCheckInterval) <= 0) {
            throw new IllegalArgumentException("The 'outboundQueueCheckInterval' property is invalid [" + outboundQueueCheckInterval + "]");
        }
        if(inboundServerType == ServerType.FILESOCKET && Integer.parseInt(inboundPollingInterval) <= 0) {
        	throw new IllegalArgumentException("The 'inboundPollingInterval' property is invalid [" + inboundPollingInterval + "]");
        }
    }

	public ServerType getInboundServerType() {
		return this.inboundServerType;
	}
    
    public void setInboundServerType(ServerType type) {
    	this.inboundServerType = type;
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

    public String getOutboundServerWsUri() {
        return this.outboundServerWsUri;
    }
    
    public void setOutboundServerWsUri(String outboundServerWsUri) {
        this.outboundServerWsUri = outboundServerWsUri;
    }

    public int getInboundPollingInterval() {
        return Integer.parseInt(inboundPollingInterval);
    }
    
    public void setInboundPollingInterval(String interval) {
        this.inboundPollingInterval = interval;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append("[")
        		.append("inboundServerType=").append(inboundServerType)
               .append(", inboundServerPort=").append(inboundServerPort)
               .append(", inboundQueueCheckInterval=").append(inboundQueueCheckInterval)
               .append(", outboundQueueCheckInterval=").append(outboundQueueCheckInterval)
               .append(", outboundServerWsUri=").append(outboundServerWsUri)
               .append(", inboundPollingInterval=").append(inboundPollingInterval)
               .append("]");
        return builder.toString();
    }

}
