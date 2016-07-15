package com.heima.mobilesafe;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

public abstract class SetUPBaseActivity extends Activity {

	//按钮点击事件的操作
	//将每个界面中的上一步，下一步按钮操作，抽取到父类中
	public void next(View v){
		next_activity();
	}
	public void pre(View v){
		pre_activity();
	}
	//因为父类中不知道子类上一步，下一步具体的执行操作代码，所以要创建一个抽象方法，让子类实现这个抽象方法，
	//根据自己的特性去实现抽象方法
	//下一步的操作
	public abstract void next_activity();
	//上一步的操作
	public abstract void pre_activity();
	
	//在父类中直接对子类中的返回键进行统一的处理
	//监听手机物理按钮的点击事件
	//keyCode:物理按钮的标识
	//event：按键的处理事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//判断keycode是否是返回键的标识
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//返回true可以屏幕按键的事件 
//			return true;
			pre_activity();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
