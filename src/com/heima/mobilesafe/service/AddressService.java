package com.heima.mobilesafe.service;

import com.heima.mobilesafe.R;
import com.heima.mobilesafe.db.dao.AddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
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
	private int screenWidth;
	private int screenHeight;
	private WindowManager.LayoutParams params;
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
		
		//获取屏幕的大小
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		int width = windowManager.getDefaultDisplay().getWidth();//过期的方法
		DisplayMetrics displayMetrics = new DisplayMetrics();//创建一张白纸
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);//给白纸设置宽高
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		
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
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;//高度包裹内容
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;//宽度包裹内容
	    params.format = PixelFormat.TRANSLUCENT; //透明
	    params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; //执行toast的类型，TYPE_PRIORITY_PHONE：优先于电话的类型，toast没有可见属性和点击事件
//	    params.type = WindowManager.LayoutParams.TYPE_TOAST;
	    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//屏幕常量
	             | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//没有焦点
//	             | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//不可触摸
	    //设置toast的位置
	    params.gravity=Gravity.LEFT | Gravity.TOP;
	    //不是坐标，表示距离边框的距离，根据gravity来设置的，如果gravity是left表示距离左边框的距离
		params.x=sp.getInt("x", 100);
		params.y=sp.getInt("y", 100);
	
		//给toast设置触摸事件
		setTouch();
		//2.将布局文件添加到windowManger中
		//view:view对象
		//params： LayoutParams	控件的属性
		windowManager.addView(view, params);
	}
	/**
	 *给toast设置触摸事件
	 */
	private void setTouch() {
		view.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//event.getAction(): 获取手势执行的事件
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://手指按下的事件
					System.out.println("手指按下了！！！");
					//手指按下的坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE://手指移动的事件
					System.out.println("手指移动了！！！");
					//手指移动到的坐标
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					//坐标的偏移量
					int dX= newX -startX;
					int dY= newY -startY;
					//移动相应的偏移量，重新绘制控件
					params.x+=dX;
					params.y+=dY;
					//控制控件的坐标不能移除外拨电话的界面
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y=0;
					}
					if (params.x > screenWidth-view.getWidth()) {
						params.x = screenWidth-view.getWidth();
					}
					if (params.y > screenHeight - view.getHeight() - 20) {
						params.y = screenHeight - view.getHeight() - 20;
					}		
					//重新绘制控件，即更新windowManager中的控件
					windowManager.updateViewLayout(view, params);//更新windowmanager中的控件
					//重新设置起始坐标
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP://手指抬起的事件
					System.out.println("手指抬起了！！！");
					//保存控件的坐标，保存的是控件的坐标。而不是手指的坐标
					int x = params.x;
					int y = params.y;
					
					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				
				//返回false:事件事件进行拦截，不能向下传递；返回true:是将事件消费即事件向下执行
				return true;
			}
		});
	}

} 