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

//��ȡ���̸�����ʣ���ڴ�Ĺ�����
public class TaskUtil {
	//��ȡϵͳ�������еĽ��̵ĸ���
	public static  int getProcessCount(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		return runningAppProcesses.size();
	}
	//��ȡϵͳ��ʣ���ڴ�
	public static long getAvailableRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//��ȡ�ڴ���Ϣ�����浽MemoryInfo��
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		//��ȡ�����ڴ�
//		outInfo.availMem;
		//��ȡ���ڴ�
//		outInfo.totalMem;
		return outInfo.availMem;
	}
	//��ȡϵͳ�����ڴ�
	public static long getTotalRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//��ȡ�ڴ���Ϣ�����浽MemoryInfo��
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		//��ȡ�����ڴ�
//			outInfo.availMem;
		//��ȡ���ڴ�
//			outInfo.totalMem;
		return outInfo.totalMem;//16�汾֮�ϲ���,֮����û�е�
	}
	//���ݵͰ汾
	public static long getTotalRam(){
		File file = new File("/proc/meminfo");
		StringBuilder sb = new StringBuilder();
		try {
			//��ȡ�ļ�
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readLine = br.readLine();
			//��ȡ����
			char[] charArray = readLine.toCharArray();
			for (char c : charArray) {
				if (c>='0'&&c<='9') {
					sb.append(c);
				}
			}
			String strRom = sb.toString();
			//ת����long����
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
