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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mims.wake.common.PushMessage;
import com.mims.wake.server.property.PushBaseProperty;
import com.mims.wake.server.property.ServiceType;
import com.mims.wake.server.queue.InboundQueue;
import com.mims.wake.util.commonUtil;

public class InboundFilePolling {

	private static final Logger logger = LogManager.getLogger(InboundFilePolling.class);

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
			logger.info("[InboundFilePolling] started, " + this.targetPath);

		} catch (Exception e) {
			logger.error("[InboundFilePolling] failed to startup", e);
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

				// get groupId and clientId
				String[] ids = getID(file.getName());
				if(ids.length != 2)
					continue;

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
				completePolling(file.getParent().toString(), file.getName());
				//file.delete(); // read only once

				PushMessage pushMsg = new PushMessage("", ids[0], ids[1], msg);
				inboundQueues.forEach((sid, queue) -> {
					queue.enqueue(
							new PushMessage(sid, pushMsg.getGroupId(), pushMsg.getClientId(), pushMsg.getMessage()));
				});

				logger.info("[InboundFilePolling] >>>>>>>>>> {}", msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		if (this.timer != null) {
			this.timer.cancel();
		}
		logger.info("[InboundFilePolling] shutdown");
	}

	public Map<String, InboundQueue> getInboundQueues() {
		return inboundQueues;
	}

	public void setInboundQueues(Map<String, InboundQueue> inboundQueues) {
		this.inboundQueues = inboundQueues;
	}
	
	private String[] getID(String fileName) {
		int pos = fileName.indexOf("_");
		String ids = fileName.substring(pos + 1, fileName.length());
		pos = ids.lastIndexOf("_");
		String groupId = ids.substring(0, pos);
		int pos2 = ids.lastIndexOf(".");
		String clientId = ids.substring(pos + 1, pos2);
		String[] arrIds = new String[] { groupId, clientId };
		if(arrIds.length != 2)
			logger.error("[InboundFilePolling] incorrect groudId or clientId");

		return arrIds;
	}

	private void completePolling(String path, String fileName) {
		String token = commonUtil.pathToken();
		String oldPathFile = path + token + fileName;
		
		int pos = fileName.indexOf("_");
		String newFileName = fileName.substring(0, pos);
		String newPathFile = path + token + newFileName + "." + ServiceType.EXE_POLLING_SIDE;
		
		File file = new File(oldPathFile);
		File fileNew = new File(newPathFile);
		if (file.exists())
			file.renameTo(fileNew);
		else
			logger.error("[InboudFilePolling] {} file not found.", oldPathFile);
	}
}
