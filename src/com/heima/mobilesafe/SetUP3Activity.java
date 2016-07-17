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
		
		//回显操作
		et_setup3_safenumber.setText(sp.getString("safenumber", ""));
	}
	/**
	 * 跳转到第4个界面
	 * @param v
	 */
	@Override
	public void next_activity() {
		//保存输入的安全号码
		//1.获取输入的安全号码
		String safenumber = et_setup3_safenumber.getText().toString().trim();
		//2.判断输入的号码是否为空
		if (TextUtils.isEmpty(safenumber)) {
			Toast.makeText(getApplicationContext(), "输入的号码为空，请重新输入！", 0).show();
			return ;
		}
		//3.保存输入的号码
		Editor edit = sp.edit();
		edit.putString("safenumber", safenumber);
		edit.commit();
		
		Intent intent = new Intent(getApplicationContext(), SetUP4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_enter_next, R.anim.setup_exit_next);
	}
	/**
	 * 跳转到第2个界面
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
	 * 按钮的点击事件
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
				//接收选择联系人界面传递过来的值
				String phone = data.getStringExtra("phone");
				et_setup3_safenumber.setText(phone);
			}
			break;
		}
	}

}
