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
 * ����WidgetService���ڷ����и���Widget����ʾ��Ϣ�Ĳ���
 * @author lenovo
 *
 */
public class WidgetService extends Service {

	private AppWidgetManager appWidgetManager;
	private WidgetReceiver widgetReceiver;
	private Timer timer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/**
	 * ����Ĺ㲥������
	 */
	public class WidgetReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//�������
			killProcess();
		}
		//�������
		private void killProcess() {
			//��ȡ���̹�����
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			//��ȡ�������е����н���
			List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
			for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
				//�ж��Լ���Ӧ�ý��̲��ܱ�����
				if (! runningAppProcessInfo.processName.equals(getPackageName())) {
					am.killBackgroundProcesses(runningAppProcessInfo.processName);
				}
			}
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//ע��������̵Ĺ㲥������
		//1.�㲥������
		widgetReceiver = new WidgetReceiver();
		//2.���ý��յĹ㲥�¼�
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("aa.bb.cc");
		//3.ע��㲥������
		registerReceiver(widgetReceiver, intentFilter);
		
		//��ȡWidget�Ĺ�����
		appWidgetManager = AppWidgetManager.getInstance(this);
		//����widget,ÿ��һ��ʱ����и��²���
		updateWidget();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ֹͣ����widget
		if (timer != null) {
			timer.cancel();
			timer =null;
		}
		//ȡ��ע��㲥������
		if (widgetReceiver != null) {
			unregisterReceiver(widgetReceiver);
			widgetReceiver=null;
		}
		
	}
	/**
	 * ����widget
	 */
	private void updateWidget() {
		//������
		
		timer = new Timer();
		
		//ִ�в���
		//task:Ҫִ�еĲ�����when:�ӳٵ�ʱ�䣻period��ÿ��ִ�еļ��ʱ��
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("widget��������ˡ�����");
				//���²���
				//��ȡwidget����ı�ʶ
				ComponentName componentName = new ComponentName(WidgetService.this, MyWidget.class);
				//��ȡԶ�̲���
				//packageName:Ӧ�ó���İ�����layoutId�������ļ�
				RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
				//���²����ļ��еĿؼ���ֵ,����Զ�̲����ļ�����ͨ��findviewByid��ȡ��ʼ���ؼ�
				//viewId:���¿ؼ���Id��text:���µ�����
				remoteViews.setTextViewText(R.id.process_count, "�������е����:"+TaskUtil.getProcessCount(WidgetService.this));			
				remoteViews.setTextViewText(R.id.process_memory, "�����ڴ�:"+Formatter.formatFileSize(WidgetService.this, TaskUtil.getAvailableRam(WidgetService.this)));			
				
				//һ���������,��ť�ĵ���¼�
				Intent intent = new Intent();
				intent.setAction("aa.bb.cc");//����Ҫ���͵Ĺ㲥��aa.bb.cc���Զ���㲥���¼�
//				sendBroadcast(intent);
				//ͨ�����ͷ�����ʾҪ�����ڴ���������չ㲥ִ���ڴ���������
				//flags:ָ����Ϣ�ı�ǩ
				PendingIntent pendingIntent= PendingIntent.getBroadcast(WidgetService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				//viewId:����ؼ���id;  pendingIntent:�ӳ���ͼ������һ��intent��ͼ�������ʱ�Ż�ִ�������ͼ��������Ͳ�ִ�������ͼ
				remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				
				
				//���²���
				appWidgetManager.updateAppWidget(componentName, remoteViews);//�����������㲥�����ߵ��������Զ�̲���
			}
		}, 2000, 2000);
	}

}
