package com.heima.mobilesafe.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Xml;
import android.widget.ProgressBar;

/**
 * ���ű��ݵ�ҵ���࣬�����ű��ݳ�xml��ʽ
 * @author lenovo
 */
public class SmsEngine {
	
	//1.����ˢ��(�ص��ӿ�)
	public interface ShowProgress{
		//����������
		public void setMax(int max);
		//���õ�ǰ����
		public void setProgress(int progress);
	}
	
	//2.��������ˢ��,��ע��ص��ӿ�
	public static void getAllSms(Context context,ShowProgress showProgress){
		//1.��ȡ����
		//1.1��ȡ���ŵ����ݽ�����
		ContentResolver contentResolver = context.getContentResolver();
		//1.2��ȡ�����ṩ�ߵĵ�ַ   sms,sms��ĵ�ַΪ null
		//1.3��ȡ��ѯ·��
		Uri uri = Uri.parse("content://sms");
		//1.4��ѯ����
		Cursor cursor = contentResolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		int count = cursor.getCount();
		showProgress.setMax(count);//����������
		//���õ�ǰ����
		int progress =0;
		
		//2.���ݶ���
		//2.1��ȡxml������
		XmlSerializer xmlSerializer = Xml.newSerializer();
		try {
			//2.2����xml�ļ��ı����·��
			xmlSerializer.setOutput(new FileOutputStream(new File("storage/sdcard1/backupsms.xml")), "utf-8");
			//2.3����ͷ��Ϣ
			xmlSerializer.startDocument("utf-8", true);
			//2.4���ø���ǩ
			xmlSerializer.startTag(null, "smss");
			//1.5����cursor
			while(cursor.moveToNext()){
				SystemClock.sleep(1000);
				//2.5���ö��ŵı�ǩ
				xmlSerializer.startTag(null, "sms");
				
				//2.6�����ı����ݵı�ǩ
				xmlSerializer.startTag(null, "address");
				String address = cursor.getString(0);
				xmlSerializer.text(address);
				xmlSerializer.endTag(null, "address");
				
				xmlSerializer.startTag(null, "date");
				String date = cursor.getString(1);
				xmlSerializer.text(date);
				xmlSerializer.endTag(null, "date");
				
				xmlSerializer.startTag(null, "type");
				String type = cursor.getString(2);
				xmlSerializer.text(type);
				xmlSerializer.endTag(null, "type");
				
				xmlSerializer.startTag(null, "body");
				String body = cursor.getString(3);
				xmlSerializer.text(body);
				xmlSerializer.endTag(null, "body");
				
				xmlSerializer.endTag(null, "sms");
//				System.out.println("address:"+address+" date:"+date+" type:"+type+" body:"+body);
				//���ý������ĵ�ǰ����
				progress++;
				showProgress.setProgress(progress);
			}
			xmlSerializer.endTag(null, "smss");
			xmlSerializer.endDocument();
			//������ˢ�µ��ļ���
			xmlSerializer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
