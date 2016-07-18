package com.heima.mobilesafe.utils;

import android.os.Handler;

/**
 * 异步加载框架
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
	 * 在子线程之前执行的方法
	 */
	public abstract void preTask(); 
	/**
	 * 在子线程中执行的方法
	 */
	public abstract void doInBack();
	/**
	 * 在子线程之后执行的方法
	 */
	public abstract void postTask();
	
	//执行
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
