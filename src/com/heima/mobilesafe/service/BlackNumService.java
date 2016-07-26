package com.heima.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.heima.mobilesafe.db.dao.BlackNumDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.style.TtsSpan.TelephoneBuilder;

public class BlackNumService extends Service {

	private SmsReceiver smsReceiver;
	private BlackNumDao blackNumDao;
	private TelephonyManager telephonyManager;
	private MyPhoneStateListener myPhoneStateListener;
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
		
		//�����绰��״̬
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		//listener:�绰��״̬��events���������¼�
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			//���������״̬���������ģʽ�Ƿ��ǵ绰���أ��������Ҷ�
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				//�жϵ绰��ģʽ
				int mode = blackNumDao.queryBlackNum(incomingNumber);
				if (mode == BlackNumDao.CALL || mode == BlackNumDao.ALL) {
					//�Ҷϵ绰
					//��1.5�汾֮�󲻿���ʹ�ã��÷��������أ��鿴Դ���֪telephonyManager����ITelephony�еķ���ʵ���йصķ���
					//Ҫ��ʵ���йصķ����������ͨ��ֱ�ӵ���ITelephony�е���ط���
//					telephonyManager.endcall();
					endCall();
					
					//ɾ��ͨ����¼
					//1.��ȡ���ݽ�����
					final ContentResolver contentResolver = getContentResolver();
					//2.��ȡ�����ṩ�ߵ�ַ:call_log calls��ĵ�ַ��calls
					//3.��ȡִ�в�����·��
					final Uri uri = Uri.parse("content://call_log/calls");
					//4.ɾ������
//					contentResolver.delete(uri, "number=?", new String[]{incomingNumber});//ֱ��ɾ��������޷�ɾ�����µ�һ�������¼��������Ϊʱ�������
					//ͨ�����ݹ۲��߹۲������ṩ�ߵ����ݣ�������ݷ����仯����ִ��ɾ������
					//registerContentObserver():���ݽ�����ע�����ݹ۲��ߣ�
					//notifyForDescendents:ƥ�����true:��ȷƥ�� false:ģ��ƥ��
					contentResolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
						//�����ṩ�����ݱ仯��ʱ�����
						@Override
						public void onChange(boolean selfChange) {
							super.onChange(selfChange);
							//ɾ��ͨ����¼
							contentResolver.delete(uri, "number=?", new String[]{incomingNumber});
							//ע�����ݹ۲���
							contentResolver.unregisterContentObserver(this);
						}
					});
				}
			}
		}
	} 
	/**
	 * �Ҷϵ绰
	 */
	public void endCall() {
		//ͨ���������ʵ��
		try {
			//1.ͨ���������������Ӧ��class�ļ�
//			Class<?> forName = Class.forName("android.os.ServiceManager");
			Class<?> loadClass = BlackNumService.class.getClassLoader().loadClass("android.os.ServiceManager");
			//2.��ȡ������Ӧ�ķ���
			//name:��������parameterTypes����������
			Method method = loadClass.getDeclaredMethod("getService", String.class);
			//3.ִ�з�������ȡ����ֵ
			//receiver : ���ʵ��;args:����Ĳ���
			IBinder invoke = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
			//��ȡITelephony����;ITelephony.Stub:��aidl
			ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
			//�Ҷϵ绰
			iTelephony.endCall();
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
	}

	//���ٷ���
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ȡ��ע��㲥������
		unregisterReceiver(smsReceiver);
		//ȡ�������绰
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
	}
	
}
