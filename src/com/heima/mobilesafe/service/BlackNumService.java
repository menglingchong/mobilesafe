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
			System.out.println("����ע����չ㲥�����߽��ն���");
			//���Ž����Ĳ���
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				//������SmsMessage
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String body = smsMessage.getMessageBody();//��ȡ���ŵ�����
				String address = smsMessage.getOriginatingAddress();//��ȡ���ŵķ�����
				//���ݷ����˵ĺ��룬��ȡ���������ģʽ
				int mode = blackNumDao.queryBlackNum(address);
				//�ж��Ƿ��Ƕ������ػ�����ȫ������
				if (mode == BlackNumDao.SMS || mode== BlackNumDao.ALL) {
					//���ض��ŵĹ㲥�¼������ض��Ų���
					abortBroadcast();
				}
			}
		}
		
	}
	//���������ڷ����м����㲥
	@Override
	public void onCreate() {
		super.onCreate();
		blackNumDao = new BlackNumDao(getApplicationContext());
		//����ע����ŵ����Ĺ㲥������,��Ҫ�㲥�����ߺͼ����¼�
		//�����㲥������
		smsReceiver = new SmsReceiver();
		//���ü����㲥�¼�
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, intentFilter);//ע��㲥������
		
	}
	//���ٷ���
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ȡ��ע��㲥������
		unregisterReceiver(smsReceiver);
	}

}
