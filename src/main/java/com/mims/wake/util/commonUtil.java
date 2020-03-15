package com.mims.wake.util;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Pattern;

public class commonUtil {

	private static String OS = System.getProperty("os.name").toLowerCase();

	// pathToken
	public static String pathToken() {
		//System.out.println(OS);

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
				// System.out.println("폴더가 생성되었습니다.");
			} catch (Exception e) {
				e.getStackTrace();
			}
		} else {
			// System.out.println("이미 폴더가 생성되어 있습니다.");
		}
	}

	public static Vector<String> getFileNames(String targetDirName, String fileExt) {
		Vector<String> fileNames = new Vector<String>();
		File dir = new File(targetDirName);
		fileExt = fileExt.toLowerCase();
		String pathToken = pathToken();

		if (dir.isDirectory()) {
			String dirName = dir.getPath();
			String[] filenames = dir.list(null);

			for (int iFile = 0; iFile < filenames.length; ++iFile) {
				String filename = filenames[iFile];
				String fullFileName = dirName + pathToken + filename;
				File file = new File(fullFileName);

				boolean isDirectory = file.isDirectory();
				if (!isDirectory && filename.toLowerCase().endsWith(fileExt)) {
					fileNames.add(fullFileName);
				}
			}
		}

		return fileNames;
	}
	
	public static String getCurrentPath(String subPath) {
		String token = pathToken();
		String[] arrWord = subPath.split("");
		if(arrWord[0].equals(token))
			subPath.replaceFirst(token, "");
		return System.getProperty("user.dir") + token + subPath;
	}
	
	public static boolean isFullPathName(String pathFile) {
		String token = commonUtil.pathToken();
		String[] arrWord = pathFile.split("");
		if(!Pattern.matches("^[a-zA-Z0-9]*$", arrWord[0]))
			return false;
		if(!arrWord[1].equals(":"))
			return false;
		if(!token.equals(arrWord[2]))
			return false;
		return true;
	}
	
	public static String dayTimeString() {
		long time = System.currentTimeMillis();
		SimpleDateFormat dayTime  = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = dayTime.format(new Date(time));
		return dateString;
	}
}
