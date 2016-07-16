package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
/**
 * 手机防盗界面
 * @author lenovo
 *
 */
public class LostFindActivity extends Activity {

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//从sp中获取保存的设置信息
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//分为两部分：1.显示设置过的手机防盗功能，2.设置手机防盗功能
		//判断用户是否是第一次进入手机防盗模式，是，则跳转到设置向导界面，否则显示设置过的手机防盗功能
		if (sp.getBoolean("first", true)) {
			//第一次进入手机防盗模式，跳转到设置向导界面
			Intent intent =new Intent(getApplicationContext(), SetUP1Activity.class);
			startActivity(intent);
			//移除LostFindActivity界面
			finish();
		} else {
			//显示手机防盗界面
			setContentView(R.layout.activity_lostfind);
		}
	}
	
	public void resetup(View v){
		Intent intent = new Intent(getApplicationContext(), SetUP1Activity.class);
		startActivity(intent);
		finish();
	}
}
