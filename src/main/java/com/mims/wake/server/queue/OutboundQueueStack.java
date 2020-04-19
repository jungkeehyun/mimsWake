package com.mims.wake.server.queue;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mims.wake.common.PushMessage;
import com.mims.wake.server.property.PushServiceProperty;
import com.mims.wake.server.property.ServiceType;
import com.mims.wake.util.commonUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

public class OutboundQueueStack extends Thread {
	
	private static final Logger logger = LogManager.getLogger(OutboundQueueStack.class);

	private static int MINUTE_MS = 60000;
	private static int HMINUTE_MS = 30000;

	private final Map<String, ServiceInfo> 			serviceGroup;			// service socket
    private final BlockingQueue<ChannelInfo> 		queue;					// message queue
    private Vector<MessageInfo> 					stack;					// message stack storage
    private Timer 									timer;
    private int										interval;

	/**
	 * constructor with parameters
	 * 
	 */
	public OutboundQueueStack(int capacity) {
		this.serviceGroup = new HashMap<String, ServiceInfo>();
		this.queue = new LinkedBlockingQueue<ChannelInfo>(capacity);
		this.stack = new Vector<MessageInfo>();
		this.interval = HMINUTE_MS;
	}
	
    /**
     * queue stack start
     */
    public void startup() {
        this.start();
        
		try {
			this.timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					clearCheck();
				}
			};

