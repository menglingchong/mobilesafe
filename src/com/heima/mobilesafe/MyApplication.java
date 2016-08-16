package com.heima.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
/**
 * 捕获异常的类,应用上线后可以将捕获的异常进行上传后台,便于搜集bug信息
 * @author lenovo
 *
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("application启动了。。。");
	
		Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	}
 
	public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//系统有未捕获的异常的时候调用
		//Throwable: Error 和Exception 的父类
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("宝强，后院有异常啊！！");
			ex.printStackTrace();
			
			try {
				//将捕获的异常放到sd卡中
				ex.printStackTrace(new PrintStream(new File("/storage/sdcard1/log.txt")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//myPid():获取当前应用进程的id
			//应用程序自己将自己杀死
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		
	}

}
