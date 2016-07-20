package com.heima.mobilesafe.receiver;

import com.heima.mobilesafe.R;
import com.heima.mobilesafe.service.GpsService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	private MediaPlayer mediaPlayer;

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
				//����һ��������к�̨��λ��������Ϊ��ȡGPS�Ǻ�ʱ���������������߳��н��У�
				//���㲥�����������߳�,��˲����ڹ㲥������ֱ�ӽ���GPS��λ�����ң��㲥�������в��ܿ������̣߳���Ӧ�ĺ�ʱ�����ڷ����д���
				Intent intent_gps = new Intent(context, GpsService.class);
				context.startService(intent_gps);//��������
				//���ض���
				abortBroadcast();//���ز�����ԭ��androidϵͳ�п��ԣ��������Ƶ�ϵͳ�п�������
				
			}else if ("#*alarm*#".equals(body)) {
				//���ű�������
				System.out.println("���ű�������");
				//�ڲ��ű�������֮ǰ����ϵͳ�������ó����
				//�����Ĺ�����
				AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				//����ϵͳ�����Ĵ�С
				//streamType:���������ͣ�index�������Ĵ�С��0��С�� 15���flags��ָ����Ϣ�ı�ǩ
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				//���ű�������֮ǰ����֮ǰ�Ĳ������ֵ���Դ�ͷ�
				if (mediaPlayer!=null) {
					mediaPlayer.release();//�ͷ���Դ
				}
				
				mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
				mediaPlayer.start();
				
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