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
	 * ��ť�ĵ���¼�
	 * @param v
	 */
	public void queryAddress(View v){
		//��ת����ѯ��������ؽ���
		Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
		startActivity(intent);
	}
	
}
