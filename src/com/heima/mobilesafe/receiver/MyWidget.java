package com.heima.mobilesafe.receiver;

import com.heima.mobilesafe.service.WidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
//AppWidgetProvider:是广播接收者
public class MyWidget extends AppWidgetProvider {

	//不管什么操作都会调用onReceive
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		System.out.println("onReceive");
	}

	//每次创建都会调用onUpdate,或者当布局文件中的更新时间到了，会调用该方法
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onUpdate");
		//开启更新widget服务
		Intent intent = new Intent(context,WidgetService.class);
		context.startService(intent);
	}

	//每次删除都会调用onDeleted
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted");
	}

	//第一次创建的时候会调用onEnabled
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		System.out.println("onEnabled");
		//开启WidgetService，用来更新Widget的显示内容
	/*	Intent intent = new Intent(context,WidgetService.class);
		context.startService(intent);*/
	}
	//最后一次删除会调用onDisabled
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("onDisabled");
		//关闭WidgetService服务
		Intent intent = new Intent(context, WidgetService.class);
		context.stopService(intent);
	}

}
