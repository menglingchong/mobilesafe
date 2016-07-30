package com.heima.mobilesafe.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

/**
 * app�Ĺ�����
 * @author lenovo
 *
 */
public class AppUtil {
	//��ȡsd���Ŀ��ÿռ�;��ע��鿴Դ�룩
	public static long getAvailableSD(){
		//��ȡsd��·��
		File path = Environment.getExternalStorageDirectory();
		//Ӳ�̵�API����
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();//��ȡÿ��Ӳ�̵Ĵ�С
		long totalBlocks = stat.getBlockCount();//��ȡ�ܿ���
		int availableBlocks = stat.getAvailableBlocks();//��ȡ���õĿ���
		return availableBlocks*blockSize;
	}
	//��ȡ�ֻ��Ŀ��ÿռ�
	public static long getAvailableRom(){
		//��ȡ�ֻ��ڴ��·��
		File path = Environment.getDataDirectory();
		//Ӳ�̵�api����
		StatFs stat = new StatFs(path.getPath());
		int blockSize = stat.getBlockSize();//��ȡÿ��Ӳ�̵Ĵ�С
		int totalBlocks = stat.getBlockCount();//��ȡӲ�̵�����
		int availableBlocks = stat.getAvailableBlocks();//��ȡ���õĿ���
		return availableBlocks*blockSize;
		
	}
	
}
