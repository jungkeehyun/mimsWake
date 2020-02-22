/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public class KMTFMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private String msgSendType;
	private String uuid;
	private String attachFileUuid;
	private int msgSeq;
	private String kmtfId;
	private String createTime;
	private String sourceSystemId;
	private String destnationSystemId;
	private String linkSystemId;
	private String messageId;
	private String cudm;
	private String mode;
	private String version;
	private int priority = 4;
	private String optMng;
	private String optAckType;
	private String optAckId;
	private int optErrorCode = -1;
	private String optStrError;
	private long optLt = -1L;
	private String optTest;
	private String optEtc;
	private List<Set> setList;
	private List<AttachFileInfo> attachFileInfoList;
	private String kmtf = "";
	private long Timestamp;
	private String hubRecvDt = "";
	private String recvDt = "";
	private String resultFlag = "";
	private String errorId;
	private String remark = "";
	private String ackReqYn;
	private String crytCode = "0";
	private String cryptKey;
	private String cryptSetKey;
	private String routingSystemId;
	private String deliveredSystemId;
	private String logType;
	private Map<String, Integer> setCount;
	private boolean responseMsg = false;
	private String msgKind;
	private String rcvId;

	public KMTFMessage() {
		this.setList = new ArrayList();
		this.attachFileInfoList = new ArrayList();
		this.setCount = new HashMap();
	}

	public String getLinkSystemId() {
		return this.linkSystemId;
	}

	public void setLinkSystemId(String linkSystemId) {
		this.linkSystemId = linkSystemId;
	}

	public String getMsgSendType() {
		return this.msgSendType;
	}

	public void setMsgSendType(String msgSendType) {
		this.msgSendType = msgSendType;
	}

	public String getErrorId() {
		return this.errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public String getRecvDt() {
		return this.recvDt;
	}

	public void setRecvDt(String recvDt) {
		this.recvDt = recvDt;
	}

	public String getHubRecvDt() {
		return this.hubRecvDt;
	}

	public void setHubRecvDt(String hubRecvDt) {
		this.hubRecvDt = hubRecvDt;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getResultFlag() {
		return this.resultFlag;
	}

	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMsgSeq() {
		return this.msgSeq;
	}

	public void setMsgSeq(int msgSeq) {
		this.msgSeq = msgSeq;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getSourceSystemId() {
		return this.sourceSystemId;
	}

	public void setSourceSystemId(String sourceSystemId) {
		this.sourceSystemId = sourceSystemId;
	}

	public String getDestnationSystemId() {
		return this.destnationSystemId;
	}

	public void setDestnationSystemId(String destnationSystemId) {
		this.destnationSystemId = destnationSystemId;
	}

	public String getMessageId() {
		return this.messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getCudm() {
		return this.cudm;
	}

	public void setCudm(String cudm) {
		this.cudm = cudm;
	}

	public String getMode() {
		return this.mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getKmtf() {
		return this.kmtf;
	}

	public void setKmtf(String kmtf) {
		this.kmtf = kmtf;
	}

	public String getOptMng() {
		return this.optMng;
	}

	public void setOptMng(String optMng) {
		this.optMng = optMng;
	}

	public String getOptAckType() {
		return this.optAckType;
	}

	public void setOptAckType(String optAckType) {
		this.optAckType = optAckType;
	}

	public String getOptAckId() {
		return this.optAckId;
	}

	public void setOptAckId(String optAckId) {
		this.optAckId = optAckId;
	}

	public String getOptStrError() {
		return this.optStrError;
	}

	public void setOptStrError(String optStrError) {
		this.optStrError = optStrError;
	}

	public int getOptErrorCode() {
		return this.optErrorCode;
	}

	public void setOptErrorCode(int optErrorCode) {
		this.optErrorCode = optErrorCode;
	}

	public long getOptLt() {
		return this.optLt;
	}

	public void setOptLt(long optLt) {
		this.optLt = optLt;
	}

	public String getOptTest() {
		return this.optTest;
	}

	public void setOptTest(String optTest) {
		this.optTest = optTest;
	}

	public String getOptEtc() {
		return this.optEtc;
	}

	public void setOptEtc(String optEtc) {
		this.optEtc = optEtc;
	}

	public void addSet(Set set) {
		this.setList.add(set);
	}

	public List<Set> getSetList() {
		return this.setList;
	}

	public void setSetList(List<Set> setList) {
		this.setList = setList;
	}

	public long getTimestamp() {
		return this.Timestamp;
	}

	public void setTimestamp(long Timestamp) {
		this.Timestamp = Timestamp;
	}

	public void toMessage(TextMessage message) throws Exception {
		toMessage(message);
		message.setText(this.kmtf);
	}

	public void toMessage(ObjectMessage message) throws Exception {
		toMessage(message);
	}

	public void toMessage(BlobMessage message) throws Exception {
		toMessage(message);
	}

	public void setByMessage(TextMessage message) throws Exception {
		setByMessage(message);
		this.kmtf = message.getText();
	}

	public void setByMessage(ObjectMessage message) throws Exception {
		setByMessage(message);
	}

	public void setByMessage(BlobMessage message) throws Exception {
		setByMessage(message);
	}

	public List<AttachFileInfo> getAttachFileInfoList() {
		return this.attachFileInfoList;
	}

	public void setAttachFileList(List<AttachFileInfo> attachFileInfoList) {
		this.attachFileInfoList = attachFileInfoList;
	}
	public String getCryptKey() {
		return this.cryptKey;
	}

	public void setCryptKey(String cryptKey) {
		this.cryptKey = cryptKey;
	}

	public String getCryptSetKey() {
		return this.cryptSetKey;
	}

	public void setCryptSetKey(String cryptSetKey) {
		this.cryptSetKey = cryptSetKey;
	}

	public String getAttachFileUuid() {
		return this.attachFileUuid;
	}

	public void setAttachFileUuid(String attachFileUuid) {
		this.attachFileUuid = attachFileUuid;
	}

	public void setKmtfId(String kmtfId) {
		this.kmtfId = kmtfId;
	}

	public String getKmtfId() {
		return this.kmtfId;
	}

	public void setCrytCode(String crytCode) {
		this.crytCode = crytCode;
	}

	public String getCrytCode() {
		return this.crytCode;
	}

	public boolean hasAttachFile() {
		return (this.attachFileInfoList.size() > 0);
	}

	public boolean isTest() {
		if (this.optTest == null) {
			return false;
		}

		return (this.optTest.length() > 0);
	}

	public String getRoutingSystemId() {
		return this.routingSystemId;
	}

	public void setRoutingSystemId(String routingSystemId) {
		this.routingSystemId = routingSystemId;
	}

	public String getDeliveredSystemId() {
		return this.deliveredSystemId;
	}

	public void setDeliveredSystemId(String deliveredSystemId) {
		this.deliveredSystemId = deliveredSystemId;
	}

	public List<Map<String, Object>> getData() {
		List rowMapList = new ArrayList();

		List<Set> setList = getSetList();

		for (Set set : setList) {
			Map rowMap = new LinkedHashMap();

			rowMap.put("ROW_SID", set.getSid());

			LinkedHashMap fieldMap = set.getFieldMap();

			for (Iterator iterator = fieldMap.keySet().iterator(); iterator
					.hasNext();) {
				Field field = (Field) fieldMap.get(iterator.next());
				rowMap.put(field.getName(), field.getValue());
			}

			rowMapList.add(rowMap);
		}

		return rowMapList;
	}

	public void setAckReqYn(String ackReqYn) {
		this.ackReqYn = ackReqYn;
	}

	public String getAckReqYn() {
		return this.ackReqYn;
	}

	public String getLogType() {
		return this.logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public boolean isResponseMsg() {
		return this.responseMsg;
	}

	public void setResponseMsg(boolean responseMsg) {
		this.responseMsg = responseMsg;
	}

	public void setMsgKind(String msgKind) {
		this.msgKind = msgKind;
	}

	public String getMsgKind() {
		return this.msgKind;
	}

	public void setRcvId(String rcvId) {
		this.rcvId = rcvId;
	}

	public String getRcvId() {
		return this.rcvId;
	}

	public Map<String, Integer> getSetCount() {
		return this.setCount;
	}

	public void setSetCount(Map<String, Integer> setCount) {
		this.setCount = setCount;
	}

	public KMTFMessage clone() {
		KMTFMessage clone = new KMTFMessage();

		clone.setLinkSystemId(this.linkSystemId);
		clone.setMsgSendType(this.msgSendType);
		clone.setErrorId(this.errorId);
		clone.setRecvDt(this.recvDt);
		clone.setHubRecvDt(this.hubRecvDt);
		clone.setRemark(this.remark);
		clone.setResultFlag(this.resultFlag);
		clone.setUuid(this.uuid);
		clone.setMsgSeq(this.msgSeq);
		clone.setCreateTime(this.createTime);
		clone.setSourceSystemId(this.sourceSystemId);
		clone.setDestnationSystemId(this.destnationSystemId);
		clone.setMessageId(this.messageId);
		clone.setCudm(this.cudm);
		clone.setMode(this.mode);
		clone.setVersion(this.version);
		clone.setPriority(this.priority);
		clone.setKmtf(this.kmtf);
		clone.setOptMng(this.optMng);
		clone.setOptAckType(this.optAckType);
		clone.setOptAckId(this.optAckId);
		clone.setOptStrError(this.optStrError);
		clone.setOptErrorCode(this.optErrorCode);
		clone.setOptLt(this.optLt);
		clone.setOptTest(this.optTest);
		clone.setOptEtc(this.optEtc);
		clone.setSetList(this.setList);
		clone.setTimestamp(this.Timestamp);
		clone.setAttachFileList(this.attachFileInfoList);
		clone.setCryptKey(this.cryptKey);
		clone.setCryptSetKey(this.cryptSetKey);
		clone.setAttachFileUuid(this.attachFileUuid);
		clone.setKmtfId(this.kmtfId);
		clone.setCrytCode(this.crytCode);
		clone.setRoutingSystemId(this.routingSystemId);
		clone.setDeliveredSystemId(this.deliveredSystemId);
		clone.setAckReqYn(this.ackReqYn);
		clone.setLogType(this.logType);
		clone.setResponseMsg(this.responseMsg);
		clone.setMsgKind(this.msgKind);
		clone.setRcvId(this.rcvId);
		clone.setSetCount(this.setCount);

		return clone;
	}

	public void addSetCount(String setId, int setCountNum) {
		this.setCount.put(setId, Integer.valueOf(setCountNum));
	}
}