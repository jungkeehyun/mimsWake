import java.io.Serializable;
import java.util.LinkedHashMap;


public class Set implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sid;
	private int setNum;
	private LinkedHashMap<Integer, Field> fieldMap;

	public Set() {
		this.fieldMap = new LinkedHashMap();
	}

	public String getSid() {
		return this.sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public LinkedHashMap<Integer, Field> getFieldMap() {
		return this.fieldMap;
	}

	public void setFieldMap(LinkedHashMap<Integer, Field> fieldMap) {
		this.fieldMap = fieldMap;
	}

	public int getSetNum() {
		return this.setNum;
	}

	public void setSetNum(int setNum) {
		this.setNum = setNum;
	}

	public void add(Field field) {
		field.setIndex(this.fieldMap.size());
		this.fieldMap.put(Integer.valueOf(field.getIndex()), field);
	}
}