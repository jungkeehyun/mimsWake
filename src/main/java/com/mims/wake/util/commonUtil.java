package com.mims.wake.util;

import java.io.File;

public class commonUtil {

	private static String OS = System.getProperty("os.name").toLowerCase();

	// pathToken
	public static String pathToken() {
		System.out.println(OS);

		String token = "/";
		if (isWindows()) {
			token = "\\";
		} else if (isMac()) {
			token = "/";
		} else if (isUnix()) {
			token = "/";
		} else if (isSolaris()) {
			token = "/";
		} else {
			System.out.println("Your OS is not support!!");
		}
		return token;
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
	
	// makeFolder
	public static void makeFolder(String path) {
		File folder = new File(path);
		if (!folder.exists()) {
			try {
				folder.mkdir();
				//System.out.println("폴더가 생성되었습니다.");
			} catch (Exception e) {
				e.getStackTrace();
			}
		} else {
			//System.out.println("이미 폴더가 생성되어 있습니다.");
		}
	}
}
