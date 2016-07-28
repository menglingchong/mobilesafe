package com.heima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.heima.mobilesafe.bean.BlackNumInfo;
import com.heima.mobilesafe.db.BlackNumOpenHelper;

public class BlackNumDao {
	
	public static final int CALL =0;
	public static final int SMS =1;
	public static final int ALL=2;
	
	private BlackNumOpenHelper blackNumOpenHelper;
	private int mode;

	//在构造函数中创建数据库
	public BlackNumDao(Context context){
		blackNumOpenHelper = new BlackNumOpenHelper(context);
	}
	/**
	 * 添加数据
	 */
	public void addBlackNum(String blacknum,int mode){
		//创建数据库，在增删改查的各种方法中必须独自创建数据库，使用一个数据库的话，会对其他方法造成影响。
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues(); 
		values.put("blacknum", blacknum);
		values.put("mode", mode);
		db.insert(BlackNumOpenHelper.DB_NAME, null, values);
		db.close();//关闭数据库，可以防止内存益出
	}
	/**
	 * 删除数据
	 */
	public void delBlackNum(String blacknum){
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		db.delete(BlackNumOpenHelper.DB_NAME, "blacknum=?", new String[]{blacknum});
		db.close();
	}

	/**
	 * 更新黑名单的拦截模式
	 */
	public void updateBalckNum(String blacknum,int mode){
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update(BlackNumOpenHelper.DB_NAME, values, "blacknum=?", new String[]{blacknum});
		db.close();
	}

	/**
	 * 查询数据,通过黑名单号码，查询黑名单号码的拦截模式
	 */
	public int queryBlackNum(String blacknum){
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(BlackNumOpenHelper.DB_NAME, new String[]{"mode"}, "blacknum=?", new String[]{blacknum}, null, null, null);
		//解析cursor
		if (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
	/**
	 * 查询数据库中的所有数据
	 */
	public List<BlackNumInfo> querryAllNum(){
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(BlackNumOpenHelper.DB_NAME, new String[]{"blacknum","mode"}, null, null, null, null,"_id desc" );//desc:倒叙查询，默认是正序查询asc
		//解析cursor
		while (cursor.moveToNext()) {
			 String blacknum = cursor.getString(0);
			 int mode = cursor.getInt(1);
			 //将得到的数据添加到bean对象
			 BlackNumInfo blackNumInfo = new BlackNumInfo(blacknum, mode);
			 list.add(blackNumInfo);
		}
		//关闭数据库
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 查询数据库中的部分数据
	 * MaxNum:查询的总条数
	 * startindex：查询的起始位置
	 */
	public List<BlackNumInfo> querryPartNum(int MaxNum,int startindex){
		
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select blacknum,mode from info order by _id desc limit ? offset ?", new String[]{MaxNum+"",startindex+""});
		//解析cursor
		while(cursor.moveToNext()) {
			//获取查询出来的数据
			String blacknum = cursor.getString(0);
			int mode = cursor.getInt(1);
			//将得到的数据添加到bean对象
			BlackNumInfo blackNumInfo = new BlackNumInfo(blacknum, mode);
			list.add(blackNumInfo);
		}
		cursor.close();
		db.close();
		return list;
	}
}
