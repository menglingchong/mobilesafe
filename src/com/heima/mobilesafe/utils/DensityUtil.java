package com.heima.mobilesafe.utils;

import android.content.Context;

public class DensityUtil {

	/** 
     * �����ֻ��ķֱ��ʴ� dip �ĵ�λ ת��Ϊ px(����) 
	 * @return 
     */  
    public static int dip2qx(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  //��ȡ��Ļ���ܶ�
        return (int) (dpValue * scale + 0.5f); //+0.5f��������   3.7  3   3.7+0.5 = 4.2   4
    }  
  
    /** 
     * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
