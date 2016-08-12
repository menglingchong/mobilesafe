package com.heima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.heima.mobilesafe.bean.BlackNumInfo;
import com.heima.mobilesafe.db.WatchDogOpenHelper;
import com.heima.mobilesafe.db.WatchDogOpenHelper;
/**
 * ע�⣺ͬһʱ�̣������ж����ݿ�����ֽ���д��������ô������������ͬʱ�������ݿ⣿ʹ��ͬ�����ͽ�watchDogOpenHelper���óɵ���ģʽ
 * @author lenovo
 *
 */
public class WatchDogDao {
	
	private WatchDogOpenHelper watchDogOpenHelper;
	private int mode;

	//�ڹ��캯���д������ݿ�
	public WatchDogDao(Context context){
		watchDogOpenHelper = new WatchDogOpenHelper(context);
	}
	/**
	 * ���Ӧ�ó������
	 */
	public void addLockApp(String packagename){
		//�������ݿ⣬����ɾ�Ĳ�ĸ��ַ����б�����Դ������ݿ⣬ʹ��һ�����ݿ�Ļ�����������������Ӱ�졣
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues(); 
		values.put("packagename",packagename);
		db.insert(WatchDogOpenHelper.DB_NAME, null, values);
		db.close();//�ر����ݿ⣬���Է�ֹ�ڴ����
	}
	/**
	 * ɾ������
	 */
	public void delLockApp(String packagename){
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		db.delete(WatchDogOpenHelper.DB_NAME, "packagename=?", new String[]{packagename});
		db.close();
	}

	/**
	 * ��ѯ���ݿ��Ƿ��а������з���true��û�з���false
	 */
	public boolean queryLockApp(String packagename){
		
		boolean isLock =false;
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(WatchDogOpenHelper.DB_NAME, new String[]{"packagename"}, "packagename=?", new String[]{packagename}, null, null, null);
		//����cursor
		if (cursor.moveToNext()) {
			//��ȡ��ѯ����������
			isLock = true;
		}
		cursor.close();
		db.close();
		return isLock;
	}
	/**
	 * ��ѯ���ݿ��е���������
	 */
	public List<String> querryAllLockApp(){
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(WatchDogOpenHelper.DB_NAME, new String[]{"packagename"}, null, null, null, null,null );//desc:�����ѯ��Ĭ���������ѯasc
		//����cursor
		while (cursor.moveToNext()) {
			 String packagename = cursor.getString(0);
			 list.add(packagename);
		}
		//�ر����ݿ�
		cursor.close();
		db.close();
		return list;
	}
}
