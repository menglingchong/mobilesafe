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
		//�ҵ��Զ���ؼ�������ʼ���Զ���ؼ��и����ؼ�
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		sv_setting_address = (SettingView) findViewById(R.id.sv_setting_address);
		scv_setting_changebg = (SettingClickView) findViewById(R.id.scv_setting_changebg);
		scv_setting_location = (SettingClickView) findViewById(R.id.scv_setting_location);
		update();
//		address();
		changebg();
		changelocation();
	}

	//activity��ʾ��ʱ�����,���Խ���ڰ�home��ʱ���������棬Ȼ��رշ����ٽ�����������ʱ����������ʾ���ݵĲ���
	@Override
	protected void onStart() {
		super.onStart();
		address();
	}
	
	/**
	 * ���ù�������ʾ���λ��
	 */
	private void changelocation() {
		//�����Զ���ؼ��ı������ʾ������
		scv_setting_location.setTitle("��������ʾ��λ��");
		scv_setting_location.setDes("���ù�������ʾ�����ʾλ��");
		//�Զ���ؼ��ĵ���¼�
		scv_setting_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DragViewActivity.class);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * ���ù�������ʾ��ķ��
	 */
	private void changebg() {
		final String[] items={"��͸��","������","��ʿ��","������","ƻ����"};
		//���ñ������ʾ����
		scv_setting_changebg.setTitle("��������ʾ����");
		//���öԻ����������ݵĻ��Բ���
//		scv_setting_changebg.setDes("��Ů��");
		scv_setting_changebg.setDes(items[sp.getInt("which", 0)]);
		
		//�����Զ���ؼ��ĵ���¼�
		scv_setting_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//������ѡ�Ի���
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				//����ͼ��
				builder.setIcon(R.drawable.ic_launcher);
				//���ñ���
				builder.setTitle("��������ʾ����");
				//���õ�ѡ��
				//items:ѡ���ı������飻checkedItem��ѡ�е���Ŀ��listener���ص�����
				builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						
						//�����ı���������Ϣ
						scv_setting_changebg.setDes(items[which]);
						//���ضԻ���
						dialog.dismiss();
					}
				});
				//����ȡ����ť
				builder.setNegativeButton("ȡ��", null);//�������ťֻ��Ҫ���ضԻ�������Ļ���ֻ��Ҫ�����������Ϊnull,��ʾ�Ի��������
				builder.show();
			}
		});
	}
	/**
	 * �����������ʾ
	 */
	private void address() {
		//���Բ���
		//��Ϊ���������Ŀ����ֶ��Ĺرշ�����˶�̬�Ļ�ȡ�����Ƿ���
		if (AddressUtils.isRunningService(this, "com.heima.mobilesafe.service.AddressService")) {
			//�����ȫ����һ�£���checkbox����Ϊѡ��״̬
			sv_setting_address.setChecked(true);
		}else {
			//�����ȫ������һ�£���checkbox����Ϊѡδ��״̬
			sv_setting_address.setChecked(false);
		}
		
		
		//�Զ���ؼ��ĵ���¼�
		sv_setting_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, AddressService.class);
				//����checkbox֮ǰ��״̬ȥ�����Զ���ؼ�����ʾ
				if (sv_setting_address.isChecked()) {
					//�ر���ʾ���������
					stopService(intent);
					//����checkbox��״̬
					sv_setting_address.setChecked(false);
				}else {
					//����ʾ���������
					startService(intent);
					//����checkbox��״̬
					sv_setting_address.setChecked(true);
				}
			}
		});
	}

	/**
	 * ��ʾ����
	 */
	private void update() {
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
