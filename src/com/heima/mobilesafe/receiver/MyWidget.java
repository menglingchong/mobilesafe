package com.heima.mobilesafe.receiver;

import com.heima.mobilesafe.service.WidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
//AppWidgetProvider:�ǹ㲥������
public class MyWidget extends AppWidgetProvider {

	//����ʲô�����������onReceive
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		System.out.println("onReceive");
	}

	//ÿ�δ����������onUpdate,���ߵ������ļ��еĸ���ʱ�䵽�ˣ�����ø÷���
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onUpdate");
		//��������widget����
		Intent intent = new Intent(context,WidgetService.class);
		context.startService(intent);
	}

	//ÿ��ɾ���������onDeleted
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted");
	}

	//��һ�δ�����ʱ������onEnabled
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		System.out.println("onEnabled");
		//����WidgetService����������Widget����ʾ����
	/*	Intent intent = new Intent(context,WidgetService.class);
		context.startService(intent);*/
	}
	//���һ��ɾ�������onDisabled
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("onDisabled");
		//�ر�WidgetService����
		Intent intent = new Intent(context, WidgetService.class);
		context.stopService(intent);
	}

}