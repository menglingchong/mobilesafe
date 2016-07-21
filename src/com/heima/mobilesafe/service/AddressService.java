package com.heima.mobilesafe.service;

import com.heima.mobilesafe.db.dao.AddressDao;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.style.TtsSpan.TelephoneBuilder;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 监听电话的服务类
 * @author lenovo
 *
 */
public class AddressService extends Service {

	private TelephonyManager telephonyManager;
	private MyPhoneListener myPhoneListener;
	private WindowManager windowManager;
	private TextView textView;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	//创建服务
	@Override
	public void onCreate() {
		super.onCreate();
		//1.获取电话的管理器
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//2.监听电话的状态
		myPhoneListener = new MyPhoneListener();
		//listener:电话的回调监听
		//events：监听电话的事件;LISTEN_CALL_STATE:监听电话的状态
		telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		
	}
	
	private class MyPhoneListener extends PhoneStateListener {
		//监听电话状态的回调方法
		//state：电话的状态；incomingNumber：来电电话
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE://空闲状态或者是挂断状态
				//隐藏toast
				hideToast();
				break;
			case TelephonyManager.CALL_STATE_RINGING://响铃状态
				//查询号码的归属地
				String location = AddressDao.queryAddress(getApplicationContext(),incomingNumber);
				if (! TextUtils.isEmpty(location)) {
					//显示号码归属地
//					Toast.makeText(getApplicationContext(), location, 1).show();
					showToast(location);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://接听状态
				
				break;
			
			default:
				break;
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//当服务关闭时，取消监听操作
		//LISTEN_NONE:不做任务监听操作
		telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_NONE);
	}
	/**
	 * 隐藏toast
	 */
	public void hideToast() {
		if (windowManager !=null && textView!= null ) {
			windowManager.removeView(textView);//移除控件
			windowManager =null;
			textView =null;
		}
	}
	/**
	 * 显示自定义Toast
	 */
	public void showToast(String location) {
		//1.创建windowManger管理器
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		textView = new TextView(getApplicationContext());
		textView.setText(location);
		textView.setTextSize(30);
		textView.setTextColor(Color.RED);
		//3.设置params属性
		//layoutparams是toast的属性，控件要添加到父控件中，控件就要使用父控件中的属性，表示控件的属性规则要符合父控件的属性规则
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;//高度包裹内容
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;//宽度包裹内容
	    params.format = PixelFormat.TRANSLUCENT; //透明
	    params.type = WindowManager.LayoutParams.TYPE_TOAST;//执行toast类型
	    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//屏幕常量
	             | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//没有焦点
	             | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//不可触摸
		
		//2.将布局文件添加到windowManger中
		//view:view对象
		//params： LayoutParams	控件的属性
		windowManager.addView(textView, params);
	}

} 