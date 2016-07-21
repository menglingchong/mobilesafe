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
		//使用正则表达式进行判断
		//^1[34578]\d{9}$
		if (num.matches("^1[34578]\\d{9}$")) {//11位电话
			Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{num.substring(0, 7)});
			//4.解析cursor
			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}
			cursor.close();
		}else {
			//对特殊电话做处理
			switch (num.length()) {
			case 3:
				location ="特殊电话";
				break;
			case 4:
				location ="虚拟电话";
				break;
			case 5:
				location ="客服电话";
				break;
			case 7://座机，本地电话
				location ="本地电话";
				break;
			default:
				//长途电话
				if (num.length()>=10 && num.startsWith("0")) {
					//根据地区号查询号码归属
					//1.获取号码的区号		substring()：获取头不获取尾
					String result = num.substring(1, 3);//010	10:区号为3位
					//2.根据区号查询号码的归属地
					Cursor cursor = database.rawQuery("select location from data2 where area=?", new String[]{result});
					//3.解析cursor
					if (cursor.moveToNext()) {
						location = cursor.getString(0);
						//截取数据
						location = location.substring(0, location.length()-2);
						cursor.close();
					}else {
						//区号为3位的没有查询到，查询区号为4位
						//获取4为的区号
						result = num.substring(1, 4);
						//根据区号查询号码的归属地
						cursor = database.rawQuery("select location from data2 where area=?", new String[]{result});
						if (cursor.moveToNext()) {
							location = cursor.getString(0);
							//截取数据
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
