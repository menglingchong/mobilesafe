package com.heima.mobilesafe;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetUP4Activity extends SetUPBaseActivity {
	
	//使用注解初始化控件，内部是通过反射的方式执行了findViewById操作
	@ViewInject(R.id.cb_setup4_protect)
	private CheckBox cb_setup4_protect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
//		cb_setup4_protect = (CheckBox) findViewById(R.id.cb_setup4_protect);
		ViewUtils.inject(this);
		//通过保存的用户状态进行回显操作
		if (sp.getBoolean("isChecked", false)) {
			//开启防盗保护
			cb_setup4_protect.setText("你开启了防盗保护");
			cb_setup4_protect.setChecked(true);
		}else {
			//没有开启防盗保护
			cb_setup4_protect.setText("你没有开启了防盗保护");
			cb_setup4_protect.setChecked(false);
		}
	
		//设置checkbox的点击事件，当checkbox发生变化时执行该方法
		cb_setup4_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//监听checkbox的操作
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//根据checkbox的状态设置checbox的信息
				Editor edit = sp.edit();
				if (isChecked) {
					//开启防盗保护
					cb_setup4_protect.setText("你开启了防盗保护");
					cb_setup4_protect.setChecked(true);
					edit.putBoolean("isChecked", true);//保存到sp中
				}else {
					//没有开启防盗保护
					cb_setup4_protect.setText("你没有开启了防盗保护");
					cb_setup4_protect.setChecked(false);
					edit.putBoolean("isChecked", false);
				}
				edit.commit();
			}
		});
		
	}
	 
	@Override
	public void next_activity() {
		Editor edit = sp.edit();
		edit.putBoolean("first", false);
		edit.commit();
		//页面的跳转,跳转到手机防盗页面
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
	}
	/**
	 * 跳转到第3个界面
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
