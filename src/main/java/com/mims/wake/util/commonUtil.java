package com.mims.wake.util;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class commonUtil {

	public static Semaphore semaphorePopStack = new Semaphore(1);
	
	public static Lock mutexStack = new ReentrantLock(true);
	
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

		if(fileNames.size() > 1)
			Collections.sort(fileNames, new CompByDate());

		return fileNames;
	}
	
	public static class CompByDate implements Comparator<String> { 		
		public int compare(String pf0, String pf1) {
			File file0 = new File(pf0);
			File file1 = new File(pf1);
			String one = file0.lastModified() + "";
			String two = file1.lastModified() + "";
			return one.compareTo(two);
		} 
	}
	
	public static File[] getFileList(String targetDirName, String fileExt) {
		File[] files = new File(targetDirName).listFiles();
		File[] fileNames = new File[files.length * 2];
		File dir = new File(targetDirName);
		fileExt = fileExt.toLowerCase();
		String pathToken = pathToken();
		int inx = 0;

		if (dir.isDirectory()) {
			String dirName = dir.getPath();
			String[] filenames = dir.list(null);

			for (int iFile = 0; iFile < filenames.length; ++iFile) {
				String filename = filenames[iFile];
				String fullFileName = dirName + pathToken + filename;
				File file = new File(fullFileName);

				boolean isDirectory = file.isDirectory();
				if (!isDirectory && filename.toLowerCase().endsWith(fileExt)) {
					fileNames[inx++] = file;
				}
			}
		}
		
		return sortFileList(fileNames, COMPARETYPE_DATE);
	}
	
	public static int COMPARETYPE_NAME = 0;
	public static int COMPARETYPE_DATE = 1;
	public static File[] sortFileList(File[] files, final int compareType) {
		Arrays.sort(files, new Comparator<Object>() {
			@Override
			public int compare(Object object1, Object object2) {

				String s1 = "";
				String s2 = "";

				if (compareType == COMPARETYPE_NAME) {
					s1 = ((File) object1).getName();
					s2 = ((File) object2).getName();
				} else if (compareType == COMPARETYPE_DATE) {
					s1 = ((File) object1).lastModified() + "";
					s2 = ((File) object2).lastModified() + "";
				}

				return s1.compareTo(s2);
			}
		});

		return files;
	}
	
	public static String getCurrentPath(String subPath) {
		String token = pathToken();
		String[] arrWord = subPath.split("");
		if(arrWord.length > 0 && arrWord[0].equals(token))
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
