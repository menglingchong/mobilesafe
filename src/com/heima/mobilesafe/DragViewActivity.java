package com.heima.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DragViewActivity extends Activity {

	private LinearLayout ll_dragview_location;
	private SharedPreferences sp;
	private int screenWidth;
	private int screenHeight;
	private TextView tv_dragview_bottom;
	private TextView tv_dragview_top;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dragview);
		sp =getSharedPreferences("config", MODE_PRIVATE);
		ll_dragview_location = (LinearLayout) findViewById(R.id.ll_dragview_location);
		tv_dragview_bottom = (TextView) findViewById(R.id.tv_dragview_bottom);
		tv_dragview_top = (TextView) findViewById(R.id.tv_dragview_top);
		//获取屏幕的大小
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		int width = windowManager.getDefaultDisplay().getWidth();//过期的方法
		DisplayMetrics displayMetrics = new DisplayMetrics();//创建一张白纸
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);//给白纸设置宽高
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		
		//设置控件的回显操作
		//1.获取保存的坐标
		int x = sp.getInt("x", 0);
		int y = sp.getInt("y", 0);
		System.out.println("x:"+x+"	y:"+y);
		 
		//初始化控件之前重新设置控件的属性
		//获取父控件的属性规则，父控件的layoutparams
		RelativeLayout.LayoutParams layoutParams = (LayoutParams) ll_dragview_location.getLayoutParams();
		//设置相应的属性
		layoutParams.leftMargin=x;
		layoutParams.topMargin=y;
		//给控件设置属性
		ll_dragview_location.setLayoutParams(layoutParams);
		
		if (y >= screenHeight/2) {
			//隐藏下方，显示上方
			tv_dragview_bottom.setVisibility(View.INVISIBLE);
			tv_dragview_top.setVisibility(View.VISIBLE);
		} else if (y <screenHeight/2) {
			//隐藏上方，显示下方
			tv_dragview_bottom.setVisibility(View.VISIBLE);
			tv_dragview_top.setVisibility(View.INVISIBLE);
		}
		setTouch();
		setDoubleClick();
	}
	
	long [] mHits = new long[2];

	/**
	 * 双击居中
	 */
	private void setDoubleClick() {
		ll_dragview_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				System.out.println("我双击了！！");
				/**
				 *  src the source array to copy the content.   拷贝的原数组
					srcPos the starting index of the content in src.  是从源数组那个位置开始拷贝
					dst the destination array to copy the data into.  拷贝的目标数组
					dstPos the starting index for the copied content in dst.  是从目标数组那个位置开始去写
					length the number of elements to be copied.   拷贝的长度
				 */
				//拷贝数组的操作
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				//判断离开机的时间，设置给数组的第二个元素，
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				//判断是否是多击操作
				if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
					System.out.println("双击了");
					//双击居中
					int l =(screenWidth-ll_dragview_location.getWidth())/2;
					int t =(screenHeight-ll_dragview_location.getHeight())/2;
					ll_dragview_location.layout(l, t, l+ll_dragview_location.getWidth(), t+ll_dragview_location.getHeight());
					
					//保存控件的坐标
					Editor edit = sp.edit();
					edit.putInt("x", l);
					edit.putInt("y", t);
					edit.commit();
				}
			}
		});
		
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
					//在控件的绘制之前要判断ltrb是否超出屏幕，如果超出屏幕就不进行绘制
					if (l<0 || r>screenWidth ||t<0 || b>screenHeight-30) {
						break;
					}
					ll_dragview_location.layout(l, t, r, b);//重新绘制控件
					//判断TextView的显示与隐藏
					int topScreen = ll_dragview_location.getTop();
					if (topScreen > screenHeight/2) {
						//隐藏下方，显示上方
						tv_dragview_bottom.setVisibility(View.INVISIBLE);
						tv_dragview_top.setVisibility(View.VISIBLE);
					} else if (topScreen <screenHeight/2) {
						//隐藏上方，显示下方
						tv_dragview_bottom.setVisibility(View.VISIBLE);
						tv_dragview_top.setVisibility(View.INVISIBLE);
					}
					//重新设置起始坐标
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP://手指抬起的事件
					System.out.println("手指抬起了！！！");
					//保存控件的坐标，保存的是控件的坐标。而不是手指的坐标
					int x = ll_dragview_location.getLeft();
					int y = ll_dragview_location.getTop();
					
					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				
				//返回false:事件事件进行拦截，不能向下传递；返回true:是将事件消费即事件向下执行
				return false;
			}
		});
	}
}
