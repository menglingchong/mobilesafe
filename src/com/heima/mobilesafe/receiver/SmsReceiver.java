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
		//接收短信解析的操作，解析短信的内容，如果是指令的话，就执行相应的操作
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			//解析成SmsMessage
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();//获取短信的内容
			String address = smsMessage.getOriginatingAddress();//获取短信的发件人
			System.out.println("发件人："+address+"	短信内容："+body);
			//判断短信是哪个指令
			if ("#*location*#".equals(body)) {
				//GPS追踪
				System.out.println("GPS追踪");
				//开启一个服务进行后台定位操作，因为获取GPS是耗时操作，不能在主线程中进行，
				//而广播接收者是主线程,因此不能在广播接收者直接进行GPS定位。并且，广播接收者中不能开启子线程，相应的耗时操作在服务中处理
				Intent intent_gps = new Intent(context, GpsService.class);
				context.startService(intent_gps);//开启服务
				//拦截短信
				abortBroadcast();//拦截操作，原生android系统中可以，国产定制的系统中可能屏蔽
				
			}else if ("#*alarm*#".equals(body)) {
				//播放报警音乐
				System.out.println("播放报警音乐");
				//在播放报警音乐之前，将系统音量设置成最大
				//声音的管理者
				AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				//设置系统音量的大小
				//streamType:声音的类型；index：声音的大小；0最小， 15最大；flags：指定信息的标签
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				//播放报警音乐之前，将之前的播放音乐的资源释放
				if (mediaPlayer!=null) {
					mediaPlayer.release();//释放资源
				}
				
				mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
				mediaPlayer.start();
				
				abortBroadcast();
				
			}else if ("#*wipedata*#".equals(body)) {
				//远程删除数据
				System.out.println("远程删除数据");
				abortBroadcast();
			}else if ("#*lockscreen*#".equals(body)) {
				//远程锁屏
				System.out.println("远程锁屏");
				abortBroadcast();
			}
		}
	}

}
