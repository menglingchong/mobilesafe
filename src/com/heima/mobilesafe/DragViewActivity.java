package com.heima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class DragViewActivity extends Activity {

	private LinearLayout ll_dragview_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dragview);
		ll_dragview_location = (LinearLayout) findViewById(R.id.ll_dragview_location);
		setTouch();
	}
	/**
	 * 设置手势的监听
	 */
	private void setTouch() {
		ll_dragview_location.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//event.getAction(): 获取手势执行的事件
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://手指按下的事件
					System.out.println("手指按下了！！！");
					//手指按下的坐标
					
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE://手指移动的事件
					System.out.println("手指移动了！！！");
					//手指移动到的坐标
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					//坐标的偏移量
					int dX= newX -startX;
					int dY= newY -startY;
					//移动相应的偏移量，重新绘制坐标
					//获取原控件距离左边和顶部的距离
					int left = ll_dragview_location.getLeft();
					int top = ll_dragview_location.getTop();
					int l = left+dX;
					int t = top+dY;
					int r = l+ll_dragview_location.getWidth();
					int b = t+ll_dragview_location.getHeight();
					ll_dragview_location.layout(l, t, r, b);//重新绘制控件
					//重新设置起始坐标
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP://手指抬起的事件
					System.out.println("手指抬起了！！！");
					break;
				}
				
				//返回false:事件事件进行拦截，不能向下传递；返回true:是将事件消费即事件向下执行
				return true;
			}
		});
	}
}
