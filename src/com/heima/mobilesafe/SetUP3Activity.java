package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetUP3Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
	}
	/**
	 * ��ת����4������
	 * @param v
	 */
	public void next(View v){
		Intent intent = new Intent(getApplicationContext(), SetUP4Activity.class);
		startActivity(intent);
	}
	/**
	 * ��ת����2������
	 * @param v
	 */
	public void pre(View v){
		Intent intent = new Intent(getApplicationContext(), SetUP2Activity.class);
		startActivity(intent);
	}
}
