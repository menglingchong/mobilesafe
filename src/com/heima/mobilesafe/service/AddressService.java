package com.heima.mobilesafe.service;

import com.heima.mobilesafe.R;
import com.heima.mobilesafe.db.dao.AddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.style.TtsSpan.TelephoneBuilder;
import android.view.Gravity;
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
	private View view;
	private TextView tv_custom_location;
	private MyoutCallReceiver myoutCallReceiver;
	private SharedPreferences sp;
//	private TextView textView;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/**
	 * 创建外拨电话的广播接收者
	 */
	private class MyoutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//查询号码归属地
			//获取外拨电话
			String phone = getResultData();
			//查询号码的归属地
			String location = AddressDao.queryAddress(getApplicationContext(), phone);
			//判断号码的归属地是否为空
			if (! TextUtils.isEmpty(location)) {
				showToast(location);//显示toast
			}
		}
		
	}
	
	//创建服务
	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//代码注册外拨电话的广播接收者,只要程序运行广播接收者才会有效，可以通过结束进程杀死广播接收者，而静态注册的广播接收者，只要程序安装了就一直在运行，不能被杀死！
		//需要的元素：广播接收者和设置监听广播的事件
		//广播接收者
		myoutCallReceiver = new MyoutCallReceiver();
		//监听广播的事件
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");//设置接收的广播事件
		//注册广播接收者
		registerReceiver(myoutCallReceiver, intentFilter);
		
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
		//注销广播接收者
		unregisterReceiver(myoutCallReceiver);
	}
	/**
	 * 隐藏toast
	 */
	public void hideToast() {
		if (windowManager !=null && view!= null ) {
			windowManager.removeView(view);//移除控件
			windowManager =null;
			view =null;
		}
	}
	/**
	 * 显示自定义Toast
	 */
	public void showToast(String location) {
		int[] bgcolor = new int[] { 
				R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		//1.创建windowManger管理器
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		//将布局文件转化为view对象
		view = View.inflate(getApplicationContext(), R.layout.toast_custom, null);
		//根据归属地提示框设置的文本内容设置toast的显示的背景情况
		view.setBackgroundResource(bgcolor[sp.getInt("which", 0)]);
		
		//找到子布局中的控件
		tv_custom_location = (TextView) view.findViewById(R.id.tv_custom_location);
		tv_custom_location.setText(location);
		/*textView = new TextView(getApplicationContext());
		textView.setText(location);
		textView.setTextSize(30);
		textView.setTextColor(Color.RED);*/
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
	    //设置toast的位置
	    params.gravity=Gravity.LEFT | Gravity.TOP;
	    //不是坐标，表示距离边框的距离，根据gravity来设置的，如果gravity是left表示距离左边框的距离
		params.x=120;
		params.y=100;
	    
		//2.将布局文件添加到windowManger中
		//view:view对象
		//params： LayoutParams	控件的属性
		windowManager.addView(view, params);
	}

} 