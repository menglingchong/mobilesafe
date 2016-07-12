package com.heima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class StreamUtil {
	/**
	 * 将流信息转化成字符串
	 * @return
	 * @throws IOException 
	 */
	public static String parserStreamUtil(InputStream in) throws IOException{
		//字符流，读取流，将字节流转换成字符流
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		//写入流
		StringWriter sw = new StringWriter();
		//进行读写操作
		String str =null;//数据缓冲区
		while ((str = br.readLine()) != null) {
			//写入操作 
			sw.write(str);
		}
		//关闭流
		sw.close();
		br.close();
		return sw.toString();
	}
}
