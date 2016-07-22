package com.heima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.heima.mobilesafe.service.AddressService;
import com.heima.mobilesafe.ui.SettingClickView;
import com.heima.mobilesafe.ui.SettingView;
import com.heima.mobilesafe.utils.AddressUtils;

public class SettingActivity extends Activity {

	private SettingView sv_setting_update;
	private SharedPreferences sp;
	private SettingView sv_setting_address;
	private SettingClickView scv_setting_changebg;
	private SettingClickView scv_setting_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//找到自定义控件，并初始化自定义控件中各个控件
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		sv_setting_address = (SettingView) findViewById(R.id.sv_setting_address);
		scv_setting_changebg = (SettingClickView) findViewById(R.id.scv_setting_changebg);
		scv_setting_location = (SettingClickView) findViewById(R.id.scv_setting_location);
		update();
//		address();
		changebg();
		changelocation();
	}

	//activity显示的时候调用,可以解决在按home键时返回主界面，然后关闭服务，再进入设置中心时，不更新显示内容的操作
	@Override
	protected void onStart() {
		super.onStart();
		address();
	}
	
	/**
	 * 设置归属地提示框的位置
	 */
	private void changelocation() {
		//设置自定义控件的标题和显示的内容
		scv_setting_location.setTitle("归属地提示框位置");
		scv_setting_location.setDes("设置归属地提示框的显示位置");
		//自定义控件的点击事件
		scv_setting_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DragViewActivity.class);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 设置归属地提示框的风格
	 */
	private void changebg() {
		final String[] items={"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		//设置标题和提示内容
		scv_setting_changebg.setTitle("归属地提示框风格");
		//设置对话框描述内容的回显操作
//		scv_setting_changebg.setDes("处女红");
		scv_setting_changebg.setDes(items[sp.getInt("which", 0)]);
		
		//设置自定义控件的点击事件
		scv_setting_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//弹出单选对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				//设置图标
				builder.setIcon(R.drawable.ic_launcher);
				//设置标题
				builder.setTitle("归属地提示框风格");
				//设置单选框
				//items:选项文本的数组；checkedItem：选中的条目；listener：回调监听
				builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						
						//设置文本的描述信息
						scv_setting_changebg.setDes(items[which]);
						//隐藏对话框
						dialog.dismiss();
					}
				});
				//设置取消按钮
				builder.setNegativeButton("取消", null);//当点击按钮只需要隐藏对话框操作的话，只需要将点击监听设为null,表示对话框的隐藏
				builder.show();
			}
		});
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
