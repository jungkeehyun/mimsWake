package com.mims.wake.server.property;

import java.util.HashMap;
import java.util.Map;

public enum S2EType {
	
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
     * 속력
     */
    SPD(6),
    /**
     * 군대부호코드
     */
    WARSBLCD(7);
    
    
	private int value;
    private static Map map = new HashMap<>();

    private S2EType(int value) {
        this.value = value;
    }

    static {
        for (S2EType s2eType : S2EType.values()) {
            map.put(s2eType.value, s2eType);
        }
    }

    public static String valueOf(int s2eType) {
    	return map.get(s2eType).toString();
    }
    
    public int getValue() {
        return value;
    }
}
