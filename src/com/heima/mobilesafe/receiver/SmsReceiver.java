package com.heima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//���ն��Ž����Ĳ������������ŵ����ݣ������ָ��Ļ�����ִ����Ӧ�Ĳ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			//������SmsMessage
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();//��ȡ���ŵ�����
			String address = smsMessage.getOriginatingAddress();//��ȡ���ŵķ�����
			System.out.println("�����ˣ�"+address+"	�������ݣ�"+body);
			//�ж϶������ĸ�ָ��
			if ("#*location*#".equals(body)) {
				//GPS׷��
				System.out.println("GPS׷��");
				//���ض���
				abortBroadcast();//���ز�����ԭ��androidϵͳ�п��ԣ��������Ƶ�ϵͳ�п�������
				
			}else if ("#*alarm*#".equals(body)) {
				//���ű�������
				System.out.println("���ű�������");
				abortBroadcast();
				
			}else if ("#*wipedata*#".equals(body)) {
				//Զ��ɾ������
				System.out.println("Զ��ɾ������");
				abortBroadcast();
			}else if ("#*lockscreen*#".equals(body)) {
				//Զ������
				System.out.println("Զ������");
				abortBroadcast();
			}
		}
	}

}
