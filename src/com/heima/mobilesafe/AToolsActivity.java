package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AToolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	/**
	 * 按钮的点击事件
	 * @param v
	 */
	public void queryAddress(View v){
		//跳转到查询号码归属地界面
		Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
		startActivity(intent);
	}
	
}
