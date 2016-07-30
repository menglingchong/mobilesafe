package com.heima.mobilesafe.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

/**
 * app的工具类
 * @author lenovo
 *
 */
public class AppUtil {
	//获取sd卡的可用空间;（注意查看源码）
	public static long getAvailableSD(){
		//获取sd的路径
		File path = Environment.getExternalStorageDirectory();
		//硬盘的API操作
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();//获取每块硬盘的大小
		long totalBlocks = stat.getBlockCount();//获取总块数
		int availableBlocks = stat.getAvailableBlocks();//获取可用的块数
		return availableBlocks*blockSize;
	}
	//获取手机的可用空间
	public static long getAvailableRom(){
		//获取手机内存的路径
		File path = Environment.getDataDirectory();
		//硬盘的api操作
		StatFs stat = new StatFs(path.getPath());
		int blockSize = stat.getBlockSize();//获取每块硬盘的大小
		int totalBlocks = stat.getBlockCount();//获取硬盘的总数
		int availableBlocks = stat.getAvailableBlocks();//获取可用的块数
		return availableBlocks*blockSize;
		
	}
	
}
