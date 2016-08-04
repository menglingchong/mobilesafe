package com.heima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.MemoryFile;

//获取进程个数和剩余内存的工具类
public class TaskUtil {
	//获取系统正在运行的进程的个数
	public static  int getProcessCount(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		return runningAppProcesses.size();
	}
	//获取系统的剩余内存
	public static long getAvailableRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//获取内存信息，保存到MemoryInfo中
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		//获取空闲内存
//		outInfo.availMem;
		//获取总内存
//		outInfo.totalMem;
		return outInfo.availMem;
	}
	//获取系统的总内存
	public static long getTotalRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//获取内存信息，保存到MemoryInfo中
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		//获取空闲内存
//			outInfo.availMem;
		//获取总内存
//			outInfo.totalMem;
		return outInfo.totalMem;//16版本之上才有,之下是没有的
	}
	//兼容低版本
	public static long getTotalRam(){
		File file = new File("/proc/meminfo");
		StringBuilder sb = new StringBuilder();
		try {
			//读取文件
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readLine = br.readLine();
			//获取数字
			char[] charArray = readLine.toCharArray();
			for (char c : charArray) {
				if (c>='0'&&c<='9') {
					sb.append(c);
				}
			}
			String strRom = sb.toString();
			//转化成long类型
			long longRom = Long.parseLong(strRom);
			return longRom*1024;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
