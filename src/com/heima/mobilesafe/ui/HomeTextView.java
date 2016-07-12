package com.heima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * 通过自定义控件实现TextView的跑马灯的效果
 * @author lenovo
 *
 */
public class HomeTextView extends TextView {

	//在代码中使用的时候调用
	public HomeTextView(Context context) {
		super(context);
	}
	//在布局文件中使用的时候调用
	//布局文件中的控件都是可以用代码来代替
	//AttributeSet：保存了控件在布局文件中的所有属性
	public HomeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//也是在布局文件中使用的时候调用，多了样式
	public HomeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	//设置自定义textView的自动获取焦点
	
	@Override
	public boolean isFocused() {
		//true:获取焦点，false：不获取焦点
		return true;
	}
	

	
}
