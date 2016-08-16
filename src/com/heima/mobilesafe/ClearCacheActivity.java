package com.heima.mobilesafe;

import com.heima.mobilesafe.fragment.CacheFragment;
import com.heima.mobilesafe.fragment.SdFragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class ClearCacheActivity extends FragmentActivity {

	private CacheFragment cacheFragment;
	private SdFragment sdFragment;
	private FragmentManager supportFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clearcache);
		
		//1.获取fragment
		cacheFragment = new CacheFragment();
		sdFragment = new SdFragment();
		//2.获取fragment的管理者
		supportFragmentManager = getSupportFragmentManager();
		//开启事务
		FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
		//添加fragment到控件中
		//参数1：要替换的控件的id；参数2：要添加的fragment
		beginTransaction.add(R.id.fl_clearcache_fragment, cacheFragment);//数据不会刷新
		beginTransaction.add(R.id.fl_clearcache_fragment, sdFragment); 
		//隐藏fragment操作
		beginTransaction.hide(sdFragment);
		//提交事务
		beginTransaction.commit();
	}
	//按钮的点击事件
	public void cache(View v){
		FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
		beginTransaction.hide(sdFragment);
	    beginTransaction.show(cacheFragment);
		beginTransaction.commit();
	}
	
	public void sd(View v){
		FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
		beginTransaction.hide(cacheFragment);
		beginTransaction.show(sdFragment);
		beginTransaction.commit();
	}
}
