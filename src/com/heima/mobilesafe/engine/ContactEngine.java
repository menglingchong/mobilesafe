package com.heima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

/**
 * 获取系统联系人，这是业务类
 * @author lenovo
 *
 */
public class ContactEngine {

	public static List<HashMap<String, String>> getAllContactInfo(Context context){
		//延时操作
		SystemClock.sleep(3000);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		//1.获取内容解析者
		ContentResolver contentResolver = context.getContentResolver();
		//2.获取内容提供者的地址：com.android.contacts
		//raw_contects表的地址：raw_contacts
		//view_data表的地址：data
		//3.生成查询的地址
		Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri data_uri = Uri.parse("content://com.android.contacts/data");
		//4.查询操作，先查询raw_contacts,查询contac_id
		//projection:查询的字段
		Cursor cursor = contentResolver.query(raw_uri, new String[]{"contact_id"}, null, null, null);
		//5.解析cursor
		while (cursor.moveToNext()) {
			//6.获取查询的数据
			String contact_id = cursor.getString(0);
			//getColumnIndex：查询字段在cursor中的索引值，一般都是用在查询字段比较多的时候
//			String contact_id = cursor.getString(cursor.getColumnIndex("contact_id"));
			//判断contact_id是否为空
			if (!TextUtils.isEmpty(contact_id)) {
			//7.根据contact_id查询view_data表中的数据 	查询data1和mimeType
			//selection:查询条件；selectionArgs:查询条件的参数；sortOrder:排序
			//空指针一般有两种：1.null.方法；2.参数为null
			Cursor datacursor = contentResolver.query(data_uri, new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{contact_id}, null);
			HashMap<String, String> map = new HashMap<String, String>();
			while (datacursor.moveToNext()) {
				//8.查询获取到的数据
				String data1 = datacursor.getString(0);
				String mimetype = datacursor.getString(1);
				//9.根据类型判断获取的data1的数据并保存
				if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
					//保存电话
					map.put("phone", data1);
				}else if (mimetype.equals("vnd.android.cursor.item/name")) {
					//保存姓名
					map.put("name", data1);
				}
			}
			//10.将获取到的数据添加到集合中
			list.add(map);
			//11.关闭cursor
			datacursor.close();
		  }
		}
		//12.关闭cursor
		cursor.close();
		return list;
	}
	
}
