// [YPK]
package com.mims.wake.server.inbound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mims.wake.common.PushMessage;
import com.mims.wake.server.property.PushBaseProperty;
import com.mims.wake.server.property.ServiceType;
import com.mims.wake.server.queue.InboundQueue;
import com.mims.wake.util.commonUtil;

public class InboundFilePolling {
	private static final Logger LOG = LoggerFactory.getLogger(InboundFilePolling.class);

	private int interval;
	private String subPath;
	private String targetPath;
	private Timer timer;
	private Map<String, InboundQueue> inboundQueues;

	public InboundFilePolling(PushBaseProperty property) {
		this.interval = property.getInboundPollingInterval();
		this.subPath = property.getOutboundServerWsUri();
		this.setInboundQueues(null);
	}

	public void startup(Map<String, InboundQueue> inboundQueues) {
		try {
			if(commonUtil.isFullPathName(subPath))
				this.targetPath = subPath; 
			else	
				this.targetPath = commonUtil.getCurrentPath(subPath);
			
			this.timer = new Timer();
			this.setInboundQueues(inboundQueues);

			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					fileRead();
				}
			};

			this.timer.schedule(timerTask, 0, this.interval);
			LOG.info("[InboundFilePolling] started, " + this.targetPath);

		} catch (Exception e) {
			LOG.error("[InboundFilePolling] failed to startup", e);
			shutdown();
		}
	}

	public void fileRead() {
		try {
			Vector<String> arrFile = commonUtil.getFileNames(this.targetPath, ServiceType.EXE_PUSH_SIDE);
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

				// read message
				String msg = "";
				FileReader fileReader = new FileReader(file);
				BufferedReader bufReader = new BufferedReader(fileReader);
				String buff = "";
				while ((buff = bufReader.readLine()) != null) {
					msg += buff;
				}
				bufReader.close();
				fileReader.close();
				backupFile(pathFile, file.getParentFile().toString());
				//file.delete(); // read only once

				PushMessage pushMsg = new PushMessage("", groupId, clientId, msg);
				inboundQueues.forEach((sid, queue) -> {
					queue.enqueue(
							new PushMessage(sid, pushMsg.getGroupId(), pushMsg.getClientId(), pushMsg.getMessage()));
				});

				LOG.info("[InboundFilePolling] >>>>>>>>>>>>>>>>>>>> {}", msg);
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
	
	private void backupFile(String pathFile, String path) {
		String pathToken = commonUtil.pathToken();
		long time = System.currentTimeMillis();
		SimpleDateFormat dayTime  = new SimpleDateFormat("yyyyMMddHHmmss");
		String name = dayTime.format(new Date(time));
		String newPathFile = path + pathToken + name + "." + ServiceType.EXE_POLLING_SIDE;

		File file = new File(pathFile);
		File fileNew = new File(newPathFile);
		if (file.exists())
			file.renameTo(fileNew);
	}
}
