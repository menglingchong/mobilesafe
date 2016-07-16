package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
/**
 * �ֻ���������
 * @author lenovo
 *
 */
public class LostFindActivity extends Activity {

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��sp�л�ȡ�����������Ϣ
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//��Ϊ�����֣�1.��ʾ���ù����ֻ��������ܣ�2.�����ֻ���������
		//�ж��û��Ƿ��ǵ�һ�ν����ֻ�����ģʽ���ǣ�����ת�������򵼽��棬������ʾ���ù����ֻ���������
		if (sp.getBoolean("first", true)) {
			//��һ�ν����ֻ�����ģʽ����ת�������򵼽���
			Intent intent =new Intent(getApplicationContext(), SetUP1Activity.class);
			startActivity(intent);
			//�Ƴ�LostFindActivity����
			finish();
		} else {
			//��ʾ�ֻ���������
			setContentView(R.layout.activity_lostfind);
		}
	}
	
	public void resetup(View v){
		Intent intent = new Intent(getApplicationContext(), SetUP1Activity.class);
		startActivity(intent);
		finish();
	}
}
