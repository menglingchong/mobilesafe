package com.heima.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	/**
	 *打开数据库，查询号码的归属地
	 */
	public static String queryAddress(Context context,String num){
		String location = "";
		//1.获取数据库的路径
		File file = new File(context.getFilesDir(), "address.db");
		//2.打开数据库
		//getAbsolutePath()：获取文件的绝对路径
		//factory:游标工厂
		SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		//3.查询号码归属地
		Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{num.substring(0, 7)});
		//4.解析cursor
		if (cursor.moveToNext()) {
			location = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return location;
		
	}
}
