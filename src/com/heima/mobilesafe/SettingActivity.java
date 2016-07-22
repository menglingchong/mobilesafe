package com.heima.mobilesafe;

import com.heima.mobilesafe.service.AddressService;
import com.heima.mobilesafe.ui.SettingView;
import com.heima.mobilesafe.utils.AddressUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private SettingView sv_setting_update;
	private SharedPreferences sp;
	private SettingView sv_setting_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//找到自定义控件，并初始化自定义控件中各个控件
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		sv_setting_address = (SettingView) findViewById(R.id.sv_setting_address);
		
		update();
		address();
	}
	//activity显示的时候调用
	@Override
	protected void onStart() {
		super.onStart();
		address();
	}
	/**
	 * 号码归属地显示
	 */
	private void address() {
		//回显操作
		//因为在设置中心可以手动的关闭服务，因此动态的获取服务是否开启
		if (AddressUtils.isRunningService(this, "com.heima.mobilesafe.service.AddressService")) {
			//服务的全类名一致，将checkbox设置为选中状态
			sv_setting_address.setChecked(true);
		}else {
			//服务的全类名不一致，将checkbox设置为选未中状态
			sv_setting_address.setChecked(false);
		}
		
		
		//自定义控件的点击事件
		sv_setting_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, AddressService.class);
				//根据checkbox之前的状态去更改自定义控件的显示
				if (sv_setting_address.isChecked()) {
					//关闭显示号码归属地
					stopService(intent);
					//更新checkbox的状态
					sv_setting_address.setChecked(false);
				}else {
					//打开显示号码归属地
					startService(intent);
					//更新checkbox的状态
					sv_setting_address.setChecked(true);
				}
			}
		});
	}

	/**
	 * 提示更新
	 */
	private void update() {
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
