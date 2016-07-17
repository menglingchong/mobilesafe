package com.heima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SetUP3Activity extends SetUPBaseActivity {

	private EditText et_setup3_safenumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
		
		//���Բ���
		et_setup3_safenumber.setText(sp.getString("safenumber", ""));
	}
	/**
	 * ��ת����4������
	 * @param v
	 */
	@Override
	public void next_activity() {
		//��������İ�ȫ����
		//1.��ȡ����İ�ȫ����
		String safenumber = et_setup3_safenumber.getText().toString().trim();
		//2.�ж�����ĺ����Ƿ�Ϊ��
		if (TextUtils.isEmpty(safenumber)) {
			Toast.makeText(getApplicationContext(), "����ĺ���Ϊ�գ����������룡", 0).show();
			return ;
		}
		//3.��������ĺ���
		Editor edit = sp.edit();
		edit.putString("safenumber", safenumber);
		edit.commit();
		
		Intent intent = new Intent(getApplicationContext(), SetUP4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_enter_next, R.anim.setup_exit_next);
	}
	/**
	 * ��ת����2������
	 * @param v
	 */
	@Override
	public void pre_activity() {
		Intent intent = new Intent(getApplicationContext(), SetUP2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_enter_pre, R.anim.setup_exit_pre);
	}
	/**
	 * ��ť�ĵ���¼�
	 * @param v
	 */
	public void selectContacts(View v){
		Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode==RESULT_OK) {
				//����ѡ����ϵ�˽��洫�ݹ�����ֵ
				String phone = data.getStringExtra("phone");
				et_setup3_safenumber.setText(phone);
			}
			break;
		}
	}

}
