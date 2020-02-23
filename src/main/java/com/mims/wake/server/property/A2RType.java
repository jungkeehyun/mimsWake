package com.mims.wake.server.property;

import java.util.HashMap;
import java.util.Map;

public enum A2RType {
	
	/**
     * 항공기 번호
     */
	ACFTNO(1),
    /**
     * 항공기피아구분코드
     */
    ACFTFNFDVCD(2),
    /**
     * 고도
     */
    ALT(3),
    /**
     * 항공기대수
     */
    ACFT_CNTUNT(4),
    /**
     * 호출부호
     */
    CLSGN(5),
    /**
     * 이륙기지번호
     */
    TKOF_BASENO(6),
    /**
     * 속력
     */
    SPD(7),
    /**
     * 항공기종명
     */
    KNDAP_NM(8),
    /**
     * 위경도좌표
     */
    LTDLNGT_COORD(9),
    /**
     * 방위각
     */
    AZ(10);
	
	private int value;
    private static Map map = new HashMap<>();

    private A2RType(int value) {
        this.value = value;
    }

    static {
        for (A2RType a2rType : A2RType.values()) {
            map.put(a2rType.value, a2rType);
        }
    }

    /*
    public static A2RType valueOf(int a2rType) {
        return (A2RType) map.get(a2rType);
    }
    */

    public static String valueOf(int a2rType) {
    	return map.get(a2rType).toString();
    }
    
    public int getValue() {
        return value;
    }
}
