package com.heima.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5���ܵĹ�����
 * @author lenovo
 *
 */
public class MD5Util {
	/**
	 * MD5����
	 * @param password
	 * @return
	 */
	public static String passwordMD5(String password){
		
		StringBuilder sb = new StringBuilder();
		try {
			//1.��ȡ����ժҪ��
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			//2.��һ��byte������м��ܣ�����һ�����ܹ���byte���飬�����ƵĹ�ϣ���㣬MD5���ܵĵ�һ��
			byte[] digest = messageDigest.digest(password.getBytes());
			//3.����byte����
			for (int i = 0; i < digest.length; i++) {
				//4.MD5����
				int result= digest[i] & 0xff;
				//���õ���int����ֵת����16�����ַ���
				String hexString = Integer.toHexString(result);
				if (hexString.length()<2) {
					sb.append("0");
				} else {
					sb.append(hexString);
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			//�Ҳ������ܷ�ʽ���쳣
			e.printStackTrace();
		}
		return null;
	}
	
}
