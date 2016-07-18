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
	
	//ʹ��ע���ʼ���ؼ����ڲ���ͨ������ķ�ʽִ����findViewById����
	@ViewInject(R.id.cb_setup4_protect)
	private CheckBox cb_setup4_protect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
//		cb_setup4_protect = (CheckBox) findViewById(R.id.cb_setup4_protect);
		ViewUtils.inject(this);
		//ͨ��������û�״̬���л��Բ���
		if (sp.getBoolean("isChecked", false)) {
			//������������
			cb_setup4_protect.setText("�㿪���˷�������");
			cb_setup4_protect.setChecked(true);
		}else {
			//û�п�����������
			cb_setup4_protect.setText("��û�п����˷�������");
			cb_setup4_protect.setChecked(false);
		}
	
		//����checkbox�ĵ���¼�����checkbox�����仯ʱִ�и÷���
		cb_setup4_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//����checkbox�Ĳ���
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//����checkbox��״̬����checbox����Ϣ
				Editor edit = sp.edit();
				if (isChecked) {
					//������������
					cb_setup4_protect.setText("�㿪���˷�������");
					cb_setup4_protect.setChecked(true);
					edit.putBoolean("isChecked", true);//���浽sp��
				}else {
					//û�п�����������
					cb_setup4_protect.setText("��û�п����˷�������");
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
		//ҳ�����ת,��ת���ֻ�����ҳ��
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
	}
	/**
	 * ��ת����3������
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
