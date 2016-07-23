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
 * �����绰�ķ�����
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
	 * �����Ⲧ�绰�Ĺ㲥������
	 */
	private class MyoutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//��ѯ���������
			//��ȡ�Ⲧ�绰
			String phone = getResultData();
			//��ѯ����Ĺ�����
			String location = AddressDao.queryAddress(getApplicationContext(), phone);
			//�жϺ���Ĺ������Ƿ�Ϊ��
			if (! TextUtils.isEmpty(location)) {
				showToast(location);//��ʾtoast
			}
		}
		
	}
	
	//��������
	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//����ע���Ⲧ�绰�Ĺ㲥������,ֻҪ�������й㲥�����߲Ż���Ч������ͨ����������ɱ���㲥�����ߣ�����̬ע��Ĺ㲥�����ߣ�ֻҪ����װ�˾�һֱ�����У����ܱ�ɱ����
		//��Ҫ��Ԫ�أ��㲥�����ߺ����ü����㲥���¼�
		//�㲥������
		myoutCallReceiver = new MyoutCallReceiver();
		//�����㲥���¼�
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");//���ý��յĹ㲥�¼�
		//ע��㲥������
		registerReceiver(myoutCallReceiver, intentFilter);
		
		//1.��ȡ�绰�Ĺ�����
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//2.�����绰��״̬
		myPhoneListener = new MyPhoneListener();
		//listener:�绰�Ļص�����
		//events�������绰���¼�;LISTEN_CALL_STATE:�����绰��״̬
		telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//��ȡ��Ļ�Ĵ�С
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		int width = windowManager.getDefaultDisplay().getWidth();//���ڵķ���
		DisplayMetrics displayMetrics = new DisplayMetrics();//����һ�Ű�ֽ
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);//����ֽ���ÿ��
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		
	}
	
	private class MyPhoneListener extends PhoneStateListener {
		//�����绰״̬�Ļص�����
		//state���绰��״̬��incomingNumber������绰
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE://����״̬�����ǹҶ�״̬
				//����toast
				hideToast();
				break;
			case TelephonyManager.CALL_STATE_RINGING://����״̬
				//��ѯ����Ĺ�����
				String location = AddressDao.queryAddress(getApplicationContext(),incomingNumber);
				if (! TextUtils.isEmpty(location)) {
					//��ʾ���������
//					Toast.makeText(getApplicationContext(), location, 1).show();
					showToast(location);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://����״̬
				
				break;
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//������ر�ʱ��ȡ����������
		//LISTEN_NONE:���������������
		telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_NONE);
		//ע���㲥������
		unregisterReceiver(myoutCallReceiver);
	}
	/**
	 * ����toast
	 */
	public void hideToast() {
		if (windowManager !=null && view!= null ) {
			windowManager.removeView(view);//�Ƴ��ؼ�
			windowManager =null;
			view =null;
		}
	}
	/**
	 * ��ʾ�Զ���Toast
	 */
	public void showToast(String location) {
		
		int[] bgcolor = new int[] { 
				R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		//1.����windowManger������
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		//�������ļ�ת��Ϊview����
		view = View.inflate(getApplicationContext(), R.layout.toast_custom, null);
		//���ݹ�������ʾ�����õ��ı���������toast����ʾ�ı������
		view.setBackgroundResource(bgcolor[sp.getInt("which", 0)]);
		
		//�ҵ��Ӳ����еĿؼ�
		tv_custom_location = (TextView) view.findViewById(R.id.tv_custom_location);
		tv_custom_location.setText(location);
	
		/*textView = new TextView(getApplicationContext());
		textView.setText(location);
		textView.setTextSize(30);
		textView.setTextColor(Color.RED);*/
		//3.����params����
		//layoutparams��toast�����ԣ��ؼ�Ҫ��ӵ����ؼ��У��ؼ���Ҫʹ�ø��ؼ��е����ԣ���ʾ�ؼ������Թ���Ҫ���ϸ��ؼ������Թ���
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;//�߶Ȱ�������
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;//��Ȱ�������
	    params.format = PixelFormat.TRANSLUCENT; //͸��
	    params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; //ִ��toast�����ͣ�TYPE_PRIORITY_PHONE�������ڵ绰�����ͣ�toastû�пɼ����Ժ͵���¼�
//	    params.type = WindowManager.LayoutParams.TYPE_TOAST;
	    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//��Ļ����
	             | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//û�н���
//	             | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//���ɴ���
	    //����toast��λ��
	    params.gravity=Gravity.LEFT | Gravity.TOP;
	    //�������꣬��ʾ����߿�ľ��룬����gravity�����õģ����gravity��left��ʾ������߿�ľ���
		params.x=sp.getInt("x", 100);
		params.y=sp.getInt("y", 100);
	
		//��toast���ô����¼�
		setTouch();
		//2.�������ļ���ӵ�windowManger��
		//view:view����
		//params�� LayoutParams	�ؼ�������
		windowManager.addView(view, params);
	}
	/**
	 *��toast���ô����¼�
	 */
	private void setTouch() {
		view.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//event.getAction(): ��ȡ����ִ�е��¼�
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://��ָ���µ��¼�
					System.out.println("��ָ�����ˣ�����");
					//��ָ���µ�����
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE://��ָ�ƶ����¼�
					System.out.println("��ָ�ƶ��ˣ�����");
					//��ָ�ƶ���������
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					//�����ƫ����
					int dX= newX -startX;
					int dY= newY -startY;
					//�ƶ���Ӧ��ƫ���������»��ƿؼ�
					params.x+=dX;
					params.y+=dY;
					//���ƿؼ������겻���Ƴ��Ⲧ�绰�Ľ���
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
					//���»��ƿؼ���������windowManager�еĿؼ�
					windowManager.updateViewLayout(view, params);//����windowmanager�еĿؼ�
					//����������ʼ����
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP://��ָ̧����¼�
					System.out.println("��ָ̧���ˣ�����");
					//����ؼ������꣬������ǿؼ������ꡣ��������ָ������
					int x = params.x;
					int y = params.y;
					
					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				
				//����false:�¼��¼��������أ��������´��ݣ�����true:�ǽ��¼����Ѽ��¼�����ִ��
				return true;
			}
		});
	}

} 