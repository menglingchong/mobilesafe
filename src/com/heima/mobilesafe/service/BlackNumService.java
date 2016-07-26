package com.heima.mobilesafe.service;

import com.heima.mobilesafe.db.dao.BlackNumDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

public class BlackNumService extends Service {

	private SmsReceiver smsReceiver;
	private BlackNumDao blackNumDao;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private class SmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("代码注册接收广播接收者接收短信");
			//短信解析的操作
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				//解析成SmsMessage
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String body = smsMessage.getMessageBody();//获取短信的内容
				String address = smsMessage.getOriginatingAddress();//获取短信的发信人
				//根据发件人的号码，获取号码的拦截模式
				int mode = blackNumDao.queryBlackNum(address);
				//判断是否是短信拦截或者是全部拦截
				if (mode == BlackNumDao.SMS || mode== BlackNumDao.ALL) {
					//拦截短信的广播事件，拦截短信操作
					abortBroadcast();
				}
			}
		}
		
	}
	//创建服务，在服务中监听广播
	@Override
	public void onCreate() {
		super.onCreate();
		blackNumDao = new BlackNumDao(getApplicationContext());
		//代码注册短信到来的广播接收者,需要广播接收者和监听事件
		//创建广播接收者
		smsReceiver = new SmsReceiver();
		//设置监听广播事件
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, intentFilter);//注册广播接收者
		
	}
	//销毁服务
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消注册广播接收者
		unregisterReceiver(smsReceiver);
	}

}
