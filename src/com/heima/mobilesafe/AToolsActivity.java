package com.heima.mobilesafe;

import org.xmlpull.v1.XmlPullParser;

import com.heima.mobilesafe.engine.SmsEngine;
import com.heima.mobilesafe.engine.SmsEngine.ShowProgress;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;

public class AToolsActivity extends Activity {

	private ProgressDialog progressDialog;
 
	private ProgressBar pb_atools_progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
		pb_atools_progress = (ProgressBar) findViewById(R.id.pb_atools_progress);
	}
	/**
	 * ��ť�ĵ���¼�
	 * @param v
	 */
	public void queryAddress(View v){
		//��ת����ѯ��������ؽ���
		Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
		startActivity(intent);
	}
	/**
	 * ���ű��ݵĵ���¼�
	 * @param v
	 */
	public void backupSms(View v){
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);//����ȡ��
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
 
		//���ŵı����Ǻ�ʱ�����������߳��б���
		new Thread(new Runnable() {
			//�ص��������Ϳ��Խ�Ҫ���Ĳ����ŵ����������ִ�У�ҵ�����ṩһ�����������ǲ�֪�����������ô�����������������������������������Ĳ��������Ǿ���
			@Override
			public void run() {
				//�ص��߽���ˢ�ӣ�
				SmsEngine.getAllSms(getApplicationContext(),new ShowProgress() {
					
					@Override
					public void setProgress(int progress) {//����ֵ�ǻص�������������ֵ
						//���õ�ǰ�������Ľ���
//						pb_atools_progress.setProgress(progress);
						progressDialog.setProgress(progress);
					}
					
					@Override
					public void setMax(int max) {
						//����������
//						pb_atools_progress.setMax(max);
						progressDialog.setMax(max);
					}
				});
//				progressDialog.getMax();
//				progressDialog.getProgress();
				progressDialog.dismiss();
			}
		}).start();
	}
	/**
	 * ���Ż�ԭ�ĵ���¼�
	 * @param v
	 */
	public void restoreSms(View v){
	
		//����xml
		XmlPullParser newPullParser = Xml.newPullParser();
		//�������
		ContentResolver contentResolver = getContentResolver();
		Uri uri = Uri.parse("content://sms");
		ContentValues values = new ContentValues();
		values.put("address", "95588");
		values.put("date",  System.currentTimeMillis());
		values.put("type", "1");
		values.put("body", "�Ұ��㣡��");
		contentResolver.insert(uri, values);
	}
	
}
