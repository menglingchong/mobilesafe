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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WatchDogActivity extends Activity {

	private ImageView iv_watchdog_icon;
	private TextView tv_watchdog_name;
	private EditText et_watchdog_password;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchdog);
		
		iv_watchdog_icon = (ImageView) findViewById(R.id.iv_watchdog_icon);
		tv_watchdog_name = (TextView) findViewById(R.id.tv_watchdog_name);
		et_watchdog_password = (EditText) findViewById(R.id.et_watchdog_password);
		
		//���ջ�ȡ��������
		Intent intent = getIntent();
		packageName = intent.getStringExtra("packageName");
		//��ȡ�����Ĺ�����
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			Drawable loadIcon = applicationInfo.loadIcon(pm);
			String name = applicationInfo.loadLabel(pm).toString();
			//������ʾ
			iv_watchdog_icon.setImageDrawable(loadIcon);
			tv_watchdog_name.setText(name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	//����ʧȥ����ʱ�����ý�������
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
			//��ת��������
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	//��ť�Ľ�������
	public void lock(View v){
		String password = et_watchdog_password.getText().toString();
		if ("123".equals(password)) {
			//����
			//һ����ͨ�����͹㲥��ʽ������Ӧ�õ���Ϣ������
			Intent intent = new Intent();
			//�����Զ���㲥
			intent.setAction("com.heima.mobilesafe.unlock");
			intent.putExtra("packageName", packageName);
			sendBroadcast(intent);
			finish();
		}else {
			//��ʾ�û�
			Toast.makeText(getApplicationContext(), "����������������룡��", 0).show();
		}
	}
}
