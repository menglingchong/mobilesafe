package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 设置向导界面
 * 
 * @author lenovo
 * 
 */
public class SetUP1Activity extends SetUPBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	/**
	 * 实现页面的跳转，跳转到第二个页面
	 * @param v
	 */
	@Override
	public void next_activity() {
		Intent intent = new Intent(getApplicationContext(), SetUP2Activity.class);
		startActivity(intent);
		finish();
	}
	@Override
	public void pre_activity() {
		// TODO Auto-generated method stub
		
	}
}
