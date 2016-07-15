package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetUP4Activity extends SetUPBaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
	}
	 
	@Override
	public void next_activity() {
		//页面的跳转,跳转到手机防盗页面
	}
	/**
	 * 跳转到第3个界面
	 * @param v
	 */
	@Override
	public void pre_activity() {
		Intent intent = new Intent(getApplicationContext(), SetUP3Activity.class);
		startActivity(intent);
		finish();
	}
}
