package com.heima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * ͨ���Զ���ؼ�ʵ��TextView������Ƶ�Ч��
 * @author lenovo
 *
 */
public class HomeTextView extends TextView {

	//�ڴ�����ʹ�õ�ʱ�����
	public HomeTextView(Context context) {
		super(context);
	}
	//�ڲ����ļ���ʹ�õ�ʱ�����
	//�����ļ��еĿؼ����ǿ����ô���������
	//AttributeSet�������˿ؼ��ڲ����ļ��е���������
	public HomeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//Ҳ���ڲ����ļ���ʹ�õ�ʱ����ã�������ʽ
	public HomeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	//�����Զ���textView���Զ���ȡ����
	
	@Override
	public boolean isFocused() {
		//true:��ȡ���㣬false������ȡ����
		return true;
	}
	

	
}
