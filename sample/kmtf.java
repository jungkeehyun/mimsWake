import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class kmtf {

	protected static final String KMTF_INTRO = "INTRO//";
	protected static final String KMTF_HEADER = "KMTF/";
	protected static final String KMTF_BODY = "BODY//";
	protected static final String KMTF_CLOSE = "CLOSE//";
	protected static boolean hasKmtfIntro;
	protected static boolean hasKmtfHeader;
	protected static boolean hasKmtfBody;
	protected static boolean hasKmtfClose;

	public static void main(String[] args) throws Exception {
		hasKmtfIntro = false;
		hasKmtfHeader = false;
		hasKmtfBody = false;
		hasKmtfClose = false;

		StringBuffer kmtfMsg = new StringBuffer();
		kmtfMsg.append("INTRO//" + "\n");
		kmtfMsg.append("KMTF/OPER/2.0/MIKJ0250/KJCCS/MIMS/20190917091226/C//"+ "\n");
		kmtfMsg.append("OPT/ACKREQ:ACKID//"+ "\n");
		kmtfMsg.append("OPT/LT:0//"+ "\n");
		kmtfMsg.append("BODY//"+ "\n");
		kmtfMsg.append("MAP0010/iJgRbVoJqJ/wZiEgMiQtLqDtFkMb/license.xml/D/:/file/license.xml/uN//"+ "\n");
		kmtfMsg.append("MAP0010/iJgRbVoJqJ/wZiEgMiQtLqDtFkMb/license.xml/D/:/file/license.xml/uN//"+ "\n");
		kmtfMsg.append("CLOSE//"+ "\n");

		KMTFMessage message = parseFormat(kmtfMsg.toString());

		System.out.println("------------------");
		System.out.println("kmtfId : " + message.getKmtfId());
		System.out.println("sourceSystemId : " + message.getSourceSystemId());
		System.out.println("cudm : " + message.getCudm());
		System.out.println("mode : " + message.getMode());
		System.out.println("msgSeq : " + message.getMsgSeq());
		System.out.println("------------------");

		List<Set> setList = message.getSetList();
		for(Set s : setList){
			LinkedHashMap<Integer, Field> map = s.getFieldMap();
			for(Object key : map.keySet()) {
				Field ff = map.get(key);
				System.out.println(s.getSid()+" | " +ff.getIndex()+" | " + ff.getValue());
			}
			System.out.println("");
		}

		//결과
		//------------------
		//kmtfId : MIKJ0250
		//sourceSystemId : MIMS
		//cudm : C
		//mode : OPER
		//msgSeq : 0
		//------------------
		//MAP0010 | 0 | iJgRbVoJqJ
		//MAP0010 | 1 | wZiEgMiQtLqDtFkMb
		//MAP0010 | 2 | license.xml
		//MAP0010 | 3 | D
		//MAP0010 | 4 | :
		//MAP0010 | 5 | file
		//MAP0010 | 6 | license.xml
		//MAP0010 | 7 | uN
		//
		//MAP0010 | 0 | iJgRbVoJqJ
		//MAP0010 | 1 | wZiEgMiQtLqDtFkMb
		//MAP0010 | 2 | license.xml
		//MAP0010 | 3 | D
		//MAP0010 | 4 | :
		//MAP0010 | 5 | file
		//MAP0010 | 6 | license.xml
		//MAP0010 | 7 | uN

	}

	/**
	 * Method Name : parseFormat
	 * Description : parseFormat
	 * @param kmtf
	 * @return
	 * @throws IIMException
	 * KMTFMessage
	 */
	public static KMTFMessage parseFormat(String kmtf) throws Exception {
		KMTFMessage message = new KMTFMessage();
		StringReader sr = new StringReader(kmtf);
		BufferedReader br = new BufferedReader(sr);

		String line = null;
		boolean headerFlag = false;
		boolean bodyFlag = false;

		List setList = new ArrayList();
		try {
			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.equals("\n"))
					continue;
				if (line.length() == 0) {
					continue;
				}
				if (line.startsWith("INTRO//")) {
					headerFlag = true;
					hasKmtfIntro = true;
				}

				if (line.startsWith("BODY//")) {
					bodyFlag = true;
					headerFlag = false;
					hasKmtfBody = true;
				}

				if (line.startsWith("CLOSE//")) {
					message.setSetList(setList);
					bodyFlag = false;
					hasKmtfClose = true;
				}

				if (headerFlag) {
					if (line.startsWith("KMTF/")) {
						List arr = parseLine(line);

						message.setMode((String) arr.get(1));
						message.setVersion((String) arr.get(2));
						message.setKmtfId((String) arr.get(3));
						message.setDestnationSystemId((String) arr.get(4));
						message.setSourceSystemId((String) arr.get(5));
						message.setCreateTime((String) arr.get(6));
						message.setCudm((String) arr.get(7));
						message.setMessageId(message.getKmtfId() + "_V"
								+ message.getVersion().replaceAll("[.]", ""));

						hasKmtfHeader = true;
					}

					if (line.startsWith("OPT/")) {
						String option = line.substring("OPT/".length(),
								line.length() - 2);

						if ((option.startsWith("ACKREQ"))
								|| (option.startsWith("ACKRES"))) {
							String[] headerInfo = option.split(":");
							message.setOptAckType(headerInfo[0]);
							message.setOptAckId(headerInfo[1]);
						} else if (option.startsWith("ERROR")) {
							String[] headerInfo = option.split(":");
							String[] error = headerInfo[1].split(";");

							message.setOptErrorCode(new Integer(error[0])
									.intValue());
							if (error.length >= 2) {
								message.setOptStrError(error[1]);
							}
						} else if (option.startsWith("LT")) {
							String[] headerInfo = option.split(":");
							message.setOptLt(new Long(headerInfo[1])
									.longValue());
						} else if (option.startsWith("MNG")) {
							String[] headerInfo = option.split(":");
							message.setOptMng(headerInfo[1]);
						} else if (option.startsWith("TEST")) {
							String[] headerInfo = option.split(":");
							message.setOptTest(headerInfo[1]);
						} else if (option.startsWith("ETC")) {
							String[] headerInfo = option.split(":");
							message.setOptEtc(headerInfo[1]);
						}
					}
				}

				if (!(bodyFlag))
					continue;
				if (line.startsWith("BODY//")) {
					continue;
				}

				List arr = parseLine(line);

				String sid = (String) arr.get(0);

				Set set = new Set();
				set.setSid(sid);

				for (int j = 1; j < arr.size(); ++j) {
					Field field = new Field();
					field.setValue(unescape((String) arr.get(j)));
					set.add(field);
				}
				setList.add(set);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
		return message;
	}

	/**
	 * Method Name : unescape
	 * Description : unescape
	 * @param sourceString
	 * @return
	 * @throws IIMException
	 * String
	 */
	public static String unescape(String sourceString) throws Exception {
		try {
			StringBuilder sbEscapre = new StringBuilder();

			int length = sourceString.length();

			if ((length == 1) && ("-".equals(sourceString)))
				return "";

			int i = 0;
			for (i = 0; i < length; ++i) {
				char f = sourceString.charAt(i);
				if (f != '\\') {
					sbEscapre.append(f);
				} else {
					if (i + 1 >= length) {
						continue;
					}
					++i;
					char s = sourceString.charAt(i);
					if (s == '\\') {
						sbEscapre.append('\\');
					} else if (s == 't') {
						sbEscapre.append('\t');
					} else if (s == 'n') {
						sbEscapre.append('\n');
					} else if (s == 'r') {
						sbEscapre.append('\r');
					} else if (s == 'f') {
						sbEscapre.append('\f');
					} else if (s == '/') {
						sbEscapre.append('/');
					} else if (s == '-') {
						sbEscapre.append('-');
					} else if (s == ':') {
						sbEscapre.append(':');
					} else if (s == ';') {
						sbEscapre.append(';');
					} else {
						sbEscapre.append(f);
						sbEscapre.append(s);
					}
				}
			}
			return sbEscapre.toString();
		} catch (Exception e) {
			throw new Exception(new StringBuilder().append("Escape(")
					.append(sourceString).append(")").toString(), e);
		}
	}
	/**
	 * Method Name : parseLine
	 * Description : parseLine
	 * @param setLine
	 * @return
	 * @throws IIMException
	 * ArrayList
	 */
	public static ArrayList parseLine(String setLine) throws Exception {
		if (!(setLine.endsWith("//"))) {
//			throw new IIMException(new StringBuilder()
//					.append("\"//\"문자로 종료되지 않았습니다.;").append(setLine)
//					.toString(), "IIM-00010");
		}
		ArrayList alField = new ArrayList();
		StringBuilder sbField = null;

		int i = 0;
		char fieldDelimeter = "/".charAt(0);

		while (i < setLine.length()) {
			char cChar = setLine.charAt(i);
			if (cChar == fieldDelimeter) {
				if ((sbField != null) && (sbField.toString().length() > 0)) {
					alField.add(sbField.toString());
				}

				sbField = null;
			} else if (cChar == '\\') {
				if (sbField == null) {
					sbField = new StringBuilder();
				}

				sbField.append(new StringBuilder().append("\\")
						.append(setLine.charAt(i + 1)).toString());
				++i;
			} else {
				if (sbField == null) {
					sbField = new StringBuilder();
				}

				sbField.append(cChar);
			}

			++i;
		}

		return alField;
	}
}
