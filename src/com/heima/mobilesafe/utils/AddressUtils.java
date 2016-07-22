package com.heima.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

/**
 * ��̬��ȡ�����ת̬�Ĺ�����
 * @author lenovo
 *
 */
public class AddressUtils {
	private static ActivityManager activityManager;

	//��̬�Ļ�ȡ�����Ƿ���
	public static boolean isRunningService(Context context,String className){
		//��ȡ���̵Ĺ����ߣ�����Ĺ�����
		activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//��ȡ�������еķ���
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(100);//maxNum:�����������еķ��������
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//��ȡ�����Ψһ��ʶ
			ComponentName service = runningServiceInfo.service;
			//��ȡ�������з����ȫ����
			String classRuningName = service.getClassName();
			//����ȡ�����������з����ȫ�����봫�ݹ����ķ����ȫ�������бȽϣ�����һ�µĻ������������У�����true����һ�·���false
			if (className.equals(classRuningName)) {
				return true;//������
			}
		}
		return false;//����ر�
	}
}
