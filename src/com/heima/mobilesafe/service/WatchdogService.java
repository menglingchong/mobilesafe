package com.heima.mobilesafe.service;

import java.util.List;

import com.heima.mobilesafe.WatchDogActivity;
import com.heima.mobilesafe.db.dao.WatchDogDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

public class WatchdogService extends Service {

	private WatchDogDao watchDogDao;
	private boolean islock;
	private UnLockReceiver unLockReceiver;
	private String unlockpackagename;
	private ScreenReceiver screenReceiver;
	private List<String> list;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/**
	 * �Զ�������Ĺ㲥������
	 * @author lenovo
	 *
	 */
	public class UnLockReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//��������
			unlockpackagename = intent.getStringExtra("packageName");
		}
		
	}
	//��Ļ�����Ĺ㲥������
	public class ScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//��Ļ����������ʹӦ�����¼���
			unlockpackagename=null;
		}
		
	}
	@Override
	public void onCreate() {
		super.onCreate();
		//ע������Ĺ㲥������
		unLockReceiver = new UnLockReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.heima.mobilesafe.unlock");
		registerReceiver(unLockReceiver, intentFilter);
		
		//ע����Ļ�����Ĺ㲥������
		screenReceiver = new ScreenReceiver();
		IntentFilter screenOffintentFilter = new IntentFilter();
		screenOffintentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, screenOffintentFilter);
		
		islock = true;
		watchDogDao = new WatchDogDao(getApplicationContext());
		//ʱʱ�̼̿����û��򿪵ĳ���
		//activity���Ǵ��������ջ�еģ�һ��Ӧ��ֻ��һ������ջ
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				//���ݿ���Ż����Ƚ����ݿ��е����ݲ�ѯ�ŵ��ڴ棬Ȼ������ݴ��ڴ���ȡ���������������˶����ݿ�ķ���
				list = watchDogDao.querryAllLockApp();
				//�����ݿⷢ���仯ʱ�����ڴ��е����ݣ������ݿ�仯��ʱ��֪ͨ���ݹ۲������ݿⷢ���仯�ˣ�Ȼ�������ݹ۲����и�������
				Uri uri = Uri.parse("content://com.heima.mobilesafe.lock.changed");
				//notifyForDescendents:ƥ��ģʽ�� true����ȷƥ�� false:����ƥ��
				
				getContentResolver().registerContentObserver(uri, true, new ContentObserver(null) {
					//new ContentObserver(null):����Ϊ�յ�ԭ���ǲ��������߳�������Hnadler
					@Override
					public void onChange(boolean selfChange) {
						super.onChange(selfChange);
						//��������
						list = watchDogDao.querryAllLockApp();
					}
				});
				
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
						boolean b = list.contains(packageName);
						if (b){
							if (!packageName.equals(unlockpackagename)) {
								Intent intent = new Intent(WatchdogService.this, WatchDogActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								//��������Ӧ�ó���������ݸ���������
								intent.putExtra("packageName", packageName);
								startActivity(intent);
							}
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
		//ע���㲥������
		if (unLockReceiver !=null) {
			unregisterReceiver(unLockReceiver);
			unLockReceiver =null;
		}
		
		//ע����Ļ�����Ĺ㲥������
		if (screenReceiver !=null) {
			unregisterReceiver(screenReceiver);
			screenReceiver =null;
		}
		
	}
}
