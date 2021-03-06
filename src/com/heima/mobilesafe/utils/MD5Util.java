package com.heima.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密的工具类
 * @author lenovo
 *
 */
public class MD5Util {
	/**
	 * MD5加密
	 * @param password
	 * @return
	 */
	public static String passwordMD5(String password){
		
		StringBuilder sb = new StringBuilder();
		try {
			//1.获取数据摘要器
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			//2.将一个byte数组进行加密，返回一个加密过的byte数组，二进制的哈希计算，MD5加密的第一步
			byte[] digest = messageDigest.digest(password.getBytes());
			//3.遍历byte数组
			for (int i = 0; i < digest.length; i++) {
				//4.MD5加密
				int result= digest[i] & 0xff;
				//将得到的int类型值转化成16进制字符串
				String hexString = Integer.toHexString(result);
				if (hexString.length()<2) {
					sb.append("0");
				} 
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			//找不到加密方式的异常
			e.printStackTrace();
		}
		return null;
	}
}
