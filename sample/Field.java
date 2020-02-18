import java.io.Serializable;


public class Field implements Serializable {
	private static final long serialVersionUID = 1L;
	private int index;
	private String name;
	private int type;
	private String value;
	private String attachFileUuid;

	public String getAttachFileUuid() {
		return this.attachFileUuid;
	}

	public void setAttachFileUuid(String attachFileUuid) {
		this.attachFileUuid = attachFileUuid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
