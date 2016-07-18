package com.heima.mobilesafe.utils;

import android.os.Handler;

/**
 * �첽���ؿ��
 * @author lenovo
 *
 */
public abstract class MyAsynctask {

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			postTask();
		};
	};
	/**
	 * �����߳�֮ǰִ�еķ���
	 */
	public abstract void preTask(); 
	/**
	 * �����߳���ִ�еķ���
	 */
	public abstract void doInBack();
	/**
	 * �����߳�֮��ִ�еķ���
	 */
	public abstract void postTask();
	
	//ִ��
	public void execute(){
		preTask();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				doInBack();
				handler.sendEmptyMessage(0);
			}
		}).start();
		
	}
}
