// [YPK]
package com.mims.wake.server.inbound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mims.wake.common.PushMessage;
import com.mims.wake.server.queue.InboundQueue;
import com.mims.wake.util.commonUtil;

public class InboundFilePolling {
	private static final Logger LOG = LoggerFactory.getLogger(InboundFilePolling.class);

	private int interval;
	private String subPath;
	private Timer timer;
	private Map<String, InboundQueue> inboundQueues;

	public InboundFilePolling(int interval, String subPath) {
		this.interval = interval;
		this.subPath = subPath;
		this.setInboundQueues(null);
	}

	public void startup(Map<String, InboundQueue> inboundQueues) {
		try {
			this.timer = new Timer();
			this.setInboundQueues(inboundQueues);

			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					fileRead();
				}
			};

			this.timer.schedule(timerTask, 0, this.interval);
			LOG.info("[InboundFilePolling] started, file paht " + this.subPath);

		} catch (Exception e) {
			LOG.error("[InboundFilePolling] failed to startup", e);
			shutdown();
		}
	}

	public void fileRead() {
		try {
			String token = commonUtil.pathToken();
			String path = System.getProperty("user.dir") + token + subPath;

			Vector<String> arrFile = commonUtil.getFileNames(path, "json");
			for (int ix = 0; ix < arrFile.size(); ++ix) {
				String pathFile = arrFile.get(ix);

				File file = new File(pathFile);
				if (file.exists() == false)
					continue;

				// parsing gid and cid
				String fname = file.getName();
				int pos = fname.lastIndexOf(".");
				fname = fname.substring(0, pos);
				pos = fname.lastIndexOf("_");
				String groupId = fname.substring(0, pos);
				String clientId = fname.substring(pos + 1, fname.length());

				FileReader fileReader = new FileReader(file);
				BufferedReader bufReader = new BufferedReader(fileReader);
				String buff = "";
				String msg = "";
				while ((buff = bufReader.readLine()) != null) {
					msg += buff;
				}
				bufReader.close();
				fileReader.close();
				file.delete(); // read only once

				// send to websocket
				String serviceId = "client.websocket";
				PushMessage pushMsgWeb = new PushMessage(serviceId, groupId, clientId, msg);
				inboundQueues.get(serviceId).enqueue(pushMsgWeb);

				// send to tcpsocket
				serviceId = "polling.file";
				PushMessage pushMsgTcp = new PushMessage(serviceId, groupId, clientId, msg);
				inboundQueues.get(serviceId).enqueue(pushMsgTcp);

				LOG.info("[InboundFilePolling] Scan : " + msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		if (this.timer != null) {
			this.timer.cancel();
		}
		LOG.info("[InboundFilePolling] shutdown");
	}

	public Map<String, InboundQueue> getInboundQueues() {
		return inboundQueues;
	}

	public void setInboundQueues(Map<String, InboundQueue> inboundQueues) {
		this.inboundQueues = inboundQueues;
	}
}
