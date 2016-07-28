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
 * ��ȡϵͳ��ϵ�ˣ�����ҵ����
 * @author lenovo
 *
 */
public class ContactEngine {

	public static List<HashMap<String, String>> getAllContactInfo(Context context){
		//��ʱ����
		SystemClock.sleep(3000);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		//1.��ȡ���ݽ�����
		ContentResolver contentResolver = context.getContentResolver();
		//2.��ȡ�����ṩ�ߵĵ�ַ��com.android.contacts
		//raw_contects��ĵ�ַ��raw_contacts
		//view_data��ĵ�ַ��data
		//3.���ɲ�ѯ�ĵ�ַ
		Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri data_uri = Uri.parse("content://com.android.contacts/data");
		//4.��ѯ�������Ȳ�ѯraw_contacts,��ѯcontac_id
		//projection:��ѯ���ֶ�
		Cursor cursor = contentResolver.query(raw_uri, new String[]{"contact_id"}, null, null, null);
		//5.����cursor
		while (cursor.moveToNext()) {
			//6.��ȡ��ѯ������
			String contact_id = cursor.getString(0);
			//getColumnIndex����ѯ�ֶ���cursor�е�����ֵ��һ�㶼�����ڲ�ѯ�ֶαȽ϶��ʱ��
//			String contact_id = cursor.getString(cursor.getColumnIndex("contact_id"));
			//�ж�contact_id�Ƿ�Ϊ��
			if (!TextUtils.isEmpty(contact_id)) {
			//7.����contact_id��ѯview_data���е����� 	��ѯdata1��mimeType
			//selection:��ѯ������selectionArgs:��ѯ�����Ĳ�����sortOrder:����
			//��ָ��һ�������֣�1.null.������2.����Ϊnull
			Cursor datacursor = contentResolver.query(data_uri, new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{contact_id}, null);
			HashMap<String, String> map = new HashMap<String, String>();
			while (datacursor.moveToNext()) {
				//8.��ѯ��ȡ��������
				String data1 = datacursor.getString(0);
				String mimetype = datacursor.getString(1);
				//9.���������жϻ�ȡ��data1�����ݲ�����
				if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
					//����绰
					map.put("phone", data1);
				}else if (mimetype.equals("vnd.android.cursor.item/name")) {
					//��������
					map.put("name", data1);
				}
			}
			//10.����ȡ����������ӵ�������
			list.add(map);
			//11.�ر�cursor
			datacursor.close();
		  }
		}
		//12.�ر�cursor
		cursor.close();
		return list;
	}
	
}
