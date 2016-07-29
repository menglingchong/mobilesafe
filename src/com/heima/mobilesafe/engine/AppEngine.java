package com.heima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.heima.mobilesafe.bean.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * 获取App信息的业务类
 * @author lenovo
 *
 */
public class AppEngine {
	//获取系统中所有应用程序的信息
	public static List<AppInfo> getAppInfos(Context context){
		List<AppInfo> list  = new ArrayList<AppInfo>();
		//获取应用程序信息
		//包的管理者
		PackageManager packageManager = context.getPackageManager();
		//获取系统中安装的所有软件信息
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			//获取包名
			String packageName = packageInfo.packageName;
			//获取版本号
			String versionName = packageInfo.versionName;
			//获取应用程序
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			//获取应用程序的图标
			Drawable icon = applicationInfo.loadIcon(packageManager);
			//获取应用程序的名称
			String name = applicationInfo.loadLabel(packageManager).toString();
			//是否是用户程序
			//获取应用程序中相关信息，是否是系统程序和是否是安装SD卡
			boolean isUser;
			int flags = applicationInfo.flags;
			if ((applicationInfo.FLAG_SYSTEM & flags) == applicationInfo.FLAG_SYSTEM) {
				//系统程序
				isUser =false;
			}else {
				//用户程序
				isUser =true;
			}
			//是否安装到SD卡
			boolean isSD;
			if ((applicationInfo.FLAG_EXTERNAL_STORAGE & flags) == applicationInfo.FLAG_EXTERNAL_STORAGE) {
				//安装到了SD卡
				isSD = true;
			}else {
				//安装到了手机中
				isSD = false;
			}
			//将数据封装到bean对象中
			AppInfo appInfo = new AppInfo(name, icon, packageName, versionName, isSD, isUser);
			//将bean对象添加到list集合中
			list.add(appInfo);
		}
		return list;
	}
	
}
