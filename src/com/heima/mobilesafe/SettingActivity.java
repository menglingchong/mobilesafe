package com.heima.mobilesafe;

import com.heima.mobilesafe.ui.SettingView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private SettingView sv_setting_update;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//找到自定义控件，并初始化自定义控件中各个控件
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
//		sv_setting_update.setTitle("提示信息");
		//defValue:缺省的值
		if (sp.getBoolean("update", true)) {
//			sv_setting_update.setDes("打开提示更新");
			sv_setting_update.setChecked(true);
		} else {
//			sv_setting_update.setDes("关闭提示更新");
			sv_setting_update.setChecked(false);
		}
		
		
		//设置自定义组合控件的点击事件
		//问题1：点击checkbox，自定义控件的内容不改变，这是因为checkbox控件本身具有点击事件和获取焦点的事件，点击ckeckbox控件，会执行自身的相应的点击事件方法，
		//而不会执行条目的点击事件
		//问题2：不能够保存设置，使用sharedPreference来保存设置
		sv_setting_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				// 更该状态，根据checkbox之前的状态来更改checkbox的状态
				if (sv_setting_update.isChecked()) {
					//设置checkbox未选中状态,即关闭提示更新
//					sv_setting_update.setDes("关闭提示更新");
					sv_setting_update.setChecked(false);
					//保存状态
					edit.putBoolean("update", false);
				} else {
					//设置ckeckbox为选中状态，即开启提示更新
//					sv_setting_update.setDes("打开提示更新");
					sv_setting_update.setChecked(true);
					//保存状态
					edit.putBoolean("update", true);
				}
				edit.commit();
			}
		});
	}
}
