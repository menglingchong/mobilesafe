package com.heima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.heima.mobilesafe.bean.BlackNumInfo;
import com.heima.mobilesafe.db.WatchDogOpenHelper;
import com.heima.mobilesafe.db.WatchDogOpenHelper;
/**
 * 注意：同一时刻，即进行读数据库操作又进行写操作，怎么避免两个操作同时操作数据库？使用同步锁和将watchDogOpenHelper设置成单例模式
 * @author lenovo
 *
 */
public class WatchDogDao {
	
	private WatchDogOpenHelper watchDogOpenHelper;
	private int mode;
	private Context context;
	//在构造函数中创建数据库
	public WatchDogDao(Context context){
		watchDogOpenHelper = new WatchDogOpenHelper(context);
		this.context = context;
	}
	/**
	 * 添加应用程序包名
	 */
	public void addLockApp(String packagename){
		//创建数据库，在增删改查的各种方法中必须独自创建数据库，使用一个数据库的话，会对其他方法造成影响。
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues(); 
		values.put("packagename",packagename);
		db.insert(WatchDogOpenHelper.DB_NAME, null, values);
		
		//数据库发生变化的时候通知内容观察者，数据库发生变化了
		ContentResolver contentResolver = context.getContentResolver();
		//因为是我们自己应用程序的数据库发生变化，所以我们要定义一个uri进行操作
		Uri uri = Uri.parse("content://com.heima.mobilesafe.lock.changed");
		//通知内容观察者数据库发生变化
		contentResolver.notifyChange(uri, null);
		db.close();//关闭数据库，可以防止内存益出
	}
	/**
	 * 删除包名
	 */
	public void delLockApp(String packagename){
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		db.delete(WatchDogOpenHelper.DB_NAME, "packagename=?", new String[]{packagename});
		
		//数据库发生变化的时候通知内容观察者，数据库发生变化了
		ContentResolver contentResolver = context.getContentResolver();
		//因为是我们自己应用程序的数据库发生变化，所以我们要定义一个uri进行操作
		Uri uri = Uri.parse("content://com.heima.mobilesafe.lock.changed");
		//通知内容观察者数据库发生变化
		contentResolver.notifyChange(uri, null);
		
		db.close();
	}

	/**
	 * 查询数据库是否有包名，有返回true，没有返回false
	 */
	public boolean queryLockApp(String packagename){
		
		boolean isLock =false;
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(WatchDogOpenHelper.DB_NAME, new String[]{"packagename"}, "packagename=?", new String[]{packagename}, null, null, null);
		//解析cursor
		if (cursor.moveToNext()) {
			//获取查询出来的数据
			isLock = true;
		}
		cursor.close();
		db.close();
		return isLock;
	}
	/**
	 * 查询数据库中的所有数据
	 */
	public List<String> querryAllLockApp(){
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = watchDogOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(WatchDogOpenHelper.DB_NAME, new String[]{"packagename"}, null, null, null, null,null );//desc:倒叙查询，默认是正序查询asc
		//解析cursor
		while (cursor.moveToNext()) {
			 String packagename = cursor.getString(0);
			 list.add(packagename);
		}
		//关闭数据库
		cursor.close();
		db.close();
		return list;
	}
}
