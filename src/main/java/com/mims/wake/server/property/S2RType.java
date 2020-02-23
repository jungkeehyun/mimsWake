package com.mims.wake.server.property;

import java.util.HashMap;
import java.util.Map;

public enum S2RType {

	/**
     * 해상항적피아식별코드
     */
	SEATIFFCD(1),
    /**
     * 식별번호
     */
    IDNO(2),
    /**
     * 함정번호
     */
    WRSHPNO(3),
    /**
     * 위치
     */
    LOCTN(4),
    /**
     * 방위각
     */
    AZ(5),
    /**
     * 해상항적표적임무코드
     */
    SEATTGTMSCD(6),
    /**
     * 해상항적국가코드
     */
    SEATNATCD(7),
    /**
     * 속력
     */
    SPD(8),
    /**
     * 해상항적표적하위범주코드
     */
    SEATTGTLRCCD(9),
    /**
     * 해상항적표적종류코드
     */
    SEATTGTKNDCD(10),
    /**
     * 유효일시
     */
	EFTV_DTTM(11),
	/**
	 * 항적실시간구분코드
	 */
	TRACRLTDCD(12);
    
    
	private int value;
    private static Map map = new HashMap<>();

    private S2RType(int value) {
        this.value = value;
    }

    static {
        for (S2RType s2rType : S2RType.values()) {
            map.put(s2rType.value, s2rType);
        }
    }

    public static String valueOf(int s2rType) {
    	return map.get(s2rType).toString();
    }
    
    public int getValue() {
        return value;
    }
}
