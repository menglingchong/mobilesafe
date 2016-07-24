package com.heima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumOpenHelper extends SQLiteOpenHelper {
	
	//将表名抽取出来方便我们使用表名，也方便后期修改表名
	public static final String DB_NAME = "info";
	public BlackNumOpenHelper(Context context) {
		super(context, "blackNum.db", null, 1);
		
	}
	//第一次创建数据库的时候调用，创建表结构
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+DB_NAME+"(_id integer primary key autoincrement, blacknum varchar(20),mode varchar(2))");
	}
	//数据库升级的时候调用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
