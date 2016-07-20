package com.heima.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	/**
	 *�����ݿ⣬��ѯ����Ĺ�����
	 */
	public static String queryAddress(Context context,String num){
		String location = "";
		//1.��ȡ���ݿ��·��
		File file = new File(context.getFilesDir(), "address.db");
		//2.�����ݿ�
		//getAbsolutePath()����ȡ�ļ��ľ���·��
		//factory:�α깤��
		SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		//3.��ѯ���������
		Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{num.substring(0, 7)});
		//4.����cursor
		if (cursor.moveToNext()) {
			location = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return location;
		
	}
}
