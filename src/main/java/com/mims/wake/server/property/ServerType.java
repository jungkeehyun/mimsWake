package com.mims.wake.server.property;

/**
 * Outbound Server 유형 정의
 */
public enum ServerType {

	/**
	 * TCP Socket 통신 방식
	 */
	TCPSOCKET,
	/**
	 * WebSocket 통신 방식
	 */
	WEBSOCKET,

	// [+] YPK
	/**
	 * File 저장
	 */
	FILESOCKET;
	// [-]
}
