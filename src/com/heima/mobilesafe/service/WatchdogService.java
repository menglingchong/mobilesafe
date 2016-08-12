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
	 * 自定义解锁的广播接收者
	 * @author lenovo
	 *
	 */
	public class UnLockReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//解锁操作
			unlockpackagename = intent.getStringExtra("packageName");
		}
		
	}
	//屏幕锁屏的广播接收者
	public class ScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//屏幕解锁操作，使应用重新加锁
			unlockpackagename=null;
		}
		
	}
	@Override
	public void onCreate() {
		super.onCreate();
		//注册解锁的广播接收者
		unLockReceiver = new UnLockReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.heima.mobilesafe.unlock");
		registerReceiver(unLockReceiver, intentFilter);
		
		//注册屏幕锁屏的广播接收者
		screenReceiver = new ScreenReceiver();
		IntentFilter screenOffintentFilter = new IntentFilter();
		screenOffintentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenReceiver, screenOffintentFilter);
		
		islock = true;
		watchDogDao = new WatchDogDao(getApplicationContext());
		//时时刻刻监听用户打开的程序
		//activity都是存放在任务栈中的，一个应用只有一个任务栈
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				//数据库的优化，先将数据库中的数据查询放到内存，然后把数据从内存中取出来，这样减少了对数据库的访问
				list = watchDogDao.querryAllLockApp();
				//当数据库发生变化时更新内存中的数据，当数据库变化的时候通知内容观察者数据库发生变化了，然后在内容观察者中更新数据
				Uri uri = Uri.parse("content://com.heima.mobilesafe.lock.changed");
				//notifyForDescendents:匹配模式： true：精确匹配 false:粗略匹配
				
				getContentResolver().registerContentObserver(uri, true, new ContentObserver(null) {
					//new ContentObserver(null):参数为空的原因是不能在子线程中声明Hnadler
					@Override
					public void onChange(boolean selfChange) {
						super.onChange(selfChange);
						//更新数据
						list = watchDogDao.querryAllLockApp();
					}
				});
				
				while (islock) {
					//监听用户打开了哪些任务栈，即打开了哪些应用
					//获取正在运行的任务栈，如果任务栈运行，则应用打开过
					//现在打开的应用的任务栈在第一个，之前(点击home最小化，没有退出)的应用的任务栈会依次往后
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);//参数表示获取正在运行的任务栈个数
					System.out.println("-----------------------");
					for (RunningTaskInfo runningTaskInfo : runningTasks) {
						//获取任务栈，栈底的activity
						ComponentName baseActivity = runningTaskInfo.baseActivity;
						String packageName = baseActivity.getPackageName();
						System.out.println(packageName);
						//通过查询数据库，如果数据库中有该包名，则跳转到解锁界面，否则不进行跳转
						boolean b = list.contains(packageName);
						if (b){
							if (!packageName.equals(unlockpackagename)) {
								Intent intent = new Intent(WatchdogService.this, WatchDogActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								//将加锁的应用程序包名传递给解锁界面
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
		//当服务关闭时，禁止监听用户打开的应用程序
		islock = false;
		//注销广播接收者
		if (unLockReceiver !=null) {
			unregisterReceiver(unLockReceiver);
			unLockReceiver =null;
		}
		
		//注销屏幕锁屏的广播接收者
		if (screenReceiver !=null) {
			unregisterReceiver(screenReceiver);
			screenReceiver =null;
		}
		
	}
}
