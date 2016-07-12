package com.heima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class StreamUtil {
	/**
	 * ������Ϣת�����ַ���
	 * @return
	 * @throws IOException 
	 */
	public static String parserStreamUtil(InputStream in) throws IOException{
		//�ַ�������ȡ�������ֽ���ת�����ַ���
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		//д����
		StringWriter sw = new StringWriter();
		//���ж�д����
		String str =null;//���ݻ�����
		while ((str = br.readLine()) != null) {
			//д����� 
			sw.write(str);
		}
		//�ر���
		sw.close();
		br.close();
		return sw.toString();
	}
}
