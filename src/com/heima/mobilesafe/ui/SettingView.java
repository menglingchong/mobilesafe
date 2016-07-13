package com.heima.mobilesafe.ui;

import com.heima.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义组合控件，为了优化布局文件中的代码，使用自定义组合控件
 * @author lenovo
 *
 */
public class SettingView extends RelativeLayout {

	private TextView tv_setting_title;
	private TextView tv_setting_des;
	private CheckBox cb_setting_chose;
	//在代码中使用的时候调用
	public SettingView(Context context) {
		super(context);
		init();//在初始化自定义控件的时候添加控件
	}
	
	//在布局文件中使用的时候调用，多了一个类型参数
	public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	//在布局文件中使用的时候调用，AttributeSet：控件的属性值
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	//添加控件
	public void init(){
		//添加布局文件
//		TextView textView = new TextView(getContext());
//		textView.setText("我是自定义组合控件");
		//第一种方式：将布局文件转化成view对象添加到自定义组合控件中
//		View view = View.inflate(getContext(), R.layout.settingview, null);//先有爹，去找孩子
//		this.addView(view);//this:代表自定义控件的类,在自定义组合控件中添加控件
		//第二种方式：获取view对象，同时给view对象设置父控件，相当于先创建一个view对象，然后再把控件放到自定义控件中
		View view = View.inflate(getContext(), R.layout.settingview, this);//孩子有了，去找爹，
		//view.findViewById():获取settingView布局文件中的相应的控件
		tv_setting_title = (TextView) view.findViewById(R.id.tv_setting_title);
		tv_setting_des = (TextView) view.findViewById(R.id.tv_setting_des);
		cb_setting_chose = (CheckBox) view.findViewById(R.id.cb_setting_chose);
	}
	
	//为了改变自定义控件的值，添加一些方法有利用修改自定义控件的值
	/**
	 *  设置标题的方法
	 */
	public void setTitle(String title){
		tv_setting_title.setText(title);
	}
	/**
	 * 设置描述详细信息的方法
	 */
	public void setDes(String des){
		tv_setting_des.setText(des);
	}
	
	/**
	 * 设置checkbox的是否被选中方法
	 */
	public void setChecked(boolean isChecked){
		cb_setting_chose.setChecked(isChecked);//设置checkbox的状态
	}
	/**
	 * 获取checkbox的状态
	 * @return
	 */
	public boolean isChecked(){
		return cb_setting_chose.isChecked();
	}
}
