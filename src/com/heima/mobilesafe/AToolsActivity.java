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
	 * 按钮的点击事件
	 * @param v
	 */
	public void queryAddress(View v){
		//跳转到查询号码归属地界面
		Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
		startActivity(intent);
	}
	/**
	 * 短信备份的点击事件
	 * @param v
	 */
	public void backupSms(View v){
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);//不能取消
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
 
		//短信的备份是耗时操作，在子线程中备份
		new Thread(new Runnable() {
			//回调函数，就可以将要做的操作放到我们这边来执行，业务类提供一个操作，但是不知道这个操作怎么做，则他把这个操作交给我们来做，具体的操作由我们决定
			@Override
			public void run() {
				//回调者接收刷子，
				SmsEngine.getAllSms(getApplicationContext(),new ShowProgress() {
					
					@Override
					public void setProgress(int progress) {//参数值是回调函数传过来的值
						//设置当前进度条的进度
//						pb_atools_progress.setProgress(progress);
						progressDialog.setProgress(progress);
					}
					
					@Override
					public void setMax(int max) {
						//设置最大进度
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
	 * 短信还原的点击事件
	 * @param v
	 */
	public void restoreSms(View v){
	
		//解析xml
		XmlPullParser newPullParser = Xml.newPullParser();
		//插入短信
		ContentResolver contentResolver = getContentResolver();
		Uri uri = Uri.parse("content://sms");
		ContentValues values = new ContentValues();
		values.put("address", "95588");
		values.put("date",  System.currentTimeMillis());
		values.put("type", "1");
		values.put("body", "我爱你！！");
		contentResolver.insert(uri, values);
	}
	
}
