package com.heima.mobilesafe;

import com.heima.mobilesafe.ui.SettingView;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SetUP2Activity extends SetUPBaseActivity {
	private SettingView sv_setup2_sim;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		sv_setup2_sim = (SettingView) findViewById(R.id.sv_setup2_sim);
		//设置回显
		//根据保存的Sim卡的状态，去初始化控件状态，有保存SIM卡号就是绑定SIM卡，如果没有保存SIM卡号，就是没有绑定
		if (TextUtils.isEmpty(sp.getString("sim", ""))) {
			//没有绑定
			sv_setup2_sim.setChecked(false);
		} else {
			//绑定Sim卡
			sv_setup2_sim.setChecked(true);
		}
		sv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				//绑定sim卡
				//根据checkbox之前的状态进行绑定和解绑操作
				if (sv_setup2_sim.isChecked()) {
					//sim卡的解绑
					sv_setup2_sim.setChecked(false);
					edit.putString("sim", "");
				} else {
					//sim卡的绑定
					//获取sim卡号
					TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//					tel.getLine1Number();//获取sim卡绑定的电话号码。但是中国的运营商一般不会将sim卡和手机号进行绑定
					String sim = tel.getSimSerialNumber();//获取SIM卡序列号，唯一标识
					edit.putString("sim", sim);//保存SIM卡号
					sv_setup2_sim.setChecked(true);
				}
				edit.commit();
			}
		});
	}
	/**
	 * 跳转到第3个界面
	 * @param v
	 */
	@Override
	public void next_activity() {
		//判断用户是否绑定SIM卡，如果绑定则进行跳转第三个界面的操作，没有绑定则必须进行绑定
		if (sv_setup2_sim.isChecked()) {
			//跳转到第三个界面
			Intent intent = new Intent(getApplicationContext(), SetUP3Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.setup_enter_next, R.anim.setup_exit_next);
		} else {
			Toast.makeText(getApplicationContext(), "请完成SIM卡的绑定", 0).show();
		}
	}
	/**
	 * 跳转到第1个界面
	 * @param v
	 */
	@Override
	public void pre_activity() {
		Intent intent = new Intent(getApplicationContext(), SetUP1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_enter_pre, R.anim.setup_exit_pre);
	}
}
