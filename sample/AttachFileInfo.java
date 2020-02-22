import java.io.Serializable;


public class AttachFileInfo implements Serializable {
	private static final String DELIMETER = "|";
	private int rowIndex;
	private String sid;
	private int fieldSeq;
	private String fieldId;
	private String attachFileUuid;
	private String fileName;
	private long fileSize;
	private String filePath;

	public AttachFileInfo() {
		this.rowIndex = -1;

		this.fieldSeq = -1;

		this.fileSize = -1L;
	}

	public int getFieldSeq() {
		return this.fieldSeq;
	}

	public void setFieldSeq(int fieldSeq) {
		this.fieldSeq = fieldSeq;
	}

	public String getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getRowIndex() {
		return this.rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String getSid() {
		return this.sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getAttachFileUuid() {
		return this.attachFileUuid;
	}

	public void setAttachFileUuid(String attachFileUuid) {
		this.attachFileUuid = attachFileUuid;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setJMSString(String jmsStr) {
		if (jmsStr == null)
			return;
		if (jmsStr.length() < 1) {
			return;
		}
		String[] splitList = jmsStr.split("\\|");

		int cnt = 0;

		this.rowIndex = new Integer(splitList[(cnt++)]).intValue();
		this.sid = splitList[(cnt++)];
		this.fieldSeq = new Integer(splitList[(cnt++)]).intValue();
		this.fieldId = splitList[(cnt++)];
		this.attachFileUuid = splitList[(cnt++)];

		this.fileName = splitList[(cnt++)];
		this.fileSize = new Long(splitList[(cnt++)]).longValue();
		this.filePath = splitList[(cnt++)];
	}

	public String getJMSString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.rowIndex).append("|").append(this.sid).append("|")
				.append(this.fieldSeq).append("|").append(this.fieldId)
				.append("|").append(this.attachFileUuid).append("|")
				.append(this.fileName).append("|").append(this.fileSize)
				.append("|").append(this.filePath);

		return sb.toString();
	}
}
