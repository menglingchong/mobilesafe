package com.heima.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.heima.mobilesafe.R;
import com.heima.mobilesafe.receiver.MyWidget;
import com.heima.mobilesafe.utils.TaskUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;
/**
 * 创建WidgetService，在服务中更新Widget的显示信息的操作
 * @author lenovo
 *
 */
public class WidgetService extends Service {

	private AppWidgetManager appWidgetManager;
	private WidgetReceiver widgetReceiver;
	private Timer timer;
	private ScreenOnReceiver screenOnReceiver;
	private ScreenOffReceiver screenOffReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/**
	 * 锁屏的广播接收者
	 * @author lenovo
	 *
	 */
	public class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("锁屏了。。。。");
			//清理进程
			killProcess();
			//停止更新
			stopUpdate();
		}
	}
	/**
	 * 解锁的广播接收者
	 * @author lenovo
	 *
	 */
	public class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//更新进程
			updateWidget();
		}
		
	}
	/**
	 * 清理的广播接收者
	 */
	public class WidgetReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//清理进程
			killProcess();
		}
	}
	//清理进程
	private void killProcess() {
		//获取进程管理者
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//获取正在运行的所有进程
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			//判断自己的应用进程不能被清理
			if (! runningAppProcessInfo.processName.equals(getPackageName())) {
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//注册清理进程的广播接收者
		//1.广播接收者
		widgetReceiver = new WidgetReceiver();
		//2.设置接收的广播事件
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("aa.bb.cc");
		//3.注册广播接收者
		registerReceiver(widgetReceiver, intentFilter);
		
		//android中锁屏和解锁的广播接收者比较特殊，必须通过代码的方式进行注册
		//注册锁屏的广播接收者
		screenOffReceiver = new ScreenOffReceiver();
		//设置广播事件
		IntentFilter offIntentFilter = new IntentFilter();
		offIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenOffReceiver, offIntentFilter);
		
		//注册解锁的广播接收者
		screenOnReceiver = new ScreenOnReceiver();
		IntentFilter onIntentFilter = new IntentFilter();
		onIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(screenOnReceiver, onIntentFilter);
		
		//获取Widget的管理者
		appWidgetManager = AppWidgetManager.getInstance(this);
		//更新widget,每隔一段时间进行更新操作
		updateWidget();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//停止更新widget
		stopUpdate();
		//取消注册广播接收者
		if (widgetReceiver != null) {
			unregisterReceiver(widgetReceiver);
			widgetReceiver=null;
		}
		//取消锁屏的广播接收者
		if (screenOffReceiver!= null) {
			unregisterReceiver(screenOffReceiver);
			screenOffReceiver=null;
		}
		//取消解锁的广播接收者
		if (screenOnReceiver!= null) {
			unregisterReceiver(screenOnReceiver);
			screenOnReceiver=null;
		}
		
	}
	//停止更新
	private void stopUpdate() {
		if (timer != null) {
			timer.cancel();
			timer =null;
		}
	}
	/**
	 * 更新widget
	 */
	private void updateWidget() {
		//计数器
		timer = new Timer();
		//执行操作
		//task:要执行的操作；when:延迟的时间；period：每次执行的间隔时间
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("widget组件更新了。。。");
				//更新操作
				//获取widget组件的标识
				ComponentName componentName = new ComponentName(WidgetService.this, MyWidget.class);
				//获取远程布局
				//packageName:应用程序的包名；layoutId：布局文件
				RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
				//更新布局文件中的控件的值,但是远程布局文件不能通过findviewByid获取初始化控件
				//viewId:更新控件的Id；text:更新的内容
				remoteViews.setTextViewText(R.id.process_count, "正在运行的软件:"+TaskUtil.getProcessCount(WidgetService.this));			
				remoteViews.setTextViewText(R.id.process_memory, "可用内存:"+Formatter.formatFileSize(WidgetService.this, TaskUtil.getAvailableRam(WidgetService.this)));			
				
				//一键清理操作,按钮的点击事件
				Intent intent = new Intent();
				intent.setAction("aa.bb.cc");//设置要发送的广播，aa.bb.cc：自定义广播的事件
//				sendBroadcast(intent);
				//通过发送发播表示要清理内存操作，接收广播执行内存的清理操作
				//flags:指定信息的标签
				PendingIntent pendingIntent= PendingIntent.getBroadcast(WidgetService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				//viewId:点击控件的id;  pendingIntent:延迟意图，包含一个intent意图，当点击时才会执行这个意图，不点击就不执行这个意图
				remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				
				
				//更新操作
				appWidgetManager.updateAppWidget(componentName, remoteViews);//两个参数：广播接收者的组件名和远程布局
			}
		}, 2000, 2000);
	}

}
