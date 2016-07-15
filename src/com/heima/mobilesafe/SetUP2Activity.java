package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetUP2Activity extends SetUPBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
	}
	/**
	 * 跳转到第3个界面
	 * @param v
	 */
	@Override
	public void next_activity() {
		Intent intent = new Intent(getApplicationContext(), SetUP3Activity.class);
		startActivity(intent);
		finish();
	}
	/**
	 * 跳转到第1个界面
	 * @param v
	 */
	@Override
	public void pre_activity() {
		Intent intent = new Intent(getApplicationContext(), SetUP1Activity.class);
		startActivity(intent);
		finish();
	}
}
