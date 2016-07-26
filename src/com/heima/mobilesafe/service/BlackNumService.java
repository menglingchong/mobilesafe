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
		
		//监听电话的状态
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		//listener:电话的状态；events：监听的事件
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			//如果是响铃状态，检测拦截模式是否是电话拦截，如果是则挂断
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				//判断电话的模式
				int mode = blackNumDao.queryBlackNum(incomingNumber);
				if (mode == BlackNumDao.CALL || mode == BlackNumDao.ALL) {
					//挂断电话
					//在1.5版本之后不可以使用，该方法被隐藏，查看源码可知telephonyManager调用ITelephony中的方法实现有关的方法
					//要想实现有关的方法，则可以通过直接调用ITelephony中的相关方法
//					telephonyManager.endcall();
					endCall();
					
					//删除通话记录
					//1.获取内容解析者
					final ContentResolver contentResolver = getContentResolver();
					//2.获取内容提供者地址:call_log calls表的地址：calls
					//3.获取执行操作的路径
					final Uri uri = Uri.parse("content://call_log/calls");
					//4.删除操作
//					contentResolver.delete(uri, "number=?", new String[]{incomingNumber});//直接删除会出现无法删除最新的一条来电记录，这是因为时间的问题
					//通过内容观察者观察内容提供者的内容，如果内容发生变化。就执行删除操作
					//registerContentObserver():内容解析者注册内容观察者；
					//notifyForDescendents:匹配规则，true:精确匹配 false:模糊匹配
					contentResolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
						//内容提供者内容变化的时候调用
						@Override
						public void onChange(boolean selfChange) {
							super.onChange(selfChange);
							//删除通话记录
							contentResolver.delete(uri, "number=?", new String[]{incomingNumber});
							//注销内容观察者
							contentResolver.unregisterContentObserver(this);
						}
					});
				}
			}
		}
	} 
	/**
	 * 挂断电话
	 */
	public void endCall() {
		//通过反射进行实现
		try {
			//1.通过类加载器加载相应的class文件
//			Class<?> forName = Class.forName("android.os.ServiceManager");
			Class<?> loadClass = BlackNumService.class.getClassLoader().loadClass("android.os.ServiceManager");
			//2.获取类中相应的方法
			//name:方法名；parameterTypes：参数类型
			Method method = loadClass.getDeclaredMethod("getService", String.class);
			//3.执行方法，获取返回值
			//receiver : 类的实例;args:具体的参数
			IBinder invoke = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
			//获取ITelephony对象;ITelephony.Stub:是aidl
			ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
			//挂断电话
			iTelephony.endCall();
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
	}

	//销毁服务
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消注册广播接收者
		unregisterReceiver(smsReceiver);
		//取消监听电话
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
	}
	
}
