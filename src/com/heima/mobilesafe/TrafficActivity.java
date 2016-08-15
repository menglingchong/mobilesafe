package com.heima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

public class TrafficActivity extends Activity {

	private DrawerLayout drawerlayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		drawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		
//		drawerlayout.openDrawer(Gravity.LEFT);//表示默认打开那个方式的布局
		drawerlayout.openDrawer(Gravity.RIGHT);
	}
	
}
