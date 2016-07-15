package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetUP4Activity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
	}
	 
	/**
	 * 跳转到第3个界面
	 * @param v
	 */
	public void pre(View v){
		Intent intent = new Intent(getApplicationContext(), SetUP3Activity.class);
		startActivity(intent);
	}
	public void next(View v){
		//页面的跳转,跳转到手机防盗页面
		
	}
}