			this.timer.schedule(timerTask, 0, this.interval);
		} catch (Exception e) {
			shutdown();
		}
    }
	
	/**
	 * set property
	 * 
	 * @param prop 			: PushServiceProperty
	 */
	public void setProperty(PushServiceProperty prop) {
		String serviceId = prop.getServiceId();
		if (serviceId.equals(ServiceType.TCPSOCKET) || serviceId.equals(ServiceType.WEBSOCKET))
			serviceGroup.put(serviceId, new ServiceInfo(prop));
	}
	
	/**
	 * connection socket
	 * 
	 */
	public ServiceInfo connection(String serviceId) {
		if (!serviceGroup.containsKey(serviceId))
            return null;
		ServiceInfo sInfo = serviceGroup.get(serviceId);
		sInfo.increase();
		return sInfo;
	}
	
	/**
	 * disconnection socket
	 * 
	 */
	public void disconnection(String serviceId) {
		if (!serviceGroup.containsKey(serviceId))
            return;
		ServiceInfo sInfo = serviceGroup.get(serviceId);
		sInfo.decrease();
	}

	/**
	 * 미전송 메시지 보관
	 * 
	 * @param msg : save message
	 */
	public void pushStack(PushMessage msg, Map<ChannelId, OutboundQueue> queueGroup) {
		String serviceId = msg.getServiceId();
		if (!serviceGroup.containsKey(serviceId))
            return;
		
		ServiceInfo sInfo = serviceGroup.get(serviceId);
		PushServiceProperty prop = sInfo.getProperty();
		int clearTime = prop.getOutboundQueueClearTime(); // minute
		
		commonUtil.mutexStack.lock();
		if (queueGroup.isEmpty()) {
			stack.add(new MessageInfo(msg, clearTime));
		} else {
			// 지금 보낸 클라이언트 IP는 추가하고, 최대 연결 허용 개수만큼 보관
			if (sInfo.isStock()) {
				MessageInfo msgInfo = new MessageInfo(msg, clearTime);
				queueGroup.forEach((channelId, queue) -> {
					Channel channel = queue.getChannel();
					msgInfo.addSentAddr(channel.remoteAddress().toString());
				});
				stack.add(msgInfo);
			}
		}
		commonUtil.mutexStack.unlock();
	}

	/**
	 * 미전송 메세지 꺼내기
	 * 
	 * @param serviceId    : service id
	 * @param channel		: connected channel
	 */
	public void popStack(String serviceId, Channel channel) {
		try {
			if (serviceId == null || serviceId.isEmpty() || channel == null)
				throw new Exception();
			
			ServiceInfo sInfo = connection(serviceId);
			if(sInfo == null)
				return;
			
			commonUtil.mutexStack.lock();
			Vector<MessageInfo> stock = new Vector<MessageInfo>();
			stack.forEach(msgInfo -> {
				String sid = msgInfo.getServiceId();
				if (sid.equals(serviceId)) {
					queue.offer(new ChannelInfo(channel, msgInfo));
					if (sInfo.isStock())
						stock.add(msgInfo);
				} else
					stock.add(msgInfo);
			});
			stack = stock;
			commonUtil.mutexStack.unlock();
		} catch (Exception e) {
			logger.error("[OutboundQueueStack popStack] >>>>>");
		}
	}
	
    /**
     * message clear check
     */
	public void clearCheck() {
		commonUtil.mutexStack.lock();
		Vector<MessageInfo> stock = new Vector<MessageInfo>();
		stack.forEach(mInfo -> {
			if(mInfo.isStock())
				stock.add(mInfo);
		});
		stack = stock;
		commonUtil.mutexStack.unlock();
	}
	
    /**
     * queue stack stop
     */
    public void shutdown() {
        this.interrupt();
		if (this.timer != null)
			this.timer.cancel();
    }
    
    /**
     * 큐에서 미전송 메시지를 추출하여 클라이언트 채널에 전송
     * @see java.lang.Thread#run()
     */
    @Override
	public void run() {
		while (!isInterrupted()) {
			ChannelInfo cInfo = null;
			try {
				cInfo = queue.take();
			} catch (InterruptedException e) {
				break;
			}
			
			if (cInfo != null) {
				PushMessage msg = cInfo.sendMessage();
				if(msg != null)
					logger.info("[{}] [{}] [{}] Pop Stack {}", getName(), msg.getServiceId(), msg.getClientId(), msg);
			}
		}
	}
    
    ////////////////////////////////////////////////////////////////////////////////
    // class MessageInfo
    //
    public class MessageInfo {
    	private final PushMessage 	message;
    	private int 				clearTime; 		// min
    	private Vector<String>		sentAddr;		
    	
    	public MessageInfo(PushMessage message, int clearMinTime) {
    		this.message = message;    		
    		long time = System.currentTimeMillis();
    		this.clearTime = (int)(new Date(time).getTime() / MINUTE_MS) + clearMinTime;
    		this.sentAddr = new Vector<String>();
    	}
    	
    	public boolean isStock() {
    		long time = System.currentTimeMillis();
    		int currentTime = (int)(new Date(time).getTime() / MINUTE_MS);
    		return (this.clearTime > currentTime);
    	}
		
		public void addSentAddr(String addr) {
			String rip = extractOnlyIP(addr);
			if(!rip.isEmpty())
				sentAddr.add(rip);
		}
		
		public boolean isAlreadySent(String addr) {
			String rip = extractOnlyIP(addr);
			return sentAddr.contains(rip);
		}
		
		public PushMessage getMessage() {
			return message;
		}
		
		public String getServiceId() {
			return message.getServiceId();
		}
		
		private String extractOnlyIP(String addr) {
			if(addr == null || addr.isEmpty())
				return addr;
			
			int pos = addr.indexOf("/");
			if(pos != -1)
				addr = addr.substring(pos + 1, addr.length());
			pos = addr.lastIndexOf(":");
			if(pos != -1)
				addr = addr.substring(0, pos);
			return addr;
		}
    } 
    
    ////////////////////////////////////////////////////////////////////////////////
    // class ServiceInfo
    //
    public class ServiceInfo {
    	private final PushServiceProperty property;
    	private int connectionCount;
    	
    	public ServiceInfo(PushServiceProperty property) {
    		this.property = property;
    		this.connectionCount = 0;
    	}
    	
    	public void increase() {
    		if(connectionCount < property.getOutboundConnectionNumber())
    			++connectionCount;
    	}
    	
    	public void decrease() {
    		if(connectionCount > 0)
    			--connectionCount;
    	}
    	
    	public boolean isStock() {
    		return (connectionCount < property.getOutboundConnectionNumber());
    	}
    	
    	public PushServiceProperty getProperty() {
    		return property;
    	}
    }    
    
    ////////////////////////////////////////////////////////////////////////////////
    // class ChannelInfo
    //
    public class ChannelInfo {
    	private final Channel channel;
    	private final MessageInfo msgInfo;
    	
    	public ChannelInfo(Channel channel, MessageInfo mInfo) {
    		this.channel = channel;
    		this.msgInfo = mInfo;
    	}
    	
		public PushMessage sendMessage() {
			if (channel == null)
				return null;

			String addr = channel.remoteAddress().toString();
			if (msgInfo.isAlreadySent(addr))
				return null;
			
			PushMessage msg = msgInfo.getMessage();
			channel.writeAndFlush(msg);
			msgInfo.addSentAddr(addr);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			return msg;
		}
	}
}
