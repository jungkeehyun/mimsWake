package com.mims.wake.server.property;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceType {
	// Service Type
	public static String TCPSOCKET = "server.tcpsocket";
	public static String WEBSOCKET = "client.websocket";
	public static String FILESOCKET = "server.filesocket";

	// File Push & Polling Extension
	public static String EXE_PUSH_SIDE = "msg";
	public static String EXE_POLLING_SIDE = "json";
	
	public static Semaphore semaphorePopStack = new Semaphore(1);
	
	public static Lock mutexPopStack = new ReentrantLock(true);
}
