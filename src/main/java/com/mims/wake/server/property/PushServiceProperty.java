package com.mims.wake.server.property;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

/**
 * 개별 Push 서비스 속성 정의
 */
@ComponentScan
public class PushServiceProperty {

	@Value("serviceId")
    private String serviceId;				// Push Service ID
	
	@Value("inboundQueueCapacity")
    private String inboundQueueCapacity;		// Inbound Message Queue capacity
    
	@Value("outboundQueueCapacity")
    private String outboundQueueCapacity;		// Outbound Message Queue capacity
    
	@Value("outboundServerPort")
    private String outboundServerPort;			// Outbound Server listen port
    
	//@Value("outboundServerType")
    private ServerType outboundServerType;	// Outbound Server communication type
    
	@Value("outboundServerWsUri")				// Inbound, Outbound Server TcpSocket IP / Outbound Server FileSocket SubPath
    private String outboundServerWsUri;		// Outbound Server WebSocket URI, if Outbound Server type is WEBSOCKET

	@Value("outboundConnectionNumber")
	private String outboundConnectionNumber;		// Number of clients to connect to TCP
	
	@Value("outboundQueueClearTime")
	private String outboundQueueClearTime;			// Queue stack clear time (min)

    @PostConstruct
    public void afterPropertiesSet() {
        if (serviceId == null) {
            throw new IllegalArgumentException("The 'serviceId' property is null");
        }
        if (Integer.parseInt(inboundQueueCapacity) <= 0) {
            throw new IllegalArgumentException("The 'inboundQueueCapacity' property is invalid [" + inboundQueueCapacity + "]");
        }
        if (Integer.parseInt(outboundQueueCapacity) <= 0) {
            throw new IllegalArgumentException("The 'inboundQueueCapacity' property is invalid [" + outboundQueueCapacity + "]");
        }
        if (Integer.parseInt(outboundServerPort) <= 0) {
            throw new IllegalArgumentException("The 'outboundServerPort' property is invalid [" + outboundServerPort + "]");
        }
        if (outboundServerType == null) {
            throw new IllegalArgumentException("The 'outboundServerType' property is null");
        }
        if (outboundServerWsUri == null) {
            throw new IllegalArgumentException("The 'outboundServerWsUri' property is null");
        }
    }

    public String getServiceId() {
        return serviceId;
    }
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getInboundQueueCapacity() {
        return Integer.parseInt(inboundQueueCapacity);
    }
    public void setInboundQueueCapacity(int inboundQueueCapacity) {
        this.inboundQueueCapacity = Integer.toString(inboundQueueCapacity);
    }

    public int getOutboundQueueCapacity() {
        return Integer.parseInt(outboundQueueCapacity);
    }
    public void setOutboundQueueCapacity(int outboundQueueCapacity) {
        this.outboundQueueCapacity = Integer.toString(outboundQueueCapacity);
    }

    public int getOutboundServerPort() {
        return Integer.parseInt(outboundServerPort);
    }
    public void setOutboundServerPort(int outboundServerPort) {
        this.outboundServerPort = Integer.toString(outboundServerPort);
    }

    public ServerType getOutboundServerType() {
        return outboundServerType;
    }
    public void setOutboundServerType(ServerType outboundServerType) {
        this.outboundServerType = outboundServerType;
    }

    public String getOutboundServerWsUri() {
        return outboundServerWsUri;
    }
    public void setOutboundServerWsUri(String outboundServerWsUri) {
		this.outboundServerWsUri = outboundServerWsUri;
		if (this.outboundServerType == ServerType.WEBSOCKET) {
			if (outboundServerWsUri != null && !outboundServerWsUri.startsWith("/"))
            this.outboundServerWsUri = "/" + outboundServerWsUri;
        }
    }

	public int getOutboundConnectionNumber() {
		return Integer.parseInt(outboundConnectionNumber);
	}
	public void setOutboundConnectionNumber(String outboundConnectionNumber) {
		this.outboundConnectionNumber = outboundConnectionNumber;
	}
	
	public int getOutboundQueueClearTime() {
		return Integer.parseInt(outboundQueueClearTime);
	}
	public void setOutboundQueueClearTime(String outboundQueueClearTime) {
		this.outboundQueueClearTime = outboundQueueClearTime;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append("[")
               .append("serviceId=").append(serviceId)
               .append(", inboundQueueCapacity=").append(inboundQueueCapacity)
               .append(", outboundQueueCapacity=").append(outboundQueueCapacity)
               .append(", outboundServerPort=").append(outboundServerPort)
               .append(", outboundServerType=").append(outboundServerType)
               .append(", outboundServerWsUri=").append(outboundServerWsUri)
               .append(", outboundConnectionNumber=").append(outboundConnectionNumber)
               .append(", outboundQueueClearTime=").append(outboundQueueClearTime)
               .append("]");
        return builder.toString();
    }
}
