package com.heima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.heima.mobilesafe.bean.TaskInfo;

/**
 * 进程管理的业务类
 * @author lenovo
 *
 */
public class TaskEngine {
	//获取所有的进程信息
	public static List<TaskInfo> getAllTaskInfo(Context context){
		List<TaskInfo> list  = new ArrayList<TaskInfo>();
		//获取所有进程信息
		//1.获取进程管理者
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//2.获取所有正在运行的进程信息
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		//遍历集合
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			TaskInfo taskInfo = new TaskInfo();
			//3.获取相应的进程的信息
			//获取进程名,实质上包名
			String packageName = runningAppProcessInfo.processName;
			taskInfo.setPackageName(packageName);
			//获取进程所占的内存空间的大小，int [] pids:输入几个进程的pid,就会返回几个进程所占的控件
			MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			int totalPss = memoryInfo[0].getTotalPss();
			int ramSize =totalPss*1024;
			taskInfo.setRomSize(ramSize);
			//获取application信息
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				Drawable loadIcon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(loadIcon);
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				//获取程序的所有标签信息，是否是系统程序，以标签的形式展示
				int flags = applicationInfo.flags;
				boolean isUser;
				if ((applicationInfo.FLAG_SYSTEM & flags) == applicationInfo.FLAG_SYSTEM) {
					//系统程序
					isUser=false;
				}else {
					//用户程序
					isUser=true;
				}
				//保存信息
				taskInfo.setUser(isUser);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			//将bena对象保存到list集合中
			list.add(taskInfo);
		}
		return list;
	}
}
