package com.heima.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

/**
 * 动态获取服务的转态的工具类
 * @author lenovo
 *
 */
public class AddressUtils {
	private static ActivityManager activityManager;

	//动态的获取服务是否开启
	public static boolean isRunningService(Context context,String className){
		//获取进程的管理者，即活动的管理者
		activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取正在运行的服务
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(100);//maxNum:返回正在运行的服务的上限
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//获取服务的唯一标识
			ComponentName service = runningServiceInfo.service;
			//获取正在运行服务的全类名
			String classRuningName = service.getClassName();
			//将获取到的正在运行服务的全类名与传递过来的服务的全类名进行比较，两者一致的话服务正在运行，返回true，不一致返回false
			if (className.equals(classRuningName)) {
				return true;//服务开启
			}
		}
		return false;//服务关闭
	}
}
