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
		//ʹ��������ʽ�����ж�
		//^1[34578]\d{9}$
		if (num.matches("^1[34578]\\d{9}$")) {//11λ�绰
			Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{num.substring(0, 7)});
			//4.����cursor
			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}
			cursor.close();
		}else {
			//������绰������
			switch (num.length()) {
			case 3:
				location ="����绰";
				break;
			case 4:
				location ="����绰";
				break;
			case 5:
				location ="�ͷ��绰";
				break;
			case 7://���������ص绰
				location ="���ص绰";
				break;
			default:
				//��;�绰
				if (num.length()>=10 && num.startsWith("0")) {
					//���ݵ����Ų�ѯ�������
					//1.��ȡ���������		substring()����ȡͷ����ȡβ
					String result = num.substring(1, 3);//010	10:����Ϊ3λ
					//2.�������Ų�ѯ����Ĺ�����
					Cursor cursor = database.rawQuery("select location from data2 where area=?", new String[]{result});
					//3.����cursor
					if (cursor.moveToNext()) {
						location = cursor.getString(0);
						//��ȡ����
						location = location.substring(0, location.length()-2);
						cursor.close();
					}else {
						//����Ϊ3λ��û�в�ѯ������ѯ����Ϊ4λ
						//��ȡ4Ϊ������
						result = num.substring(1, 4);
						//�������Ų�ѯ����Ĺ�����
						cursor = database.rawQuery("select location from data2 where area=?", new String[]{result});
						if (cursor.moveToNext()) {
							location = cursor.getString(0);
							//��ȡ����
							location= location.substring(0, location.length()-2);
							cursor.close();
						}
					}
				}
				break;
			}
		}
		database.close();
		return location;
		
	}
}
