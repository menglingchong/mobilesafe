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
 * �����绰�ķ�����
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
	//��������
	@Override
	public void onCreate() {
		super.onCreate();
		//1.��ȡ�绰�Ĺ�����
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//2.�����绰��״̬
		myPhoneListener = new MyPhoneListener();
		//listener:�绰�Ļص�����
		//events�������绰���¼�;LISTEN_CALL_STATE:�����绰��״̬
		telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		
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
			
			default:
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
	}
	/**
	 * ����toast
	 */
	public void hideToast() {
		if (windowManager !=null && textView!= null ) {
			windowManager.removeView(textView);//�Ƴ��ؼ�
			windowManager =null;
			textView =null;
		}
	}
	/**
	 * ��ʾ�Զ���Toast
	 */
	public void showToast(String location) {
		//1.����windowManger������
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		textView = new TextView(getApplicationContext());
		textView.setText(location);
		textView.setTextSize(30);
		textView.setTextColor(Color.RED);
		//3.����params����
		//layoutparams��toast�����ԣ��ؼ�Ҫ��ӵ����ؼ��У��ؼ���Ҫʹ�ø��ؼ��е����ԣ���ʾ�ؼ������Թ���Ҫ���ϸ��ؼ������Թ���
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;//�߶Ȱ�������
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;//��Ȱ�������
	    params.format = PixelFormat.TRANSLUCENT; //͸��
	    params.type = WindowManager.LayoutParams.TYPE_TOAST;//ִ��toast����
	    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//��Ļ����
	             | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//û�н���
	             | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//���ɴ���
		
		//2.�������ļ���ӵ�windowManger��
		//view:view����
		//params�� LayoutParams	�ؼ�������
		windowManager.addView(textView, params);
	}

} 