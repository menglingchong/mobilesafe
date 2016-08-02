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
 * ���̹����ҵ����
 * @author lenovo
 *
 */
public class TaskEngine {
	//��ȡ���еĽ�����Ϣ
	public static List<TaskInfo> getAllTaskInfo(Context context){
		List<TaskInfo> list  = new ArrayList<TaskInfo>();
		//��ȡ���н�����Ϣ
		//1.��ȡ���̹�����
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//2.��ȡ�����������еĽ�����Ϣ
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
		//��������
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			TaskInfo taskInfo = new TaskInfo();
			//3.��ȡ��Ӧ�Ľ��̵���Ϣ
			//��ȡ������,ʵ���ϰ���
			String packageName = runningAppProcessInfo.processName;
			taskInfo.setPackageName(packageName);
			//��ȡ������ռ���ڴ�ռ�Ĵ�С��int [] pids:���뼸�����̵�pid,�ͻ᷵�ؼ���������ռ�Ŀؼ�
			MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			int totalPss = memoryInfo[0].getTotalPss();
			int ramSize =totalPss*1024;
			taskInfo.setRomSize(ramSize);
			//��ȡapplication��Ϣ
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				Drawable loadIcon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(loadIcon);
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				//��ȡ��������б�ǩ��Ϣ���Ƿ���ϵͳ�����Ա�ǩ����ʽչʾ
				int flags = applicationInfo.flags;
				boolean isUser;
				if ((applicationInfo.FLAG_SYSTEM & flags) == applicationInfo.FLAG_SYSTEM) {
					//ϵͳ����
					isUser=false;
				}else {
					//�û�����
					isUser=true;
				}
				//������Ϣ
				taskInfo.setUser(isUser);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			//��bena���󱣴浽list������
			list.add(taskInfo);
		}
		return list;
	}
}
