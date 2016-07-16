package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

public class SetUP4Activity extends SetUPBaseActivity {
	
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
	}
	 
	@Override
	public void next_activity() {
		Editor edit = sp.edit();
		edit.putBoolean("first", false);
		edit.commit();
		//ҳ�����ת,��ת���ֻ�����ҳ��
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
	}
	/**
	 * ��ת����3������
	 * @param v
	 */
	@Override
	public void pre_activity() {
		Intent intent = new Intent(getApplicationContext(), SetUP3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_enter_pre, R.anim.setup_exit_pre);
	}
}
