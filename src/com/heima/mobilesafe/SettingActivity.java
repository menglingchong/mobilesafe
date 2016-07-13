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
		//�ҵ��Զ���ؼ�������ʼ���Զ���ؼ��и����ؼ�
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
//		sv_setting_update.setTitle("��ʾ��Ϣ");
		//defValue:ȱʡ��ֵ
		if (sp.getBoolean("update", true)) {
//			sv_setting_update.setDes("����ʾ����");
			sv_setting_update.setChecked(true);
		} else {
//			sv_setting_update.setDes("�ر���ʾ����");
			sv_setting_update.setChecked(false);
		}
		
		
		//�����Զ�����Ͽؼ��ĵ���¼�
		//����1�����checkbox���Զ���ؼ������ݲ��ı䣬������Ϊcheckbox�ؼ�������е���¼��ͻ�ȡ������¼������ckeckbox�ؼ�����ִ���������Ӧ�ĵ���¼�������
		//������ִ����Ŀ�ĵ���¼�
		//����2�����ܹ��������ã�ʹ��sharedPreference����������
		sv_setting_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				// ����״̬������checkbox֮ǰ��״̬������checkbox��״̬
				if (sv_setting_update.isChecked()) {
					//����checkboxδѡ��״̬,���ر���ʾ����
//					sv_setting_update.setDes("�ر���ʾ����");
					sv_setting_update.setChecked(false);
					//����״̬
					edit.putBoolean("update", false);
				} else {
					//����ckeckboxΪѡ��״̬����������ʾ����
//					sv_setting_update.setDes("����ʾ����");
					sv_setting_update.setChecked(true);
					//����״̬
					edit.putBoolean("update", true);
				}
				edit.commit();
			}
		});
	}
}
