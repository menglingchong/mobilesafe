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
		//���û���
		//���ݱ����Sim����״̬��ȥ��ʼ���ؼ�״̬���б���SIM���ž��ǰ�SIM�������û�б���SIM���ţ�����û�а�
		if (TextUtils.isEmpty(sp.getString("sim", ""))) {
			//û�а�
			sv_setup2_sim.setChecked(false);
		} else {
			//��Sim��
			sv_setup2_sim.setChecked(true);
		}
		sv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				//��sim��
				//����checkbox֮ǰ��״̬���а󶨺ͽ�����
				if (sv_setup2_sim.isChecked()) {
					//sim���Ľ��
					sv_setup2_sim.setChecked(false);
					edit.putString("sim", "");
				} else {
					//sim���İ�
					//��ȡsim����
					TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//					tel.getLine1Number();//��ȡsim���󶨵ĵ绰���롣�����й�����Ӫ��һ�㲻�Ὣsim�����ֻ��Ž��а�
					String sim = tel.getSimSerialNumber();//��ȡSIM�����кţ�Ψһ��ʶ
					edit.putString("sim", sim);//����SIM����
					sv_setup2_sim.setChecked(true);
				}
				edit.commit();
			}
		});
	}
	/**
	 * ��ת����3������
	 * @param v
	 */
	@Override
	public void next_activity() {
		//�ж��û��Ƿ��SIM����������������ת����������Ĳ�����û�а��������а�
		if (sv_setup2_sim.isChecked()) {
			//��ת������������
			Intent intent = new Intent(getApplicationContext(), SetUP3Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.setup_enter_next, R.anim.setup_exit_next);
		} else {
			Toast.makeText(getApplicationContext(), "�����SIM���İ�", 0).show();
		}
	}
	/**
	 * ��ת����1������
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
