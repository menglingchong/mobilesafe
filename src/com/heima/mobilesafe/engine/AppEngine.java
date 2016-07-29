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
 * ��ȡApp��Ϣ��ҵ����
 * @author lenovo
 *
 */
public class AppEngine {
	//��ȡϵͳ������Ӧ�ó������Ϣ
	public static List<AppInfo> getAppInfos(Context context){
		List<AppInfo> list  = new ArrayList<AppInfo>();
		//��ȡӦ�ó�����Ϣ
		//���Ĺ�����
		PackageManager packageManager = context.getPackageManager();
		//��ȡϵͳ�а�װ�����������Ϣ
		List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			//��ȡ����
			String packageName = packageInfo.packageName;
			//��ȡ�汾��
			String versionName = packageInfo.versionName;
			//��ȡӦ�ó���
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			//��ȡӦ�ó����ͼ��
			Drawable icon = applicationInfo.loadIcon(packageManager);
			//��ȡӦ�ó��������
			String name = applicationInfo.loadLabel(packageManager).toString();
			//�Ƿ����û�����
			//��ȡӦ�ó����������Ϣ���Ƿ���ϵͳ������Ƿ��ǰ�װSD��
			boolean isUser;
			int flags = applicationInfo.flags;
			if ((applicationInfo.FLAG_SYSTEM & flags) == applicationInfo.FLAG_SYSTEM) {
				//ϵͳ����
				isUser =false;
			}else {
				//�û�����
				isUser =true;
			}
			//�Ƿ�װ��SD��
			boolean isSD;
			if ((applicationInfo.FLAG_EXTERNAL_STORAGE & flags) == applicationInfo.FLAG_EXTERNAL_STORAGE) {
				//��װ����SD��
				isSD = true;
			}else {
				//��װ�����ֻ���
				isSD = false;
			}
			//�����ݷ�װ��bean������
			AppInfo appInfo = new AppInfo(name, icon, packageName, versionName, isSD, isUser);
			//��bean������ӵ�list������
			list.add(appInfo);
		}
		return list;
	}
	
}
