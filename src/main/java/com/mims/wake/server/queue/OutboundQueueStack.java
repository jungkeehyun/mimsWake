package com.mims.wake.server.queue;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mims.wake.common.PushMessage;

public class OutboundQueueStack extends Thread {
	private static final Logger LOG = LoggerFactory.getLogger(OutboundQueueStack.class);

	private final OutboundQueueManager outboundQueueManager;
    private final BlockingQueue<PushMessage> queue;		// message queue
    private Vector<PushMessage> stack;					// message stack storage

	/**
	 * constructor with parameters
	 * 
	 */
	public OutboundQueueStack(int capacity, OutboundQueueManager outboundMngr) {
		this.outboundQueueManager = outboundMngr;
		this.stack = new Vector<PushMessage>();
        this.queue = new LinkedBlockingQueue<PushMessage>(capacity);
	}

	/**
	 * 미전송 메시지 보관
	 * 
	 * @param msg : push message
	 */
	public void pushStack(PushMessage msg) {
		stack.add(msg);
	}

	/**
	 * 미전송 메세지 꺼내기
	 * 
	 * @param serviceId    : service id
	 */
	public void popStack(String serviceId) {
		try {
			if (serviceId == null || serviceId.isEmpty())
				throw new Exception();
			
			Vector<PushMessage> stock = new Vector<PushMessage>();
			for(int ix=0; ix < stack.size(); ++ix) {
				PushMessage msg = stack.get(ix);
				String sid = msg.getServiceId();
				if (sid.equals(serviceId))
					queue.offer(msg);
				else
					stock.add(msg);
			}
			stack = stock;
		} catch (Exception e) {
			LOG.error("[OutboundQueueStack popStack] >>>>>");
		}
	}
	
    /**
     * 쓰레드 종료
     */
    public void shutdown() {
        this.interrupt();
    }
    
    /**
     * 큐에서 메시지를 추출하여 클라이언트 채널에 전송
     * @see java.lang.Thread#run()
     */
    @Override
	public void run() {
		while (!isInterrupted()) {
			PushMessage msg = null;
			try {
				msg = queue.take();
			} catch (InterruptedException e) {
				break;
			}
			
			if (msg != null && outboundQueueManager != null) {
				outboundQueueManager.transfer(msg);
				LOG.info("[{}] [{}] [{}] Pop Stack {}", getName(), msg.getServiceId(), msg.getClientId(), msg);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
