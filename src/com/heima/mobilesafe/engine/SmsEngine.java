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
 * 短信备份的业务类，将短信备份成xml格式
 * @author lenovo
 */
public class SmsEngine {
	
	//1.创建刷子(回调接口)
	public interface ShowProgress{
		//设置最大进度
		public void setMax(int max);
		//设置当前进度
		public void setProgress(int progress);
	}
	
	//2.给调用者刷子,并注册回调接口
	public static void getAllSms(Context context,ShowProgress showProgress){
		//1.获取短信
		//1.1获取短信的内容解析者
		ContentResolver contentResolver = context.getContentResolver();
		//1.2获取内容提供者的地址   sms,sms表的地址为 null
		//1.3获取查询路径
		Uri uri = Uri.parse("content://sms");
		//1.4查询操作
		Cursor cursor = contentResolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		int count = cursor.getCount();
		showProgress.setMax(count);//设置最大进度
		//设置当前进度
		int progress =0;
		
		//2.备份短信
		//2.1获取xml序列器
		XmlSerializer xmlSerializer = Xml.newSerializer();
		try {
			//2.2保存xml文件的保存的路径
			xmlSerializer.setOutput(new FileOutputStream(new File("storage/sdcard1/backupsms.xml")), "utf-8");
			//2.3设置头信息
			xmlSerializer.startDocument("utf-8", true);
			//2.4设置根标签
			xmlSerializer.startTag(null, "smss");
			//1.5解析cursor
			while(cursor.moveToNext()){
				SystemClock.sleep(1000);
				//2.5设置短信的标签
				xmlSerializer.startTag(null, "sms");
				
				//2.6设置文本内容的标签
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
				//设置进度条的当前进度
				progress++;
				showProgress.setProgress(progress);
			}
			xmlSerializer.endTag(null, "smss");
			xmlSerializer.endDocument();
			//将数据刷新到文件中
			xmlSerializer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
