package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * �����򵼽���
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
	 * ʵ��ҳ�����ת����ת���ڶ���ҳ��
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
