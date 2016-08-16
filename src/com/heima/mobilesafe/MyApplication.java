package com.heima.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
/**
 * �����쳣����,Ӧ�����ߺ���Խ�������쳣�����ϴ���̨,�����Ѽ�bug��Ϣ
 * @author lenovo
 *
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("application�����ˡ�����");
	
		Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	}
 
	public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//ϵͳ��δ������쳣��ʱ�����
		//Throwable: Error ��Exception �ĸ���
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("��ǿ����Ժ���쳣������");
			ex.printStackTrace();
			
			try {
				//��������쳣�ŵ�sd����
				ex.printStackTrace(new PrintStream(new File("/storage/sdcard1/log.txt")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//myPid():��ȡ��ǰӦ�ý��̵�id
			//Ӧ�ó����Լ����Լ�ɱ��
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		
	}

}
