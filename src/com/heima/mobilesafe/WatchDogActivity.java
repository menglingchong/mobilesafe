package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WatchDogActivity extends Activity {

	private ImageView iv_watchdog_icon;
	private TextView tv_watchdog_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchdog);
		
		iv_watchdog_icon = (ImageView) findViewById(R.id.iv_watchdog_icon);
		tv_watchdog_name = (TextView) findViewById(R.id.tv_watchdog_name);
		
		//接收获取到的数据
		Intent intent = getIntent();
		String packageName = intent.getStringExtra("packageName");
		//获取包名的管理者
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			Drawable loadIcon = applicationInfo.loadIcon(pm);
			String name = applicationInfo.loadLabel(pm).toString();
			//设置显示
			iv_watchdog_icon.setImageDrawable(loadIcon);
			tv_watchdog_name.setText(name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	//界面失去焦点时，将该界面销毁
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/*
			   Starting: Intent { 
			   act=android.intent.action.MAIN 
			   cat=[android.intent.category.HOME
			   ] cmp=com.android.launcher/com.android.launcher2.Launcher } from pid 208
			 */
			//跳转到主界面
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
