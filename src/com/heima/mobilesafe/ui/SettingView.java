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
	private String des_on;
	private String des_off;
	//在代码中使用的时候调用，即使用java代码创建视图的时候调用
	public SettingView(Context context) {
		super(context);
		init();//在初始化自定义控件的时候添加控件
	}
	 
	//在布局文件中使用的时候调用（即使用xml创建视图的时候调用），多了一个类型参数
	public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	//在布局文件中使用的时候调用（即使用xml创建视图的时候调用），AttributeSet：保存控件所有的属性值
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		//通过AttributeSet获取控件的属性,这种方式不能获取控件的某个单独的属性值
		//AttributeSet：保存控件所有的属性值
/*		int count = attrs.getAttributeCount();//获取控件的个数
		System.out.println("控件的个数："+count);
		for (int i = 0; i < count; i++) {
			//获取某个属性的值
			String value = attrs.getAttributeValue(i);
			System.out.println(value);
		}*/
		//通过命名空间和属性的名称获取控件的属性值
		//namespace:命名空间	,name:属性的名称
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.heima.mobilesafe", "title");
		des_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.heima.mobilesafe", "des_on");
		des_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.heima.mobilesafe", "des_off");
		//给自定义组合控件设置相应的值
		//初始化控件的值
		tv_setting_title.setText(title);
		if (isChecked()) {
			tv_setting_des.setText(des_on);
		} else {
			tv_setting_des.setText(des_off);
		}
		
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
	 * 设置checkbox的是否被选中的方法
	 */
	public void setChecked(boolean isChecked){
		cb_setting_chose.setChecked(isChecked);//设置checkbox的状态
		//其实就是把sv_setting_update.setDes("打开提示更新");封装到了setChecked()方法中
		if (isChecked()) {
			tv_setting_des.setText(des_on);
		} else {
			tv_setting_des.setText(des_off);
		}
	}
	/**
	 * 获取checkbox的状态
	 * @return
	 */
	public boolean isChecked(){
		return cb_setting_chose.isChecked();
	}
}
