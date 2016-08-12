package com.heima.mobilesafe.service;

import java.util.List;

import com.heima.mobilesafe.WatchDogActivity;
import com.heima.mobilesafe.db.dao.WatchDogDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class WatchdogService extends Service {

	private WatchDogDao watchDogDao;
	private boolean islock;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		islock = true;
		watchDogDao = new WatchDogDao(getApplicationContext());
		//ʱʱ�̼̿����û��򿪵ĳ���
		//activity���Ǵ��������ջ�еģ�һ��Ӧ��ֻ��һ������ջ
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (islock) {
					//�����û�������Щ����ջ����������ЩӦ��
					//��ȡ�������е�����ջ���������ջ���У���Ӧ�ô򿪹�
					//���ڴ򿪵�Ӧ�õ�����ջ�ڵ�һ����֮ǰ(���home��С����û���˳�)��Ӧ�õ�����ջ����������
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);//������ʾ��ȡ�������е�����ջ����
					System.out.println("-----------------------");
					for (RunningTaskInfo runningTaskInfo : runningTasks) {
						//��ȡ����ջ��ջ�׵�activity
						ComponentName baseActivity = runningTaskInfo.baseActivity;
						String packageName = baseActivity.getPackageName();
						System.out.println(packageName);
						//ͨ����ѯ���ݿ⣬������ݿ����иð���������ת���������棬���򲻽�����ת
						if (watchDogDao.queryLockApp(packageName)){
							Intent intent = new Intent(WatchdogService.this, WatchDogActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							//��������Ӧ�ó���������ݸ���������
							intent.putExtra("packageName", packageName);
							startActivity(intent);
						}
					}
					SystemClock.sleep(300);
				}
			}
		}).start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//������ر�ʱ����ֹ�����û��򿪵�Ӧ�ó���
		islock = false;
	}
}
