package com.heima.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
//AppWidgetProvider:�ǹ㲥������
public class MyWidget extends AppWidgetProvider {

	//����ʲô�����������onReceive
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		System.out.println("onReceive");
	}

	//ÿ�δ����������onUpdate
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onUpdate");
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
	}
	//���һ��ɾ�������onDisabled
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("onDisabled");
	}

}
