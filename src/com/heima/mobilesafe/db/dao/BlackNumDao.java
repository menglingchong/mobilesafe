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

	//�ڹ��캯���д������ݿ�
	public BlackNumDao(Context context){
		blackNumOpenHelper = new BlackNumOpenHelper(context);
	}
	/**
	 * �������
	 */
	public void addBlackNum(String blacknum,int mode){
		//�������ݿ⣬����ɾ�Ĳ�ĸ��ַ����б�����Դ������ݿ⣬ʹ��һ�����ݿ�Ļ�����������������Ӱ�졣
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues(); 
		values.put("blacknum", blacknum);
		values.put("mode", mode);
		db.insert(BlackNumOpenHelper.DB_NAME, null, values);
		db.close();//�ر����ݿ⣬���Է�ֹ�ڴ����
	}
	/**
	 * ɾ������
	 */
	public void delBlackNum(String blacknum){
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		db.delete(BlackNumOpenHelper.DB_NAME, "blacknum=?", new String[]{blacknum});
		db.close();
	}

	/**
	 * ���º�����������ģʽ
	 */
	public void updateBalckNum(String blacknum,int mode){
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update(BlackNumOpenHelper.DB_NAME, values, "blacknum=?", new String[]{blacknum});
		db.close();
	}

	/**
	 * ��ѯ����,ͨ�����������룬��ѯ���������������ģʽ
	 */
	public int queryBlackNum(String blacknum){
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(BlackNumOpenHelper.DB_NAME, new String[]{"mode"}, "blacknum=?", new String[]{blacknum}, null, null, null);
		//����cursor
		if (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
	/**
	 * ��ѯ���ݿ��е���������
	 */
	public List<BlackNumInfo> querryAllNum(){
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(BlackNumOpenHelper.DB_NAME, new String[]{"blacknum","mode"}, null, null, null, null,"_id desc" );//desc:�����ѯ��Ĭ���������ѯasc
		//����cursor
		while (cursor.moveToNext()) {
			 String blacknum = cursor.getString(0);
			 int mode = cursor.getInt(1);
			 //���õ���������ӵ�bean����
			 BlackNumInfo blackNumInfo = new BlackNumInfo(blacknum, mode);
			 list.add(blackNumInfo);
		}
		//�ر����ݿ�
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * ��ѯ���ݿ��еĲ�������
	 * MaxNum:��ѯ��������
	 * startindex����ѯ����ʼλ��
	 */
	public List<BlackNumInfo> querryPartNum(int MaxNum,int startindex){
		
		List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
		SQLiteDatabase db = blackNumOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select blacknum,mode from info order by _id desc limit ? offset ?", new String[]{MaxNum+"",startindex+""});
		//����cursor
		while(cursor.moveToNext()) {
			//��ȡ��ѯ����������
			String blacknum = cursor.getString(0);
			int mode = cursor.getInt(1);
			//���õ���������ӵ�bean����
			BlackNumInfo blackNumInfo = new BlackNumInfo(blacknum, mode);
			list.add(blackNumInfo);
		}
		cursor.close();
		db.close();
		return list;
	}
}
